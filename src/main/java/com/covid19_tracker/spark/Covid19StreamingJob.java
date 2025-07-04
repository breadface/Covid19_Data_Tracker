package com.covid19_tracker.spark;

import com.covid19_tracker.model.Covid19Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.time.LocalDate;
import java.util.*;

/**
 * Spark Streaming job for real-time processing of COVID-19 data from Kafka
 */
public class Covid19StreamingJob {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19StreamingJob.class);
    
    // Kafka configuration
    private static final String BOOTSTRAP_SERVERS = "kafka:29092";
    private static final String TOPIC_NAME = "covid19-data";
    private static final String GROUP_ID = "spark-streaming-group";
    
    // HDFS output paths
    private static final String HDFS_OUTPUT_PATH = "hdfs://namenode:9000/covid19/streaming/";
    private static final String HDFS_CHECKPOINT_PATH = "hdfs://namenode:9000/covid19/checkpoints/";
    
    private final JavaStreamingContext streamingContext;
    private final ObjectMapper objectMapper;
    
    public Covid19StreamingJob() {
        // Spark configuration
        SparkConf sparkConf = new SparkConf()
                .setAppName("COVID-19 Streaming Analytics")
                .setMaster("local[*]")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        
        // Create streaming context with 10-second batch interval
        this.streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(10));
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDate
        
        // Set checkpoint directory for fault tolerance
        streamingContext.checkpoint(HDFS_CHECKPOINT_PATH);
    }
    
    /**
     * Start the streaming job
     */
    public void start() {
        logger.info("Starting COVID-19 Spark Streaming job");
        
        // Create Kafka stream
        JavaDStream<Covid19Data> covid19Stream = createKafkaStream();
        
        // Process the stream
        processCovid19Stream(covid19Stream);
        
        // Start the streaming context
        streamingContext.start();
        
        try {
            streamingContext.awaitTermination();
        } catch (InterruptedException e) {
            logger.error("Streaming job interrupted: {}", e.getMessage());
        }
    }
    
    /**
     * Create Kafka stream
     */
    private JavaDStream<Covid19Data> createKafkaStream() {
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", GROUP_ID);
        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", false);
        
        Collection<String> topics = Arrays.asList(TOPIC_NAME);
        
        JavaDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
        );
        
        // Transform to Covid19Data objects
        return stream.map((Function<ConsumerRecord<String, String>, Covid19Data>) record -> {
            try {
                return objectMapper.readValue(record.value(), Covid19Data.class);
            } catch (JsonProcessingException e) {
                logger.error("Error deserializing COVID-19 data: {}", e.getMessage());
                return null;
            }
        }).filter((Function<Covid19Data, Boolean>) Objects::nonNull);
    }
    
    /**
     * Process the COVID-19 data stream
     */
    private void processCovid19Stream(JavaDStream<Covid19Data> covid19Stream) {
        
        // 1. Real-time data validation and filtering
        JavaDStream<Covid19Data> validData = covid19Stream.filter(data -> 
            data.getConfirmedCases() != null && data.getConfirmedCases() >= 0 &&
            data.getDeaths() != null && data.getDeaths() >= 0 &&
            data.getCountry() != null && !data.getCountry().isEmpty()
        );
        
        // 2. Country-wise aggregation (real-time)
        JavaPairDStream<String, Covid19Data> countryData = validData.mapToPair(
            (PairFunction<Covid19Data, String, Covid19Data>) data -> 
                new Tuple2<>(data.getCountry(), data)
        );
        
        // 3. Windowed aggregation (last 5 minutes)
        JavaPairDStream<String, Covid19Data> windowedData = countryData.window(
            Durations.minutes(5), Durations.seconds(10)
        );
        
        // 4. Calculate real-time statistics
        JavaDStream<String> statistics = windowedData.groupByKey().map(
            (Function<Tuple2<String, Iterable<Covid19Data>>, String>) tuple -> {
                String country = tuple._1;
                Iterable<Covid19Data> dataList = tuple._2;
                
                int totalCases = 0;
                int totalDeaths = 0;
                int count = 0;
                
                for (Covid19Data data : dataList) {
                    totalCases += data.getConfirmedCases() != null ? data.getConfirmedCases() : 0;
                    totalDeaths += data.getDeaths() != null ? data.getDeaths() : 0;
                    count++;
                }
                
                double mortalityRate = totalCases > 0 ? (double) totalDeaths / totalCases * 100 : 0;
                
                return String.format("Country: %s, Total Cases: %d, Total Deaths: %d, Mortality Rate: %.2f%%, Records: %d",
                        country, totalCases, totalDeaths, mortalityRate, count);
            }
        );
        
        // 5. Alert for high mortality rates
        JavaDStream<String> alerts = validData.filter(data -> {
            if (data.getConfirmedCases() != null && data.getConfirmedCases() > 0) {
                double mortalityRate = (double) data.getDeaths() / data.getConfirmedCases() * 100;
                return mortalityRate > 5.0; // Alert if mortality rate > 5%
            }
            return false;
        }).map(data -> {
            double mortalityRate = (double) data.getDeaths() / data.getConfirmedCases() * 100;
            return String.format("ALERT: High mortality rate in %s - %.2f%% (Cases: %d, Deaths: %d)",
                    data.getCountry(), mortalityRate, data.getConfirmedCases(), data.getDeaths());
        });
        
        // 6. Save to HDFS
        validData.foreachRDD((JavaRDD<Covid19Data> rdd, org.apache.spark.streaming.Time time) -> {
            if (!rdd.isEmpty()) {
                String timestamp = String.valueOf(time.milliseconds());
                String outputPath = HDFS_OUTPUT_PATH + "batch-" + timestamp;
                
                // Convert to JSON and save
                JavaRDD<String> jsonRDD = rdd.map(data -> {
                    try {
                        return objectMapper.writeValueAsString(data);
                    } catch (JsonProcessingException e) {
                        return "";
                    }
                }).filter(s -> !s.isEmpty());
                
                jsonRDD.saveAsTextFile(outputPath);
                logger.info("Saved {} records to HDFS: {}", rdd.count(), outputPath);
            }
        });
        
        // 7. Print statistics and alerts
        statistics.foreachRDD((JavaRDD<String> rdd, org.apache.spark.streaming.Time time) -> {
            if (!rdd.isEmpty()) {
                logger.info("=== Real-time Statistics (Batch: {}) ===", time.milliseconds());
                rdd.collect().forEach(logger::info);
            }
        });
        
        alerts.foreachRDD((JavaRDD<String> rdd, org.apache.spark.streaming.Time time) -> {
            if (!rdd.isEmpty()) {
                logger.warn("=== COVID-19 Alerts (Batch: {}) ===", time.milliseconds());
                rdd.collect().forEach(logger::warn);
            }
        });
        
        // 8. Print incoming data
        validData.foreachRDD((JavaRDD<Covid19Data> rdd, org.apache.spark.streaming.Time time) -> {
            if (!rdd.isEmpty()) {
                logger.info("=== Incoming COVID-19 Data (Batch: {}) ===", time.milliseconds());
                rdd.collect().forEach(data -> 
                    logger.info("Received: {} - {} - Cases: {}, Deaths: {}", 
                              data.getDate(), data.getCountry(), 
                              data.getConfirmedCases(), data.getDeaths())
                );
            }
        });
    }
    
    /**
     * Stop the streaming job
     */
    public void stop() {
        if (streamingContext != null) {
            streamingContext.stop(true, true);
            logger.info("COVID-19 Spark Streaming job stopped");
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        Covid19StreamingJob streamingJob = new Covid19StreamingJob();
        
        try {
            streamingJob.start();
        } catch (Exception e) {
            logger.error("Error in streaming job: {}", e.getMessage());
        } finally {
            streamingJob.stop();
        }
    }
} 
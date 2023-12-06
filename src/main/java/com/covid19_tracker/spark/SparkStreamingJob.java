package com.covid19_tracker.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SparkStreamingJob {
    public static void main(String[] args) throws InterruptedException {
        // Spark configuration
        SparkConf sparkConf = new SparkConf().setAppName("SparkStreamingExample");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(5));

        // Kafka configuration
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("group.id", "spark-consumer-group");

        Collection<String> topics = Arrays.asList("flume-topic");

        // Create Kafka direct stream
        JavaDStream<String> kafkaStream = KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
        ).map(record -> record.value());

        // Process the Kafka stream (example: count the number of events in each batch)
        kafkaStream.foreachRDD((JavaRDD<String> rdd) -> {
            System.out.println("Number of events in this batch: " + rdd.count());

            // Add your processing logic here
        });

        // Start the Spark Streaming context
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
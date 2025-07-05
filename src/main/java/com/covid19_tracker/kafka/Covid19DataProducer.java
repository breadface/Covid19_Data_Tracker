package com.covid19_tracker.kafka;

import com.covid19_tracker.model.Covid19Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDate;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Kafka producer for ingesting COVID-19 data into the big data pipeline
 */
public class Covid19DataProducer {
    
    private static final Logger logger = Logger.getLogger(Covid19DataProducer.class.getName());
    private static final String TOPIC_NAME = "covid19-data";
    
    // Use environment variable for Cloudera CDH, fallback to localhost
    private static final String BOOTSTRAP_SERVERS = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092");
    
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;
    
    public Covid19DataProducer() {
        this.producer = createProducer();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Explicitly register JavaTimeModule for LocalDate
        
        logger.info("Initialized Kafka producer with bootstrap servers: " + BOOTSTRAP_SERVERS);
    }
    
    private KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        return new KafkaProducer<>(props);
    }
    
    /**
     * Send COVID-19 data to Kafka topic
     */
    public void sendCovid19Data(Covid19Data covid19Data) {
        try {
            String jsonData = objectMapper.writeValueAsString(covid19Data);
            String key = generateKey(covid19Data);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, jsonData);
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.log(Level.SEVERE, "Error sending COVID-19 data to Kafka: " + exception.getMessage());
                } else {
                    logger.info("COVID-19 data sent successfully to topic: " + metadata.topic() + 
                              ", partition: " + metadata.partition() + ", offset: " + metadata.offset());
                }
            });
            
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing COVID-19 data: " + e.getMessage());
        }
    }
    
    /**
     * Send COVID-19 data synchronously (for testing)
     */
    public void sendCovid19DataSync(Covid19Data covid19Data) throws ExecutionException, InterruptedException {
        try {
            String jsonData = objectMapper.writeValueAsString(covid19Data);
            String key = generateKey(covid19Data);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, jsonData);
            producer.send(record).get();
            
            logger.info("COVID-19 data sent synchronously: " + covid19Data);
            
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing COVID-19 data: " + e.getMessage());
        }
    }
    
    /**
     * Generate a key for the Kafka record based on date and country
     */
    private String generateKey(Covid19Data covid19Data) {
        return covid19Data.getDate() + "-" + covid19Data.getCountry();
    }
    
    /**
     * Close the producer
     */
    public void close() {
        producer.flush();
        producer.close();
        logger.info("COVID-19 data producer closed");
    }
} 
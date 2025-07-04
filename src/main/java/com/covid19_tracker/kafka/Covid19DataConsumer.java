package com.covid19_tracker.kafka;

import com.covid19_tracker.model.Covid19Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Kafka consumer for consuming COVID-19 data from the big data pipeline
 */
public class Covid19DataConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19DataConsumer.class);
    private static final String TOPIC_NAME = "covid19-data";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "covid19-consumer-group";
    
    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper objectMapper;
    private volatile boolean running = true;
    
    public Covid19DataConsumer() {
        this.consumer = createConsumer();
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDate
    }
    
    private KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        
        return consumer;
    }
    
    /**
     * Start consuming messages with a custom handler
     */
    public void startConsuming(Consumer<Covid19Data> dataHandler) {
        logger.info("Starting COVID-19 data consumer for topic: {}", TOPIC_NAME);
        
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Covid19Data covid19Data = objectMapper.readValue(record.value(), Covid19Data.class);
                        logger.info("Received COVID-19 data: key={}, partition={}, offset={}", 
                                  record.key(), record.partition(), record.offset());
                        
                        // Process the data
                        dataHandler.accept(covid19Data);
                        
                    } catch (JsonProcessingException e) {
                        logger.error("Error deserializing COVID-19 data: {}", e.getMessage());
                    }
                }
            }
        } finally {
            consumer.close();
            logger.info("COVID-19 data consumer stopped");
        }
    }
    
    /**
     * Start consuming messages with default logging handler
     */
    public void startConsuming() {
        startConsuming(covid19Data -> {
            logger.info("Processing COVID-19 data: {} - {} - Cases: {}, Deaths: {}", 
                       covid19Data.getDate(), covid19Data.getCountry(), 
                       covid19Data.getConfirmedCases(), covid19Data.getDeaths());
        });
    }
    
    /**
     * Stop the consumer
     */
    public void stop() {
        running = false;
        logger.info("Stopping COVID-19 data consumer");
    }
    
    /**
     * Close the consumer
     */
    public void close() {
        stop();
        consumer.close();
        logger.info("COVID-19 data consumer closed");
    }
    
    /**
     * Test method to consume messages for a limited time
     */
    public void consumeForDuration(Duration duration, Consumer<Covid19Data> dataHandler) {
        logger.info("Consuming COVID-19 data for duration: {}", duration);
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + duration.toMillis();
        
        try {
            while (System.currentTimeMillis() < endTime && running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Covid19Data covid19Data = objectMapper.readValue(record.value(), Covid19Data.class);
                        logger.info("Received COVID-19 data: key={}, partition={}, offset={}", 
                                  record.key(), record.partition(), record.offset());
                        
                        dataHandler.accept(covid19Data);
                        
                    } catch (JsonProcessingException e) {
                        logger.error("Error deserializing COVID-19 data: {}", e.getMessage());
                    }
                }
            }
        } finally {
            consumer.close();
            logger.info("COVID-19 data consumer stopped after duration");
        }
    }
} 
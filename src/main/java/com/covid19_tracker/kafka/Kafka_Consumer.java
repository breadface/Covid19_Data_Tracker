package com.covid19_tracker.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Kafka_Consumer {
    public static void main(String[] args) {
        // Kafka consumer configuration
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "flume-consumer-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // Create Kafka consumer
        Consumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        // Subscribe to the Kafka topic
        kafkaConsumer.subscribe(Collections.singletonList("flume-topic"));

        // Poll for incoming records
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));

            // Process received records
            records.forEach(record -> {
                System.out.printf("Received record (key=%s, value=%s) from partition %d at offset %d%n",
                        record.key(), record.value(), record.partition(), record.offset());

                // Add your processing logic here
            });
        }
    }
}

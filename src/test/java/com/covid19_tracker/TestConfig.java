package com.covid19_tracker;

/**
 * Test configuration for COVID-19 Data Tracker
 * Contains test settings for Kafka, Spark, and Hive components
 */
public class TestConfig {
    
    // Test Kafka Configuration
    public static final String TEST_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String TEST_KAFKA_TOPIC = "covid19-test-topic";
    public static final String TEST_KAFKA_GROUP_ID = "test-consumer-group";
    
    // Test Spark Configuration
    public static final String TEST_SPARK_MASTER = "local[2]";
    public static final String TEST_SPARK_APP_NAME = "COVID-19 Test Streaming";
    public static final int TEST_BATCH_INTERVAL_SECONDS = 2;
    
    // Test HDFS Configuration
    public static final String TEST_HDFS_OUTPUT_PATH = "hdfs://localhost:9000/covid19/test/";
    public static final String TEST_HDFS_CHECKPOINT_PATH = "hdfs://localhost:9000/covid19/test-checkpoints/";
    
    // Test Hive Configuration
    public static final String TEST_HIVE_JDBC_URL = "jdbc:hive2://localhost:10000/default";
    public static final String TEST_HIVE_USER = "";
    public static final String TEST_HIVE_PASSWORD = "";
    
    // Test Data Configuration
    public static final int TEST_SAMPLE_RECORDS = 10;
    public static final String TEST_COUNTRY = "TestCountry";
    public static final int TEST_CONFIRMED_CASES = 1000;
    public static final int TEST_DEATHS = 50;
    
    // Test API Configuration
    public static final String TEST_JHU_API_URL = "https://disease.sh/v3/covid-19/historical/all?lastdays=1";
    public static final int TEST_API_TIMEOUT_SECONDS = 30;
    
    // Test Alert Configuration
    public static final double TEST_MORTALITY_THRESHOLD = 5.0;
    public static final int TEST_MIN_CASES_THRESHOLD = 100;
} 
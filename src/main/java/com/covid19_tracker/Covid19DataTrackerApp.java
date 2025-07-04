package com.covid19_tracker;

import com.covid19_tracker.hive.Covid19HiveService;
import com.covid19_tracker.ingestion.Covid19DataIngestionService;
import com.covid19_tracker.kafka.Covid19DataProducer;
import com.covid19_tracker.spark.Covid19StreamingJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main application class for COVID-19 Data Tracker
 * Orchestrates Kafka, Spark Streaming, and Hive components
 */
public class Covid19DataTrackerApp {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19DataTrackerApp.class);
    
    private final Covid19DataProducer kafkaProducer;
    private final Covid19DataIngestionService ingestionService;
    private final Covid19StreamingJob streamingJob;
    private final Covid19HiveService hiveService;
    private final ExecutorService executorService;
    
    public Covid19DataTrackerApp() {
        this.kafkaProducer = new Covid19DataProducer();
        this.ingestionService = new Covid19DataIngestionService(kafkaProducer);
        this.streamingJob = new Covid19StreamingJob();
        this.hiveService = new Covid19HiveService();
        this.executorService = Executors.newFixedThreadPool(3);
    }
    
    /**
     * Start the complete COVID-19 data tracking system
     */
    public void start() {
        logger.info("Starting COVID-19 Data Tracker Application");
        
        try {
            // Start data ingestion service
            executorService.submit(() -> {
                try {
                    logger.info("Starting COVID-19 data ingestion service");
                    ingestionService.start();
                } catch (Exception e) {
                    logger.error("Error in data ingestion service: {}", e.getMessage());
                }
            });
            
            // Wait a bit for Kafka to receive initial data
            Thread.sleep(5000);
            
            // Start Spark streaming job (Kafka consumer + processing)
            executorService.submit(() -> {
                try {
                    logger.info("Starting Spark streaming job");
                    streamingJob.start();
                } catch (Exception e) {
                    logger.error("Error in Spark streaming job: {}", e.getMessage());
                }
            });
            
            // Start periodic Hive analytics
            executorService.submit(() -> {
                try {
                    logger.info("Starting periodic Hive analytics");
                    runPeriodicAnalytics();
                } catch (Exception e) {
                    logger.error("Error in Hive analytics: {}", e.getMessage());
                }
            });
            
            logger.info("All components started successfully");
            
        } catch (Exception e) {
            logger.error("Error starting application: {}", e.getMessage());
        }
    }
    
    /**
     * Run periodic Hive analytics
     */
    private void runPeriodicAnalytics() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Wait for 5 minutes between analytics runs
                Thread.sleep(5 * 60 * 1000);
                
                logger.info("Running periodic Hive analytics");
                
                // Create tables if they don't exist
                hiveService.createTables();
                
                // Load data from HDFS
                hiveService.loadStreamingData();
                hiveService.loadBatchData();
                
                // Perform analytics
                hiveService.performBatchAnalytics();
                
                // Display results
                displayAnalyticsResults();
                
            } catch (InterruptedException e) {
                logger.info("Hive analytics interrupted");
                break;
            } catch (Exception e) {
                logger.error("Error in periodic analytics: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Display analytics results
     */
    private void displayAnalyticsResults() {
        try {
            logger.info("=== COVID-19 Analytics Results ===");
            
            // Top countries by cases
            logger.info("Top 10 Countries by Confirmed Cases:");
            hiveService.getTopCountriesByCases(10).forEach(logger::info);
            
            // High mortality countries
            logger.info("Countries with High Mortality Rate (>5%):");
            hiveService.getHighMortalityCountries(5.0).forEach(logger::warn);
            
            // Daily trends
            logger.info("Daily Trend Analysis (Last 10 days):");
            hiveService.getDailyTrendAnalysis().forEach(logger::info);
            
            logger.info("=== End Analytics Results ===");
            
        } catch (Exception e) {
            logger.error("Error displaying analytics results: {}", e.getMessage());
        }
    }
    
    /**
     * Stop the application gracefully
     */
    public void stop() {
        logger.info("Stopping COVID-19 Data Tracker Application");
        
        try {
            // Stop ingestion service
            ingestionService.stop();
            
            // Stop streaming job
            streamingJob.stop();
            
            // Close Hive service
            hiveService.close();
            
            // Close Kafka producer
            kafkaProducer.close();
            
            // Shutdown executor service
            executorService.shutdown();
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            
            logger.info("Application stopped successfully");
            
        } catch (Exception e) {
            logger.error("Error stopping application: {}", e.getMessage());
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        Covid19DataTrackerApp app = new Covid19DataTrackerApp();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered");
            app.stop();
        }));
        
        try {
            // Start the application
            app.start();
            
            // Keep the main thread alive
            Thread.currentThread().join();
            
        } catch (InterruptedException e) {
            logger.info("Main thread interrupted");
        } catch (Exception e) {
            logger.error("Error in main application: {}", e.getMessage());
        } finally {
            app.stop();
        }
    }
} 
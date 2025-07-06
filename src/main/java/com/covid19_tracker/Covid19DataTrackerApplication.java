package com.covid19_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application for COVID-19 Data Tracker
 * Batch-focused data processing pipeline for COVID-19 and cancer patient data
 */
@SpringBootApplication
@EnableScheduling
public class Covid19DataTrackerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(Covid19DataTrackerApplication.class, args);
    }
} 
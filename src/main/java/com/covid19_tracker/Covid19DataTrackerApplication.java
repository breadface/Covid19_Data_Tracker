package com.covid19_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application for COVID-19 Data Tracker
 * 
 * This application provides a comprehensive system for tracking COVID-19 
 * morbidity and mortality in cancer patients using big data technologies.
 * 
 * Features:
 * - Real-time data ingestion from multiple COVID-19 data sources
 * - Big data processing with Apache Spark
 * - Data storage and analytics with Apache Hive
 * - Web dashboard for data visualization
 * - REST API for data access
 * 
 * @author COVID-19 Data Tracker Team
 * @version 2.0.0
 */
@SpringBootApplication
@EnableScheduling
public class Covid19DataTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Covid19DataTrackerApplication.class, args);
    }
} 
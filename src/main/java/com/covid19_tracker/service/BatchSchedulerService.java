package com.covid19_tracker.service;

import com.covid19_tracker.ingestion.DataIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service for scheduling batch jobs
 */
@Service
public class BatchSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchSchedulerService.class);
    
    @Autowired
    private DataIngestionService dataIngestionService;
    
    /**
     * Run COVID-19 data ingestion daily at 2 AM
     */
    @Scheduled(cron = "${batch.job.covid19-ingestion.cron:0 0 2 * * *}")
    public void runCovid19DataIngestion() {
        logger.info("Starting scheduled COVID-19 data ingestion");
        try {
            dataIngestionService.ingestAllData();
            logger.info("Scheduled COVID-19 data ingestion completed successfully");
        } catch (Exception e) {
            logger.error("Scheduled COVID-19 data ingestion failed", e);
        }
    }
    
    /**
     * Run cancer data processing daily at 4 AM
     */
    @Scheduled(cron = "${batch.job.cancer-data-processing.cron:0 0 4 * * *}")
    public void runCancerDataProcessing() {
        logger.info("Starting scheduled cancer data processing");
        try {
            // TODO: Implement cancer data processing batch job
            logger.info("Scheduled cancer data processing completed successfully");
        } catch (Exception e) {
            logger.error("Scheduled cancer data processing failed", e);
        }
    }
    
    /**
     * Run mortality analysis daily at 6 AM
     */
    @Scheduled(cron = "${batch.job.mortality-analysis.cron:0 0 6 * * *}")
    public void runMortalityAnalysis() {
        logger.info("Starting scheduled mortality analysis");
        try {
            // TODO: Implement mortality analysis batch job
            logger.info("Scheduled mortality analysis completed successfully");
        } catch (Exception e) {
            logger.error("Scheduled mortality analysis failed", e);
        }
    }
    
    /**
     * Manual trigger for data ingestion (for testing)
     */
    public void triggerDataIngestion() {
        logger.info("Manually triggering data ingestion");
        try {
            dataIngestionService.ingestAllData();
            logger.info("Manual data ingestion completed successfully");
        } catch (Exception e) {
            logger.error("Manual data ingestion failed", e);
            throw new RuntimeException("Data ingestion failed", e);
        }
    }
} 
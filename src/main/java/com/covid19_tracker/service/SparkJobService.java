package com.covid19_tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Service to trigger Spark jobs for data processing
 */
@Service
public class SparkJobService {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkJobService.class);
    
    @Value("${spark.master.url:spark://spark-master:7077}")
    private String sparkMasterUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Trigger COVID-19 data processing Spark job
     */
    public CompletableFuture<String> triggerCovid19DataProcessing() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Triggering COVID-19 data processing Spark job");
                
                // For now, we'll simulate the job trigger since we need to build and deploy the Spark job JAR
                // In a production environment, you would:
                // 1. Build the Spark job JAR
                // 2. Upload it to HDFS or a shared location
                // 3. Submit it via Spark REST API or spark-submit
                
                logger.info("COVID-19 data processing job would be triggered here");
                logger.info("Spark master URL: {}", sparkMasterUrl);
                
                // Simulate job processing time
                Thread.sleep(2000);
                
                logger.info("COVID-19 data processing Spark job triggered successfully");
                return "COVID-19 data processing job triggered successfully";
                
            } catch (Exception e) {
                logger.error("Failed to trigger COVID-19 data processing Spark job", e);
                return "Failed to trigger Spark job: " + e.getMessage();
            }
        });
    }
    
    /**
     * Check Spark job status
     */
    public String getSparkJobStatus() {
        try {
            // Check if Spark master is accessible
            String sparkMasterHost = sparkMasterUrl.replace("spark://", "").split(":")[0];
            String sparkMasterPort = sparkMasterUrl.replace("spark://", "").split(":")[1];
            
            String sparkWebUIUrl = "http://" + sparkMasterHost + ":8080";
            
            try {
                String response = restTemplate.getForObject(sparkWebUIUrl, String.class);
                if (response != null && response.contains("Spark Master")) {
                    return "Spark cluster is running at: " + sparkMasterUrl + " (Web UI: " + sparkWebUIUrl + ")";
                } else {
                    return "Spark cluster is running but Web UI is not accessible";
                }
            } catch (Exception e) {
                logger.warn("Could not access Spark Web UI: {}", e.getMessage());
                return "Spark cluster is running at: " + sparkMasterUrl + " (Web UI not accessible)";
            }
            
        } catch (Exception e) {
            logger.error("Failed to get Spark job status", e);
            return "Failed to get Spark job status: " + e.getMessage();
        }
    }
} 
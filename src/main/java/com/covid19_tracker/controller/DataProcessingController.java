package com.covid19_tracker.controller;

import com.covid19_tracker.service.SparkJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for data processing operations
 */
@RestController
@RequestMapping("/api/processing")
public class DataProcessingController {
    
    @Autowired
    private SparkJobService sparkJobService;
    
    /**
     * Trigger COVID-19 data processing
     */
    @PostMapping("/covid19")
    public ResponseEntity<String> triggerCovid19Processing() {
        try {
            CompletableFuture<String> result = sparkJobService.triggerCovid19DataProcessing();
            return ResponseEntity.ok("COVID-19 data processing job triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to trigger COVID-19 data processing: " + e.getMessage());
        }
    }
    
    /**
     * Get Spark job status
     */
    @GetMapping("/status")
    public ResponseEntity<String> getSparkJobStatus() {
        try {
            String status = sparkJobService.getSparkJobStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to get Spark job status: " + e.getMessage());
        }
    }
} 
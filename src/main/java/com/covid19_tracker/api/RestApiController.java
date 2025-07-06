package com.covid19_tracker.api;

import com.covid19_tracker.model.Covid19Data;
import com.covid19_tracker.model.CancerPatientData;
import com.covid19_tracker.model.MortalityAnalysis;
import com.covid19_tracker.repository.Covid19DataRepository;
import com.covid19_tracker.repository.CancerPatientDataRepository;
import com.covid19_tracker.repository.MortalityAnalysisRepository;
import com.covid19_tracker.ingestion.DataIngestionService;
import com.covid19_tracker.hive.HiveDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for COVID-19 data endpoints
 * Provides data from the database to the React frontend
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
    
    @Autowired
    private Covid19DataRepository covid19DataRepository;
    
    @Autowired
    private CancerPatientDataRepository cancerPatientDataRepository;
    
    @Autowired
    private MortalityAnalysisRepository mortalityAnalysisRepository;
    
    @Autowired
    private DataIngestionService dataIngestionService;
    
    @Autowired
    private HiveDataService hiveDataService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Get latest COVID-19 data
     * GET /api/covid19/latest
     */
    @GetMapping("/covid19/latest")
    public ResponseEntity<Map<String, Object>> getLatestCovid19Data() {
        try {
            List<Covid19Data> data = hiveDataService.getLatestCovid19Data();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting latest COVID-19 data", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve COVID-19 data", e.getMessage()));
        }
    }
    
    /**
     * Get COVID-19 data by country
     * GET /api/covid19/country/{country}
     */
    @GetMapping("/covid19/country/{country}")
    public ResponseEntity<Map<String, Object>> getCovid19DataByCountry(@PathVariable String country) {
        try {
            List<Covid19Data> data = hiveDataService.getCovid19DataByCountry(country);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("country", country);
            response.put("count", data.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting COVID-19 data for country: {}", country, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve data for country: " + country, e.getMessage()));
        }
    }
    
    /**
     * Get COVID-19 data by date range
     * GET /api/covid19/range?start={startDate}&end={endDate}
     */
    @GetMapping("/covid19/range")
    public ResponseEntity<Map<String, Object>> getCovid19DataByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Covid19Data> data = hiveDataService.getCovid19DataByDateRange(startDate.toString(), endDate.toString());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("count", data.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting COVID-19 data for date range: {} to {}", startDate, endDate, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve data for date range", e.getMessage()));
        }
    }
    
    /**
     * Get summary statistics
     * GET /api/covid19/summary
     */
    @GetMapping("/covid19/summary")
    public ResponseEntity<Map<String, Object>> getSummaryStatistics() {
        try {
            Map<String, Object> summary = hiveDataService.getSummaryStatistics();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", summary);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting summary statistics", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve summary statistics", e.getMessage()));
        }
    }
    
    /**
     * Get cancer patient data
     * GET /api/cancer-patients
     */
    @GetMapping("/cancer-patients")
    public ResponseEntity<Map<String, Object>> getCancerPatientData() {
        try {
            List<CancerPatientData> data = cancerPatientDataRepository.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting cancer patient data", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve cancer patient data", e.getMessage()));
        }
    }
    
    /**
     * Get COVID-19 impact by cancer type
     * GET /api/cancer-patients/covid-impact
     */
    @GetMapping("/cancer-patients/covid-impact")
    public ResponseEntity<Map<String, Object>> getCovidImpactByCancerType() {
        try {
            List<Object[]> impact = cancerPatientDataRepository.getCovidImpactByCancerType();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", impact);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting COVID impact by cancer type", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve COVID impact data", e.getMessage()));
        }
    }
    
    /**
     * Get mortality analysis
     * GET /api/mortality-analysis
     */
    @GetMapping("/mortality-analysis")
    public ResponseEntity<Map<String, Object>> getMortalityAnalysis() {
        try {
            List<MortalityAnalysis> data = mortalityAnalysisRepository.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting mortality analysis", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to retrieve mortality analysis", e.getMessage()));
        }
    }
    
    /**
     * Trigger data ingestion
     * POST /api/ingest
     */
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> triggerDataIngestion() {
        try {
            dataIngestionService.ingestAllData();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Data ingestion completed successfully");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error during data ingestion", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Data ingestion failed", e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "healthy");
            health.put("service", "COVID-19 Data API");
            health.put("version", "3.0.0");
            health.put("timestamp", System.currentTimeMillis());
            health.put("database", "connected");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", health);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in health check", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Health check failed", e.getMessage()));
        }
    }
    
    /**
     * Create error response
     */
    private Map<String, Object> createErrorResponse(String message, String details) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("details", details);
        error.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        
        return response;
    }
    
    /**
     * Get top countries by total cases
     */
    public String getTopCountriesByCases(int limit) {
        try {
            List<Map<String, Object>> topCountries = hiveDataService.getTopCountriesByCases(limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", topCountries);
            response.put("count", topCountries.size());
            response.put("timestamp", System.currentTimeMillis());
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return errorJson("Failed to get top countries", e.getMessage());
        }
    }

    // Stringified versions for use by Covid19HttpServer
    public String getLatestCovid19DataJson() {
        try {
            return objectMapper.writeValueAsString(getLatestCovid19Data().getBody());
        } catch (Exception e) {
            return errorJson("Failed to get latest data", e.getMessage());
        }
    }
    public String getSummaryStatisticsJson() {
        try {
            return objectMapper.writeValueAsString(getSummaryStatistics().getBody());
        } catch (Exception e) {
            return errorJson("Failed to get summary statistics", e.getMessage());
        }
    }
    public String getCovid19DataByCountryJson(String country) {
        try {
            return objectMapper.writeValueAsString(getCovid19DataByCountry(country).getBody());
        } catch (Exception e) {
            return errorJson("Failed to get data by country", e.getMessage());
        }
    }
    public String getCovid19DataByDateRangeJson(String start, String end) {
        try {
            java.time.LocalDate startDate = java.time.LocalDate.parse(start);
            java.time.LocalDate endDate = java.time.LocalDate.parse(end);
            return objectMapper.writeValueAsString(getCovid19DataByDateRange(startDate, endDate).getBody());
        } catch (Exception e) {
            return errorJson("Failed to get data by date range", e.getMessage());
        }
    }
    public String getHealthJson() {
        try {
            return objectMapper.writeValueAsString(getHealth().getBody());
        } catch (Exception e) {
            return errorJson("Failed to get health", e.getMessage());
        }
    }
    private String errorJson(String message, String details) {
        try {
            return objectMapper.writeValueAsString(createErrorResponse(message, details));
        } catch (Exception ex) {
            return "{\"success\":false,\"error\":{\"message\":\"" + message + "\",\"details\":\"" + details + "\"}}";
        }
    }
} 
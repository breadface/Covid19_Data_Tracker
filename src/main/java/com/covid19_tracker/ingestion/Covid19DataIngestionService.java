package com.covid19_tracker.ingestion;

import com.covid19_tracker.kafka.Covid19DataProducer;
import com.covid19_tracker.model.Covid19Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Service for ingesting COVID-19 data from external APIs into the Kafka pipeline
 */
public class Covid19DataIngestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19DataIngestionService.class);
    
    // COVID-19 data sources
    private static final String JHU_CSSE_API = "https://disease.sh/v3/covid-19/historical/all?lastdays=1";
    private static final String WHO_API = "https://covid19.who.int/WHO-COVID-19-global-data.csv";
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Covid19DataProducer kafkaProducer;
    
    public Covid19DataIngestionService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.kafkaProducer = new Covid19DataProducer();
    }
    
    /**
     * Ingest COVID-19 data from JHU CSSE API
     */
    public void ingestFromJHU() {
        try {
            logger.info("Ingesting COVID-19 data from JHU CSSE API");
            
            Request request = new Request.Builder()
                    .url(JHU_CSSE_API)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    processJHUData(jsonData);
                } else {
                    logger.error("Failed to fetch data from JHU API: {}", response.code());
                }
            }
            
        } catch (IOException e) {
            logger.error("Error ingesting data from JHU API: {}", e.getMessage());
        }
    }
    
    /**
     * Process JHU CSSE API response
     */
    private void processJHUData(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            
            // Extract global data
            JsonNode casesNode = rootNode.get("cases");
            JsonNode deathsNode = rootNode.get("deaths");
            JsonNode recoveredNode = rootNode.get("recovered");
            
            if (casesNode != null && casesNode.isObject()) {
                // Get the latest date (last key in the object)
                String latestDate = casesNode.fieldNames().next();
                
                Covid19Data covid19Data = new Covid19Data();
                covid19Data.setDate(LocalDate.parse(latestDate, DateTimeFormatter.ofPattern("M/d/yy")));
                covid19Data.setCountry("Global");
                covid19Data.setConfirmedCases(casesNode.get(latestDate).asInt());
                covid19Data.setDeaths(deathsNode != null ? deathsNode.get(latestDate).asInt() : 0);
                covid19Data.setRecovered(recoveredNode != null ? recoveredNode.get(latestDate).asInt() : 0);
                covid19Data.setDataSource("JHU-CSSE");
                covid19Data.setLastUpdated(LocalDate.now());
                
                // Send to Kafka
                kafkaProducer.sendCovid19Data(covid19Data);
                logger.info("Ingested global COVID-19 data: Cases={}, Deaths={}", 
                           covid19Data.getConfirmedCases(), covid19Data.getDeaths());
            }
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JHU data: {}", e.getMessage());
        }
    }
    
    /**
     * Ingest sample data for testing
     */
    public void ingestSampleData() {
        logger.info("Ingesting sample COVID-19 data");
        
        // Sample data for different countries
        String[] countries = {"United States", "India", "Brazil", "United Kingdom", "France"};
        int[] cases = {1000000, 800000, 600000, 400000, 300000};
        int[] deaths = {50000, 40000, 30000, 20000, 15000};
        
        for (int i = 0; i < countries.length; i++) {
            Covid19Data covid19Data = new Covid19Data(
                LocalDate.now(),
                countries[i],
                cases[i],
                deaths[i]
            );
            covid19Data.setDataSource("sample-data");
            covid19Data.setLastUpdated(LocalDate.now());
            
            kafkaProducer.sendCovid19Data(covid19Data);
            
            try {
                Thread.sleep(1000); // Small delay between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        logger.info("Sample data ingestion completed");
    }
    
    /**
     * Start continuous ingestion with specified interval
     */
    public void startContinuousIngestion(long intervalMinutes) {
        logger.info("Starting continuous COVID-19 data ingestion every {} minutes", intervalMinutes);
        
        Thread ingestionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ingestFromJHU();
                    Thread.sleep(intervalMinutes * 60 * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("Error in continuous ingestion: {}", e.getMessage());
                }
            }
        });
        
        ingestionThread.setDaemon(true);
        ingestionThread.start();
    }
    
    /**
     * Close the ingestion service
     */
    public void close() {
        kafkaProducer.close();
        logger.info("COVID-19 data ingestion service closed");
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        Covid19DataIngestionService service = new Covid19DataIngestionService();
        
        try {
            // Test with sample data
            service.ingestSampleData();
            
            // Test with real API data
            service.ingestFromJHU();
            
        } finally {
            service.close();
        }
    }
} 
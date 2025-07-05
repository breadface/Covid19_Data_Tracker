package com.covid19_tracker.simple;

import com.covid19_tracker.kafka.Covid19DataProducer;
import com.covid19_tracker.model.Covid19Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simple COVID-19 data pipeline that fetches real data and sends to Kafka
 */
public class SimpleDataPipelineApp {
    
    private static final Logger logger = Logger.getLogger(SimpleDataPipelineApp.class.getName());
    
    private final Covid19DataProducer producer;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler;
    
    // Data sources
    private static final String OUR_WORLD_IN_DATA_URL = "https://covid-19.nyc3.digitaloceanspaces.com/public/owid-covid-data.json";
    
    public SimpleDataPipelineApp() {
        this.producer = new Covid19DataProducer();
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS) // Increased timeout for large file
            .followRedirects(true)
            .followSslRedirects(true)
            .build();
        this.objectMapper = new ObjectMapper();
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        logger.info("Initialized Simple COVID-19 Data Pipeline");
    }
    
    public void start() {
        logger.info("Starting Simple COVID-19 data pipeline...");
        
        // Initial data load
        fetchAndSendData();
        
        // Schedule periodic data fetch (every 5 minutes for testing)
        scheduler.scheduleAtFixedRate(this::fetchAndSendData, 5, 5, TimeUnit.MINUTES);
        
        logger.info("Simple COVID-19 data pipeline started");
    }
    
    public void stop() {
        logger.info("Stopping Simple COVID-19 data pipeline...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Simple COVID-19 data pipeline stopped");
    }
    
    private void fetchAndSendData() {
        try {
            logger.info("Fetching COVID-19 data from Our World in Data...");
            logger.info("Making HTTP request to: " + OUR_WORLD_IN_DATA_URL);
            
            Request request = new Request.Builder()
                .url(OUR_WORLD_IN_DATA_URL)
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                logger.info("HTTP Response Code: " + response.code());
                logger.info("HTTP Response Headers: " + response.headers());
                
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    logger.info("Received JSON data length: " + jsonData.length() + " characters");
                    logger.info("First 500 characters of JSON: " + jsonData.substring(0, Math.min(500, jsonData.length())));
                    
                    List<Covid19Data> covidDataList = parseOurWorldInDataJSON(jsonData);
                    logger.info("Parsed " + covidDataList.size() + " COVID-19 records from JSON");
                    
                    // Log first few records for debugging
                    for (int i = 0; i < Math.min(5, covidDataList.size()); i++) {
                        Covid19Data data = covidDataList.get(i);
                        logger.info("Sample record " + (i+1) + ": " + data.toString());
                    }
                    
                    for (Covid19Data data : covidDataList) {
                        data.setDataSource("Our-World-in-Data");
                        producer.sendCovid19Data(data);
                    }
                    
                    logger.info("Sent " + covidDataList.size() + " COVID-19 records to Kafka");
                } else {
                    logger.warning("Failed to fetch data: HTTP " + response.code());
                    logger.warning("Response body: " + (response.body() != null ? response.body().string() : "null"));
                }
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching data: " + e.getMessage(), e);
        }
    }
    
    private List<Covid19Data> parseOurWorldInDataJSON(String jsonData) {
        List<Covid19Data> dataList = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            
            // Get the latest data for each country
            rootNode.fieldNames().forEachRemaining(countryCode -> {
                JsonNode countryNode = rootNode.get(countryCode);
                if (countryNode.has("data") && countryNode.get("data").isArray()) {
                    JsonNode dataArray = countryNode.get("data");
                    if (dataArray.size() > 0) {
                        // Get the latest data point
                        JsonNode latestData = dataArray.get(dataArray.size() - 1);
                        
                        try {
                            String country = countryNode.get("location").asText();
                            int confirmed = latestData.has("total_cases") ? 
                                (int) latestData.get("total_cases").asDouble() : 0;
                            int deaths = latestData.has("total_deaths") ? 
                                (int) latestData.get("total_deaths").asDouble() : 0;
                            
                            Covid19Data data = new Covid19Data(
                                LocalDate.now(),
                                country,
                                confirmed,
                                deaths
                            );
                            dataList.add(data);
                            
                        } catch (Exception e) {
                            logger.warning("Error parsing data for country " + countryCode + ": " + e.getMessage());
                        }
                    }
                }
            });
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing JSON data: " + e.getMessage(), e);
        }
        
        return dataList;
    }
    
    public static void main(String[] args) {
        logger.info("Starting Simple COVID-19 Data Pipeline...");
        
        try {
            SimpleDataPipelineApp pipeline = new SimpleDataPipelineApp();
            pipeline.start();
            
            logger.info("Simple data pipeline started successfully!");
            logger.info("The service will now fetch real COVID-19 data from Our World in Data");
            logger.info("Data will be sent to Kafka topics every 5 minutes");
            logger.info("Your WebSocket bridge will receive real data instead of test data");
            
            // Keep the application running
            while (true) {
                Thread.sleep(60000); // Sleep for 1 minute
                logger.info("Simple data pipeline is running...");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start simple data pipeline", e);
            System.exit(1);
        }
    }
} 
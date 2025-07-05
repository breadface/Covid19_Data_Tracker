package com.covid19_tracker.ingestion;

import com.covid19_tracker.kafka.Covid19DataProducer;
import com.covid19_tracker.model.Covid19Data;
import com.covid19_tracker.model.CancerPatientData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for ingesting COVID-19 data from multiple sources
 */
public class Covid19DataIngestionService implements DataIngestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19DataIngestionService.class);
    
    private final Covid19DataProducer producer;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler;
    
    // Data sources
    private static final String JHU_CSSE_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private static final String WHO_API_URL = "https://covid19.who.int/WHO-COVID-19-global-data.csv";
    private static final String OUR_WORLD_IN_DATA_URL = "https://covid.ourworldindata.org/data/owid-covid-data.json";
    
    public Covid19DataIngestionService(Covid19DataProducer producer) {
        this.producer = producer;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        this.objectMapper = new ObjectMapper();
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        logger.info("Initialized COVID-19 Data Ingestion Service");
    }
    
    /**
     * Start the data ingestion service
     */
    public void start() {
        logger.info("Starting COVID-19 data ingestion service...");
        
        // Initial data load
        ingestAllSources();
        
        // Schedule periodic ingestion (every 6 hours)
        scheduler.scheduleAtFixedRate(this::ingestAllSources, 6, 6, TimeUnit.HOURS);
        
        logger.info("COVID-19 data ingestion service started");
    }
    
    /**
     * Stop the data ingestion service
     */
    public void stop() {
        logger.info("Stopping COVID-19 data ingestion service...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("COVID-19 data ingestion service stopped");
    }
    
    /**
     * Ingest data from all sources
     */
    private void ingestAllSources() {
        try {
            logger.info("Starting data ingestion from all sources...");
            
            // Ingest from JHU CSSE
            ingestFromJHU();
            
            // Ingest from Our World in Data
            ingestFromOurWorldInData();
            
            logger.info("Data ingestion completed");
            
        } catch (Exception e) {
            logger.error("Error during data ingestion: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Ingest data from JHU CSSE GitHub repository
     */
    private void ingestFromJHU() {
        try {
            logger.info("Ingesting data from JHU CSSE...");
            
            // Get today's date in the format used by JHU
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            String url = JHU_CSSE_URL + today + ".csv";
            
            Request request = new Request.Builder()
                .url(url)
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String csvData = response.body().string();
                    List<Covid19Data> covidDataList = parseJHUCSV(csvData);
                    
                    for (Covid19Data data : covidDataList) {
                        data.setDataSource("JHU-CSSE");
                        producer.sendCovid19Data(data);
                    }
                    
                    logger.info("Ingested {} records from JHU CSSE", covidDataList.size());
                } else {
                    logger.warn("Failed to fetch JHU data: HTTP {}", response.code());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error ingesting from JHU CSSE: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Ingest data from Our World in Data
     */
    private void ingestFromOurWorldInData() {
        try {
            logger.info("Ingesting data from Our World in Data...");
            
            Request request = new Request.Builder()
                .url(OUR_WORLD_IN_DATA_URL)
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    List<Covid19Data> covidDataList = parseOurWorldInDataJSON(jsonData);
                    
                    for (Covid19Data data : covidDataList) {
                        data.setDataSource("Our-World-in-Data");
                        producer.sendCovid19Data(data);
                    }
                    
                    logger.info("Ingested {} records from Our World in Data", covidDataList.size());
                } else {
                    logger.warn("Failed to fetch Our World in Data: HTTP {}", response.code());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error ingesting from Our World in Data: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Parse JHU CSSE CSV data
     */
    private List<Covid19Data> parseJHUCSV(String csvData) {
        List<Covid19Data> dataList = new ArrayList<>();
        
        try {
            String[] lines = csvData.split("\n");
            
            // Skip header
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.trim().isEmpty()) continue;
                
                String[] fields = line.split(",");
                if (fields.length >= 8) {
                    try {
                        String country = fields[3].replace("\"", "");
                        String state = fields[2].replace("\"", "");
                        int confirmed = Integer.parseInt(fields[7].replace("\"", ""));
                        int deaths = Integer.parseInt(fields[8].replace("\"", ""));
                        
                        Covid19Data data = new Covid19Data(
                            LocalDate.now(),
                            country,
                            confirmed,
                            deaths
                        );
                        data.setStateProvince(state);
                        dataList.add(data);
                        
                    } catch (NumberFormatException e) {
                        logger.debug("Skipping invalid data line: {}", line);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing JHU CSV: {}", e.getMessage(), e);
        }
        
        return dataList;
    }
    
    /**
     * Parse Our World in Data JSON
     */
    private List<Covid19Data> parseOurWorldInDataJSON(String jsonData) {
        List<Covid19Data> dataList = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            
            for (JsonNode countryNode : root) {
                String country = countryNode.get("location").asText();
                JsonNode data = countryNode.get("data");
                
                if (data != null && data.isArray()) {
                    for (JsonNode dayData : data) {
                        try {
                            String dateStr = dayData.get("date").asText();
                            LocalDate date = LocalDate.parse(dateStr);
                            
                            int confirmed = dayData.has("total_cases") ? dayData.get("total_cases").asInt() : 0;
                            int deaths = dayData.has("total_deaths") ? dayData.get("total_deaths").asInt() : 0;
                            
                            Covid19Data covidData = new Covid19Data(date, country, confirmed, deaths);
                            dataList.add(covidData);
                            
                        } catch (Exception e) {
                            logger.debug("Skipping invalid day data for {}: {}", country, e.getMessage());
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing Our World in Data JSON: {}", e.getMessage(), e);
        }
        
        return dataList;
    }
    
    /**
     * Manual ingestion trigger for testing
     */
    public void triggerIngestion() {
        logger.info("Manual ingestion triggered");
        scheduler.submit(this::ingestAllSources);
    }
    
    // DataIngestionService interface implementations
    
    @Override
    public List<Covid19Data> getLatestCovid19Data() {
        // This method is no longer needed as data comes from Kafka/WebSocket
        // In a real implementation, this would query the database
        logger.info("getLatestCovid19Data called - data now comes from real-time pipeline");
        return new ArrayList<>();
    }
    
    @Override
    public List<CancerPatientData> getCancerPatientData() {
        // This method is no longer needed as data comes from Kafka/WebSocket
        // In a real implementation, this would query the database
        logger.info("getCancerPatientData called - data now comes from real-time pipeline");
        return new ArrayList<>();
    }
    
    @Override
    public List<Covid19Data> getCovid19DataByCountry(String country) {
        // This method is no longer needed as data comes from Kafka/WebSocket
        logger.info("getCovid19DataByCountry called for {} - data now comes from real-time pipeline", country);
        return new ArrayList<>();
    }
    
    @Override
    public List<Covid19Data> getCovid19DataByDateRange(String startDate, String endDate) {
        // This method is no longer needed as data comes from Kafka/WebSocket
        logger.info("getCovid19DataByDateRange called - data now comes from real-time pipeline");
        return new ArrayList<>();
    }
} 
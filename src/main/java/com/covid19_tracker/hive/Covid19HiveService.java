package com.covid19_tracker.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hive service for creating tables and performing batch analytics on COVID-19 data
 */
public class Covid19HiveService {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19HiveService.class);
    
    // Hive configuration
    private static final String HIVE_JDBC_URL = "jdbc:hive2://localhost:10000/default";
    private static final String HIVE_USER = "";
    private static final String HIVE_PASSWORD = "";
    
    // HDFS paths
    private static final String HDFS_STREAMING_DATA_PATH = "/covid19/streaming/*";
    private static final String HDFS_BATCH_DATA_PATH = "/covid19/batch/*";
    
    private Connection connection;
    private Driver hiveDriver;
    
    public Covid19HiveService() {
        initializeHiveConnection();
    }
    
    /**
     * Initialize Hive connection
     */
    private void initializeHiveConnection() {
        try {
            // Load Hive JDBC driver
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            
            // Create connection
            connection = DriverManager.getConnection(HIVE_JDBC_URL, HIVE_USER, HIVE_PASSWORD);
            logger.info("Successfully connected to Hive");
            
            // Initialize Hive driver for DDL operations
            initializeHiveDriver();
            
        } catch (Exception e) {
            logger.error("Error initializing Hive connection: {}", e.getMessage());
        }
    }
    
    /**
     * Initialize Hive driver for DDL operations
     */
    private void initializeHiveDriver() {
        try {
            HiveConf hiveConf = new HiveConf();
            hiveConf.setVar(HiveConf.ConfVars.METASTOREWAREHOUSE, "/user/hive/warehouse");
            hiveConf.setVar(HiveConf.ConfVars.HIVE_SERVER2_ENABLE_DOAS, "false");
            
            hiveDriver = new Driver(hiveConf);
            SessionState.start(hiveConf);
            
            logger.info("Hive driver initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing Hive driver: {}", e.getMessage());
        }
    }
    
    /**
     * Create COVID-19 data tables
     */
    public void createTables() {
        createCovid19StreamingTable();
        createCovid19BatchTable();
        createCovid19AnalyticsTable();
        logger.info("All COVID-19 tables created successfully");
    }
    
    /**
     * Create table for streaming data
     */
    private void createCovid19StreamingTable() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS covid19_streaming (" +
            "  date STRING," +
            "  country STRING," +
            "  province STRING," +
            "  confirmed_cases INT," +
            "  deaths INT," +
            "  recovered INT," +
            "  active_cases INT," +
            "  latitude DOUBLE," +
            "  longitude DOUBLE," +
            "  batch_timestamp BIGINT" +
            ") " +
            "PARTITIONED BY (dt STRING) " +
            "STORED AS TEXTFILE " +
            "LOCATION '/user/hive/warehouse/covid19_streaming'";
        
        executeDDL(createTableSQL);
    }
    
    /**
     * Create table for batch data
     */
    private void createCovid19BatchTable() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS covid19_batch (" +
            "  date STRING," +
            "  country STRING," +
            "  province STRING," +
            "  confirmed_cases INT," +
            "  deaths INT," +
            "  recovered INT," +
            "  active_cases INT," +
            "  latitude DOUBLE," +
            "  longitude DOUBLE" +
            ") " +
            "PARTITIONED BY (dt STRING) " +
            "STORED AS TEXTFILE " +
            "LOCATION '/user/hive/warehouse/covid19_batch'";
        
        executeDDL(createTableSQL);
    }
    
    /**
     * Create analytics table for aggregated results
     */
    private void createCovid19AnalyticsTable() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS covid19_analytics (" +
            "  country STRING," +
            "  total_cases INT," +
            "  total_deaths INT," +
            "  total_recovered INT," +
            "  mortality_rate DOUBLE," +
            "  recovery_rate DOUBLE," +
            "  last_updated STRING," +
            "  data_source STRING" +
            ") " +
            "PARTITIONED BY (dt STRING) " +
            "STORED AS PARQUET " +
            "LOCATION '/user/hive/warehouse/covid19_analytics'";
        
        executeDDL(createTableSQL);
    }
    
    /**
     * Execute DDL statement
     */
    private void executeDDL(String sql) {
        try {
            if (hiveDriver != null) {
                int result = hiveDriver.run(sql).getResponseCode();
                if (result == 0) {
                    logger.info("DDL executed successfully: {}", sql.substring(0, Math.min(50, sql.length())) + "...");
                } else {
                    logger.error("DDL execution failed with code: {}", result);
                }
            }
        } catch (Exception e) {
            logger.error("Error executing DDL: {}", e.getMessage());
        }
    }
    
    /**
     * Load streaming data into Hive table
     */
    public void loadStreamingData() {
        String loadSQL = 
            "LOAD DATA INPATH '" + HDFS_STREAMING_DATA_PATH + "' " +
            "OVERWRITE INTO TABLE covid19_streaming " +
            "PARTITION (dt='" + getCurrentDate() + "')";
        
        executeDDL(loadSQL);
        logger.info("Streaming data loaded into Hive table");
    }
    
    /**
     * Load batch data into Hive table
     */
    public void loadBatchData() {
        String loadSQL = 
            "LOAD DATA INPATH '" + HDFS_BATCH_DATA_PATH + "' " +
            "OVERWRITE INTO TABLE covid19_batch " +
            "PARTITION (dt='" + getCurrentDate() + "')";
        
        executeDDL(loadSQL);
        logger.info("Batch data loaded into Hive table");
    }
    
    /**
     * Perform batch analytics and store results
     */
    public void performBatchAnalytics() {
        String analyticsSQL = 
            "INSERT OVERWRITE TABLE covid19_analytics PARTITION (dt='" + getCurrentDate() + "') " +
            "SELECT " +
            "  country," +
            "  SUM(confirmed_cases) as total_cases," +
            "  SUM(deaths) as total_deaths," +
            "  SUM(recovered) as total_recovered," +
            "  CASE WHEN SUM(confirmed_cases) > 0 THEN (SUM(deaths) * 100.0 / SUM(confirmed_cases)) ELSE 0 END as mortality_rate," +
            "  CASE WHEN SUM(confirmed_cases) > 0 THEN (SUM(recovered) * 100.0 / SUM(confirmed_cases)) ELSE 0 END as recovery_rate," +
            "  MAX(date) as last_updated," +
            "  'batch_analytics' as data_source " +
            "FROM covid19_batch " +
            "WHERE dt='" + getCurrentDate() + "' " +
            "GROUP BY country " +
            "ORDER BY total_cases DESC";
        
        executeDDL(analyticsSQL);
        logger.info("Batch analytics completed and stored");
    }
    
    /**
     * Get top countries by confirmed cases
     */
    public List<String> getTopCountriesByCases(int limit) {
        List<String> results = new ArrayList<>();
        
        String query = 
            "SELECT country, total_cases, total_deaths, mortality_rate " +
            "FROM covid19_analytics " +
            "WHERE dt='" + getCurrentDate() + "' " +
            "ORDER BY total_cases DESC " +
            "LIMIT " + limit;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String result = String.format("Country: %s, Cases: %d, Deaths: %d, Mortality Rate: %.2f%%",
                        rs.getString("country"),
                        rs.getInt("total_cases"),
                        rs.getInt("total_deaths"),
                        rs.getDouble("mortality_rate"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.error("Error executing query: {}", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Get countries with high mortality rates
     */
    public List<String> getHighMortalityCountries(double threshold) {
        List<String> results = new ArrayList<>();
        
        String query = 
            "SELECT country, total_cases, total_deaths, mortality_rate " +
            "FROM covid19_analytics " +
            "WHERE dt='" + getCurrentDate() + "' " +
            "AND mortality_rate > " + threshold + " " +
            "AND total_cases > 100 " +  // Minimum cases threshold
            "ORDER BY mortality_rate DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String result = String.format("High Mortality: %s - %.2f%% (Cases: %d, Deaths: %d)",
                        rs.getString("country"),
                        rs.getDouble("mortality_rate"),
                        rs.getInt("total_cases"),
                        rs.getInt("total_deaths"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.error("Error executing query: {}", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Get daily trend analysis
     */
    public List<String> getDailyTrendAnalysis() {
        List<String> results = new ArrayList<>();
        
        String query = 
            "SELECT " +
            "  date," +
            "  SUM(confirmed_cases) as daily_cases," +
            "  SUM(deaths) as daily_deaths," +
            "  COUNT(DISTINCT country) as countries_affected " +
            "FROM covid19_batch " +
            "WHERE dt='" + getCurrentDate() + "' " +
            "GROUP BY date " +
            "ORDER BY date DESC " +
            "LIMIT 10";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String result = String.format("Date: %s, Cases: %d, Deaths: %d, Countries: %d",
                        rs.getString("date"),
                        rs.getInt("daily_cases"),
                        rs.getInt("daily_deaths"),
                        rs.getInt("countries_affected"));
                results.add(result);
            }
            
        } catch (SQLException e) {
            logger.error("Error executing query: {}", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Get current date in YYYY-MM-DD format
     */
    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }
    
    /**
     * Close Hive connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Hive connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing Hive connection: {}", e.getMessage());
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        Covid19HiveService hiveService = new Covid19HiveService();
        
        try {
            // Create tables
            hiveService.createTables();
            
            // Load data
            hiveService.loadBatchData();
            
            // Perform analytics
            hiveService.performBatchAnalytics();
            
            // Get results
            System.out.println("=== Top Countries by Cases ===");
            hiveService.getTopCountriesByCases(10).forEach(System.out::println);
            
            System.out.println("\n=== High Mortality Countries (>5%) ===");
            hiveService.getHighMortalityCountries(5.0).forEach(System.out::println);
            
            System.out.println("\n=== Daily Trend Analysis ===");
            hiveService.getDailyTrendAnalysis().forEach(System.out::println);
            
        } catch (Exception e) {
            logger.error("Error in Hive service: {}", e.getMessage());
        } finally {
            hiveService.close();
        }
    }
} 
package com.covid19_tracker.hive;

import com.covid19_tracker.model.Covid19Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for accessing COVID-19 data from Hive
 */
@Service
public class HiveDataService {
    private static final Logger logger = LoggerFactory.getLogger(HiveDataService.class);
    
    private static final String HIVE_JDBC_URL = "jdbc:hive2://hive-server:10000/default";
    private static final String HIVE_USER = "hive";
    private static final String HIVE_PASSWORD = "";
    
    /**
     * Get latest COVID-19 data from Hive
     */
    public List<Covid19Data> getLatestCovid19Data() {
        List<Covid19Data> dataList = new ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection(HIVE_JDBC_URL, HIVE_USER, HIVE_PASSWORD)) {
            String query = "SELECT * FROM covid19_data ORDER BY date DESC LIMIT 100";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    Covid19Data data = new Covid19Data();
                    data.setDate(rs.getDate("date").toLocalDate());
                    data.setCountry(rs.getString("country"));
                    data.setStateProvince(rs.getString("state_province"));
                    data.setConfirmedCases(rs.getInt("confirmed_cases"));
                    data.setDeaths(rs.getInt("deaths"));
                    data.setRecovered(rs.getInt("recovered"));
                    data.setActiveCases(rs.getInt("active_cases"));
                    data.setDataSource(rs.getString("data_source"));
                    
                    dataList.add(data);
                }
            }
            
            logger.info("Retrieved {} COVID-19 records from Hive", dataList.size());
            
        } catch (SQLException e) {
            logger.error("Error querying Hive for COVID-19 data", e);
            // Return sample data for demonstration if Hive is not available
            dataList = getSampleCovid19Data();
        }
        
        return dataList;
    }
    
    /**
     * Get COVID-19 data by country
     */
    public List<Covid19Data> getCovid19DataByCountry(String country) {
        List<Covid19Data> dataList = new ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection(HIVE_JDBC_URL, HIVE_USER, HIVE_PASSWORD)) {
            String query = "SELECT * FROM covid19_data WHERE country = ? ORDER BY date DESC LIMIT 50";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, country);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Covid19Data data = new Covid19Data();
                        data.setDate(rs.getDate("date").toLocalDate());
                        data.setCountry(rs.getString("country"));
                        data.setStateProvince(rs.getString("state_province"));
                        data.setConfirmedCases(rs.getInt("confirmed_cases"));
                        data.setDeaths(rs.getInt("deaths"));
                        data.setRecovered(rs.getInt("recovered"));
                        data.setActiveCases(rs.getInt("active_cases"));
                        data.setDataSource(rs.getString("data_source"));
                        
                        dataList.add(data);
                    }
                }
            }
            
            logger.info("Retrieved {} COVID-19 records for country: {}", dataList.size(), country);
            
        } catch (SQLException e) {
            logger.error("Error querying Hive for country data: {}", country, e);
            // Return sample data for demonstration
            dataList = getSampleCovid19Data().stream()
                .filter(data -> data.getCountry().equalsIgnoreCase(country))
                .toList();
        }
        
        return dataList;
    }
    
    /**
     * Get COVID-19 data by date range
     */
    public List<Covid19Data> getCovid19DataByDateRange(String startDate, String endDate) {
        List<Covid19Data> dataList = new ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection(HIVE_JDBC_URL, HIVE_USER, HIVE_PASSWORD)) {
            String query = "SELECT * FROM covid19_data WHERE date BETWEEN ? AND ? ORDER BY date DESC";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Covid19Data data = new Covid19Data();
                        data.setDate(rs.getDate("date").toLocalDate());
                        data.setCountry(rs.getString("country"));
                        data.setStateProvince(rs.getString("state_province"));
                        data.setConfirmedCases(rs.getInt("confirmed_cases"));
                        data.setDeaths(rs.getInt("deaths"));
                        data.setRecovered(rs.getInt("recovered"));
                        data.setActiveCases(rs.getInt("active_cases"));
                        data.setDataSource(rs.getString("data_source"));
                        
                        dataList.add(data);
                    }
                }
            }
            
            logger.info("Retrieved {} COVID-19 records for date range: {} to {}", 
                dataList.size(), startDate, endDate);
            
        } catch (SQLException e) {
            logger.error("Error querying Hive for date range data: {} to {}", startDate, endDate, e);
            // Return sample data for demonstration
            dataList = getSampleCovid19Data();
        }
        
        return dataList;
    }
    
    /**
     * Get summary statistics from Hive
     */
    public Map<String, Object> getSummaryStatistics() {
        Map<String, Object> summary = new HashMap<>();
        
        try (Connection connection = DriverManager.getConnection(HIVE_JDBC_URL, HIVE_USER, HIVE_PASSWORD)) {
            String query = "SELECT " +
                "SUM(confirmed_cases) as total_cases, " +
                "SUM(deaths) as total_deaths, " +
                "SUM(recovered) as total_recovered, " +
                "SUM(active_cases) as total_active, " +
                "COUNT(DISTINCT country) as countries_affected " +
                "FROM covid19_data";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                if (rs.next()) {
                    summary.put("totalCases", rs.getLong("total_cases"));
                    summary.put("totalDeaths", rs.getLong("total_deaths"));
                    summary.put("totalRecovered", rs.getLong("total_recovered"));
                    summary.put("totalActive", rs.getLong("total_active"));
                    summary.put("countriesAffected", rs.getInt("countries_affected"));
                }
            }
            
            logger.info("Retrieved summary statistics from Hive");
            
        } catch (SQLException e) {
            logger.error("Error querying Hive for summary statistics", e);
            // Return sample statistics for demonstration
            summary.put("totalCases", 676609955L);
            summary.put("totalDeaths", 6881797L);
            summary.put("totalRecovered", 652728158L);
            summary.put("totalActive", 17000000L);
            summary.put("countriesAffected", 195);
        }
        
        return summary;
    }
    
    /**
     * Get top countries by cases
     */
    public List<Map<String, Object>> getTopCountriesByCases(int limit) {
        List<Map<String, Object>> countries = new ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection(HIVE_JDBC_URL, HIVE_USER, HIVE_PASSWORD)) {
            String query = "SELECT country, SUM(confirmed_cases) as total_cases, " +
                "SUM(deaths) as total_deaths " +
                "FROM covid19_data " +
                "GROUP BY country " +
                "ORDER BY total_cases DESC " +
                "LIMIT ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, limit);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> country = new HashMap<>();
                        country.put("country", rs.getString("country"));
                        country.put("totalCases", rs.getLong("total_cases"));
                        country.put("totalDeaths", rs.getLong("total_deaths"));
                        countries.add(country);
                    }
                }
            }
            
            logger.info("Retrieved top {} countries by cases from Hive", limit);
            
        } catch (SQLException e) {
            logger.error("Error querying Hive for top countries", e);
            // Return sample data for demonstration
            countries = getSampleTopCountries();
        }
        
        return countries;
    }
    
    /**
     * Sample data for demonstration when Hive is not available
     */
    private List<Covid19Data> getSampleCovid19Data() {
        List<Covid19Data> sampleData = new ArrayList<>();
        
        // Add sample data for demonstration
        sampleData.add(new Covid19Data(LocalDate.now(), "US", 103436829, 1127152));
        sampleData.add(new Covid19Data(LocalDate.now().minusDays(1), "US", 103400000, 1127000));
        sampleData.add(new Covid19Data(LocalDate.now(), "India", 44986461, 531832));
        sampleData.add(new Covid19Data(LocalDate.now().minusDays(1), "India", 44980000, 531800));
        sampleData.add(new Covid19Data(LocalDate.now(), "Brazil", 37034003, 704659));
        sampleData.add(new Covid19Data(LocalDate.now().minusDays(1), "Brazil", 37030000, 704600));
        
        return sampleData;
    }
    
    /**
     * Sample top countries data for demonstration
     */
    private List<Map<String, Object>> getSampleTopCountries() {
        List<Map<String, Object>> countries = new ArrayList<>();
        
        Map<String, Object> us = new HashMap<>();
        us.put("country", "United States");
        us.put("totalCases", 103436829L);
        us.put("totalDeaths", 1127152L);
        countries.add(us);
        
        Map<String, Object> india = new HashMap<>();
        india.put("country", "India");
        india.put("totalCases", 44986461L);
        india.put("totalDeaths", 531832L);
        countries.add(india);
        
        Map<String, Object> brazil = new HashMap<>();
        brazil.put("country", "Brazil");
        brazil.put("totalCases", 37034003L);
        brazil.put("totalDeaths", 704659L);
        countries.add(brazil);
        
        return countries;
    }
} 
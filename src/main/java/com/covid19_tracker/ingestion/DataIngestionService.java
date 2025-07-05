package com.covid19_tracker.ingestion;

import com.covid19_tracker.model.Covid19Data;
import com.covid19_tracker.model.CancerPatientData;

import java.util.List;

/**
 * Service interface for data ingestion operations
 * Provides methods for retrieving COVID-19 and cancer patient data
 */
public interface DataIngestionService {
    
    /**
     * Get the latest COVID-19 data
     * @return List of COVID-19 data points
     */
    List<Covid19Data> getLatestCovid19Data();
    
    /**
     * Get cancer patient data
     * @return List of cancer patient data
     */
    List<CancerPatientData> getCancerPatientData();
    
    /**
     * Get COVID-19 data for a specific country
     * @param country The country name
     * @return List of COVID-19 data points for the country
     */
    List<Covid19Data> getCovid19DataByCountry(String country);
    
    /**
     * Get COVID-19 data for a date range
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return List of COVID-19 data points for the date range
     */
    List<Covid19Data> getCovid19DataByDateRange(String startDate, String endDate);
} 
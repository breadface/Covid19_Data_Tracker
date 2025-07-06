package com.covid19_tracker.repository;

import com.covid19_tracker.model.Covid19Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for COVID-19 data operations
 */
@Repository
public interface Covid19DataRepository extends JpaRepository<Covid19Data, Long> {
    
    /**
     * Find COVID-19 data by country
     */
    List<Covid19Data> findByCountry(String country);
    
    /**
     * Find COVID-19 data by date range
     */
    List<Covid19Data> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find COVID-19 data by country and date range
     */
    List<Covid19Data> findByCountryAndDateBetween(String country, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find COVID-19 data by data source
     */
    List<Covid19Data> findByDataSource(String dataSource);
    
    /**
     * Get latest COVID-19 data by country
     */
    @Query("SELECT c FROM Covid19Data c WHERE c.country = :country ORDER BY c.date DESC")
    List<Covid19Data> findLatestByCountry(@Param("country") String country);
    
    /**
     * Get summary statistics by country
     */
    @Query("SELECT c.country, " +
           "SUM(c.confirmedCases) as totalCases, " +
           "SUM(c.deaths) as totalDeaths, " +
           "SUM(c.recovered) as totalRecovered, " +
           "MAX(c.date) as lastUpdated " +
           "FROM Covid19Data c " +
           "GROUP BY c.country")
    List<Object[]> getSummaryByCountry();
    
    /**
     * Get daily new cases by country
     */
    @Query("SELECT c.date, c.country, c.newCases " +
           "FROM Covid19Data c " +
           "WHERE c.country = :country AND c.date BETWEEN :startDate AND :endDate " +
           "ORDER BY c.date")
    List<Object[]> getDailyNewCases(@Param("country") String country, 
                                   @Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Get daily new deaths by country
     */
    @Query("SELECT c.date, c.country, c.newDeaths " +
           "FROM Covid19Data c " +
           "WHERE c.country = :country AND c.date BETWEEN :startDate AND :endDate " +
           "ORDER BY c.date")
    List<Object[]> getDailyNewDeaths(@Param("country") String country, 
                                    @Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
} 
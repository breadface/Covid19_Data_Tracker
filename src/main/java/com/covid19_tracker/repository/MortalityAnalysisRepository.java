package com.covid19_tracker.repository;

import com.covid19_tracker.model.MortalityAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for mortality analysis data operations
 */
@Repository
public interface MortalityAnalysisRepository extends JpaRepository<MortalityAnalysis, Long> {
    
    /**
     * Find mortality analysis by country
     */
    List<MortalityAnalysis> findByCountry(String country);
    
    /**
     * Find mortality analysis by cancer type
     */
    List<MortalityAnalysis> findByCancerType(String cancerType);
    
    /**
     * Find mortality analysis by date range
     */
    List<MortalityAnalysis> findByAnalysisDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find mortality analysis by country and cancer type
     */
    List<MortalityAnalysis> findByCountryAndCancerType(String country, String cancerType);
    
    /**
     * Find statistically significant results
     */
    List<MortalityAnalysis> findByStatisticalSignificanceTrue();
    
    /**
     * Get latest mortality analysis by country and cancer type
     */
    @Query("SELECT m FROM MortalityAnalysis m " +
           "WHERE m.country = :country AND m.cancerType = :cancerType " +
           "ORDER BY m.analysisDate DESC")
    List<MortalityAnalysis> findLatestByCountryAndCancerType(@Param("country") String country, 
                                                            @Param("cancerType") String cancerType);
    
    /**
     * Get mortality ratios by cancer type
     */
    @Query("SELECT m.cancerType, " +
           "AVG(m.mortalityRatio) as avgMortalityRatio, " +
           "MIN(m.mortalityRatio) as minMortalityRatio, " +
           "MAX(m.mortalityRatio) as maxMortalityRatio " +
           "FROM MortalityAnalysis m " +
           "GROUP BY m.cancerType")
    List<Object[]> getMortalityRatiosByCancerType();
    
    /**
     * Get risk factors by cancer type
     */
    @Query("SELECT m.cancerType, " +
           "AVG(m.riskFactor) as avgRiskFactor, " +
           "MIN(m.riskFactor) as minRiskFactor, " +
           "MAX(m.riskFactor) as maxRiskFactor " +
           "FROM MortalityAnalysis m " +
           "WHERE m.riskFactor IS NOT NULL " +
           "GROUP BY m.cancerType")
    List<Object[]> getRiskFactorsByCancerType();
    
    /**
     * Get time series of mortality ratios
     */
    @Query("SELECT m.analysisDate, m.cancerType, m.mortalityRatio " +
           "FROM MortalityAnalysis m " +
           "WHERE m.country = :country AND m.cancerType = :cancerType " +
           "AND m.analysisDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.analysisDate")
    List<Object[]> getMortalityRatioTimeSeries(@Param("country") String country,
                                              @Param("cancerType") String cancerType,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
} 
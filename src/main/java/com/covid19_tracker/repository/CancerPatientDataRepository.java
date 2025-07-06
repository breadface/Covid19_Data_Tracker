package com.covid19_tracker.repository;

import com.covid19_tracker.model.CancerPatientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for cancer patient data operations
 */
@Repository
public interface CancerPatientDataRepository extends JpaRepository<CancerPatientData, Long> {
    
    /**
     * Find cancer patient data by cancer type
     */
    List<CancerPatientData> findByCancerType(String cancerType);
    
    /**
     * Find cancer patient data by age group
     */
    @Query("SELECT c FROM CancerPatientData c WHERE c.age BETWEEN :minAge AND :maxAge")
    List<CancerPatientData> findByAgeGroup(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    /**
     * Find cancer patients with COVID-19
     */
    List<CancerPatientData> findByCovid19PositiveTrue();
    
    /**
     * Find cancer patients by COVID-19 severity
     */
    List<CancerPatientData> findByCovid19Severity(String severity);
    
    /**
     * Find cancer patients by COVID-19 outcome
     */
    List<CancerPatientData> findByCovid19Outcome(String outcome);
    
    /**
     * Find cancer patients by vaccination status
     */
    List<CancerPatientData> findByVaccinationStatus(String vaccinationStatus);
    
    /**
     * Get COVID-19 impact statistics by cancer type
     */
    @Query("SELECT c.cancerType, " +
           "COUNT(c) as totalPatients, " +
           "SUM(CASE WHEN c.covid19Positive = true THEN 1 ELSE 0 END) as covidPositive, " +
           "SUM(CASE WHEN c.covid19Outcome = 'deceased' THEN 1 ELSE 0 END) as deceased " +
           "FROM CancerPatientData c " +
           "GROUP BY c.cancerType")
    List<Object[]> getCovidImpactByCancerType();
    
    /**
     * Get mortality rate by cancer type and COVID-19 status
     */
    @Query("SELECT c.cancerType, " +
           "c.covid19Positive, " +
           "COUNT(c) as totalPatients, " +
           "SUM(CASE WHEN c.covid19Outcome = 'deceased' THEN 1 ELSE 0 END) as deceased, " +
           "CAST(SUM(CASE WHEN c.covid19Outcome = 'deceased' THEN 1 ELSE 0 END) AS double) / COUNT(c) * 100 as mortalityRate " +
           "FROM CancerPatientData c " +
           "GROUP BY c.cancerType, c.covid19Positive")
    List<Object[]> getMortalityRateByCancerTypeAndCovid();
    
    /**
     * Get vaccination impact on COVID-19 outcomes
     */
    @Query("SELECT c.vaccinationStatus, " +
           "COUNT(c) as totalPatients, " +
           "SUM(CASE WHEN c.covid19Positive = true THEN 1 ELSE 0 END) as covidPositive, " +
           "SUM(CASE WHEN c.covid19Outcome = 'deceased' THEN 1 ELSE 0 END) as deceased " +
           "FROM CancerPatientData c " +
           "WHERE c.covid19Positive = true " +
           "GROUP BY c.vaccinationStatus")
    List<Object[]> getVaccinationImpact();
} 
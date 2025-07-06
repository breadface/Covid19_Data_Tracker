package com.covid19_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data model representing mortality analysis comparing COVID-19 impact on cancer patients vs general population
 */
@Entity
@Table(name = "mortality_analysis")
public class MortalityAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "analysis_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisDate;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "age_group")
    private String ageGroup;
    
    @Column(name = "cancer_type")
    private String cancerType;
    
    @Column(name = "cancer_stage")
    private String cancerStage;
    
    @Column(name = "general_population_cases")
    private Integer generalPopulationCases;
    
    @Column(name = "general_population_deaths")
    private Integer generalPopulationDeaths;
    
    @Column(name = "general_population_mortality_rate")
    private Double generalPopulationMortalityRate;
    
    @Column(name = "cancer_patient_cases")
    private Integer cancerPatientCases;
    
    @Column(name = "cancer_patient_deaths")
    private Integer cancerPatientDeaths;
    
    @Column(name = "cancer_patient_mortality_rate")
    private Double cancerPatientMortalityRate;
    
    @Column(name = "mortality_ratio")
    private Double mortalityRatio; // cancer_patient_mortality_rate / general_population_mortality_rate
    
    @Column(name = "risk_factor")
    private Double riskFactor; // Additional risk factor for cancer patients
    
    @Column(name = "confidence_interval_lower")
    private Double confidenceIntervalLower;
    
    @Column(name = "confidence_interval_upper")
    private Double confidenceIntervalUpper;
    
    @Column(name = "p_value")
    private Double pValue;
    
    @Column(name = "statistical_significance")
    private Boolean statisticalSignificance;
    
    @Column(name = "data_source")
    private String dataSource;
    
    @Column(name = "analysis_period_start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisPeriodStart;
    
    @Column(name = "analysis_period_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisPeriodEnd;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructors
    public MortalityAnalysis() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MortalityAnalysis(LocalDate analysisDate, String country, String cancerType) {
        this();
        this.analysisDate = analysisDate;
        this.country = country;
        this.cancerType = cancerType;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getAnalysisDate() {
        return analysisDate;
    }
    
    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getAgeGroup() {
        return ageGroup;
    }
    
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    
    public String getCancerType() {
        return cancerType;
    }
    
    public void setCancerType(String cancerType) {
        this.cancerType = cancerType;
    }
    
    public String getCancerStage() {
        return cancerStage;
    }
    
    public void setCancerStage(String cancerStage) {
        this.cancerStage = cancerStage;
    }
    
    public Integer getGeneralPopulationCases() {
        return generalPopulationCases;
    }
    
    public void setGeneralPopulationCases(Integer generalPopulationCases) {
        this.generalPopulationCases = generalPopulationCases;
    }
    
    public Integer getGeneralPopulationDeaths() {
        return generalPopulationDeaths;
    }
    
    public void setGeneralPopulationDeaths(Integer generalPopulationDeaths) {
        this.generalPopulationDeaths = generalPopulationDeaths;
    }
    
    public Double getGeneralPopulationMortalityRate() {
        return generalPopulationMortalityRate;
    }
    
    public void setGeneralPopulationMortalityRate(Double generalPopulationMortalityRate) {
        this.generalPopulationMortalityRate = generalPopulationMortalityRate;
    }
    
    public Integer getCancerPatientCases() {
        return cancerPatientCases;
    }
    
    public void setCancerPatientCases(Integer cancerPatientCases) {
        this.cancerPatientCases = cancerPatientCases;
    }
    
    public Integer getCancerPatientDeaths() {
        return cancerPatientDeaths;
    }
    
    public void setCancerPatientDeaths(Integer cancerPatientDeaths) {
        this.cancerPatientDeaths = cancerPatientDeaths;
    }
    
    public Double getCancerPatientMortalityRate() {
        return cancerPatientMortalityRate;
    }
    
    public void setCancerPatientMortalityRate(Double cancerPatientMortalityRate) {
        this.cancerPatientMortalityRate = cancerPatientMortalityRate;
    }
    
    public Double getMortalityRatio() {
        return mortalityRatio;
    }
    
    public void setMortalityRatio(Double mortalityRatio) {
        this.mortalityRatio = mortalityRatio;
    }
    
    public Double getRiskFactor() {
        return riskFactor;
    }
    
    public void setRiskFactor(Double riskFactor) {
        this.riskFactor = riskFactor;
    }
    
    public Double getConfidenceIntervalLower() {
        return confidenceIntervalLower;
    }
    
    public void setConfidenceIntervalLower(Double confidenceIntervalLower) {
        this.confidenceIntervalLower = confidenceIntervalLower;
    }
    
    public Double getConfidenceIntervalUpper() {
        return confidenceIntervalUpper;
    }
    
    public void setConfidenceIntervalUpper(Double confidenceIntervalUpper) {
        this.confidenceIntervalUpper = confidenceIntervalUpper;
    }
    
    public Double getPValue() {
        return pValue;
    }
    
    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }
    
    public Boolean getStatisticalSignificance() {
        return statisticalSignificance;
    }
    
    public void setStatisticalSignificance(Boolean statisticalSignificance) {
        this.statisticalSignificance = statisticalSignificance;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    
    public LocalDate getAnalysisPeriodStart() {
        return analysisPeriodStart;
    }
    
    public void setAnalysisPeriodStart(LocalDate analysisPeriodStart) {
        this.analysisPeriodStart = analysisPeriodStart;
    }
    
    public LocalDate getAnalysisPeriodEnd() {
        return analysisPeriodEnd;
    }
    
    public void setAnalysisPeriodEnd(LocalDate analysisPeriodEnd) {
        this.analysisPeriodEnd = analysisPeriodEnd;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "MortalityAnalysis{" +
                "id=" + id +
                ", analysisDate=" + analysisDate +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", cancerType='" + cancerType + '\'' +
                ", cancerStage='" + cancerStage + '\'' +
                ", generalPopulationCases=" + generalPopulationCases +
                ", generalPopulationDeaths=" + generalPopulationDeaths +
                ", generalPopulationMortalityRate=" + generalPopulationMortalityRate +
                ", cancerPatientCases=" + cancerPatientCases +
                ", cancerPatientDeaths=" + cancerPatientDeaths +
                ", cancerPatientMortalityRate=" + cancerPatientMortalityRate +
                ", mortalityRatio=" + mortalityRatio +
                ", riskFactor=" + riskFactor +
                ", confidenceIntervalLower=" + confidenceIntervalLower +
                ", confidenceIntervalUpper=" + confidenceIntervalUpper +
                ", pValue=" + pValue +
                ", statisticalSignificance=" + statisticalSignificance +
                ", dataSource='" + dataSource + '\'' +
                ", analysisPeriodStart=" + analysisPeriodStart +
                ", analysisPeriodEnd=" + analysisPeriodEnd +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 
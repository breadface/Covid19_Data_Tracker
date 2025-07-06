package com.covid19_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data model representing cancer patient data for COVID-19 impact analysis
 */
@Entity
@Table(name = "cancer_patient_data")
public class CancerPatientData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", unique = true)
    private String patientId;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "cancer_type")
    private String cancerType;
    
    @Column(name = "cancer_stage")
    private String cancerStage;
    
    @Column(name = "diagnosis_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate diagnosisDate;
    
    @Column(name = "treatment_type")
    private String treatmentType;
    
    @Column(name = "treatment_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate treatmentStartDate;
    
    @Column(name = "treatment_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate treatmentEndDate;
    
    @Column(name = "comorbidities")
    private String comorbidities;
    
    @Column(name = "smoking_status")
    private String smokingStatus;
    
    @Column(name = "bmi")
    private Double bmi;
    
    @Column(name = "covid19_positive")
    private Boolean covid19Positive;
    
    @Column(name = "covid19_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate covid19Date;
    
    @Column(name = "covid19_severity")
    private String covid19Severity; // mild, moderate, severe, critical
    
    @Column(name = "covid19_outcome")
    private String covid19Outcome; // recovered, hospitalized, deceased
    
    @Column(name = "vaccination_status")
    private String vaccinationStatus; // unvaccinated, partially, fully, boosted
    
    @Column(name = "data_source")
    private String dataSource;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructors
    public CancerPatientData() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public CancerPatientData(String patientId, Integer age, String gender, String cancerType) {
        this();
        this.patientId = patientId;
        this.age = age;
        this.gender = gender;
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
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
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
    
    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }
    
    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }
    
    public String getTreatmentType() {
        return treatmentType;
    }
    
    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }
    
    public LocalDate getTreatmentStartDate() {
        return treatmentStartDate;
    }
    
    public void setTreatmentStartDate(LocalDate treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }
    
    public LocalDate getTreatmentEndDate() {
        return treatmentEndDate;
    }
    
    public void setTreatmentEndDate(LocalDate treatmentEndDate) {
        this.treatmentEndDate = treatmentEndDate;
    }
    
    public String getComorbidities() {
        return comorbidities;
    }
    
    public void setComorbidities(String comorbidities) {
        this.comorbidities = comorbidities;
    }
    
    public String getSmokingStatus() {
        return smokingStatus;
    }
    
    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }
    
    public Double getBmi() {
        return bmi;
    }
    
    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }
    
    public Boolean getCovid19Positive() {
        return covid19Positive;
    }
    
    public void setCovid19Positive(Boolean covid19Positive) {
        this.covid19Positive = covid19Positive;
    }
    
    public LocalDate getCovid19Date() {
        return covid19Date;
    }
    
    public void setCovid19Date(LocalDate covid19Date) {
        this.covid19Date = covid19Date;
    }
    
    public String getCovid19Severity() {
        return covid19Severity;
    }
    
    public void setCovid19Severity(String covid19Severity) {
        this.covid19Severity = covid19Severity;
    }
    
    public String getCovid19Outcome() {
        return covid19Outcome;
    }
    
    public void setCovid19Outcome(String covid19Outcome) {
        this.covid19Outcome = covid19Outcome;
    }
    
    public String getVaccinationStatus() {
        return vaccinationStatus;
    }
    
    public void setVaccinationStatus(String vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
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
        return "CancerPatientData{" +
                "id=" + id +
                ", patientId='" + patientId + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", cancerType='" + cancerType + '\'' +
                ", cancerStage='" + cancerStage + '\'' +
                ", diagnosisDate=" + diagnosisDate +
                ", treatmentType='" + treatmentType + '\'' +
                ", treatmentStartDate=" + treatmentStartDate +
                ", treatmentEndDate=" + treatmentEndDate +
                ", comorbidities='" + comorbidities + '\'' +
                ", smokingStatus='" + smokingStatus + '\'' +
                ", bmi=" + bmi +
                ", covid19Positive=" + covid19Positive +
                ", covid19Date=" + covid19Date +
                ", covid19Severity='" + covid19Severity + '\'' +
                ", covid19Outcome='" + covid19Outcome + '\'' +
                ", vaccinationStatus='" + vaccinationStatus + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 
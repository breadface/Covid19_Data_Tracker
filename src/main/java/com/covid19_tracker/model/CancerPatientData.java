package com.covid19_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Data model representing cancer patient data for big data processing
 */
public class CancerPatientData {
    
    private Long id;
    
    private String patientId;
    
    private Integer age;
    
    private String gender;
    
    private String cancerType;
    
    private String cancerStage;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate diagnosisDate;
    
    private String treatmentType;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate treatmentStartDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate treatmentEndDate;
    
    private String comorbidities;
    
    private String smokingStatus;
    
    private Double bmi;
    
    private String dataSource;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate lastUpdated;
    
    // Constructors
    public CancerPatientData() {}
    
    public CancerPatientData(String patientId, Integer age, String gender, String cancerType) {
        this.patientId = patientId;
        this.age = age;
        this.gender = gender;
        this.cancerType = cancerType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getCancerType() { return cancerType; }
    public void setCancerType(String cancerType) { this.cancerType = cancerType; }
    
    public String getCancerStage() { return cancerStage; }
    public void setCancerStage(String cancerStage) { this.cancerStage = cancerStage; }
    
    public LocalDate getDiagnosisDate() { return diagnosisDate; }
    public void setDiagnosisDate(LocalDate diagnosisDate) { this.diagnosisDate = diagnosisDate; }
    
    public String getTreatmentType() { return treatmentType; }
    public void setTreatmentType(String treatmentType) { this.treatmentType = treatmentType; }
    
    public LocalDate getTreatmentStartDate() { return treatmentStartDate; }
    public void setTreatmentStartDate(LocalDate treatmentStartDate) { this.treatmentStartDate = treatmentStartDate; }
    
    public LocalDate getTreatmentEndDate() { return treatmentEndDate; }
    public void setTreatmentEndDate(LocalDate treatmentEndDate) { this.treatmentEndDate = treatmentEndDate; }
    
    public String getComorbidities() { return comorbidities; }
    public void setComorbidities(String comorbidities) { this.comorbidities = comorbidities; }
    
    public String getSmokingStatus() { return smokingStatus; }
    public void setSmokingStatus(String smokingStatus) { this.smokingStatus = smokingStatus; }
    
    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    
    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
    
    @Override
    public String toString() {
        return "CancerPatientData{" +
                "id=" + id +
                ", patientId='" + patientId + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", cancerType='" + cancerType + '\'' +
                '}';
    }
} 
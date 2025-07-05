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
    
    // COVID-19 related fields
    private String country;
    private LocalDate covid19PositiveDate;
    private String covid19Severity;
    private Boolean hospitalized;
    private Boolean icuAdmission;
    private Boolean ventilatorRequired;
    private String covid19Outcome;
    private Boolean cancerTreatmentInterrupted;
    private String vaccinationStatus;
    
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
    
    // COVID-19 related getters and setters
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public LocalDate getCovid19PositiveDate() { return covid19PositiveDate; }
    public void setCovid19PositiveDate(LocalDate covid19PositiveDate) { this.covid19PositiveDate = covid19PositiveDate; }
    
    public String getCovid19Severity() { return covid19Severity; }
    public void setCovid19Severity(String covid19Severity) { this.covid19Severity = covid19Severity; }
    
    public Boolean getHospitalized() { return hospitalized; }
    public void setHospitalized(Boolean hospitalized) { this.hospitalized = hospitalized; }
    
    public Boolean getIcuAdmission() { return icuAdmission; }
    public void setIcuAdmission(Boolean icuAdmission) { this.icuAdmission = icuAdmission; }
    
    public Boolean getVentilatorRequired() { return ventilatorRequired; }
    public void setVentilatorRequired(Boolean ventilatorRequired) { this.ventilatorRequired = ventilatorRequired; }
    
    public String getCovid19Outcome() { return covid19Outcome; }
    public void setCovid19Outcome(String covid19Outcome) { this.covid19Outcome = covid19Outcome; }
    
    public Boolean getCancerTreatmentInterrupted() { return cancerTreatmentInterrupted; }
    public void setCancerTreatmentInterrupted(Boolean cancerTreatmentInterrupted) { this.cancerTreatmentInterrupted = cancerTreatmentInterrupted; }
    
    public String getVaccinationStatus() { return vaccinationStatus; }
    public void setVaccinationStatus(String vaccinationStatus) { this.vaccinationStatus = vaccinationStatus; }
    
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
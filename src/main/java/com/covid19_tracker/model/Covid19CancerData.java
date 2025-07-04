package com.covid19_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Data model representing the intersection of COVID-19 and cancer patient data for big data processing
 */
public class Covid19CancerData {
    
    private Long id;
    
    private CancerPatientData cancerPatient;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate covid19PositiveDate;
    
    private String covid19Severity; // mild, moderate, severe, critical
    
    private Boolean hospitalized;
    
    private Boolean icuAdmission;
    
    private Boolean ventilatorRequired;
    
    private String covid19Outcome; // recovered, died, ongoing
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deathDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recoveryDate;
    
    private Boolean cancerTreatmentInterrupted;
    
    private Integer interruptionDurationDays;
    
    private String vaccinationStatus; // unvaccinated, partially, fully, boosted
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vaccinationDate;
    
    private String dataSource;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate lastUpdated;
    
    // Constructors
    public Covid19CancerData() {}
    
    public Covid19CancerData(CancerPatientData cancerPatient, LocalDate covid19PositiveDate, String covid19Severity) {
        this.cancerPatient = cancerPatient;
        this.covid19PositiveDate = covid19PositiveDate;
        this.covid19Severity = covid19Severity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public CancerPatientData getCancerPatient() { return cancerPatient; }
    public void setCancerPatient(CancerPatientData cancerPatient) { this.cancerPatient = cancerPatient; }
    
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
    
    public LocalDate getDeathDate() { return deathDate; }
    public void setDeathDate(LocalDate deathDate) { this.deathDate = deathDate; }
    
    public LocalDate getRecoveryDate() { return recoveryDate; }
    public void setRecoveryDate(LocalDate recoveryDate) { this.recoveryDate = recoveryDate; }
    
    public Boolean getCancerTreatmentInterrupted() { return cancerTreatmentInterrupted; }
    public void setCancerTreatmentInterrupted(Boolean cancerTreatmentInterrupted) { this.cancerTreatmentInterrupted = cancerTreatmentInterrupted; }
    
    public Integer getInterruptionDurationDays() { return interruptionDurationDays; }
    public void setInterruptionDurationDays(Integer interruptionDurationDays) { this.interruptionDurationDays = interruptionDurationDays; }
    
    public String getVaccinationStatus() { return vaccinationStatus; }
    public void setVaccinationStatus(String vaccinationStatus) { this.vaccinationStatus = vaccinationStatus; }
    
    public LocalDate getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(LocalDate vaccinationDate) { this.vaccinationDate = vaccinationDate; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    
    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
    
    @Override
    public String toString() {
        return "Covid19CancerData{" +
                "id=" + id +
                ", cancerPatient=" + (cancerPatient != null ? cancerPatient.getPatientId() : "null") +
                ", covid19PositiveDate=" + covid19PositiveDate +
                ", covid19Severity='" + covid19Severity + '\'' +
                ", covid19Outcome='" + covid19Outcome + '\'' +
                '}';
    }
} 
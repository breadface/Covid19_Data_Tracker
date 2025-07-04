package com.covid19_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Data model representing COVID-19 data points for big data processing
 */
public class Covid19Data {
    
    private Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private String country;
    
    private String stateProvince;
    
    private Integer confirmedCases;
    
    private Integer deaths;
    
    private Integer recovered;
    
    private Integer activeCases;
    
    private Integer hospitalizations;
    
    private Integer icuCases;
    
    private Integer vaccinations;
    
    private String dataSource;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate lastUpdated;
    
    // Constructors
    public Covid19Data() {}
    
    public Covid19Data(LocalDate date, String country, Integer confirmedCases, Integer deaths) {
        this.date = date;
        this.country = country;
        this.confirmedCases = confirmedCases;
        this.deaths = deaths;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
    
    public Integer getConfirmedCases() { return confirmedCases; }
    public void setConfirmedCases(Integer confirmedCases) { this.confirmedCases = confirmedCases; }
    
    public Integer getDeaths() { return deaths; }
    public void setDeaths(Integer deaths) { this.deaths = deaths; }
    
    public Integer getRecovered() { return recovered; }
    public void setRecovered(Integer recovered) { this.recovered = recovered; }
    
    public Integer getActiveCases() { return activeCases; }
    public void setActiveCases(Integer activeCases) { this.activeCases = activeCases; }
    
    public Integer getHospitalizations() { return hospitalizations; }
    public void setHospitalizations(Integer hospitalizations) { this.hospitalizations = hospitalizations; }
    
    public Integer getIcuCases() { return icuCases; }
    public void setIcuCases(Integer icuCases) { this.icuCases = icuCases; }
    
    public Integer getVaccinations() { return vaccinations; }
    public void setVaccinations(Integer vaccinations) { this.vaccinations = vaccinations; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    
    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
    
    @Override
    public String toString() {
        return "Covid19Data{" +
                "id=" + id +
                ", date=" + date +
                ", country='" + country + '\'' +
                ", confirmedCases=" + confirmedCases +
                ", deaths=" + deaths +
                '}';
    }
} 
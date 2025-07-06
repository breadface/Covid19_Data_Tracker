package com.covid19_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing COVID-19 data records for batch processing
 */
@Entity
@Table(name = "covid19_data")
public class Covid19Data {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "state_province")
    private String stateProvince;
    
    @Column(name = "confirmed_cases")
    private Integer confirmedCases;
    
    @Column(name = "deaths")
    private Integer deaths;
    
    @Column(name = "recovered")
    private Integer recovered;
    
    @Column(name = "active_cases")
    private Integer activeCases;
    
    @Column(name = "new_cases")
    private Integer newCases;
    
    @Column(name = "new_deaths")
    private Integer newDeaths;
    
    @Column(name = "population")
    private Long population;
    
    @Column(name = "data_source")
    private String dataSource;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public Covid19Data() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Covid19Data(LocalDate date, String country, Integer confirmedCases, Integer deaths) {
        this();
        this.date = date;
        this.country = country;
        this.confirmedCases = confirmedCases;
        this.deaths = deaths;
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
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getStateProvince() {
        return stateProvince;
    }
    
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }
    
    public Integer getConfirmedCases() {
        return confirmedCases;
    }
    
    public void setConfirmedCases(Integer confirmedCases) {
        this.confirmedCases = confirmedCases;
    }
    
    public Integer getDeaths() {
        return deaths;
    }
    
    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }
    
    public Integer getRecovered() {
        return recovered;
    }
    
    public void setRecovered(Integer recovered) {
        this.recovered = recovered;
    }
    
    public Integer getActiveCases() {
        return activeCases;
    }
    
    public void setActiveCases(Integer activeCases) {
        this.activeCases = activeCases;
    }
    
    public Integer getNewCases() {
        return newCases;
    }
    
    public void setNewCases(Integer newCases) {
        this.newCases = newCases;
    }
    
    public Integer getNewDeaths() {
        return newDeaths;
    }
    
    public void setNewDeaths(Integer newDeaths) {
        this.newDeaths = newDeaths;
    }
    
    public Long getPopulation() {
        return population;
    }
    
    public void setPopulation(Long population) {
        this.population = population;
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
        return "Covid19Data{" +
                "id=" + id +
                ", date=" + date +
                ", country='" + country + '\'' +
                ", stateProvince='" + stateProvince + '\'' +
                ", confirmedCases=" + confirmedCases +
                ", deaths=" + deaths +
                ", recovered=" + recovered +
                ", activeCases=" + activeCases +
                ", newCases=" + newCases +
                ", newDeaths=" + newDeaths +
                ", population=" + population +
                ", dataSource='" + dataSource + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 
package com.covid19_tracker.model;

import java.util.Objects;

/**
 * Model class for COVID-19 mortality analysis data
 */
public class MortalityAnalysis {
    private String country;
    private long totalCases;
    private long totalDeaths;
    private double mortalityRate;
    private Double cancerPatientMortalityRate;
    private Double generalPopulationMortalityRate;

    public MortalityAnalysis() {}

    public MortalityAnalysis(String country, long totalCases, long totalDeaths, double mortalityRate) {
        this.country = country;
        this.totalCases = totalCases;
        this.totalDeaths = totalDeaths;
        this.mortalityRate = mortalityRate;
    }

    public MortalityAnalysis(String country, long totalCases, long totalDeaths, double mortalityRate,
                           Double cancerPatientMortalityRate, Double generalPopulationMortalityRate) {
        this.country = country;
        this.totalCases = totalCases;
        this.totalDeaths = totalDeaths;
        this.mortalityRate = mortalityRate;
        this.cancerPatientMortalityRate = cancerPatientMortalityRate;
        this.generalPopulationMortalityRate = generalPopulationMortalityRate;
    }

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(long totalCases) {
        this.totalCases = totalCases;
    }

    public long getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(long totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public double getMortalityRate() {
        return mortalityRate;
    }

    public void setMortalityRate(double mortalityRate) {
        this.mortalityRate = mortalityRate;
    }

    public Double getCancerPatientMortalityRate() {
        return cancerPatientMortalityRate;
    }

    public void setCancerPatientMortalityRate(Double cancerPatientMortalityRate) {
        this.cancerPatientMortalityRate = cancerPatientMortalityRate;
    }

    public Double getGeneralPopulationMortalityRate() {
        return generalPopulationMortalityRate;
    }

    public void setGeneralPopulationMortalityRate(Double generalPopulationMortalityRate) {
        this.generalPopulationMortalityRate = generalPopulationMortalityRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MortalityAnalysis that = (MortalityAnalysis) o;
        return totalCases == that.totalCases &&
               totalDeaths == that.totalDeaths &&
               Double.compare(that.mortalityRate, mortalityRate) == 0 &&
               Objects.equals(country, that.country) &&
               Objects.equals(cancerPatientMortalityRate, that.cancerPatientMortalityRate) &&
               Objects.equals(generalPopulationMortalityRate, that.generalPopulationMortalityRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, totalCases, totalDeaths, mortalityRate, cancerPatientMortalityRate, generalPopulationMortalityRate);
    }

    @Override
    public String toString() {
        return "MortalityAnalysis{" +
                "country='" + country + '\'' +
                ", totalCases=" + totalCases +
                ", totalDeaths=" + totalDeaths +
                ", mortalityRate=" + mortalityRate +
                ", cancerPatientMortalityRate=" + cancerPatientMortalityRate +
                ", generalPopulationMortalityRate=" + generalPopulationMortalityRate +
                '}';
    }
} 
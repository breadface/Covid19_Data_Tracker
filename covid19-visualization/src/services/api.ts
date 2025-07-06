// API service for COVID-19 data from Spring Boot REST endpoints
const API_BASE_URL = 'http://localhost:8081/api';

export interface Covid19Data {
  id?: number;
  date: string;
  country: string;
  stateProvince?: string;
  confirmedCases: number;
  deaths: number;
  recovered?: number;
  activeCases?: number;
  newCases?: number;
  newDeaths?: number;
  population?: number;
  dataSource?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CancerPatientData {
  id?: number;
  patientId: string;
  age?: number;
  gender?: string;
  cancerType?: string;
  cancerStage?: string;
  diagnosisDate?: string;
  treatmentType?: string;
  treatmentStartDate?: string;
  treatmentEndDate?: string;
  comorbidities?: string;
  smokingStatus?: string;
  bmi?: number;
  covid19Positive?: boolean;
  covid19Date?: string;
  covid19Severity?: string;
  covid19Outcome?: string;
  vaccinationStatus?: string;
  dataSource?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MortalityAnalysis {
  id?: number;
  analysisDate: string;
  country?: string;
  region?: string;
  ageGroup?: string;
  cancerType?: string;
  cancerStage?: string;
  generalPopulationCases?: number;
  generalPopulationDeaths?: number;
  generalPopulationMortalityRate?: number;
  cancerPatientCases?: number;
  cancerPatientDeaths?: number;
  cancerPatientMortalityRate?: number;
  mortalityRatio?: number;
  riskFactor?: number;
  confidenceIntervalLower?: number;
  confidenceIntervalUpper?: number;
  pValue?: number;
  statisticalSignificance?: boolean;
  dataSource?: string;
  analysisPeriodStart?: string;
  analysisPeriodEnd?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SummaryStatistics {
  totalCases: number;
  totalDeaths: number;
  totalRecovered: number;
  totalActive: number;
  countriesAffected: number;
}

export interface TopCountry {
  country: string;
  totalCases: number;
  totalDeaths: number;
}

export interface CovidImpactByCancerType {
  cancerType: string;
  totalPatients: number;
  covidPositive: number;
  deceased: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  timestamp: number;
  error?: {
    message: string;
    details: string;
    timestamp: number;
  };
}

class ApiService {
  private async makeRequest<T>(endpoint: string, options?: RequestInit): Promise<ApiResponse<T>> {
    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        headers: {
          'Content-Type': 'application/json',
        },
        ...options,
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      return data;
    } catch (error) {
      console.error(`API request failed for ${endpoint}:`, error);
      throw error;
    }
  }

  // COVID-19 Data endpoints
  async getLatestCovid19Data(): Promise<Covid19Data[]> {
    const response = await this.makeRequest<Covid19Data[]>('/covid19/latest');
    return response.data;
  }

  async getCovid19DataByCountry(country: string): Promise<Covid19Data[]> {
    const response = await this.makeRequest<Covid19Data[]>(`/covid19/country/${encodeURIComponent(country)}`);
    return response.data;
  }

  async getCovid19DataByDateRange(startDate: string, endDate: string): Promise<Covid19Data[]> {
    const response = await this.makeRequest<Covid19Data[]>(`/covid19/range?start=${startDate}&end=${endDate}`);
    return response.data;
  }

  async getSummaryStatistics(): Promise<SummaryStatistics> {
    const response = await this.makeRequest<SummaryStatistics>('/covid19/summary');
    return response.data;
  }

  async getTopCountriesByCases(limit: number = 10): Promise<TopCountry[]> {
    try {
      // Get all COVID-19 data and process it to get top countries
      const covidData = await this.getLatestCovid19Data();
      
      // Group by country and sum cases/deaths
      const countryStats = new Map<string, { totalCases: number; totalDeaths: number }>();
      
      covidData.forEach(data => {
        const existing = countryStats.get(data.country) || { totalCases: 0, totalDeaths: 0 };
        countryStats.set(data.country, {
          totalCases: existing.totalCases + (data.confirmedCases || 0),
          totalDeaths: existing.totalDeaths + (data.deaths || 0)
        });
      });
      
      // Convert to array and sort by total cases
      const topCountries: TopCountry[] = Array.from(countryStats.entries())
        .map(([country, stats]) => ({
          country,
          totalCases: stats.totalCases,
          totalDeaths: stats.totalDeaths
        }))
        .sort((a, b) => b.totalCases - a.totalCases)
        .slice(0, limit);
      
      return topCountries;
    } catch (error) {
      console.error('Error getting top countries by cases:', error);
      // Return sample data if API fails
      return [
        { country: 'United States', totalCases: 100000000, totalDeaths: 1000000 },
        { country: 'India', totalCases: 45000000, totalDeaths: 530000 },
        { country: 'Brazil', totalCases: 35000000, totalDeaths: 680000 },
        { country: 'France', totalCases: 38000000, totalDeaths: 160000 },
        { country: 'Germany', totalCases: 36000000, totalDeaths: 160000 }
      ].slice(0, limit);
    }
  }

  // Cancer Patient Data endpoints
  async getCancerPatientData(): Promise<CancerPatientData[]> {
    const response = await this.makeRequest<CancerPatientData[]>('/cancer-patients');
    return response.data;
  }

  async getCovidImpactByCancerType(): Promise<CovidImpactByCancerType[]> {
    const response = await this.makeRequest<CovidImpactByCancerType[]>('/cancer-patients/covid-impact');
    return response.data;
  }

  // Mortality Analysis endpoints
  async getMortalityAnalysis(): Promise<MortalityAnalysis[]> {
    const response = await this.makeRequest<MortalityAnalysis[]>('/mortality-analysis');
    return response.data;
  }

  // Data Ingestion endpoint
  async triggerDataIngestion(): Promise<{ message: string }> {
    const response = await this.makeRequest<{ message: string }>('/ingest', {
      method: 'POST',
    });
    return response.data;
  }

  // Health check
  async getHealth(): Promise<any> {
    const response = await this.makeRequest<any>('/health');
    return response.data;
  }
}

// Export singleton instance
const apiService = new ApiService();
export default apiService; 
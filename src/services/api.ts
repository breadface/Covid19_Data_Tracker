import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export interface Covid19DataPoint {
  date: string;
  country: string;
  confirmedCases: number;
  deaths: number;
  recovered?: number;
  activeCases?: number;
  dataSource: string;
}

export interface CancerPatientData {
  patientId: string;
  age: number;
  gender: string;
  cancerType: string;
  cancerStage: string;
  covid19PositiveDate?: string;
  covid19Severity?: string;
  hospitalized?: boolean;
  icuAdmission?: boolean;
  ventilatorRequired?: boolean;
  covid19Outcome?: string;
  cancerTreatmentInterrupted?: boolean;
  vaccinationStatus?: string;
}

export interface MortalityAnalysis {
  country: string;
  totalCases: number;
  totalDeaths: number;
  mortalityRate: number;
  cancerPatientMortalityRate?: number;
  generalPopulationMortalityRate?: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  error?: string;
}

class ApiService {
  private api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // COVID-19 Data endpoints
  async getCovid19Data(country?: string, startDate?: string, endDate?: string): Promise<Covid19DataPoint[]> {
    try {
      const params = new URLSearchParams();
      if (country) params.append('country', country);
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);

      const response = await this.api.get<ApiResponse<Covid19DataPoint[]>>(`/api/covid19/data?${params}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching COVID-19 data:', error);
      throw error;
    }
  }

  async getCovid19DataByCountry(country: string): Promise<Covid19DataPoint[]> {
    return this.getCovid19Data(country);
  }

  async getCovid19DataByDateRange(startDate: string, endDate: string): Promise<Covid19DataPoint[]> {
    return this.getCovid19Data(undefined, startDate, endDate);
  }

  // Cancer Patient Data endpoints
  async getCancerPatientData(filters?: {
    cancerType?: string;
    country?: string;
    ageRange?: { min: number; max: number };
    hasCovid?: boolean;
  }): Promise<CancerPatientData[]> {
    try {
      const params = new URLSearchParams();
      if (filters?.cancerType) params.append('cancerType', filters.cancerType);
      if (filters?.country) params.append('country', filters.country);
      if (filters?.hasCovid !== undefined) params.append('hasCovid', filters.hasCovid.toString());
      if (filters?.ageRange) {
        params.append('minAge', filters.ageRange.min.toString());
        params.append('maxAge', filters.ageRange.max.toString());
      }

      const response = await this.api.get<ApiResponse<CancerPatientData[]>>(`/api/cancer-patients?${params}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching cancer patient data:', error);
      throw error;
    }
  }

  // Mortality Analysis endpoints
  async getMortalityAnalysis(country?: string): Promise<MortalityAnalysis[]> {
    try {
      const params = new URLSearchParams();
      if (country) params.append('country', country);

      const response = await this.api.get<ApiResponse<MortalityAnalysis[]>>(`/api/mortality-analysis?${params}`);
      return response.data.data;
    } catch (error) {
      console.error('Error fetching mortality analysis:', error);
      throw error;
    }
  }

  // Real-time data endpoints
  async getRealTimeStats(): Promise<{
    totalCases: number;
    totalDeaths: number;
    totalRecovered: number;
    activeCases: number;
    lastUpdated: string;
  }> {
    try {
      const response = await this.api.get<ApiResponse<any>>('/api/stats/realtime');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching real-time stats:', error);
      throw error;
    }
  }

  // Data source endpoints
  async getDataSources(): Promise<{
    jhuCss: { lastUpdated: string; status: string };
    ourWorldInData: { lastUpdated: string; status: string };
    whoApi: { lastUpdated: string; status: string };
  }> {
    try {
      const response = await this.api.get<ApiResponse<any>>('/api/sources/status');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching data sources status:', error);
      throw error;
    }
  }

  // Analytics endpoints
  async getCancerTypeAnalysis(): Promise<{
    cancerType: string;
    totalPatients: number;
    covidPositiveCount: number;
    mortalityRate: number;
  }[]> {
    try {
      const response = await this.api.get<ApiResponse<any[]>>('/api/analytics/cancer-types');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching cancer type analysis:', error);
      throw error;
    }
  }

  async getVaccinationImpact(): Promise<{
    vaccinationStatus: string;
    totalPatients: number;
    covidPositiveCount: number;
    severeCasesCount: number;
    mortalityRate: number;
  }[]> {
    try {
      const response = await this.api.get<ApiResponse<any[]>>('/api/analytics/vaccination-impact');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching vaccination impact:', error);
      throw error;
    }
  }

  async getAgeGroupAnalysis(): Promise<{
    ageGroup: string;
    totalPatients: number;
    covidPositiveCount: number;
    mortalityRate: number;
  }[]> {
    try {
      const response = await this.api.get<ApiResponse<any[]>>('/api/analytics/age-groups');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching age group analysis:', error);
      throw error;
    }
  }

  // Health check
  async healthCheck(): Promise<boolean> {
    try {
      const response = await this.api.get('/health');
      return response.status === 200;
    } catch (error) {
      console.error('Health check failed:', error);
      return false;
    }
  }

  // Mock data for development (when backend is not available)
  getMockCovid19Data(): Covid19DataPoint[] {
    const countries = ['United States', 'India', 'Brazil', 'United Kingdom', 'France', 'Germany'];
    const data: Covid19DataPoint[] = [];
    
    for (let i = 0; i < 30; i++) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      
      countries.forEach(country => {
        data.push({
          date: date.toISOString().split('T')[0],
          country,
          confirmedCases: Math.floor(Math.random() * 1000000) + 10000,
          deaths: Math.floor(Math.random() * 50000) + 1000,
          recovered: Math.floor(Math.random() * 800000) + 5000,
          activeCases: Math.floor(Math.random() * 200000) + 1000,
          dataSource: 'JHU-CSSE'
        });
      });
    }
    
    return data;
  }

  getMockCancerPatientData(): CancerPatientData[] {
    const cancerTypes = ['Lung', 'Breast', 'Colorectal', 'Prostate', 'Leukemia', 'Lymphoma'];
    const stages = ['I', 'II', 'III', 'IV'];
    const severities = ['mild', 'moderate', 'severe', 'critical'];
    const outcomes = ['recovered', 'died', 'ongoing'];
    const vaccinationStatuses = ['unvaccinated', 'partially', 'fully', 'boosted'];
    
    const data: CancerPatientData[] = [];
    
    for (let i = 0; i < 500; i++) {
      const hasCovid = Math.random() > 0.7; // 30% of cancer patients have COVID-19
      
      data.push({
        patientId: `PAT-${String(i + 1).padStart(4, '0')}`,
        age: Math.floor(Math.random() * 50) + 30,
        gender: Math.random() > 0.5 ? 'Male' : 'Female',
        cancerType: cancerTypes[Math.floor(Math.random() * cancerTypes.length)],
        cancerStage: stages[Math.floor(Math.random() * stages.length)],
        covid19PositiveDate: hasCovid ? new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0] : undefined,
        covid19Severity: hasCovid ? severities[Math.floor(Math.random() * severities.length)] : undefined,
        hospitalized: hasCovid ? Math.random() > 0.6 : undefined,
        icuAdmission: hasCovid ? Math.random() > 0.8 : undefined,
        ventilatorRequired: hasCovid ? Math.random() > 0.9 : undefined,
        covid19Outcome: hasCovid ? outcomes[Math.floor(Math.random() * outcomes.length)] : undefined,
        cancerTreatmentInterrupted: hasCovid ? Math.random() > 0.4 : undefined,
        vaccinationStatus: vaccinationStatuses[Math.floor(Math.random() * vaccinationStatuses.length)]
      });
    }
    
    return data;
  }
}

export default new ApiService(); 
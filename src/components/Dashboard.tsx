import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  LineChart, Line, AreaChart, Area, BarChart, Bar, PieChart, Pie,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  Cell, ScatterChart, Scatter
} from 'recharts';
import './Dashboard.css';

interface Covid19Data {
  date: string;
  country: string;
  confirmedCases: number;
  deaths: number;
  recovered?: number;
  activeCases?: number;
  dataSource: string;
}

interface CancerPatientData {
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

interface MortalityAnalysis {
  country: string;
  totalCases: number;
  totalDeaths: number;
  mortalityRate: number;
  cancerPatientMortalityRate?: number;
  generalPopulationMortalityRate?: number;
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D'];

const Dashboard: React.FC = () => {
  const [covidData, setCovidData] = useState<Covid19Data[]>([]);
  const [cancerData, setCancerData] = useState<CancerPatientData[]>([]);
  const [mortalityAnalysis, setMortalityAnalysis] = useState<MortalityAnalysis[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCountry, setSelectedCountry] = useState<string>('All');
  const [selectedCancerType, setSelectedCancerType] = useState<string>('All');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      // In a real implementation, these would be API calls to your backend
      // For now, we'll use mock data
      const mockCovidData = generateMockCovidData();
      const mockCancerData = generateMockCancerData();
      
      setCovidData(mockCovidData);
      setCancerData(mockCancerData);
      setMortalityAnalysis(calculateMortalityAnalysis(mockCovidData, mockCancerData));
      setLoading(false);
    } catch (error) {
      console.error('Error fetching data:', error);
      setLoading(false);
    }
  };

  const generateMockCovidData = (): Covid19Data[] => {
    const countries = ['United States', 'India', 'Brazil', 'United Kingdom', 'France', 'Germany'];
    const data: Covid19Data[] = [];
    
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
  };

  const generateMockCancerData = (): CancerPatientData[] => {
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
  };

  const calculateMortalityAnalysis = (covidData: Covid19Data[], cancerData: CancerPatientData[]): MortalityAnalysis[] => {
    const countries = [...new Set(covidData.map(d => d.country))];
    
    return countries.map(country => {
      const countryCovidData = covidData.filter(d => d.country === country);
      const countryCancerData = cancerData.filter(d => 
        d.covid19PositiveDate && d.covid19Outcome
      );
      
      const totalCases = countryCovidData.reduce((sum, d) => sum + d.confirmedCases, 0);
      const totalDeaths = countryCovidData.reduce((sum, d) => sum + d.deaths, 0);
      const mortalityRate = totalCases > 0 ? (totalDeaths / totalCases) * 100 : 0;
      
      const cancerPatientsWithCovid = countryCancerData.length;
      const cancerPatientDeaths = countryCancerData.filter(d => d.covid19Outcome === 'died').length;
      const cancerPatientMortalityRate = cancerPatientsWithCovid > 0 ? 
        (cancerPatientDeaths / cancerPatientsWithCovid) * 100 : 0;
      
      return {
        country,
        totalCases,
        totalDeaths,
        mortalityRate,
        cancerPatientMortalityRate,
        generalPopulationMortalityRate: mortalityRate
      };
    });
  };

  const filteredCovidData = covidData.filter(d => 
    selectedCountry === 'All' || d.country === selectedCountry
  );

  const filteredCancerData = cancerData.filter(d => 
    (selectedCountry === 'All' || d.country === selectedCountry) &&
    (selectedCancerType === 'All' || d.cancerType === selectedCancerType)
  );

  const cancerPatientsWithCovid = filteredCancerData.filter(d => d.covid19PositiveDate);
  const cancerPatientMortality = cancerPatientsWithCovid.filter(d => d.covid19Outcome === 'died').length;
  const cancerPatientMortalityRate = cancerPatientsWithCovid.length > 0 ? 
    (cancerPatientMortality / cancerPatientsWithCovid.length) * 100 : 0;

  if (loading) {
    return <div className="loading">Loading COVID-19 Data Analysis...</div>;
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>COVID-19 & Cancer Patient Analysis Dashboard</h1>
        <p>Real-time analysis of COVID-19 morbidity and mortality in cancer patients</p>
      </header>

      <div className="filters">
        <div className="filter-group">
          <label>Country:</label>
          <select 
            value={selectedCountry} 
            onChange={(e) => setSelectedCountry(e.target.value)}
          >
            <option value="All">All Countries</option>
            {[...new Set(covidData.map(d => d.country))].map(country => (
              <option key={country} value={country}>{country}</option>
            ))}
          </select>
        </div>
        
        <div className="filter-group">
          <label>Cancer Type:</label>
          <select 
            value={selectedCancerType} 
            onChange={(e) => setSelectedCancerType(e.target.value)}
          >
            <option value="All">All Types</option>
            {[...new Set(cancerData.map(d => d.cancerType))].map(type => (
              <option key={type} value={type}>{type}</option>
            ))}
          </select>
        </div>
      </div>

      <div className="metrics-grid">
        <div className="metric-card">
          <h3>Total COVID-19 Cases</h3>
          <div className="metric-value">
            {filteredCovidData.reduce((sum, d) => sum + d.confirmedCases, 0).toLocaleString()}
          </div>
        </div>
        
        <div className="metric-card">
          <h3>Total Deaths</h3>
          <div className="metric-value">
            {filteredCovidData.reduce((sum, d) => sum + d.deaths, 0).toLocaleString()}
          </div>
        </div>
        
        <div className="metric-card">
          <h3>General Mortality Rate</h3>
          <div className="metric-value">
            {filteredCovidData.length > 0 ? 
              ((filteredCovidData.reduce((sum, d) => sum + d.deaths, 0) / 
                filteredCovidData.reduce((sum, d) => sum + d.confirmedCases, 0)) * 100).toFixed(2) : 0}%
          </div>
        </div>
        
        <div className="metric-card">
          <h3>Cancer Patient Mortality Rate</h3>
          <div className="metric-value">
            {cancerPatientMortalityRate.toFixed(2)}%
          </div>
        </div>
      </div>

      <div className="charts-grid">
        <div className="chart-container">
          <h3>COVID-19 Cases Over Time</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={filteredCovidData.slice(-20)}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="confirmedCases" stroke="#8884d8" name="Confirmed Cases" />
              <Line type="monotone" dataKey="deaths" stroke="#ff7300" name="Deaths" />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Mortality Rate Comparison</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={mortalityAnalysis}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="country" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="generalPopulationMortalityRate" fill="#8884d8" name="General Population" />
              <Bar dataKey="cancerPatientMortalityRate" fill="#ff7300" name="Cancer Patients" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Cancer Types Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={Object.entries(
                  filteredCancerData.reduce((acc, d) => {
                    acc[d.cancerType] = (acc[d.cancerType] || 0) + 1;
                    return acc;
                  }, {} as Record<string, number>)
                ).map(([type, count]) => ({ name: type, value: count }))}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {Object.entries(
                  filteredCancerData.reduce((acc, d) => {
                    acc[d.cancerType] = (acc[d.cancerType] || 0) + 1;
                    return acc;
                  }, {} as Record<string, number>)
                ).map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>COVID-19 Severity in Cancer Patients</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={Object.entries(
              cancerPatientsWithCovid.reduce((acc, d) => {
                if (d.covid19Severity) {
                  acc[d.covid19Severity] = (acc[d.covid19Severity] || 0) + 1;
                }
                return acc;
              }, {} as Record<string, number>)
            ).map(([severity, count]) => ({ severity, count }))}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="severity" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="count" fill="#82ca9d" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Age vs COVID-19 Outcome in Cancer Patients</h3>
          <ResponsiveContainer width="100%" height={300}>
            <ScatterChart data={cancerPatientsWithCovid}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="age" name="Age" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Scatter name="Recovered" dataKey="age" fill="#8884d8" />
              <Scatter name="Died" dataKey="age" fill="#ff7300" />
            </ScatterChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Vaccination Status Impact</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={Object.entries(
              cancerPatientsWithCovid.reduce((acc, d) => {
                acc[d.vaccinationStatus || 'unknown'] = (acc[d.vaccinationStatus || 'unknown'] || 0) + 1;
                return acc;
              }, {} as Record<string, number>)
            ).map(([status, count]) => ({ status, count }))}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="status" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="count" fill="#ffc658" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 
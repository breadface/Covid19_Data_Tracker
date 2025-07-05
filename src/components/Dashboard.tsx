import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { websocketService } from '../services/websocketService';
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
  country: string;
  covid19PositiveDate?: string;
  covid19Severity?: string;
  hospitalized?: boolean;
  icuAdmission?: boolean;
  ventilatorRequired?: boolean;
  covid19Outcome?: string;
  cancerTreatmentInterrupted?: boolean;
  vaccinationStatus: string;
}

interface MortalityAnalysis {
  country: string;
  totalCases: number;
  totalDeaths: number;
  mortalityRate: number;
  cancerPatientMortalityRate?: number;
  generalPopulationMortalityRate?: number;
}

const Dashboard: React.FC = () => {
  const [covidData, setCovidData] = useState<Covid19Data[]>([]);
  const [cancerData, setCancerData] = useState<CancerPatientData[]>([]);
  const [mortalityAnalysis, setMortalityAnalysis] = useState<MortalityAnalysis[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCountry, setSelectedCountry] = useState('All');
  const [selectedCancerType, setSelectedCancerType] = useState('All');

  useEffect(() => {
    const setupWebSocket = () => {
      websocketService.connect();
      
      websocketService.onMessage((data) => {
        try {
          const parsedData = JSON.parse(data);
          
          if (parsedData.type === 'covid19-data') {
            setCovidData(prevData => {
              const newData = [...prevData, parsedData.data];
              // Keep only last 100 records to prevent memory issues
              return newData.slice(-100);
            });
          } else if (parsedData.type === 'cancer-patient-data') {
            setCancerData(prevData => {
              const newData = [...prevData, parsedData.data];
              // Keep only last 100 records to prevent memory issues
              return newData.slice(-100);
            });
          } else if (parsedData.type === 'mortality-analysis') {
            setMortalityAnalysis(prevData => {
              const newData = [...prevData, parsedData.data];
              // Keep only last 50 records to prevent memory issues
              return newData.slice(-50);
            });
          }
          
          setLoading(false);
        } catch (error) {
          console.error('Error parsing WebSocket data:', error);
        }
      });

      websocketService.onError((error) => {
        console.error('WebSocket error:', error);
        setLoading(false);
      });

      websocketService.onClose(() => {
        console.log('WebSocket connection closed');
        setLoading(false);
      });
    };

    setupWebSocket();

    return () => {
      websocketService.disconnect();
    };
  }, []);

  const filteredCovidData = covidData.filter((d: Covid19Data) => 
    selectedCountry === 'All' || d.country === selectedCountry
  );

  const filteredCancerData = cancerData.filter((d: CancerPatientData) => 
    (selectedCountry === 'All' || d.country === selectedCountry) &&
    (selectedCancerType === 'All' || d.cancerType === selectedCancerType)
  );

  const cancerPatientsWithCovid = filteredCancerData.filter((d: CancerPatientData) => d.covid19PositiveDate);
  const cancerPatientMortality = cancerPatientsWithCovid.filter((d: CancerPatientData) => d.covid19Outcome === 'died').length;
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
            {Array.from(new Set(covidData.map((d: Covid19Data) => d.country))).map((country: string) => (
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
            {Array.from(new Set(cancerData.map((d: CancerPatientData) => d.cancerType))).map((type: string) => (
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
          <h3>Mortality Analysis by Country</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={mortalityAnalysis}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="country" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="mortalityRate" stroke="#82ca9d" name="General Mortality Rate" />
              <Line type="monotone" dataKey="cancerPatientMortalityRate" stroke="#ffc658" name="Cancer Patient Mortality Rate" />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="data-tables">
        <div className="table-container">
          <h3>Latest COVID-19 Data</h3>
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Country</th>
                <th>Confirmed Cases</th>
                <th>Deaths</th>
                <th>Recovered</th>
                <th>Active Cases</th>
              </tr>
            </thead>
            <tbody>
              {filteredCovidData.slice(-10).map((data, index) => (
                <tr key={index}>
                  <td>{data.date}</td>
                  <td>{data.country}</td>
                  <td>{data.confirmedCases.toLocaleString()}</td>
                  <td>{data.deaths.toLocaleString()}</td>
                  <td>{data.recovered?.toLocaleString() || 'N/A'}</td>
                  <td>{data.activeCases?.toLocaleString() || 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="table-container">
          <h3>Cancer Patients with COVID-19</h3>
          <table>
            <thead>
              <tr>
                <th>Patient ID</th>
                <th>Age</th>
                <th>Cancer Type</th>
                <th>Country</th>
                <th>COVID-19 Outcome</th>
                <th>Vaccination Status</th>
              </tr>
            </thead>
            <tbody>
              {cancerPatientsWithCovid.slice(-10).map((data, index) => (
                <tr key={index}>
                  <td>{data.patientId}</td>
                  <td>{data.age}</td>
                  <td>{data.cancerType}</td>
                  <td>{data.country}</td>
                  <td>{data.covid19Outcome || 'N/A'}</td>
                  <td>{data.vaccinationStatus}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 
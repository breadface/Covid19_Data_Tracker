import * as React from 'react';
import { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar, PieChart, Pie } from 'recharts';
import apiService, { Covid19Data, SummaryStatistics, TopCountry, CancerPatientData, MortalityAnalysis } from '../services/api';
import './Dashboard.css';

interface RealTimeStats {
  timestamp: string;
  totalCases: number;
  totalDeaths: number;
  totalRecovered: number;
  activeCases: number;
  globalMortalityRate: number;
}

function Dashboard() {
  const [covidData, setCovidData] = useState<Covid19Data[]>([]);
  const [summaryStats, setSummaryStats] = useState<SummaryStatistics | null>(null);
  const [topCountries, setTopCountries] = useState<TopCountry[]>([]);
  const [cancerData, setCancerData] = useState<CancerPatientData[]>([]);
  const [mortalityAnalysis, setMortalityAnalysis] = useState<MortalityAnalysis[]>([]);
  const [realTimeStats, setRealTimeStats] = useState<RealTimeStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCountry, setSelectedCountry] = useState<string>('All');
  const [selectedCancerType, setSelectedCancerType] = useState<string>('All');
  const [selectedTimeRange, setSelectedTimeRange] = useState<string>('7d');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch data from REST API
        const [covidDataResponse, summaryResponse, topCountriesResponse] = await Promise.all([
          apiService.getLatestCovid19Data(),
          apiService.getSummaryStatistics(),
          apiService.getTopCountriesByCases(10)
        ]);

        setCovidData(covidDataResponse);
        setSummaryStats(summaryResponse);
        setTopCountries(topCountriesResponse);

        // Generate sample data for demonstration (since we don't have real cancer/mortality data)
        generateSampleData();

        setLoading(false);
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('Failed to load data. Please check if the API server is running.');
        setLoading(false);
      }
    };

    fetchData();

    // Set up polling to refresh data every 30 seconds
    const interval = setInterval(fetchData, 30000);

    return () => clearInterval(interval);
  }, []);

  const generateSampleData = () => {
    // Generate sample cancer patient data
    const sampleCancerData: CancerPatientData[] = [
      {
        patientId: 'CP001',
        age: 65,
        gender: 'Female',
        cancerType: 'Breast Cancer',
        cancerStage: 'Stage II',
        covid19Positive: true,
        covid19Date: '2023-01-15',
        covid19Severity: 'Moderate',
        covid19Outcome: 'recovered',
        vaccinationStatus: 'Fully Vaccinated',
        dataSource: 'Sample Data'
      },
      {
        patientId: 'CP002',
        age: 72,
        gender: 'Male',
        cancerType: 'Lung Cancer',
        cancerStage: 'Stage III',
        covid19Positive: true,
        covid19Date: '2023-02-20',
        covid19Severity: 'Severe',
        covid19Outcome: 'deceased',
        vaccinationStatus: 'Partially Vaccinated',
        dataSource: 'Sample Data'
      }
    ];

    setCancerData(sampleCancerData);

    // Generate sample mortality analysis
    const sampleMortalityData: MortalityAnalysis[] = [
      {
        analysisDate: '2023-01-01',
        country: 'US',
        cancerType: 'Breast Cancer',
        generalPopulationCases: 1000000,
        generalPopulationDeaths: 15000,
        generalPopulationMortalityRate: 1.5,
        cancerPatientCases: 5000,
        cancerPatientDeaths: 200,
        cancerPatientMortalityRate: 4.0,
        mortalityRatio: 2.67,
        riskFactor: 2.5,
        statisticalSignificance: true,
        dataSource: 'Sample Data'
      },
      {
        analysisDate: '2023-02-01',
        country: 'US',
        cancerType: 'Lung Cancer',
        generalPopulationCases: 1000000,
        generalPopulationDeaths: 15000,
        generalPopulationMortalityRate: 1.5,
        cancerPatientCases: 3000,
        cancerPatientDeaths: 180,
        cancerPatientMortalityRate: 6.0,
        mortalityRatio: 4.0,
        riskFactor: 3.8,
        statisticalSignificance: true,
        dataSource: 'Sample Data'
      }
    ];

    setMortalityAnalysis(sampleMortalityData);

    // Generate sample real-time stats
    const sampleRealTimeStats: RealTimeStats[] = [
      {
        timestamp: new Date().toISOString(),
        totalCases: summaryStats?.totalCases || 676609955,
        totalDeaths: summaryStats?.totalDeaths || 6881797,
        totalRecovered: summaryStats?.totalRecovered || 652728158,
        activeCases: summaryStats?.totalActive || 17000000,
        globalMortalityRate: 1.02
      }
    ];

    setRealTimeStats(sampleRealTimeStats);
  };

  const filteredCovidData = covidData.filter((d: Covid19Data) => 
    selectedCountry === 'All' || d.country === selectedCountry
  );

  const filteredCancerData = cancerData.filter((d: CancerPatientData) => 
    selectedCancerType === 'All' || d.cancerType === selectedCancerType
  );

  const cancerPatientsWithCovid = filteredCancerData.filter((d: CancerPatientData) => d.covid19Positive);
  const cancerPatientMortality = cancerPatientsWithCovid.filter((d: CancerPatientData) => d.covid19Outcome === 'deceased').length;
  const cancerPatientMortalityRate = cancerPatientsWithCovid.length > 0 ? 
    (cancerPatientMortality / cancerPatientsWithCovid.length) * 100 : 0;

  if (loading) {
    return <div className="loading">Loading COVID-19 Data Analysis...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <h2>Error Loading Data</h2>
        <p>{error}</p>
        <p>Please ensure the API server is running on port 8081</p>
        <button onClick={() => window.location.reload()}>Retry</button>
      </div>
    );
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
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setSelectedCountry(e.target.value)}
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
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setSelectedCancerType(e.target.value)}
          >
            <option value="All">All Types</option>
            {Array.from(new Set(cancerData.map((d: CancerPatientData) => d.cancerType).filter((type): type is string => type !== undefined))).map((type: string) => (
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
          <h3>Total Recovered</h3>
          <div className="metric-value">
            {filteredCovidData.reduce((sum, d) => sum + (d.recovered || 0), 0).toLocaleString()}
          </div>
        </div>
        
        <div className="metric-card">
          <h3>Active Cases</h3>
          <div className="metric-value">
            {filteredCovidData.reduce((sum, d) => sum + (d.activeCases || 0), 0).toLocaleString()}
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
        
        <div className="metric-card">
          <h3>Total Cancer Patients</h3>
          <div className="metric-value">
            {filteredCancerData.length.toLocaleString()}
          </div>
        </div>
        
        <div className="metric-card">
          <h3>COVID-19 Positive Cancer Patients</h3>
          <div className="metric-value">
            {cancerPatientsWithCovid.length.toLocaleString()}
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
          <h3>Country-wise COVID-19 Cases</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={filteredCovidData.slice(-10)}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="country" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="confirmedCases" fill="#8884d8" name="Confirmed Cases" />
              <Bar dataKey="deaths" fill="#ff7300" name="Deaths" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Recovery vs Active Cases</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={filteredCovidData.slice(-10)}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="country" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="recovered" fill="#82ca9d" name="Recovered" />
              <Bar dataKey="activeCases" fill="#ffc658" name="Active Cases" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Mortality Analysis by Cancer Type</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={mortalityAnalysis}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="cancerType" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="generalPopulationMortalityRate" stroke="#82ca9d" name="General Mortality Rate" />
              <Line type="monotone" dataKey="cancerPatientMortalityRate" stroke="#ffc658" name="Cancer Patient Mortality Rate" />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>Cancer Types Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={Array.from(new Set(cancerData.map(d => d.cancerType))).map(type => ({
              cancerType: type,
              count: cancerData.filter(d => d.cancerType === type).length
            }))}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="cancerType" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#8884d8" name="Patient Count" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-container">
          <h3>COVID-19 Impact on Cancer Patients</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={[
                  { name: 'COVID-19 Positive', value: cancerPatientsWithCovid.length, fill: '#ff7300' },
                  { name: 'COVID-19 Negative', value: cancerData.length - cancerPatientsWithCovid.length, fill: '#82ca9d' }
                ]}
                cx="50%"
                cy="50%"
                outerRadius={80}
                label={({ name, percent }: { name: string; percent?: number }) => `${name} ${((percent || 0) * 100).toFixed(0)}%`}
              />
              <Tooltip />
            </PieChart>
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
}

export default Dashboard; 
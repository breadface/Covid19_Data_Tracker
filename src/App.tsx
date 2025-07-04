import React from 'react';
import './App.css';
import Dashboard from './components/Dashboard';
import CustomSVGChart from './components/CustomSVGChart';

function App() {
  // Sample data for the custom SVG chart
  const sampleData = [
    { date: '2023-01-01', cases: 1000, deaths: 50, country: 'United States' },
    { date: '2023-01-02', cases: 1200, deaths: 60, country: 'United States' },
    { date: '2023-01-03', cases: 1100, deaths: 55, country: 'United States' },
    { date: '2023-01-04', cases: 1300, deaths: 65, country: 'United States' },
    { date: '2023-01-05', cases: 1400, deaths: 70, country: 'United States' },
    { date: '2023-01-06', cases: 1500, deaths: 75, country: 'United States' },
    { date: '2023-01-07', cases: 1600, deaths: 80, country: 'United States' },
    { date: '2023-01-08', cases: 1700, deaths: 85, country: 'United States' },
    { date: '2023-01-09', cases: 1800, deaths: 90, country: 'United States' },
    { date: '2023-01-10', cases: 1900, deaths: 95, country: 'United States' },
  ];

  return (
    <div className="App">
      <Dashboard />
      
      {/* Custom SVG Chart Example */}
      <div style={{ 
        padding: '20px', 
        background: '#f5f5f5', 
        marginTop: '20px',
        borderRadius: '8px'
      }}>
        <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>
          Custom D3.js SVG Visualization
        </h2>
        <CustomSVGChart 
          data={sampleData}
          width={800}
          height={400}
          title="COVID-19 Cases and Deaths Over Time"
        />
      </div>
    </div>
  );
}

export default App; 
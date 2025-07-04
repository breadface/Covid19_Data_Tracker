# COVID-19 Data Visualization Dashboard

A modern React-based visualization dashboard for analyzing COVID-19 morbidity and mortality in cancer patients. This frontend application provides rich, interactive visualizations with SVG charts and real-time data analysis capabilities.

## 🎯 Features

### 📊 **Rich Data Visualizations**
- **Interactive Charts**: Line charts, bar charts, pie charts, and scatter plots
- **Custom SVG Visualizations**: Advanced D3.js-based charts with gradients and animations
- **Real-time Data**: Live updates from the COVID-19 data pipeline
- **Responsive Design**: Mobile-friendly interface with adaptive layouts

### 🔍 **Advanced Analytics**
- **Mortality Rate Analysis**: Compare general population vs. cancer patient mortality rates
- **Cancer Type Distribution**: Visualize COVID-19 impact across different cancer types
- **Age Group Analysis**: Analyze age-related patterns in COVID-19 outcomes
- **Vaccination Impact**: Study the effect of vaccination on cancer patient outcomes

### 🎨 **Modern UI/UX**
- **Gradient Backgrounds**: Beautiful visual design with modern aesthetics
- **Smooth Animations**: CSS animations and transitions for enhanced user experience
- **Interactive Filters**: Country and cancer type filtering capabilities
- **Hover Effects**: Rich tooltips and interactive elements

## 🏗️ Architecture

```
covid19-visualization/
├── src/
│   ├── components/
│   │   ├── Dashboard.tsx          # Main dashboard component
│   │   ├── Dashboard.css          # Dashboard styling
│   │   └── CustomSVGChart.tsx     # D3.js custom visualizations
│   ├── services/
│   │   └── api.ts                 # API service for backend communication
│   ├── App.tsx                    # Main application component
│   └── App.css                    # Global application styling
├── public/                        # Static assets
└── package.json                   # Dependencies and scripts
```

## 🚀 Quick Start

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn
- COVID-19 Data Tracker backend running (optional for mock data)

### Installation

1. **Navigate to the visualization directory:**
   ```bash
   cd covid19-visualization
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start the development server:**
   ```bash
   npm start
   ```

4. **Open your browser:**
   Navigate to `http://localhost:3000` to view the dashboard.

## 📈 Visualization Components

### 1. **Dashboard Component**
- **Location**: `src/components/Dashboard.tsx`
- **Features**:
  - Real-time metrics display
  - Interactive filtering by country and cancer type
  - Multiple chart types (line, bar, pie, scatter)
  - Responsive grid layout

### 2. **Custom SVG Chart**
- **Location**: `src/components/CustomSVGChart.tsx`
- **Features**:
  - D3.js-powered custom visualizations
  - Gradient-filled area charts
  - Interactive tooltips
  - Smooth animations
  - Responsive design

### 3. **API Service**
- **Location**: `src/services/api.ts`
- **Features**:
  - RESTful API communication
  - TypeScript interfaces
  - Error handling
  - Mock data fallback

## 🎨 Chart Types

### **Line Charts**
- COVID-19 cases and deaths over time
- Trend analysis and pattern recognition
- Multi-series data visualization

### **Bar Charts**
- Mortality rate comparisons
- COVID-19 severity distribution
- Vaccination status impact

### **Pie Charts**
- Cancer type distribution
- Patient demographics
- Outcome percentages

### **Scatter Plots**
- Age vs. COVID-19 outcome correlation
- Risk factor analysis
- Patient clustering

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the root directory:

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
```

### API Endpoints
The application expects the following backend endpoints:

- `GET /api/covid19/data` - COVID-19 case data
- `GET /api/cancer-patients` - Cancer patient data
- `GET /api/mortality-analysis` - Mortality analysis
- `GET /api/stats/realtime` - Real-time statistics
- `GET /api/analytics/*` - Various analytics endpoints

## 📱 Responsive Design

The dashboard is fully responsive and optimized for:
- **Desktop**: Full-featured experience with all charts visible
- **Tablet**: Adaptive layout with stacked charts
- **Mobile**: Single-column layout with touch-friendly interactions

## 🎯 Key Metrics Displayed

### **COVID-19 Metrics**
- Total confirmed cases
- Total deaths
- General population mortality rate
- Active cases and recovery rates

### **Cancer Patient Analysis**
- Cancer patient mortality rate
- COVID-19 severity distribution
- Treatment interruption impact
- Vaccination status correlation

### **Comparative Analysis**
- General population vs. cancer patient mortality
- Age group risk assessment
- Cancer type vulnerability analysis
- Geographic variation patterns

## 🔄 Data Flow

1. **Data Ingestion**: Real-time data from COVID-19 APIs
2. **Processing**: Spark streaming for real-time analytics
3. **Storage**: HDFS for data persistence
4. **API Layer**: RESTful endpoints serving processed data
5. **Frontend**: React components consuming API data
6. **Visualization**: D3.js and Recharts for rich visualizations

## 🛠️ Development

### Available Scripts

```bash
npm start          # Start development server
npm run build      # Build for production
npm test           # Run tests
npm run eject      # Eject from Create React App
```

### Adding New Visualizations

1. **Create a new component** in `src/components/`
2. **Import required libraries** (D3.js, Recharts)
3. **Define TypeScript interfaces** for data structures
4. **Implement the visualization logic**
5. **Add to the main dashboard**

### Styling Guidelines

- Use CSS modules or styled-components for component-specific styles
- Follow the existing color scheme and design patterns
- Ensure responsive design for all new components
- Add smooth animations and transitions

## 🚀 Deployment

### Production Build
```bash
npm run build
```

### Docker Deployment
```dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

## 🔗 Integration with Backend

The visualization app integrates seamlessly with the COVID-19 Data Tracker backend:

1. **Real-time Data**: Connects to Kafka streams for live updates
2. **Batch Analytics**: Accesses Hive tables for historical analysis
3. **API Gateway**: RESTful endpoints for data retrieval
4. **Authentication**: Secure API access (future enhancement)

## 📊 Data Sources

The dashboard visualizes data from:
- **JHU CSSE**: Johns Hopkins University COVID-19 data
- **Our World in Data**: Comprehensive global dataset
- **WHO API**: World Health Organization data
- **Cancer Registry**: Patient-specific cancer data

## 🎯 Future Enhancements

- **Real-time WebSocket connections** for live data updates
- **Advanced filtering** with date ranges and multiple criteria
- **Export functionality** for charts and reports
- **User authentication** and personalized dashboards
- **Machine learning insights** integration
- **Mobile app** development

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Check the main project README
- Review the API documentation
- Open an issue on GitHub

---

**Built with ❤️ for COVID-19 research and cancer patient care**

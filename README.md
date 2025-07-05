# COVID-19 Data Tracker - Big Data Edition

A comprehensive big data solution for tracking COVID-19 morbidity and mortality in cancer patients using the **Hadoop ecosystem** with a modern **React visualization frontend**.

## 🏗️ Architecture Overview

This project implements a complete big data pipeline using individual **Hadoop ecosystem components** with a rich web-based visualization interface:

### Backend (Big Data Pipeline)
- **Apache Kafka** - Real-time data streaming and message queuing
- **Apache Spark Streaming** - Real-time data processing and analytics
- **Apache Hive** - Data warehousing and batch analytics
- **HDFS (Hadoop Distributed File System)** - Distributed file storage
- **PostgreSQL** - Metadata storage for Hive metastore
- **ZooKeeper** - Distributed coordination service
- **Java** - Core application development

### Frontend (Visualization Dashboard)
- **React 18** - Modern UI framework
- **TypeScript** - Type-safe development
- **D3.js** - Custom interactive charts
- **Recharts** - React chart library
- **Axios** - HTTP client for API communication

## 📊 Data Flow

```
External APIs (JHU CSSE, WHO, Our World in Data) 
    ↓
Kafka Producer (Data Ingestion)
    ↓
Kafka Topic (covid19-data)
    ↓
Spark Streaming (Real-time Processing)
    ↓
HDFS Storage
    ↓
Hive Analytics (Batch Processing)
    ↓
REST API
    ↓
React Frontend (Interactive Dashboard)
```

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 11+
- Node.js 18+ (for React frontend)
- Maven (or use the included Maven wrapper)
- **Note**: This setup is optimized for Apple Silicon (M1/M2) Macs but works on all platforms

### 1. Build the Backend Application
```bash
./mvnw clean package -DskipTests
```

### 2. Start Hadoop Ecosystem
```bash
docker-compose up -d
```

This will start the following services:
- **HDFS NameNode** - Distributed file system management
- **HDFS DataNode** - Distributed file storage
- **Kafka** - Real-time streaming platform
- **ZooKeeper** - Distributed coordination
- **Spark Master** - Spark cluster management
- **Spark Worker** - Spark processing nodes
- **PostgreSQL** - Hive metastore database
- **Hive Metastore** - Metadata management
- **Hive Server** - SQL query interface
- **COVID-19 Tracker** - Main application

### 3. Start React Frontend
```bash
# Install dependencies
npm install

# Start the React development server
npm start
```

The React app will be available at: http://localhost:3000

### 4. Monitor the Services
```bash
# Check service status
docker-compose ps

# View application logs
docker-compose logs covid19-tracker

# Monitor real-time logs
docker-compose logs -f covid19-tracker

# Check HDFS status
docker-compose logs namenode
```

### 5. Access Web Interfaces
- **React Dashboard**: http://localhost:3000
- **HDFS NameNode Web UI**: http://localhost:9870
- **Spark Master**: http://localhost:8080
- **Hive Server**: http://localhost:10002 (HiveServer2 Web UI)

## 🎨 Frontend Features

### Interactive Dashboard
- **Real-time COVID-19 Data Visualization**
  - Line charts showing cases and deaths over time
  - Country-wise filtering and comparison
  - Interactive tooltips with detailed information
  - Responsive design for all screen sizes

- **Cancer Patient Analytics**
  - Mortality rate analysis for cancer patients with COVID-19
  - Cancer type distribution charts
  - Age and gender demographics
  - Treatment interruption analysis

- **Custom D3.js Charts**
  - Interactive SVG-based visualizations
  - Smooth animations and transitions
  - Zoom and pan capabilities
  - Export functionality

### Chart Types
- **Line Charts** - COVID-19 trends over time
- **Bar Charts** - Country-wise comparisons
- **Pie Charts** - Distribution analysis
- **Area Charts** - Cumulative data visualization
- **Custom D3 Charts** - Advanced interactive visualizations

## 📈 Data Sources

The application automatically ingests COVID-19 data from:

1. **JHU CSSE GitHub Repository** - Daily COVID-19 reports
2. **Our World in Data** - Comprehensive global COVID-19 dataset
3. **WHO API** - Official World Health Organization data

Data ingestion runs every 6 hours automatically.

## 🔧 Configuration

### Backend Environment Variables
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka cluster endpoints (kafka:29092)
- `HDFS_NAMENODE` - HDFS NameNode address (hdfs://namenode:9000)
- `SPARK_MASTER` - Spark master URL (spark://spark-master:7077)
- `HIVE_SERVER` - Hive server address (hive-server:10000)
- `CLUSTER_MODE` - Set to "hadoop" for Hadoop ecosystem

### Frontend Configuration
- `REACT_APP_API_BASE_URL` - Backend API endpoint (http://localhost:8082)

### Ports
- **3000** - React Frontend
- **9870** - HDFS NameNode Web UI
- **9000** - HDFS NameNode RPC
- **9864** - HDFS DataNode Web UI
- **9092** - Kafka (external)
- **29092** - Kafka (internal)
- **2181** - ZooKeeper
- **10000** - Hive Server
- **10002** - HiveServer2 Web UI
- **9083** - Hive Metastore
- **7077** - Spark Master
- **8080** - Spark Master Web UI
- **5432** - PostgreSQL
- **8082** - COVID-19 Tracker Application

## 🧪 Testing

### Test Real Data Pipeline
```bash
# Check if real data is being ingested from Our World in Data
docker-compose logs covid19-tracker | grep "Ingested"

# Test Kafka connectivity
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check WebSocket bridge connectivity
docker-compose logs kafka-websocket-bridge | grep "Connected"
```

### Test Frontend
```bash
# Test React app
npm test
npm run build
```

### Test Spark Processing
```bash
# Check Spark application status
curl http://localhost:8080

# View Spark application logs
docker-compose logs covid19-tracker | grep "Spark"
```

### Test HDFS
```bash
# Check HDFS status
docker-compose exec namenode hdfs dfsadmin -report

# List HDFS files
docker-compose exec namenode hdfs dfs -ls /
```

### Test Hive Analytics
```bash
# Connect to Hive
docker-compose exec hive-server beeline -u jdbc:hive2://localhost:10000

# Run sample queries
SHOW TABLES;
SELECT * FROM covid19_data LIMIT 10;
```

## 📊 Analytics

The system provides real-time and batch analytics:

### Real-time Analytics
- Live COVID-19 case tracking from Our World in Data
- Country-wise mortality rates
- Daily trend analysis
- Real-time data streaming via WebSocket

### Batch Analytics
- Historical trend analysis
- Top affected countries
- Mortality rate analysis
- Data quality metrics

### Frontend Analytics
- Interactive data exploration
- Real-time chart updates via WebSocket
- Filtered data views
- Export capabilities

## 🛠️ Development

### Project Structure
```
├── src/                          # Backend Java source
│   ├── main/java/com/covid19_tracker/
│   │   ├── model/               # Data models
│   │   ├── kafka/               # Kafka producer/consumer
│   │   ├── spark/               # Spark streaming jobs
│   │   ├── hive/                # Hive analytics
│   │   ├── simple/              # Simple data pipeline
│   │   └── ingestion/           # Data ingestion services
│   └── test/                    # Unit tests
├── src/                          # React frontend
│   ├── components/              # React components
│   │   ├── Dashboard.tsx        # Main dashboard
│   │   └── Dashboard.css        # Styling
│   ├── services/                # API services
│   │   ├── api.ts               # Backend communication
│   │   └── websocketService.ts  # WebSocket service
│   └── App.tsx                  # Main app component
├── kafka-websocket-bridge.js    # WebSocket bridge service
├── docker-compose.yml           # Docker services
├── Dockerfile                   # Backend container
└── README.md                    # This file
```

### Building
```bash
# Build backend with tests
./mvnw clean package

# Build backend without tests
./mvnw clean package -DskipTests

# Build frontend
npm install
npm run build
```

### Development Workflow
```bash
# Start all services including data pipeline
docker-compose up -d

# Start frontend in development mode
npm start

# Run tests
npm test

# Build for production
npm run build
```

## 🔍 Troubleshooting

### Common Issues

1. **HDFS services not starting**
   ```bash
   # Check HDFS logs
   docker-compose logs namenode
   docker-compose logs datanode
   
   # Restart HDFS services
   docker-compose restart namenode datanode
   ```

2. **Application not connecting to HDFS**
   ```bash
   # Check HDFS status
   docker-compose exec namenode hdfs dfsadmin -report
   
   # Check network connectivity
   docker-compose exec covid19-tracker nc -zv namenode 9000
   ```

3. **Application not connecting to Kafka**
   ```bash
   # Check Kafka status
   docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092
   
   # Check network connectivity
   docker-compose exec covid19-tracker nc -zv kafka 29092
   ```

4. **Frontend TypeScript errors**
   ```bash
   # Install missing dependencies
   npm install @types/react @types/d3
   
   # Clear TypeScript cache
   rm -rf node_modules/.cache
   npm start
   ```

5. **Platform-specific issues (Apple Silicon)**
   ```bash
   # HDFS images are emulated on Apple Silicon and may be slower
   # If you experience issues, consider:
   # - Increasing memory allocation in Docker Desktop
   # - Using a VM for HDFS services
   # - Using S3-compatible storage instead of HDFS
   ```

### Logs
```bash
# Application logs
docker-compose logs covid19-tracker

# HDFS logs
docker-compose logs namenode
docker-compose logs datanode

# Kafka logs
docker-compose logs kafka

# Frontend logs
npm start

# All logs
docker-compose logs
```

### Health Checks
```bash
# Check all service health
docker-compose ps

# Check specific service health
docker-compose exec namenode curl -f http://localhost:9870
docker-compose exec datanode curl -f http://localhost:9864

# Check frontend
curl -f http://localhost:3000
```

## 🐳 Docker Services

### Core Services
- **namenode** - HDFS NameNode (bde2020/hadoop-namenode:2.0.0-hadoop3.2.1-java8)
- **datanode** - HDFS DataNode (bde2020/hadoop-datanode:2.0.0-hadoop3.2.1-java8)
- **kafka** - Apache Kafka (confluentinc/cp-kafka:7.4.0)
- **zookeeper** - Apache ZooKeeper (confluentinc/cp-zookeeper:7.4.0)
- **spark-master** - Spark Master (bitnami/spark:3.5.0)
- **spark-worker** - Spark Worker (bitnami/spark:3.5.0)
- **postgres** - PostgreSQL (postgres:13)
- **hive-metastore** - Hive Metastore (apache/hive:3.1.3)
- **hive-server** - Hive Server (apache/hive:3.1.3)
- **covid19-tracker** - Main application (custom build)

### Volumes
- **hadoop_namenode** - HDFS NameNode data
- **hadoop_datanode** - HDFS DataNode data
- **postgres_data** - PostgreSQL data

## 🆕 Recent Updates

### TypeScript & D3.js Fixes
- Fixed D3 tickFormat type errors with proper function signatures
- Added explicit type annotations for D3 NumberValue and Date types
- Fixed .call(xAxis) errors with 'as any' type casting
- Added missing 'country' property to CancerPatientData interface
- Fixed percent undefined error in Pie chart labels
- Updated tsconfig.json for proper TypeScript configuration
- Installed missing type definitions for React and D3

### React Frontend
- Complete interactive dashboard with multiple chart types
- Custom D3.js visualizations with interactive features
- Real-time data filtering and comparison
- Responsive design for all screen sizes
- TypeScript for type-safe development

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📞 Support

For issues and questions:
- Check the troubleshooting section
- Review the logs
- Open an issue on GitHub
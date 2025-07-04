# COVID-19 Data Tracker - Big Data Edition

A comprehensive big data solution for tracking COVID-19 morbidity and mortality in cancer patients using **Cloudera CDH** - a complete Hadoop ecosystem.

## 🏗️ Architecture Overview

This project implements a complete big data pipeline using **Cloudera CDH 7.1.4**:

- **Cloudera Manager** - Cluster management and monitoring
- **Apache Kafka** - Real-time data streaming and message queuing
- **Apache Spark Streaming** - Real-time data processing and analytics
- **Apache Hive** - Data warehousing and batch analytics
- **HDFS** - Distributed file storage
- **Hue** - Web-based Hadoop user interface
- **Java** - Core application development

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
Analytics Dashboard
```

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 11+
- Maven (or use the included Maven wrapper)

### 1. Build the Application
```bash
./mvnw clean package -DskipTests
```

### 2. Start Cloudera CDH Cluster
```bash
docker-compose up -d
```

### 3. Monitor the Cluster
```bash
# Check service status
docker-compose ps

# View application logs
docker-compose logs covid19-tracker

# Monitor real-time logs
docker-compose logs -f covid19-tracker
```

### 4. Access Web Interfaces
- **Cloudera Manager**: http://localhost:7180
- **Hue (Hadoop UI)**: http://localhost:8888
- **HDFS Web UI**: http://localhost:50070
- **Spark Master**: http://localhost:8080
- **Spark History Server**: http://localhost:18080

## 📈 Data Sources

The application automatically ingests COVID-19 data from:

1. **JHU CSSE GitHub Repository** - Daily COVID-19 reports
2. **Our World in Data** - Comprehensive global COVID-19 dataset
3. **WHO API** - Official World Health Organization data

Data ingestion runs every 6 hours automatically.

## 🔧 Configuration

### Environment Variables
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka cluster endpoints
- `HDFS_NAMENODE` - HDFS NameNode address
- `SPARK_MASTER` - Spark master URL
- `HIVE_SERVER` - Hive server address
- `CLUSTER_MODE` - Set to "cloudera" for CDH cluster

### Ports
- **7180** - Cloudera Manager
- **8888** - Hue Web UI
- **8020** - HDFS NameNode
- **50070** - HDFS Web UI
- **9092** - Kafka
- **2181** - ZooKeeper
- **10000** - Hive Server
- **7077** - Spark Master
- **8080** - Spark Master Web UI
- **18080** - Spark History Server

## 🧪 Testing

### Test Data Ingestion
```bash
# Check if data is being ingested
docker-compose logs covid19-tracker | grep "Ingested"

# Test Kafka connectivity
docker-compose exec cloudera-cdh kafka-topics --list --bootstrap-server localhost:9092
```

### Test Spark Processing
```bash
# Check Spark application status
curl http://localhost:8080

# View Spark application logs
docker-compose logs covid19-tracker | grep "Spark"
```

### Test Hive Analytics
```bash
# Connect to Hive
docker-compose exec cloudera-cdh beeline -u jdbc:hive2://localhost:10000

# Run sample queries
SHOW TABLES;
SELECT * FROM covid19_data LIMIT 10;
```

## 📊 Analytics

The system provides real-time and batch analytics:

### Real-time Analytics
- Live COVID-19 case tracking
- Country-wise mortality rates
- Daily trend analysis
- Data source comparison

### Batch Analytics
- Historical trend analysis
- Top affected countries
- Mortality rate analysis
- Data quality metrics

## 🛠️ Development

### Project Structure
```
src/
├── main/java/com/covid19_tracker/
│   ├── model/           # Data models
│   ├── kafka/           # Kafka producer/consumer
│   ├── spark/           # Spark streaming jobs
│   ├── hive/            # Hive analytics
│   └── ingestion/       # Data ingestion services
└── test/                # Unit tests
```

### Building
```bash
# Build with tests
./mvnw clean package

# Build without tests
./mvnw clean package -DskipTests

# Run tests only
./mvnw test
```

## 🔍 Troubleshooting

### Common Issues

1. **Cloudera CDH not starting**
   ```bash
   # Check cluster logs
   docker-compose logs cloudera-cdh
   
   # Restart cluster
   docker-compose restart cloudera-cdh
   ```

2. **Application not connecting to Kafka**
   ```bash
   # Check Kafka status
   docker-compose exec cloudera-cdh kafka-topics --list --bootstrap-server localhost:9092
   
   # Check network connectivity
   docker-compose exec covid19-tracker nc -zv cloudera-cdh 9092
   ```

3. **HDFS not accessible**
   ```bash
   # Check HDFS status
   docker-compose exec cloudera-cdh hdfs dfsadmin -report
   
   # Check NameNode logs
   docker-compose logs cloudera-cdh | grep NameNode
   ```

### Logs
```bash
# Application logs
docker-compose logs covid19-tracker

# Cluster logs
docker-compose logs cloudera-cdh

# All logs
docker-compose logs
```

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
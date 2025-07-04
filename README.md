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
External APIs (JHU CSSE, WHO) 
    ↓
Kafka Producer (Data Ingestion)
    ↓
Kafka Topic (covid19-data)
    ↓
Spark Streaming (Real-time Processing)
    ↓
HDFS Storage + Hive Analytics
    ↓
Batch Analytics & Reporting
```

## 🚀 Features

### Real-time Data Processing
- **Kafka Producer**: Ingests COVID-19 data from external APIs (JHU CSSE, WHO)
- **Spark Streaming**: Processes data in real-time with 10-second batch intervals
- **Real-time Analytics**: Country-wise aggregation, mortality rate calculations, alerts

### Batch Analytics
- **Hive Tables**: Structured data storage with partitioning
- **SQL Analytics**: Complex queries for trend analysis and reporting
- **Data Warehousing**: Efficient storage and retrieval of historical data

### Data Quality & Monitoring
- **Data Validation**: Filters invalid or missing data
- **Alert System**: High mortality rate notifications (>5%)
- **Logging**: Comprehensive logging throughout the pipeline

## 📁 Project Structure

```
src/main/java/com/covid19_tracker/
├── model/
│   ├── Covid19Data.java          # COVID-19 data model
│   ├── CancerPatientData.java    # Cancer patient data model
│   └── Covid19CancerData.java    # Combined data model
├── kafka/
│   ├── Covid19DataProducer.java  # Kafka producer for COVID-19 data
│   └── Covid19DataConsumer.java  # Kafka consumer
├── ingestion/
│   └── Covid19DataIngestionService.java  # Data ingestion from APIs
├── spark/
│   └── Covid19StreamingJob.java  # Spark streaming processing
├── hive/
│   └── Covid19HiveService.java   # Hive analytics and data warehousing
└── Covid19DataTrackerApp.java    # Main application orchestrator
```

## 🛠️ Prerequisites

### Required Software
- **Java 11+**
- **Apache Maven 3.6+**
- **Docker & Docker Compose** (for Cloudera CDH cluster)
- **8GB+ RAM** (16GB+ recommended for optimal performance)

### System Requirements
- **Memory**: 8GB+ RAM (16GB+ for Cloudera CDH cluster)
- **Storage**: 50GB+ free space
- **Network**: Internet connection for API data ingestion

## 🐳 **Quick Start with Cloudera CDH Cluster**

The easiest way to test the complete system is using the provided **Cloudera CDH** setup:

### 1. **Build the Application**
```bash
# Build the COVID-19 Data Tracker application
./mvnw clean package -DskipTests
```

### 2. **Start the Cloudera CDH Cluster**
```bash
# Start the complete Cloudera CDH cluster
docker-compose up -d
```

This will:
- Start a complete **Cloudera CDH 7.1.4** cluster
- Initialize all Hadoop ecosystem services
- Set up the data pipeline

**⚠️ First startup may take 5-10 minutes as it downloads and initializes the full Cloudera CDH cluster.**

### 3. **Access Cloudera CDH Services**
- **Cloudera Manager**: http://localhost:7180 (admin/admin)
- **Hue Web UI**: http://localhost:8888
- **HDFS NameNode**: http://localhost:50070
- **Spark Master**: http://localhost:8080
- **Spark History**: http://localhost:18080

### 4. **Monitor the Application**
```bash
# View application logs
docker-compose logs -f covid19-tracker

# Monitor cluster health
docker-compose logs -f cloudera-manager

# Check service status
docker-compose ps
```

### 5. **Stop the Cluster**
```bash
docker-compose down
```

## ⚙️ Configuration

### Cloudera CDH Configuration
The cluster is pre-configured with:
- **CDH Version**: 7.1.4
- **Services**: HDFS, Spark, Kafka, Hive, Hue, ZooKeeper
- **Memory Allocation**: Optimized for development/testing

### Application Configuration
```properties
# Kafka broker settings
bootstrap.servers=cloudera-manager:9092
topic.name=covid19-data
group.id=covid19-tracker-group

# Spark streaming settings
spark.master=spark://cloudera-manager:7077
spark.app.name=COVID-19 Streaming Analytics
batch.interval=10 seconds
window.duration=5 minutes

# HDFS paths
hdfs.output.path=hdfs://cloudera-manager:8020/covid19/streaming/
hdfs.checkpoint.path=hdfs://cloudera-manager:8020/covid19/checkpoints/
hdfs.batch.path=hdfs://cloudera-manager:8020/covid19/batch/
```

## 🧪 **Testing Guide**

### **Individual Component Testing**
```bash
# Test application components
mvn exec:java -Dexec.mainClass="com.covid19_tracker.ingestion.Covid19DataIngestionService"
mvn exec:java -Dexec.mainClass="com.covid19_tracker.spark.Covid19StreamingJob"
mvn exec:java -Dexec.mainClass="com.covid19_tracker.hive.Covid19HiveService"

# Test complete pipeline
mvn exec:java -Dexec.mainClass="com.covid19_tracker.Covid19DataTrackerApp"
```

### **Testing with Docker**
```bash
# Test Kafka topic creation
docker-compose exec cloudera-manager kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 3 \
    --topic covid19-data

# Test HDFS
docker-compose exec cloudera-manager hdfs dfs -ls /

# Test Spark
docker-compose exec cloudera-manager spark-submit --version

# Test Hive
docker-compose exec cloudera-manager beeline -u jdbc:hive2://localhost:10000 -e "SHOW DATABASES;"
```

## 📈 Analytics Capabilities

### Real-time Analytics
- **Country-wise Case Aggregation**: Real-time totals by country
- **Mortality Rate Monitoring**: Instant calculation and alerting
- **Data Quality Metrics**: Validation and filtering statistics

### Batch Analytics
- **Top Countries by Cases**: Ranking and comparison
- **High Mortality Countries**: Risk assessment (>5% threshold)
- **Daily Trend Analysis**: Time-series analysis
- **Recovery Rate Analysis**: Patient outcome tracking

### Sample Queries (via Hue)

```sql
-- Top 10 countries by confirmed cases
SELECT country, total_cases, total_deaths, mortality_rate 
FROM covid19_analytics 
WHERE dt='2024-01-01' 
ORDER BY total_cases DESC 
LIMIT 10;

-- High mortality countries
SELECT country, mortality_rate, total_cases 
FROM covid19_analytics 
WHERE mortality_rate > 5.0 AND total_cases > 100 
ORDER BY mortality_rate DESC;

-- Daily trend analysis
SELECT date, SUM(confirmed_cases) as daily_cases, 
       SUM(deaths) as daily_deaths 
FROM covid19_batch 
GROUP BY date 
ORDER BY date DESC;
```

## 🔧 **Troubleshooting**

### Common Issues

**1. Cluster Startup Takes Too Long**
- First startup downloads ~4GB of Cloudera CDH images
- Ensure stable internet connection
- Check available disk space (50GB+ recommended)

**2. Memory Issues**
- Ensure 8GB+ RAM available
- Close other memory-intensive applications
- Consider increasing Docker memory limits

**3. Port Conflicts**
- Ensure ports 7180, 8888, 50070, 8080, 9092 are available
- Stop other services using these ports

**4. Service Not Starting**
- Check logs: `docker-compose logs cloudera-manager`
- Wait for Cloudera Manager to fully initialize
- Use Cloudera Manager web UI to start services manually

### Getting Help
- Check application logs: `docker-compose logs covid19-tracker`
- Monitor cluster health via Cloudera Manager
- Use Hue for data exploration and debugging

## 📊 Data Sources

### Primary Sources
- **JHU CSSE API**: Johns Hopkins University COVID-19 data
- **WHO API**: World Health Organization global data
- **Sample Data**: Generated test data for development

### Data Schema
```json
{
  "date": "2024-01-01",
  "country": "United States",
  "province": "California",
  "confirmedCases": 1000000,
  "deaths": 50000,
  "recovered": 800000,
  "activeCases": 150000,
  "latitude": 37.7749,
  "longitude": -122.4194,
  "dataSource": "JHU-CSSE",
  "lastUpdated": "2024-01-01"
}
```

## 🚨 Monitoring & Alerts

### Real-time Alerts
- **High Mortality Rate**: >5% mortality rate notifications
- **Data Quality Issues**: Missing or invalid data alerts
- **System Health**: Pipeline status monitoring

### Logging
- **Application Logs**: Comprehensive logging with SLF4J
- **Performance Metrics**: Processing time and throughput
- **Error Tracking**: Exception handling and reporting

## 🔒 Security Considerations

- **Data Privacy**: No PII (Personally Identifiable Information) stored
- **API Rate Limiting**: Respectful API usage with delays
- **Network Security**: Secure communication protocols
- **Access Control**: HDFS and Hive access management

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Johns Hopkins University CSSE for COVID-19 data
- World Health Organization for global health data
- Apache Software Foundation for open-source big data tools

## 📞 Support

For questions or issues:
- Create an issue in the repository
- Check the documentation
- Review the logs for troubleshooting

---

**Note**: This is a demonstration project for big data processing. For production use, additional security, monitoring, and scalability considerations should be implemented. 
# COVID-19 Data Tracker - Big Data Edition

A comprehensive big data solution for tracking COVID-19 morbidity and mortality in cancer patients using the Hadoop ecosystem.

## 🏗️ Architecture Overview

This project implements a complete big data pipeline using:

- **Apache Kafka** - Real-time data streaming and message queuing
- **Apache Spark Streaming** - Real-time data processing and analytics
- **Apache Hive** - Data warehousing and batch analytics
- **HDFS** - Distributed file storage
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
- **Apache Kafka 2.8+**
- **Apache Spark 3.0+**
- **Apache Hive 3.1+**
- **Hadoop HDFS 3.2+**

### System Requirements
- **Memory**: 8GB+ RAM
- **Storage**: 50GB+ free space
- **Network**: Internet connection for API data ingestion

## ⚙️ Configuration

### Kafka Configuration
```properties
# Kafka broker settings
bootstrap.servers=localhost:9092
topic.name=covid19-data
group.id=covid19-tracker-group
```

### Spark Configuration
```properties
# Spark streaming settings
spark.master=local[*]
spark.app.name=COVID-19 Streaming Analytics
batch.interval=10 seconds
window.duration=5 minutes
```

### HDFS Configuration
```properties
# HDFS paths
hdfs.output.path=hdfs://localhost:9000/covid19/streaming/
hdfs.checkpoint.path=hdfs://localhost:9000/covid19/checkpoints/
hdfs.batch.path=hdfs://localhost:9000/covid19/batch/
```

## 🚀 Quick Start

### 1. Build the Project
```bash
mvn clean compile
```

### 2. Start Required Services
```bash
# Start Kafka (ensure Zookeeper is running)
kafka-server-start.sh config/server.properties

# Start HDFS
start-dfs.sh

# Start Hive
hive --service metastore &
hive --service hiveserver2 &
```

### 3. Run the Application
```bash
# Run the main application
mvn exec:java -Dexec.mainClass="com.covid19_tracker.Covid19DataTrackerApp"
```

### 4. Monitor the Pipeline
```bash
# Check Kafka topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Monitor HDFS
hdfs dfs -ls /covid19/

# Query Hive tables
hive -e "SELECT * FROM covid19_analytics LIMIT 10;"
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

### Sample Queries

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

## 🔧 Development

### Adding New Data Sources
1. Extend `Covid19DataIngestionService` with new API endpoints
2. Update data models if needed
3. Modify Spark streaming logic for new data types

### Custom Analytics
1. Add new methods to `Covid19HiveService`
2. Create new Hive tables as needed
3. Update the main application orchestrator

### Testing
```bash
# Run unit tests
mvn test

# Test individual components
mvn exec:java -Dexec.mainClass="com.covid19_tracker.ingestion.Covid19DataIngestionService"
mvn exec:java -Dexec.mainClass="com.covid19_tracker.spark.Covid19StreamingJob"
mvn exec:java -Dexec.mainClass="com.covid19_tracker.hive.Covid19HiveService"
```

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
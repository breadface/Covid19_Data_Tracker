# COVID-19 Data Tracker - Big Data Edition

A comprehensive big data solution for tracking COVID-19 morbidity and mortality in cancer patients using the **Hadoop ecosystem** - a complete distributed data processing platform.

## 🏗️ Architecture Overview

This project implements a complete big data pipeline using individual **Hadoop ecosystem components**:

- **Apache Kafka** - Real-time data streaming and message queuing
- **Apache Spark Streaming** - Real-time data processing and analytics
- **Apache Hive** - Data warehousing and batch analytics
- **HDFS (Hadoop Distributed File System)** - Distributed file storage
- **PostgreSQL** - Metadata storage for Hive metastore
- **ZooKeeper** - Distributed coordination service
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
- **Note**: This setup is optimized for Apple Silicon (M1/M2) Macs but works on all platforms

### 1. Build the Application
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

### 3. Monitor the Services
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

### 4. Access Web Interfaces
- **HDFS NameNode Web UI**: http://localhost:9870
- **Spark Master**: http://localhost:8080
- **Hive Server**: http://localhost:10002 (HiveServer2 Web UI)

## 📈 Data Sources

The application automatically ingests COVID-19 data from:

1. **JHU CSSE GitHub Repository** - Daily COVID-19 reports
2. **Our World in Data** - Comprehensive global COVID-19 dataset
3. **WHO API** - Official World Health Organization data

Data ingestion runs every 6 hours automatically.

## 🔧 Configuration

### Environment Variables
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka cluster endpoints (kafka:29092)
- `HDFS_NAMENODE` - HDFS NameNode address (hdfs://namenode:9000)
- `SPARK_MASTER` - Spark master URL (spark://spark-master:7077)
- `HIVE_SERVER` - Hive server address (hive-server:10000)
- `CLUSTER_MODE` - Set to "hadoop" for Hadoop ecosystem

### Ports
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

### Test Data Ingestion
```bash
# Check if data is being ingested
docker-compose logs covid19-tracker | grep "Ingested"

# Test Kafka connectivity
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092
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

4. **Platform-specific issues (Apple Silicon)**
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
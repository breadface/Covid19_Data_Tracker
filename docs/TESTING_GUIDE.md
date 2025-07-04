# 🧪 COVID-19 Data Tracker - Testing Guide

This guide shows you how to test the COVID-19 Data Tracker in a Docker-based Hadoop cluster environment.

## 🚀 Quick Start Testing

### 1. **Prerequisites**
- Docker and Docker Compose installed
- At least 8GB RAM available
- Internet connection for data ingestion

### 2. **Start the Cluster**
```bash
# Run the setup script
./scripts/setup-cluster.sh
```

This will:
- Build the COVID-19 Data Tracker application
- Start all Hadoop ecosystem services
- Create necessary Kafka topics and HDFS directories
- Initialize the data pipeline

### 3. **Monitor the Services**

#### **HDFS NameNode Web UI**
- URL: http://localhost:9870
- Check: File system status, data nodes, file browser

#### **Spark Master Web UI**
- URL: http://localhost:8080
- Check: Application status, worker nodes, job monitoring

#### **Spark Worker Web UI**
- URL: http://localhost:8081
- Check: Worker status, task execution

## 📊 **Testing the Data Pipeline**

### **1. Check Service Status**
```bash
# View all running containers
docker ps

# Check specific service logs
docker logs kafka
docker logs namenode
docker logs spark-master
docker logs covid19-tracker
```

### **2. Test Kafka Data Ingestion**
```bash
# Connect to Kafka container
docker exec -it kafka bash

# List topics
kafka-topics --list --bootstrap-server localhost:9092

# Monitor messages in real-time
kafka-console-consumer --bootstrap-server localhost:9092 \
    --topic covid19-data --from-beginning
```

### **3. Test HDFS Storage**
```bash
# Connect to NameNode
docker exec -it namenode bash

# List HDFS directories
hdfs dfs -ls /covid19/

# Check streaming data
hdfs dfs -ls /covid19/streaming/

# View file contents
hdfs dfs -cat /covid19/streaming/batch-*/part-*
```

### **4. Test Hive Analytics**
```bash
# Connect to Hive server
docker exec -it hive-server bash

# Start Hive CLI
hive

# Create test table
CREATE TABLE test_covid19 (
    date STRING,
    country STRING,
    confirmed_cases INT,
    deaths INT
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

# Load sample data
LOAD DATA LOCAL INPATH '/tmp/sample_data.csv' INTO TABLE test_covid19;

# Run analytics query
SELECT country, SUM(confirmed_cases) as total_cases, 
       SUM(deaths) as total_deaths,
       (SUM(deaths) * 100.0 / SUM(confirmed_cases)) as mortality_rate
FROM test_covid19 
GROUP BY country 
ORDER BY total_cases DESC;
```

### **5. Test Spark Streaming**
```bash
# Monitor Spark application
curl http://localhost:8080

# Check streaming job status
docker logs covid19-tracker | grep "Spark Streaming"

# View real-time processing logs
docker logs -f covid19-tracker
```

## 🔍 **Testing Individual Components**

### **Test Data Ingestion Service**
```bash
# Run ingestion service test
docker exec -it covid19-tracker java -cp target/classes:target/dependency/* \
    com.covid19_tracker.ingestion.Covid19DataIngestionService
```

### **Test Kafka Producer/Consumer**
```bash
# Test producer
docker exec -it covid19-tracker java -cp target/classes:target/dependency/* \
    com.covid19_tracker.kafka.Covid19DataProducer

# Test consumer
docker exec -it covid19-tracker java -cp target/classes:target/dependency/* \
    com.covid19_tracker.kafka.Covid19DataConsumer
```

### **Test Spark Streaming Job**
```bash
# Run Spark streaming test
docker exec -it covid19-tracker java -cp target/classes:target/dependency/* \
    com.covid19_tracker.spark.Covid19StreamingJob
```

### **Test Hive Analytics**
```bash
# Run Hive service test
docker exec -it covid19-tracker java -cp target/classes:target/dependency/* \
    com.covid19_tracker.hive.Covid19HiveService
```

## 📈 **Performance Testing**

### **Load Testing**
```bash
# Generate high-volume test data
docker exec -it covid19-tracker java -cp target/classes:target/dependency/* \
    com.covid19_tracker.ingestion.Covid19DataIngestionService --load-test 1000
```

### **Stress Testing**
```bash
# Monitor system resources
docker stats

# Check memory usage
docker exec -it namenode free -h
docker exec -it spark-master free -h
```

## 🐛 **Troubleshooting**

### **Common Issues**

#### **1. Services Not Starting**
```bash
# Check Docker logs
docker-compose logs

# Restart specific service
docker-compose restart kafka
```

#### **2. Connection Issues**
```bash
# Test network connectivity
docker exec -it covid19-tracker ping kafka
docker exec -it covid19-tracker ping namenode
```

#### **3. HDFS Permission Issues**
```bash
# Fix HDFS permissions
docker exec -it namenode hdfs dfs -chmod -R 777 /covid19
```

#### **4. Kafka Topic Issues**
```bash
# Recreate Kafka topic
docker exec -it kafka kafka-topics --delete \
    --bootstrap-server localhost:9092 \
    --topic covid19-data

docker exec -it kafka kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic covid19-data
```

## 📋 **Test Scenarios**

### **Scenario 1: Basic Data Flow**
1. Start the cluster
2. Verify all services are running
3. Check data ingestion from APIs
4. Monitor Kafka messages
5. Verify HDFS storage
6. Check Hive analytics

### **Scenario 2: Real-time Processing**
1. Monitor Spark streaming logs
2. Check real-time analytics
3. Verify alert generation
4. Test data validation

### **Scenario 3: Batch Analytics**
1. Run periodic Hive analytics
2. Check aggregated results
3. Verify data partitioning
4. Test complex queries

### **Scenario 4: Error Handling**
1. Stop Kafka service
2. Check application behavior
3. Restart Kafka
4. Verify recovery

## 🎯 **Success Criteria**

### **Functional Tests**
- ✅ Data ingestion from external APIs
- ✅ Real-time processing with Spark Streaming
- ✅ Data storage in HDFS
- ✅ Batch analytics with Hive
- ✅ Alert generation for high mortality rates

### **Performance Tests**
- ✅ Processing latency < 10 seconds
- ✅ Data throughput > 1000 records/minute
- ✅ Memory usage < 4GB total
- ✅ CPU usage < 80% average

### **Integration Tests**
- ✅ All services communicate properly
- ✅ Data flows end-to-end
- ✅ Error recovery works
- ✅ Monitoring and logging functional

## 🧹 **Cleanup**

### **Stop the Cluster**
```bash
docker-compose down
```

### **Remove Data Volumes**
```bash
docker-compose down -v
```

### **Clean Build**
```bash
./mvnw clean
docker system prune -f
```

## 📞 **Support**

If you encounter issues:
1. Check the logs: `docker-compose logs`
2. Verify service status: `docker-compose ps`
3. Check resource usage: `docker stats`
4. Review this testing guide
5. Check the main README.md for additional information 
#!/bin/bash

echo "Starting COVID-19 Live Data Pipeline..."

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Start the Spring Boot application with live data ingestion
echo "Starting Spring Boot application with live data ingestion..."
java -jar target/Covid19_Data_Tracker-2.0.0.jar \
  --spring.profiles.active=live \
  --covid19.ingestion.enabled=true \
  --covid19.ingestion.interval=300 \
  --kafka.bootstrap-servers=localhost:9092

echo "Live data pipeline started!"
echo "The application will now:"
echo "1. Fetch real COVID-19 data from JHU CSSE and Our World in Data"
echo "2. Process data through Spark Streaming"
echo "3. Send processed data to Kafka topics"
echo "4. Your WebSocket bridge will receive real data from Our World in Data" 
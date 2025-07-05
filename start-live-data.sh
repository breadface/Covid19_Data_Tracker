#!/bin/bash

echo "Starting COVID-19 Live Data Pipeline..."

# Build the application
echo "Building the application..."
mvn clean compile

# Copy dependencies if not already done
echo "Copying dependencies..."
mvn dependency:copy-dependencies

# Start the live data pipeline
echo "Starting live data ingestion service..."
java -cp "target/classes:target/dependency/*" com.covid19_tracker.LiveDataPipelineApp

echo "Live data pipeline started!"
echo "The service will now fetch real COVID-19 data from:"
echo "- JHU CSSE (Johns Hopkins University)"
echo "- Our World in Data"
echo "- WHO API"
echo ""
echo "Data will be sent to Kafka topics every 6 hours"
echo "Your WebSocket bridge will receive real data from Our World in Data" 
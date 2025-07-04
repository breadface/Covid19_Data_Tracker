#!/bin/bash

# COVID-19 Data Tracker - Hadoop Cluster Setup Script
# This script sets up a complete Hadoop ecosystem using Docker

set -e

echo "🚀 Setting up COVID-19 Data Tracker Hadoop Cluster..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create necessary directories
echo "📁 Creating directories..."
mkdir -p logs
mkdir -p data

# Build the application
echo "🔨 Building COVID-19 Data Tracker application..."
./mvnw clean package -DskipTests

# Start the cluster
echo "🐳 Starting Hadoop cluster with Docker Compose..."
cd docker
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check service status
echo "🔍 Checking service status..."
docker-compose ps

# Create Kafka topic
echo "📢 Creating Kafka topic..."
docker-compose exec kafka kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic covid19-data

# Initialize HDFS directories
echo "🗂️ Initializing HDFS directories..."
docker-compose exec namenode hdfs dfs -mkdir -p /covid19/streaming
docker-compose exec namenode hdfs dfs -mkdir -p /covid19/batch
docker-compose exec namenode hdfs dfs -mkdir -p /covid19/checkpoints
docker-compose exec namenode hdfs dfs -chmod -R 777 /covid19

echo "✅ Cluster setup complete!"
echo ""
echo "🌐 Service URLs:"
echo "  - HDFS NameNode: http://localhost:9870"
echo "  - Spark Master: http://localhost:8080"
echo "  - Spark Worker: http://localhost:8081"
echo "  - Hive Server: localhost:10000"
echo "  - Kafka: localhost:9092"
echo ""
echo "📊 To monitor the application:"
echo "  - Check logs: docker-compose logs -f covid19-tracker"
echo "  - View HDFS: http://localhost:9870"
echo "  - Monitor Spark: http://localhost:8080"
echo ""
echo "🛑 To stop the cluster:"
echo "  docker-compose down" 
#!/bin/bash

# Quick Test Script for COVID-19 Data Tracker
# This script performs basic functionality tests

set -e

echo "🧪 Quick Test for COVID-19 Data Tracker"

# Check if cluster is running
if ! docker ps | grep -q "covid19-tracker"; then
    echo "❌ COVID-19 Tracker is not running. Start the cluster first:"
    echo "   ./scripts/setup-cluster.sh"
    exit 1
fi

echo "✅ Cluster is running"

# Test 1: Check Kafka
echo "📢 Testing Kafka..."
if docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 | grep -q "covid19-data"; then
    echo "✅ Kafka topic exists"
else
    echo "❌ Kafka topic not found"
fi

# Test 2: Check HDFS
echo "🗂️ Testing HDFS..."
if docker exec namenode hdfs dfs -test -d /covid19; then
    echo "✅ HDFS directories exist"
else
    echo "❌ HDFS directories not found"
fi

# Test 3: Check Spark
echo "⚡ Testing Spark..."
if curl -s http://localhost:8080 > /dev/null; then
    echo "✅ Spark Master is accessible"
else
    echo "❌ Spark Master not accessible"
fi

# Test 4: Check Hive
echo "🐝 Testing Hive..."
if docker exec hive-server hive -e "SHOW TABLES;" > /dev/null 2>&1; then
    echo "✅ Hive is accessible"
else
    echo "❌ Hive not accessible"
fi

# Test 5: Check Application Logs
echo "📋 Checking application logs..."
if docker logs covid19-tracker 2>&1 | grep -q "COVID-19 Data Tracker"; then
    echo "✅ Application is running"
else
    echo "❌ Application not running properly"
fi

echo ""
echo "🎯 Quick Test Summary:"
echo "  - Kafka: $(docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 | grep -c 'covid19-data') topics"
echo "  - HDFS: $(docker exec namenode hdfs dfs -count /covid19 | awk '{print $2}') directories"
echo "  - Spark: $(curl -s http://localhost:8080 | grep -c 'worker' || echo '0') workers"
echo "  - Application: $(docker logs covid19-tracker 2>&1 | grep -c 'INFO' || echo '0') log entries"

echo ""
echo "🌐 Service URLs:"
echo "  - HDFS: http://localhost:9870"
echo "  - Spark: http://localhost:8080"
echo "  - Application Logs: docker logs -f covid19-tracker"

echo ""
echo "✅ Quick test completed!" 
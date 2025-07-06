#!/bin/bash

echo "Starting COVID-19 Data Tracker Application..."

# Wait for HDFS NameNode with timeout
echo "Checking HDFS NameNode availability..."
timeout 60 bash -c 'until nc -z namenode 9000; do echo "Waiting for HDFS NameNode..."; sleep 5; done' || echo "Warning: HDFS NameNode not available, continuing anyway"

# Wait for HDFS DataNode with timeout  
echo "Checking HDFS DataNode availability..."
timeout 60 bash -c 'until nc -z datanode 9864; do echo "Waiting for HDFS DataNode..."; sleep 5; done' || echo "Warning: HDFS DataNode not available, continuing anyway"

# Wait for Hive Server with timeout
echo "Checking Hive Server availability..."
timeout 120 bash -c 'until nc -z hive-server 10000; do echo "Waiting for Hive Server..."; sleep 10; done' || echo "Warning: Hive Server not available, continuing anyway"

# Set Java options to avoid Tomcat logging issues
export JAVA_OPTS="-Djava.util.logging.config.file=/dev/null -Dorg.apache.juli.logging.LogFactory=org.apache.juli.logging.LogFactory"

echo "Starting Spring Boot application..."
java $JAVA_OPTS -jar /app/app.jar 
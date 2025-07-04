#!/bin/bash

echo "📊 COVID-19 Data Tracker - Real-time Monitoring"
echo "==============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    if [ "$status" = "SUCCESS" ]; then
        echo -e "${GREEN}✅ $message${NC}"
    elif [ "$status" = "ERROR" ]; then
        echo -e "${RED}❌ $message${NC}"
    elif [ "$status" = "INFO" ]; then
        echo -e "${BLUE}ℹ️  $message${NC}"
    elif [ "$status" = "WARNING" ]; then
        echo -e "${YELLOW}⚠️  $message${NC}"
    elif [ "$status" = "DATA" ]; then
        echo -e "${CYAN}📊 $message${NC}"
    fi
}

# Function to show current timestamp
show_timestamp() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC}"
}

# Function to monitor Kafka topics
monitor_kafka() {
    echo ""
    print_status "INFO" "Kafka Topics Status:"
    docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092 2>/dev/null | while read topic; do
        if [ ! -z "$topic" ]; then
            print_status "DATA" "Topic: $topic"
        fi
    done
}

# Function to monitor HDFS data
monitor_hdfs() {
    echo ""
    print_status "INFO" "HDFS Data Status:"
    
    # Check if covid19-data directory exists
    if docker-compose exec namenode hdfs dfs -test -d /covid19-data 2>/dev/null; then
        print_status "SUCCESS" "COVID-19 data directory exists"
        
        # Show directory contents
        echo "Directory contents:"
        docker-compose exec namenode hdfs dfs -ls /covid19-data 2>/dev/null | head -10 || echo "No files found"
    else
        print_status "WARNING" "COVID-19 data directory not found"
    fi
    
    # Show HDFS usage
    echo ""
    print_status "INFO" "HDFS Usage:"
    docker-compose exec namenode hdfs dfsadmin -report 2>/dev/null | grep -E "Configured Capacity|DFS Used|DFS Used%" | head -3
}

# Function to monitor application logs
monitor_logs() {
    echo ""
    print_status "INFO" "Recent Application Activity:"
    
    # Get recent logs and highlight important events
    docker-compose logs covid19-tracker --tail=10 2>/dev/null | while IFS= read -r line; do
        if echo "$line" | grep -q "ERROR\|Exception"; then
            echo -e "${RED}❌ $line${NC}"
        elif echo "$line" | grep -q "Processing\|Ingested\|Stored"; then
            echo -e "${GREEN}✅ $line${NC}"
        elif echo "$line" | grep -q "WARN\|Warning"; then
            echo -e "${YELLOW}⚠️  $line${NC}"
        else
            echo "$line"
        fi
    done
}

# Function to monitor service health
monitor_health() {
    echo ""
    print_status "INFO" "Service Health Status:"
    
    # Check each service
    services=("namenode" "datanode" "kafka" "spark-master" "spark-worker")
    
    for service in "${services[@]}"; do
        if docker-compose ps | grep -q "$service.*Up"; then
            print_status "SUCCESS" "$service: Running"
        else
            print_status "ERROR" "$service: Not running"
        fi
    done
}

# Function to show real-time data flow
monitor_data_flow() {
    echo ""
    print_status "INFO" "Data Flow Status:"
    
    # Check Kafka message count
    echo "Kafka messages in covid19-data topic:"
    docker-compose exec kafka kafka-run-class kafka.tools.GetOffsetShell --bootstrap-server localhost:9092 --topic covid19-data --time -1 2>/dev/null | while read line; do
        if [ ! -z "$line" ]; then
            print_status "DATA" "Messages: $line"
        fi
    done
    
    # Check HDFS file count
    echo ""
    echo "HDFS file count in covid19-data:"
    file_count=$(docker-compose exec namenode hdfs dfs -count /covid19-data 2>/dev/null | awk '{print $2}' || echo "0")
    print_status "DATA" "Files: $file_count"
}

# Main monitoring loop
main() {
    clear
    echo "🚀 COVID-19 Data Tracker - Real-time Monitoring Dashboard"
    echo "========================================================="
    echo "Press Ctrl+C to stop monitoring"
    echo ""
    
    while true; do
        show_timestamp
        monitor_health
        monitor_kafka
        monitor_hdfs
        monitor_data_flow
        monitor_logs
        
        echo ""
        echo "----------------------------------------"
        echo "Refreshing in 30 seconds... (Press Ctrl+C to stop)"
        echo ""
        
        sleep 30
        clear
        echo "🚀 COVID-19 Data Tracker - Real-time Monitoring Dashboard"
        echo "========================================================="
        echo "Press Ctrl+C to stop monitoring"
        echo ""
    done
}

# Check if user wants continuous monitoring or single snapshot
if [ "$1" = "--continuous" ] || [ "$1" = "-c" ]; then
    main
else
    # Single snapshot
    show_timestamp
    monitor_health
    monitor_kafka
    monitor_hdfs
    monitor_data_flow
    monitor_logs
    
    echo ""
    print_status "INFO" "For continuous monitoring, run: $0 --continuous"
fi 
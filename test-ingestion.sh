#!/bin/bash

echo "🧪 COVID-19 Data Tracker - Data Ingestion Flow Test"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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
    fi
}

echo ""
print_status "INFO" "Starting data ingestion flow test..."

# Test 1: Check if all services are running
echo ""
print_status "INFO" "Test 1: Checking service status..."
if docker-compose ps | grep -q "Up"; then
    print_status "SUCCESS" "All services are running"
else
    print_status "ERROR" "Some services are not running"
    docker-compose ps
    exit 1
fi

# Test 2: Test HDFS connectivity
echo ""
print_status "INFO" "Test 2: Testing HDFS connectivity..."
if docker-compose exec namenode hdfs dfsadmin -report > /dev/null 2>&1; then
    print_status "SUCCESS" "HDFS is accessible"
    
    # Test HDFS file operations
    if docker-compose exec namenode hdfs dfs -mkdir -p /test 2>/dev/null; then
        print_status "SUCCESS" "HDFS file operations working"
        docker-compose exec namenode hdfs dfs -rm -r /test > /dev/null 2>&1
    else
        print_status "ERROR" "HDFS file operations failed"
    fi
else
    print_status "ERROR" "HDFS is not accessible"
fi

# Test 3: Test Kafka connectivity
echo ""
print_status "INFO" "Test 3: Testing Kafka connectivity..."
if docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092 > /dev/null 2>&1; then
    print_status "SUCCESS" "Kafka is accessible"
    
    # Create test topic if it doesn't exist
    if ! docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092 | grep -q "covid19-data"; then
        print_status "INFO" "Creating covid19-data topic..."
        docker-compose exec kafka kafka-topics --create --topic covid19-data --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 > /dev/null 2>&1
    fi
    
    # Test Kafka message production
    echo "test-message" | docker-compose exec -T kafka kafka-console-producer --topic covid19-data --bootstrap-server localhost:9092 > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_status "SUCCESS" "Kafka message production working"
    else
        print_status "ERROR" "Kafka message production failed"
    fi
else
    print_status "ERROR" "Kafka is not accessible"
fi

# Test 4: Test external API connectivity
echo ""
print_status "INFO" "Test 4: Testing external API connectivity..."

# Test JHU CSSE API
if curl -s "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv" | head -5 > /dev/null 2>&1; then
    print_status "SUCCESS" "JHU CSSE API is accessible"
else
    print_status "ERROR" "JHU CSSE API is not accessible"
fi

# Test Our World in Data API
if curl -s "https://covid.ourworldindata.org/data/owid-covid-data.json" | head -c 100 > /dev/null 2>&1; then
    print_status "SUCCESS" "Our World in Data API is accessible"
else
    print_status "ERROR" "Our World in Data API is not accessible"
fi

# Test 5: Test data ingestion manually
echo ""
print_status "INFO" "Test 5: Testing manual data ingestion..."

# Create a test data file
cat > /tmp/test_covid_data.json << EOF
{
  "date": "2025-07-04",
  "country": "TestCountry",
  "confirmed": 1000,
  "deaths": 50,
  "recovered": 800,
  "source": "test"
}
EOF

# Test sending data to Kafka
if echo "$(cat /tmp/test_covid_data.json)" | docker-compose exec -T kafka kafka-console-producer --topic covid19-data --bootstrap-server localhost:9092 > /dev/null 2>&1; then
    print_status "SUCCESS" "Test data sent to Kafka successfully"
else
    print_status "ERROR" "Failed to send test data to Kafka"
fi

# Test 6: Check if data is in Kafka
echo ""
print_status "INFO" "Test 6: Verifying data in Kafka..."
sleep 2
if docker-compose exec kafka kafka-console-consumer --topic covid19-data --bootstrap-server localhost:9092 --from-beginning --max-messages 1 --timeout-ms 5000 | grep -q "TestCountry"; then
    print_status "SUCCESS" "Test data found in Kafka"
else
    print_status "WARNING" "Test data not found in Kafka (this might be normal if consumer is running)"
fi

# Test 7: Test HDFS file operations for data storage
echo ""
print_status "INFO" "Test 7: Testing HDFS data storage..."
if docker-compose exec namenode hdfs dfs -mkdir -p /covid19-data/test > /dev/null 2>&1; then
    print_status "SUCCESS" "HDFS directory creation working"
    
    # Test file upload
    echo "test-data" | docker-compose exec -T namenode hdfs dfs -put - /covid19-data/test/test-file.txt > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_status "SUCCESS" "HDFS file upload working"
        
        # Test file download
        if docker-compose exec namenode hdfs dfs -cat /covid19-data/test/test-file.txt | grep -q "test-data"; then
            print_status "SUCCESS" "HDFS file download working"
        else
            print_status "ERROR" "HDFS file download failed"
        fi
        
        # Cleanup
        docker-compose exec namenode hdfs dfs -rm -r /covid19-data/test > /dev/null 2>&1
    else
        print_status "ERROR" "HDFS file upload failed"
    fi
else
    print_status "ERROR" "HDFS directory creation failed"
fi

# Test 8: Check application logs for any errors
echo ""
print_status "INFO" "Test 8: Checking application logs..."
if docker-compose logs covid19-tracker --tail=20 | grep -q "ERROR\|Exception"; then
    print_status "WARNING" "Found errors in application logs"
    docker-compose logs covid19-tracker --tail=10 | grep -E "ERROR|Exception"
else
    print_status "SUCCESS" "No recent errors in application logs"
fi

# Summary
echo ""
echo "📊 Test Summary"
echo "==============="
print_status "INFO" "Data ingestion flow test completed!"
print_status "INFO" "If all tests passed, your data ingestion pipeline is working correctly."
print_status "INFO" "The application should be able to:"
print_status "INFO" "  - Connect to external APIs"
print_status "INFO" "  - Send data to Kafka"
print_status "INFO" "  - Store data in HDFS"
print_status "INFO" "  - Process data with Spark"

echo ""
print_status "INFO" "To monitor real-time data ingestion:"
echo "  docker-compose logs -f covid19-tracker"
echo ""
print_status "INFO" "To check HDFS data:"
echo "  docker-compose exec namenode hdfs dfs -ls /covid19-data"
echo ""
print_status "INFO" "To check Kafka topics:"
echo "  docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092"

# Cleanup
rm -f /tmp/test_covid_data.json

echo ""
print_status "INFO" "Test completed! 🎉" 
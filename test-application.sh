#!/bin/bash

# Test script for COVID-19 Data Tracker
echo "🧪 Testing COVID-19 Data Tracker Application"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local endpoint=$1
    local description=$2
    
    echo -e "\n${YELLOW}Testing: $description${NC}"
    echo "Endpoint: $endpoint"
    
    response=$(curl -s -w "%{http_code}" "$endpoint")
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✅ Success (HTTP $http_code)${NC}"
        echo "Response: $body" | head -c 200
        echo "..."
    else
        echo -e "${RED}❌ Failed (HTTP $http_code)${NC}"
        echo "Response: $body"
    fi
}

# Wait for services to start
echo "⏳ Waiting for services to start..."
sleep 30

# Test health endpoint
test_endpoint "http://localhost:8080/api/health" "Health Check"

# Test COVID-19 data endpoints
test_endpoint "http://localhost:8080/api/covid19/latest" "Latest COVID-19 Data"
test_endpoint "http://localhost:8080/api/covid19/summary" "COVID-19 Summary"

# Test cancer patient endpoints
test_endpoint "http://localhost:8080/api/cancer-patients" "Cancer Patient Data"
test_endpoint "http://localhost:8080/api/cancer-patients/covid-impact" "COVID Impact by Cancer Type"

# Test mortality analysis endpoints
test_endpoint "http://localhost:8080/api/mortality-analysis" "Mortality Analysis"

# Test data ingestion (POST request)
echo -e "\n${YELLOW}Testing: Data Ingestion${NC}"
echo "Endpoint: POST /api/ingest"

response=$(curl -s -w "%{http_code}" -X POST "http://localhost:8080/api/ingest")
http_code="${response: -3}"
body="${response%???}"

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ Data Ingestion Success (HTTP $http_code)${NC}"
    echo "Response: $body"
else
    echo -e "${RED}❌ Data Ingestion Failed (HTTP $http_code)${NC}"
    echo "Response: $body"
fi

# Test frontend
echo -e "\n${YELLOW}Testing: Frontend${NC}"
echo "Endpoint: http://localhost:3000"

response=$(curl -s -w "%{http_code}" "http://localhost:3000")
http_code="${response: -3}"

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ Frontend Success (HTTP $http_code)${NC}"
else
    echo -e "${RED}❌ Frontend Failed (HTTP $http_code)${NC}"
fi

# Test HDFS
echo -e "\n${YELLOW}Testing: HDFS NameNode${NC}"
echo "Endpoint: http://localhost:9870"

response=$(curl -s -w "%{http_code}" "http://localhost:9870")
http_code="${response: -3}"

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ HDFS NameNode Success (HTTP $http_code)${NC}"
else
    echo -e "${RED}❌ HDFS NameNode Failed (HTTP $http_code)${NC}"
fi

echo -e "\n${GREEN}🎉 Testing completed!${NC}"
echo -e "\n📊 Access the applications:"
echo -e "   Frontend: ${GREEN}http://localhost:3000${NC}"
echo -e "   Backend API: ${GREEN}http://localhost:8080/api${NC}"
echo -e "   HDFS NameNode: ${GREEN}http://localhost:9870${NC}"
echo -e "   H2 Console: ${GREEN}http://localhost:8080/h2-console${NC}" 
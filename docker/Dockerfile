FROM openjdk:11-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    netcat \
    && rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create logs directory
RUN mkdir -p /app/logs

# Create startup script
RUN echo '#!/bin/bash\n\
echo "Waiting for Kafka..."\n\
while ! nc -z kafka 29092; do\n\
  sleep 1\n\
done\n\
echo "Kafka is ready!"\n\
\n\
echo "Waiting for HDFS..."\n\
while ! nc -z namenode 9870; do\n\
  sleep 1\n\
done\n\
echo "HDFS is ready!"\n\
\n\
echo "Waiting for Hive..."\n\
while ! nc -z hive-server 10000; do\n\
  sleep 1\n\
done\n\
echo "Hive is ready!"\n\
\n\
echo "Starting COVID-19 Data Tracker..."\n\
java -cp target/classes:target/dependency/* com.covid19_tracker.Covid19DataTrackerApp\n\
' > /app/start.sh && chmod +x /app/start.sh

# Expose port
EXPOSE 8082

# Set entrypoint
ENTRYPOINT ["/app/start.sh"] 
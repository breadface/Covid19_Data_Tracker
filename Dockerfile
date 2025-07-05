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
RUN ./mvnw clean package -DskipTests \
    && cp target/Covid19_Data_Tracker-2.0.0.jar /app/app.jar

# Simple verification that the JAR was created
RUN ls -la /app/app.jar && echo "JAR file created successfully"

# Create logs directory
RUN mkdir -p /app/logs

# Create startup script
RUN echo '#!/bin/bash\n\
\n\
echo "Waiting for Hadoop ecosystem services..."\n\
\n\
echo "Waiting for HDFS NameNode..."\n\
while ! nc -z namenode 9000; do\n\
  echo "Waiting for HDFS NameNode..."\n\
  sleep 10\n\
done\n\
echo "HDFS NameNode is ready!"\n\
\n\
echo "Waiting for HDFS DataNode..."\n\
while ! nc -z datanode 9864; do\n\
  echo "Waiting for HDFS DataNode..."\n\
  sleep 5\n\
done\n\
echo "HDFS DataNode is ready!"\n\
\n\
echo "Waiting for Kafka..."\n\
while ! nc -z kafka 29092; do\n\
  echo "Waiting for Kafka..."\n\
  sleep 5\n\
done\n\
echo "Kafka is ready!"\n\
\n\
echo "Waiting for Spark Master..."\n\
while ! nc -z spark-master 7077; do\n\
  echo "Waiting for Spark Master..."\n\
  sleep 5\n\
done\n\
echo "Spark Master is ready!"\n\
\n\
echo "Core Hadoop ecosystem services are ready!"\n\
echo "Starting COVID-19 Data Tracker Application..."\n\
\n\
java -Dslf4j.provider=org.slf4j.simple.SimpleServiceProvider -Dlog4j2.disable.jmx=true -Dlog4j2.disable.web=true -Dlog4j2.skipJansi=true -jar /app/app.jar\n\
' > /app/start.sh && chmod +x /app/start.sh

# Expose port
EXPOSE 8082

# Start the application
CMD ["/app/start.sh"] 
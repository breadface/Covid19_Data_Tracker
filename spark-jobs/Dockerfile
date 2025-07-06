FROM maven:3.8.6-eclipse-temurin-11 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
# Force rebuild by adding timestamp
RUN echo "Build timestamp: $(date)" && mvn clean package -DskipTests

FROM bitnami/spark:3.5.0

USER root

# Copy the built JAR
COPY --from=build /app/target/spark-jobs-1.0.0.jar app.jar

# Copy the run script
COPY run-covid-job.sh /opt/spark/run-covid-job.sh
RUN chmod +x /opt/spark/run-covid-job.sh

# Keep container running
CMD ["tail", "-f", "/dev/null"] 
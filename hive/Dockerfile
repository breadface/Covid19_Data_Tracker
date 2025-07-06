FROM apache/hive:3.1.3

# Install necessary tools
USER root
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    netcat \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Download PostgreSQL JDBC driver
RUN wget -O /opt/hive/lib/postgresql-42.6.0.jar \
    https://jdbc.postgresql.org/download/postgresql-42.6.0.jar

# Copy custom entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Copy hive configuration
COPY hive-site.xml /opt/hive/conf/hive-site.xml

# Switch back to hive user
USER hive

# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"] 
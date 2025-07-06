#!/bin/bash
set -e

echo "Starting Hive Metastore initialization..."

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
for i in {1..30}; do
    if nc -z postgres 5432; then
        echo "PostgreSQL is ready!"
        break
    fi
    echo "PostgreSQL is not ready yet. Waiting... (attempt $i/30)"
    sleep 2
done

# Try to initialize schema (it will fail gracefully if already exists)
echo "Attempting to initialize Hive metastore schema..."
if /opt/hive/bin/schematool -dbType postgres -initSchema -verbose; then
    echo "Hive metastore schema initialized successfully!"
else
    echo "Schema initialization completed (may have already existed)"
fi

# Start the metastore service
echo "Starting Hive metastore service..."
exec /opt/hive/bin/hive --service metastore 
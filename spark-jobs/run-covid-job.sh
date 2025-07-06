#!/bin/bash

echo "Starting Covid19 Data Processing Spark Job..."

spark-submit \
  --class com.covid19_tracker.spark.Covid19DataProcessor \
  --master spark://spark-master:7077 \
  --deploy-mode client \
  --conf spark.sql.warehouse.dir=/user/hive/warehouse \
  --conf spark.hadoop.hive.metastore.uris=thrift://hive-metastore:9083 \
  --conf spark.hadoop.fs.defaultFS=hdfs://namenode:9000 \
  --conf spark.executor.memory=2g \
  --conf spark.driver.memory=1g \
  --conf spark.executor.cores=2 \
  --conf spark.sql.adaptive.enabled=true \
  --conf spark.sql.adaptive.coalescePartitions.enabled=true \
  /opt/bitnami/spark/app.jar

echo "Spark job completed!" 
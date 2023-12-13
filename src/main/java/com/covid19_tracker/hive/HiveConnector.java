package com.covid19_tracker.hive;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.hive.HiveContext;

import java.util.Arrays;

public class HiveConnector {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("SparkHiveIntegrationExample");
        try (JavaSparkContext sparkContext = new JavaSparkContext(sparkConf)) {

            // Replace "your/input/path" with the actual path to your historical data
            String inputPath = "your/input/path";

            // Create a HiveContext
            HiveContext hiveContext = new HiveContext(sparkContext.sc());

            // Read historical data from text file
            JavaRDD<String> rawData = sparkContext.textFile(inputPath);

            // Process and clean the data (Example: Split each line into fields)
            JavaRDD<String[]> cleanedData = rawData.map(line -> line.split(","));

            // Create a DataFrame from the cleaned data
            Class<?> yourSchema = null;

            Dataset<Row> dataFrame = hiveContext.createDataFrame(cleanedData.map(fields -> RowFactory.create((Object[]) fields)), yourSchema);

            // Store the DataFrame in Hive (replace "your_table" with the actual table name)
            dataFrame.write().mode(SaveMode.Overwrite).saveAsTable("your_table");

            // Stop Spark context
            sparkContext.stop();
        }
    }
}
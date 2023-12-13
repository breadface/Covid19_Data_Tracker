package com.covid19_tracker.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;

public class SparkBatchJob {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("SparkBatchProcessingExample");
        try (JavaSparkContext sparkContext = new JavaSparkContext(sparkConf)) {

            // Replace "your/input/path" with the actual path to your historical data
            String inputPath = "your/input/path";

            // Read historical data from text file
            JavaRDD<String> rawData = sparkContext.textFile(inputPath);

            // Process and clean the data (Example: Split each line into fields)
            JavaRDD<String[]> cleanedData = rawData.map(line -> line.split(","));

            // Perform analytics tasks
            long totalRecords = cleanedData.count();
            System.out.println("Total records: " + totalRecords);

            // Example: Count the occurrences of each field in the dataset
            cleanedData
                    .flatMap(fields -> Arrays.asList(fields).iterator())
                    .countByValue()
                    .forEach((field, count) -> System.out.println("Field: " + field + ", Count: " + count));

            // Stop Spark context
            sparkContext.stop();
        }
    }
}

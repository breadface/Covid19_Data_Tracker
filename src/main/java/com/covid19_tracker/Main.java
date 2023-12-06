package com.covid19_tracker;

import com.covid19_tracker.flume.CsvFlumeSource;
import org.apache.flume.Context;
import org.apache.flume.node.Application;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // Configure and start Flume
        startFlume();

        // Configure and start Spark Streaming
        startSparkStreaming();
    }

    private static void startFlume() {
        // Create a Flume source
        CsvFlumeSource flumeSource = new CsvFlumeSource();
        Context context = new Context();
        context.put("csvFilePath", "/path/to/your/csv/file.csv");
        flumeSource.configure(context);

        // Start the Flume agent
        Application flumeApplication = new Application();
        flumeApplication.handleConfigurationEvent();
        flumeApplication.start();

        // TODO: Add logic to stop Flume when needed
    }

    private static void startSparkStreaming() {
        // Create a Spark Streaming context
        SparkConf sparkConf = new SparkConf().setAppName("Covid19Tracker");
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, new Duration(5000)); // Adjust the batch interval

        // Create a Flume stream
        JavaDStream<SparkFlumeEvent> flumeStream = FlumeUtils.createStream(streamingContext, "localhost", 41414);

        // Process the Flume stream (example: count the number of events in each batch)
        flumeStream.foreachRDD((JavaRDD<SparkFlumeEvent> rdd) -> {
            long count = rdd.count();
            System.out.println("Number of events in this batch: " + count);
        });

        // Start the Spark Streaming context
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}

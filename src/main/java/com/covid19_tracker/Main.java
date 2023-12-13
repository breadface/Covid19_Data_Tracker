package com.covid19_tracker;

import com.covid19_tracker.flume.CsvFlumeSource;
import com.covid19_tracker.flume.CustomFlumeReceiver;
import org.apache.flume.Context;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
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
        // The Application class is deprecated in recent versions of Flume
//        Application flumeApplication = new Application();
//        flumeApplication.handleConfigurationEvent();
//        flumeApplication.start();

        String[] command = {
                "flume-ng",
                "agent",
                "--name",
                "myAgent",
                "-c",
                "/path/to/flume/conf", // Replace with your Flume configuration directory
                "-f",
                "/path/to/flume.conf", // Replace with your Flume configuration file
                "-Dflume.root.logger=INFO,console"
        };

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // check process output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(line);
        }

        // TODO: Add logic to stop Flume when needed
    }

    private static void startSparkStreaming() {
        // Create a Spark Streaming context
        SparkConf sparkConf = new SparkConf().setAppName("Covid19Tracker");
        try (JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, new Duration(5000))) {

            // Create a Flume stream
            // FlumeUtils is deprecated
//        JavaReceiverInputDStream<byte[]> flumeStream = FlumeUtils.createStream(streamingContext, "localhost", 41414);

            CustomFlumeReceiver flumeReceiver = new CustomFlumeReceiver(41414);
            streamingContext.receiverStream(flumeReceiver).print();
            // Process the Flume stream (example: count the number of events in each batch)
//        flumeStream.foreachRDD((JavaRDD<SparkFlumeEvent> rdd) -> {
//            long count = rdd.count();
//            System.out.println("Number of events in this batch: " + count);
//        });

//        flumeStream.foreachRDD(rdd -> {
//            rdd.foreach(flumeEvent -> {
//                String eventMessage = new String(flumeEvent);
//                // Process the event message as needed
//                System.out.println("Received Flume event: " + eventMessage);
//
//                long count = rdd.count();
//                System.out.println("Number of events in this batch: " + count);
//            });
//        });

            // Start the Spark Streaming context
            streamingContext.start();
            try {
                streamingContext.awaitTermination();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } // Adjust the batch interval
    }
}

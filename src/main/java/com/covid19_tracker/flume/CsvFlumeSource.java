package com.covid19_tracker.flume;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.DefaultSourceFactory;
import org.apache.flume.source.http.HTTPSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvFlumeSource extends AbstractSource implements EventDrivenSource, Configurable {

    private String csvFilePath;

    @Override
    public void configure(Context context) {
        csvFilePath = context.getString("csvFilePath");
        if (csvFilePath == null) {
            throw new ConfigurationException("CSV file path must be specified.");
        }
    }

    @Override
    public synchronized void start() {
        // Initialize the connection to the CSV file or perform any setup here
        super.start();
    }

    @Override
    public synchronized void stop() {
        // Perform cleanup or close connections here
        super.stop();
    }

    @Override
    public Status process() throws EventDeliveryException {
        try {
            // Read data from the CSV file and send it to the Flume channel
            BufferedReader reader = new BufferedReader(new FileReader(new File(csvFilePath)));
            String line;
            while ((line = reader.readLine()) != null) {
                // Create a Flume event and send it to the channel
                Map<String, String> headers = new HashMap<>();
                headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                Event event = new SimpleEvent();
                event.setBody(line.getBytes());
                event.setHeaders(headers);
                getChannelProcessor().processEvent(event);
            }

            return Status.READY;
        } catch (Exception e) {
            return Status.BACKOFF;
        }
    }
}

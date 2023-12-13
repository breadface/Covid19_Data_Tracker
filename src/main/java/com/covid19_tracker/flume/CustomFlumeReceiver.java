package com.covid19_tracker.flume;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CustomFlumeReceiver extends Receiver<String> {
    private int port;

    public CustomFlumeReceiver(int port) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.port = port;
    }

    private void receiveFlumeEvents() {
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String receivedData;
            while ((receivedData = in.readLine()) != null) {
                store(receivedData);
            }
        } catch (Exception e) {
            restart("Error receiving data from Flume", e);
        }
    }

    @Override
    public void onStart() {
        new Thread(this::receiveFlumeEvents).start();
    }

    @Override
    public void onStop() {
        // Todo - resource clean up
    }
}

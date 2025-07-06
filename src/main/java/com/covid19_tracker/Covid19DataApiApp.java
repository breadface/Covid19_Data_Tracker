package com.covid19_tracker;

import com.covid19_tracker.api.Covid19HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for COVID-19 Data API
 * Starts HTTP server to serve REST API endpoints
 */
public class Covid19DataApiApp {
    private static final Logger logger = LoggerFactory.getLogger(Covid19DataApiApp.class);
    
    private static final int DEFAULT_PORT = 8080;
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Parse command line arguments for port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("Invalid port number: {}. Using default port: {}", args[0], DEFAULT_PORT);
            }
        }
        
        try {
            logger.info("Starting COVID-19 Data API Server on port {}", port);
            
            Covid19HttpServer server = new Covid19HttpServer(port);
            server.start();
            
            logger.info("COVID-19 Data API Server started successfully!");
            logger.info("Available endpoints:");
            logger.info("  GET /api/health - Health check");
            logger.info("  GET /api/covid19/latest - Latest COVID-19 data");
            logger.info("  GET /api/covid19/summary - Summary statistics");
            logger.info("  GET /api/covid19/top-countries?limit=10 - Top countries by cases");
            logger.info("  GET /api/covid19/country?country=US - Data by country");
            logger.info("  GET /api/covid19/range?start=2020-01-01&end=2024-12-31 - Data by date range");
            logger.info("");
            logger.info("Press Ctrl+C to stop the server");
            
            // Keep the server running
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down COVID-19 Data API Server...");
                server.stop();
                logger.info("Server stopped");
            }));
            
            // Wait indefinitely
            Thread.currentThread().join();
            
        } catch (Exception e) {
            logger.error("Failed to start COVID-19 Data API Server", e);
            System.exit(1);
        }
    }
} 
package com.covid19_tracker.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple HTTP server to serve REST API endpoints
 */
public class Covid19HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(Covid19HttpServer.class);
    
    private final com.sun.net.httpserver.HttpServer server;
    private final RestApiController apiController;
    
    public Covid19HttpServer(int port) throws IOException {
        this.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
        this.apiController = new RestApiController();
        
        setupRoutes();
        logger.info("HTTP Server initialized on port {}", port);
    }
    
    private void setupRoutes() {
        // Health check
        server.createContext("/api/health", new HealthHandler());
        
        // COVID-19 data endpoints
        server.createContext("/api/covid19/latest", new LatestDataHandler());
        server.createContext("/api/covid19/summary", new SummaryHandler());
        server.createContext("/api/covid19/top-countries", new TopCountriesHandler());
        server.createContext("/api/covid19/country", new CountryDataHandler());
        server.createContext("/api/covid19/range", new DateRangeHandler());
        
        // CORS headers
        server.setExecutor(null);
    }
    
    public void start() {
        server.start();
        logger.info("HTTP Server started successfully");
    }
    
    public void stop() {
        server.stop(0);
        logger.info("HTTP Server stopped");
    }
    
    /**
     * Health check handler
     */
    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = apiController.getHealthJson();
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    /**
     * Latest COVID-19 data handler
     */
    private class LatestDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = apiController.getLatestCovid19DataJson();
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    /**
     * Summary statistics handler
     */
    private class SummaryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = apiController.getSummaryStatisticsJson();
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    /**
     * Top countries handler
     */
    private class TopCountriesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int limit = 10; // default limit
                
                if (query != null) {
                    Map<String, String> params = parseQueryString(query);
                    if (params.containsKey("limit")) {
                        try {
                            limit = Integer.parseInt(params.get("limit"));
                        } catch (NumberFormatException e) {
                            // use default limit
                        }
                    }
                }
                
                String response = apiController.getTopCountriesByCases(limit);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    /**
     * Country data handler
     */
    private class CountryDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String country = "US"; // default country
                
                if (query != null) {
                    Map<String, String> params = parseQueryString(query);
                    if (params.containsKey("country")) {
                        country = params.get("country");
                    }
                }
                
                String response = apiController.getCovid19DataByCountryJson(country);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    /**
     * Date range handler
     */
    private class DateRangeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String startDate = "2020-01-01"; // default start date
                String endDate = "2024-12-31"; // default end date
                
                if (query != null) {
                    Map<String, String> params = parseQueryString(query);
                    if (params.containsKey("start")) {
                        startDate = params.get("start");
                    }
                    if (params.containsKey("end")) {
                        endDate = params.get("end");
                    }
                }
                
                String response = apiController.getCovid19DataByDateRangeJson(startDate, endDate);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }
    
    /**
     * Send HTTP response with CORS headers
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * Parse query string parameters
     */
    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                    params.put(key, value);
                } catch (Exception e) {
                    // skip invalid parameters
                }
            }
        }
        
        return params;
    }
} 
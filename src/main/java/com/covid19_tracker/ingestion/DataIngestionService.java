package com.covid19_tracker.ingestion;

import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for ingesting COVID-19 data from external sources to HDFS
 */
@Service
public class DataIngestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataIngestionService.class);
    
    @Autowired
    private FileSystem fileSystem;
    
    @Autowired
    private List<CovidDataSource> dataSources;
    
    private final String rawDataPath = "/covid19-data/raw";
    
    /**
     * Ingest all data sources
     */
    public void ingestAllData() {
        logger.info("Starting data ingestion for all sources");
        boolean anySuccess = false;
        for (CovidDataSource source : dataSources) {
            try {
                boolean success = source.ingest(fileSystem, rawDataPath, LocalDate.now());
                if (success) {
                    anySuccess = true;
                }
            } catch (Exception e) {
                logger.error("{} ingestion failed", source.getName(), e);
            }
        }
        if (anySuccess) {
            logger.info("Data ingestion completed with some success");
        } else {
            logger.error("All data ingestion attempts failed");
        }
    }
} 
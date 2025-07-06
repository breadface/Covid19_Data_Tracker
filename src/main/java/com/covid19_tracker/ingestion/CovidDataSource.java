package com.covid19_tracker.ingestion;

import org.apache.hadoop.fs.FileSystem;
import java.time.LocalDate;

public interface CovidDataSource {
    String getName();
    boolean ingest(FileSystem fileSystem, String rawDataPath, LocalDate date);
} 
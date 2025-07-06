package com.covid19_tracker.ingestion;

import com.covid19_tracker.config.DataSourcesConfig;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;

@Component
public class OurWorldInDataSource implements CovidDataSource {
    private static final Logger logger = LoggerFactory.getLogger(OurWorldInDataSource.class);

    @Autowired
    private DataSourcesConfig dataSourcesConfig;

    @Override
    public String getName() {
        return "Our World in Data";
    }

    @Override
    public boolean ingest(FileSystem fileSystem, String rawDataPath, LocalDate date) {
        String url = dataSourcesConfig.getOurWorldInData().getUrl();
        String fileName = "our_world_in_data_" + date + ".json";
        Path hdfsPath = new Path(rawDataPath, fileName);
        logger.info("Ingesting Our World in Data from: {}", url);
        try (InputStream inputStream = new URL(url).openStream();
             FSDataOutputStream out = fileSystem.create(hdfsPath, true)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            logger.info("Our World in Data ingested successfully to: {}", hdfsPath);
            return true;
        } catch (Exception e) {
            logger.error("Failed to ingest Our World in Data", e);
            return false;
        }
    }
} 
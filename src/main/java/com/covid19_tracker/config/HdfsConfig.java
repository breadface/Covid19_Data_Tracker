package com.covid19_tracker.config;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;

/**
 * Configuration for HDFS integration
 */
@Configuration
@ConfigurationProperties(prefix = "hdfs")
public class HdfsConfig {
    
    private String namenode;
    private String basePath;
    private String rawDataPath;
    private String processedDataPath;
    private String archivePath;
    
    @Bean
    public FileSystem fileSystem() throws IOException {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("fs.defaultFS", namenode);
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        
        return FileSystem.get(URI.create(namenode), conf);
    }
    
    @Bean
    public Path basePath() {
        return new Path(basePath);
    }
    
    @Bean
    public Path rawDataPath() {
        return new Path(rawDataPath);
    }
    
    @Bean
    public Path processedDataPath() {
        return new Path(processedDataPath);
    }
    
    @Bean
    public Path archivePath() {
        return new Path(archivePath);
    }
    
    // Getters and Setters
    public String getNamenode() {
        return namenode;
    }
    
    public void setNamenode(String namenode) {
        this.namenode = namenode;
    }
    
    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    public String getRawDataPath() {
        return rawDataPath;
    }
    
    public void setRawDataPath(String rawDataPath) {
        this.rawDataPath = rawDataPath;
    }
    
    public String getProcessedDataPath() {
        return processedDataPath;
    }
    
    public void setProcessedDataPath(String processedDataPath) {
        this.processedDataPath = processedDataPath;
    }
    
    public String getArchivePath() {
        return archivePath;
    }
    
    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }
} 
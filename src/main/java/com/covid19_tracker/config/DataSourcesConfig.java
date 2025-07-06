package com.covid19_tracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for external data sources
 */
@Configuration
@ConfigurationProperties(prefix = "data-sources")
public class DataSourcesConfig {
    
    private WhoDataSource who;
    private CdcDataSource cdc;
    private JohnsHopkinsDataSource johnsHopkins;
    private OurWorldInDataDataSource ourWorldInData;
    
    public static class WhoDataSource {
        private String url;
        private String updateFrequency;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUpdateFrequency() {
            return updateFrequency;
        }
        
        public void setUpdateFrequency(String updateFrequency) {
            this.updateFrequency = updateFrequency;
        }
    }
    
    public static class CdcDataSource {
        private String url;
        private String updateFrequency;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUpdateFrequency() {
            return updateFrequency;
        }
        
        public void setUpdateFrequency(String updateFrequency) {
            this.updateFrequency = updateFrequency;
        }
    }
    
    public static class JohnsHopkinsDataSource {
        private String baseUrl;
        private String updateFrequency;
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getUpdateFrequency() {
            return updateFrequency;
        }
        
        public void setUpdateFrequency(String updateFrequency) {
            this.updateFrequency = updateFrequency;
        }
    }
    
    public static class OurWorldInDataDataSource {
        private String url;
        private String updateFrequency;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUpdateFrequency() {
            return updateFrequency;
        }
        
        public void setUpdateFrequency(String updateFrequency) {
            this.updateFrequency = updateFrequency;
        }
    }
    
    // Getters and Setters
    public WhoDataSource getWho() {
        return who;
    }
    
    public void setWho(WhoDataSource who) {
        this.who = who;
    }
    
    public CdcDataSource getCdc() {
        return cdc;
    }
    
    public void setCdc(CdcDataSource cdc) {
        this.cdc = cdc;
    }
    
    public JohnsHopkinsDataSource getJohnsHopkins() {
        return johnsHopkins;
    }
    
    public void setJohnsHopkins(JohnsHopkinsDataSource johnsHopkins) {
        this.johnsHopkins = johnsHopkins;
    }
    
    public OurWorldInDataDataSource getOurWorldInData() {
        return ourWorldInData;
    }
    
    public void setOurWorldInData(OurWorldInDataDataSource ourWorldInData) {
        this.ourWorldInData = ourWorldInData;
    }
} 
package com.covid19_tracker.batch;

import com.covid19_tracker.model.Covid19Data;
import com.covid19_tracker.repository.Covid19DataRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Batch job for processing COVID-19 data
 */
@Configuration
public class Covid19DataProcessingJob {
    
    private static final Logger logger = LoggerFactory.getLogger(Covid19DataProcessingJob.class);
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @Autowired
    private Covid19DataRepository covid19DataRepository;
    
    @Bean
    public Job covid19DataProcessingJobBean() {
        return new JobBuilder("covid19DataProcessingJob", jobRepository)
            .start(processWhoDataStep())
            .next(processCdcDataStep())
            .next(processJohnsHopkinsDataStep())
            .build();
    }
    
    @Bean
    public Step processWhoDataStep() {
        return new StepBuilder("processWhoDataStep", jobRepository)
            .<Covid19Data, Covid19Data>chunk(100, transactionManager)
            .reader(whoDataReader())
            .processor(whoDataProcessor())
            .writer(covid19DataWriter())
            .build();
    }
    
    @Bean
    public Step processCdcDataStep() {
        return new StepBuilder("processCdcDataStep", jobRepository)
            .<Covid19Data, Covid19Data>chunk(100, transactionManager)
            .reader(cdcDataReader())
            .processor(cdcDataProcessor())
            .writer(covid19DataWriter())
            .build();
    }
    
    @Bean
    public Step processJohnsHopkinsDataStep() {
        return new StepBuilder("processJohnsHopkinsDataStep", jobRepository)
            .<Covid19Data, Covid19Data>chunk(100, transactionManager)
            .reader(johnsHopkinsDataReader())
            .processor(johnsHopkinsDataProcessor())
            .writer(covid19DataWriter())
            .build();
    }
    
    @Bean
    public ItemReader<Covid19Data> whoDataReader() {
        FlatFileItemReader<Covid19Data> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("data/who_covid19_data.csv"));
        reader.setLinesToSkip(1); // Skip header
        
        DefaultLineMapper<Covid19Data> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("date", "country", "newCases", "cumulativeCases", "newDeaths", "cumulativeDeaths");
        
        BeanWrapperFieldSetMapper<Covid19Data> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Covid19Data.class);
        
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);
        
        return reader;
    }
    
    @Bean
    public ItemReader<Covid19Data> cdcDataReader() {
        FlatFileItemReader<Covid19Data> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("data/cdc_covid19_data.csv"));
        reader.setLinesToSkip(1); // Skip header
        
        DefaultLineMapper<Covid19Data> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("date", "state", "cases", "deaths");
        
        BeanWrapperFieldSetMapper<Covid19Data> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Covid19Data.class);
        
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);
        
        return reader;
    }
    
    @Bean
    public ItemReader<Covid19Data> johnsHopkinsDataReader() {
        FlatFileItemReader<Covid19Data> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("data/johns_hopkins_data.csv"));
        reader.setLinesToSkip(1); // Skip header
        
        DefaultLineMapper<Covid19Data> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("date", "country", "state", "confirmed", "deaths", "recovered");
        
        BeanWrapperFieldSetMapper<Covid19Data> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Covid19Data.class);
        
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);
        
        return reader;
    }
    
    @Bean
    public ItemProcessor<Covid19Data, Covid19Data> whoDataProcessor() {
        return item -> {
            // Set data source
            item.setDataSource("WHO");
            
            // Parse date if needed
            if (item.getDate() == null && item.getCountry() != null) {
                // Handle date parsing logic here
            }
            
            return item;
        };
    }
    
    @Bean
    public ItemProcessor<Covid19Data, Covid19Data> cdcDataProcessor() {
        return item -> {
            // Set data source
            item.setDataSource("CDC");
            
            // Set country to US for CDC data
            item.setCountry("United States");
            
            return item;
        };
    }
    
    @Bean
    public ItemProcessor<Covid19Data, Covid19Data> johnsHopkinsDataProcessor() {
        return item -> {
            // Set data source
            item.setDataSource("Johns Hopkins");
            
            return item;
        };
    }
    
    @Bean
    public ItemWriter<Covid19Data> covid19DataWriter() {
        return items -> {
            logger.info("Writing {} COVID-19 data records", items.size());
            covid19DataRepository.saveAll(items);
        };
    }
} 
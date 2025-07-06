package com.covid19_tracker.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import static org.apache.spark.sql.functions.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.Column;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.coalesce;
import java.util.Arrays;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StringType;
import org.apache.spark.sql.types.Metadata;

/**
 * Optimized Spark job to process COVID-19 data with schema flexibility
 */
public class Covid19DataProcessor {

    // Define the canonical list of required columns for the FINAL OUTPUT DataFrame
    // This MUST match the final output schema you expect in Hive.
    // IMPORTANT: Aliases for specific columns (e.g., confirmed_cases, deaths)
    // are now reflected here to match the selection logic in processCountryData.
    private static final String[] FINAL_OUTPUT_COLS_ORDER = new String[] {
        "date", "country",
        "confirmed_cases", "new_cases", "new_cases_smoothed", // Renamed from total_cases
        "deaths", "new_deaths", "new_deaths_smoothed",       // Renamed from total_deaths
        "cases_per_million", "new_cases_per_million", "new_cases_smoothed_per_million", // Renamed from total_cases_per_million
        "deaths_per_million", "new_deaths_per_million", "new_deaths_smoothed_per_million", // Renamed from total_deaths_per_million
        "reproduction_rate",
        "stringency_index",
        "icu_patients", "icu_patients_per_million",
        "hosp_patients", "hosp_patients_per_million",
        "weekly_icu_admissions", "weekly_icu_admissions_per_million",
        "weekly_hosp_admissions", "weekly_hosp_admissions_per_million",
        "total_tests", "new_tests", "total_tests_per_thousand", "new_tests_per_thousand",
        "new_tests_smoothed", "new_tests_smoothed_per_thousand",
        "positive_rate", "tests_per_case", "tests_units",
        "total_vaccinations", "people_vaccinated", "people_fully_vaccinated", "total_boosters",
        "new_vaccinations", "new_vaccinations_smoothed",
        "total_vaccinations_per_hundred", "people_vaccinated_per_hundred",
        "people_fully_vaccinated_per_hundred", "total_boosters_per_hundred",
        "new_vaccinations_smoothed_per_million",
        "new_people_vaccinated_smoothed", "new_people_vaccinated_smoothed_per_hundred",
        // Adding back excess mortality fields as they appear in daily data points for some countries
        "excess_mortality",
        "excess_mortality_cumulative",
        "excess_mortality_cumulative_absolute",
        "excess_mortality_cumulative_per_million",
        "data_source"
    };

    // Define the comprehensive schema for a single 'data_point' object inside the 'data' array.
    // This MUST include ALL possible fields that might appear in ANY country's data_point.
    // Mark fields as nullable=true as they might be missing in some records/countries.
    // This has been updated to strictly reflect fields found *within the daily data point objects* online.
    private static final StructType DATA_POINT_CANONICAL_SCHEMA = new StructType(new StructField[]{
        new StructField("date", DataTypes.StringType, true, Metadata.empty()),
        new StructField("total_cases", DataTypes.LongType, true, Metadata.empty()), // Use original names here for parsing
        new StructField("new_cases", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_cases_smoothed", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("total_deaths", DataTypes.LongType, true, Metadata.empty()), // Use original names here for parsing
        new StructField("new_deaths", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_deaths_smoothed", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("total_cases_per_million", DataTypes.DoubleType, true, Metadata.empty()), // Use original names here for parsing
        new StructField("new_cases_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("new_cases_smoothed_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("total_deaths_per_million", DataTypes.DoubleType, true, Metadata.empty()), // Use original names here for parsing
        new StructField("new_deaths_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("new_deaths_smoothed_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("reproduction_rate", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("stringency_index", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("icu_patients", DataTypes.LongType, true, Metadata.empty()),
        new StructField("icu_patients_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("hosp_patients", DataTypes.LongType, true, Metadata.empty()),
        new StructField("hosp_patients_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("weekly_icu_admissions", DataTypes.LongType, true, Metadata.empty()),
        new StructField("weekly_icu_admissions_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("weekly_hosp_admissions", DataTypes.LongType, true, Metadata.empty()),
        new StructField("weekly_hosp_admissions_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("total_tests", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_tests", DataTypes.LongType, true, Metadata.empty()),
        new StructField("total_tests_per_thousand", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("new_tests_per_thousand", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("new_tests_smoothed", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_tests_smoothed_per_thousand", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("positive_rate", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("tests_per_case", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("tests_units", DataTypes.StringType, true, Metadata.empty()),
        new StructField("total_vaccinations", DataTypes.LongType, true, Metadata.empty()),
        new StructField("people_vaccinated", DataTypes.LongType, true, Metadata.empty()),
        new StructField("people_fully_vaccinated", DataTypes.LongType, true, Metadata.empty()),
        new StructField("total_boosters", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_vaccinations", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_vaccinations_smoothed", DataTypes.LongType, true, Metadata.empty()),
        new StructField("total_vaccinations_per_hundred", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("people_vaccinated_per_hundred", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("people_fully_vaccinated_per_hundred", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("total_boosters_per_hundred", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("new_vaccinations_smoothed_per_million", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("new_people_vaccinated_smoothed", DataTypes.LongType, true, Metadata.empty()),
        new StructField("new_people_vaccinated_smoothed_per_hundred", DataTypes.DoubleType, true, Metadata.empty()),
        // Adding back excess mortality fields as confirmed by error trace that they are sometimes in data_point
        new StructField("excess_mortality", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("excess_mortality_cumulative", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("excess_mortality_cumulative_absolute", DataTypes.DoubleType, true, Metadata.empty()),
        new StructField("excess_mortality_cumulative_per_million", DataTypes.DoubleType, true, Metadata.empty())
    });


    public static void main(String[] args) {
        System.out.println("COVID19 MAIN ENTRY -- UNIQUE RUN ID: " + System.currentTimeMillis());
        System.out.println("=== UPDATED CODE VERSION - " + System.currentTimeMillis() + " ===");
        SparkSession spark = SparkSession.builder()
            .appName("COVID-19 Data Processing")
            .config("spark.sql.warehouse.dir", "/user/hive/warehouse")
            .config("spark.sql.adaptive.enabled", "true")
            .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
            .enableHiveSupport()
            .getOrCreate();

        try {
            System.out.println("Starting COVID-19 data processing..."); // THIS SHOULD ALWAYS PRINT

            // Read raw JSON data. Spark will infer the top-level schema.
            Dataset<Row> rawData = spark.read()
                .option("multiline", true)
                .json("hdfs://namenode:9000/covid19-data/raw/our_world_in_data_*.json");

            System.out.println("Raw data schema inferred:"); // THIS SHOULD ALWAYS PRINT
            rawData.printSchema(); // Debug: See inferred top-level schema

            // Get all country columns (e.g., "USA", "AFG")
            String[] allColumns = rawData.columns();
            String[] countryColumns = Arrays.stream(allColumns)
                .filter(col -> col.length() == 3 && col.matches("[A-Z]{3}"))
                .toArray(String[]::new);

            System.out.println("Found " + countryColumns.length + " countries as top-level columns in the dataset"); // THIS SHOULD ALWAYS PRINT

            // Process a sample of countries. For production, iterate through 'countryColumns'.
            String[] sampleCountries = {"USA", "IND", "BRA", "GBR", "FRA", "AFG", "ABW"}; // Added ABW back to sample for debugging

            List<Dataset<Row>> processedCountryDataFrames = new ArrayList<>();
            for (String countryCode : sampleCountries) {
                System.out.println("DEBUG: Entering loop for country: " + countryCode); // NEW DEBUG PRINT
                try {
                    System.out.println("Attempting to process country: " + countryCode);
                    if (!Arrays.asList(rawData.columns()).contains(countryCode)) {
                        System.out.println("Country column '" + countryCode + "' not found in the raw data, skipping...");
                        continue;
                    }

                    Dataset<Row> processedCountryData = processCountryData(spark, rawData, countryCode);

                    System.out.println("DEBUG: Returned from processCountryData for " + countryCode); // NEW DEBUG PRINT

                    if (processedCountryData != null && processedCountryData.count() > 0) {
                        processedCountryDataFrames.add(processedCountryData);
                        System.out.println("Successfully processed " + processedCountryData.count() + " records for " + countryCode);
                    } else {
                        System.out.println("No valid data processed for " + countryCode + " after explosion or filtering.");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing country " + countryCode + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            Dataset<Row> allProcessedData = null;
            if (!processedCountryDataFrames.isEmpty()) {
                allProcessedData = processedCountryDataFrames.get(0);
                for (int i = 1; i < processedCountryDataFrames.size(); i++) {
                    Dataset<Row> df = processedCountryDataFrames.get(i);
                    System.out.println("DEBUG: Unioning DataFrame with canonical columns (count: " + df.columns().length + "): " + Arrays.toString(df.columns())); // NEW DEBUG PRINT
                    allProcessedData = allProcessedData.unionByName(df, true); // `true` allows missing columns, filling with null
                }
            }

            if (allProcessedData != null && allProcessedData.count() > 0) {
                System.out.println("Total processed records after union: " + allProcessedData.count());

                System.out.println("Setting database to default...");
                spark.sql("USE default");

                // Create Hive table - DDL must match FINAL_OUTPUT_COLS_ORDER and its types
                System.out.println("Creating Hive table 'covid19_data'...");
                // IMPORTANT: This DDL must be kept in sync with FINAL_OUTPUT_COLS_ORDER!
                spark.sql("CREATE TABLE IF NOT EXISTS covid19_data (" +
                    "date DATE," +
                    "country STRING," +
                    "confirmed_cases BIGINT," + // Renamed in DDL
                    "new_cases BIGINT," +
                    "new_cases_smoothed DOUBLE," +
                    "deaths BIGINT," + // Renamed in DDL
                    "new_deaths BIGINT," +
                    "new_deaths_smoothed DOUBLE," +
                    "cases_per_million DOUBLE," + // Renamed in DDL
                    "new_cases_per_million DOUBLE," +
                    "new_cases_smoothed_per_million DOUBLE," +
                    "deaths_per_million DOUBLE," + // Renamed in DDL
                    "new_deaths_per_million DOUBLE," +
                    "new_deaths_smoothed_per_million DOUBLE," +
                    "reproduction_rate DOUBLE," +
                    "stringency_index DOUBLE," +
                    "icu_patients BIGINT," +
                    "icu_patients_per_million DOUBLE," +
                    "hosp_patients BIGINT," +
                    "hosp_patients_per_million DOUBLE," +
                    "weekly_icu_admissions BIGINT," +
                    "weekly_icu_admissions_per_million DOUBLE," +
                    "weekly_hosp_admissions BIGINT," +
                    "weekly_hosp_admissions_per_million DOUBLE," +
                    "total_tests BIGINT," +
                    "new_tests BIGINT," +
                    "total_tests_per_thousand DOUBLE," +
                    "new_tests_per_thousand DOUBLE," +
                    "new_tests_smoothed BIGINT," +
                    "new_tests_smoothed_per_thousand DOUBLE," +
                    "positive_rate DOUBLE," +
                    "tests_per_case DOUBLE," +
                    "tests_units STRING," +
                    "total_vaccinations BIGINT," +
                    "people_vaccinated BIGINT," +
                    "people_fully_vaccinated BIGINT," +
                    "total_boosters BIGINT," +
                    "new_vaccinations BIGINT," +
                    "new_vaccinations_smoothed BIGINT," +
                    "total_vaccinations_per_hundred DOUBLE," +
                    "people_vaccinated_per_hundred DOUBLE," +
                    "people_fully_vaccinated_per_hundred DOUBLE," +
                    "total_boosters_per_hundred DOUBLE," +
                    "new_vaccinations_smoothed_per_million DOUBLE," +
                    "new_people_vaccinated_smoothed BIGINT," +
                    "new_people_vaccinated_smoothed_per_hundred DOUBLE," +
                    // Adding back excess mortality fields to DDL
                    "excess_mortality DOUBLE," +
                    "excess_mortality_cumulative DOUBLE," +
                    "excess_mortality_cumulative_absolute DOUBLE," +
                    "excess_mortality_cumulative_per_million DOUBLE," +
                    "data_source STRING" +
                    ") STORED AS PARQUET");

                allProcessedData.write().mode("overwrite").saveAsTable("covid19_data");

                System.out.println("Creating summary statistics 'covid19_summary'...");
                Dataset<Row> summary = allProcessedData.groupBy("country")
                    .agg(
                        functions.count("*").as("total_records"),
                        functions.max("confirmed_cases").as("max_confirmed_cases"), // Use aliased name
                        functions.max("deaths").as("max_deaths"), // Use aliased name
                        functions.max("date").as("latest_date")
                    );

                summary.write().mode("overwrite").saveAsTable("covid19_summary");

                System.out.println("COVID-19 data processing completed successfully!");
                System.out.println("Tables created: covid19_data, covid19_summary");

            } else {
                System.err.println("No data was successfully processed or combined for output!");
            }

        } catch (Exception e) {
            System.err.println("Critical error during COVID-19 data processing: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }

    /**
     * Helper function to process data for a single country with guaranteed schema consistency.
     * It ensures the output DataFrame has the exact columns defined in FINAL_OUTPUT_COLS_ORDER.
     *
     * This method uses a direct approach: explode the data array, then explicitly select
     * and cast each field to ensure schema consistency.
     */
    private static Dataset<Row> processCountryData(SparkSession spark, Dataset<Row> rawData, String countryCode) {
        System.out.println("DEBUG: Inside processCountryData for " + countryCode);
        try {
            // Step 1: Select the specific country data and explode the data array
            Dataset<Row> exploded = rawData.select(col(countryCode))
                .filter(col(countryCode).isNotNull())
                .select(explode(col(countryCode + ".data")).as("data_point"));

            if (exploded.count() == 0) {
                System.out.println("DEBUG: No data points found for " + countryCode + ", returning null.");
                return null;
            }

            System.out.println("DEBUG: Found " + exploded.count() + " data points for " + countryCode);

            // Step 2: Create a DataFrame with explicitly selected and cast columns
            // This ensures all DataFrames have the same schema regardless of source data
            Dataset<Row> df = exploded.select(
                lit(countryCode).as("country"),
                to_date(col("data_point.date")).cast(DataTypes.DateType).as("date"),
                
                // Cases - map total_cases to confirmed_cases
                coalesce(col("data_point.total_cases"), lit(null)).cast(DataTypes.LongType).as("confirmed_cases"),
                coalesce(col("data_point.new_cases"), lit(null)).cast(DataTypes.LongType).as("new_cases"),
                coalesce(col("data_point.new_cases_smoothed"), lit(null)).cast(DataTypes.DoubleType).as("new_cases_smoothed"),
                
                // Deaths - map total_deaths to deaths
                coalesce(col("data_point.total_deaths"), lit(null)).cast(DataTypes.LongType).as("deaths"),
                coalesce(col("data_point.new_deaths"), lit(null)).cast(DataTypes.LongType).as("new_deaths"),
                coalesce(col("data_point.new_deaths_smoothed"), lit(null)).cast(DataTypes.DoubleType).as("new_deaths_smoothed"),
                
                // Cases per million - map total_cases_per_million to cases_per_million
                coalesce(col("data_point.total_cases_per_million"), lit(null)).cast(DataTypes.DoubleType).as("cases_per_million"),
                coalesce(col("data_point.new_cases_per_million"), lit(null)).cast(DataTypes.DoubleType).as("new_cases_per_million"),
                coalesce(col("data_point.new_cases_smoothed_per_million"), lit(null)).cast(DataTypes.DoubleType).as("new_cases_smoothed_per_million"),
                
                // Deaths per million - map total_deaths_per_million to deaths_per_million
                coalesce(col("data_point.total_deaths_per_million"), lit(null)).cast(DataTypes.DoubleType).as("deaths_per_million"),
                coalesce(col("data_point.new_deaths_per_million"), lit(null)).cast(DataTypes.DoubleType).as("new_deaths_per_million"),
                coalesce(col("data_point.new_deaths_smoothed_per_million"), lit(null)).cast(DataTypes.DoubleType).as("new_deaths_smoothed_per_million"),
                
                // Other metrics
                coalesce(col("data_point.reproduction_rate"), lit(null)).cast(DataTypes.DoubleType).as("reproduction_rate"),
                coalesce(col("data_point.stringency_index"), lit(null)).cast(DataTypes.DoubleType).as("stringency_index"),
                
                // Hospital/ICU data
                coalesce(col("data_point.icu_patients"), lit(null)).cast(DataTypes.LongType).as("icu_patients"),
                coalesce(col("data_point.icu_patients_per_million"), lit(null)).cast(DataTypes.DoubleType).as("icu_patients_per_million"),
                coalesce(col("data_point.hosp_patients"), lit(null)).cast(DataTypes.LongType).as("hosp_patients"),
                coalesce(col("data_point.hosp_patients_per_million"), lit(null)).cast(DataTypes.DoubleType).as("hosp_patients_per_million"),
                coalesce(col("data_point.weekly_icu_admissions"), lit(null)).cast(DataTypes.LongType).as("weekly_icu_admissions"),
                coalesce(col("data_point.weekly_icu_admissions_per_million"), lit(null)).cast(DataTypes.DoubleType).as("weekly_icu_admissions_per_million"),
                coalesce(col("data_point.weekly_hosp_admissions"), lit(null)).cast(DataTypes.LongType).as("weekly_hosp_admissions"),
                coalesce(col("data_point.weekly_hosp_admissions_per_million"), lit(null)).cast(DataTypes.DoubleType).as("weekly_hosp_admissions_per_million"),
                
                // Testing data
                coalesce(col("data_point.total_tests"), lit(null)).cast(DataTypes.LongType).as("total_tests"),
                coalesce(col("data_point.new_tests"), lit(null)).cast(DataTypes.LongType).as("new_tests"),
                coalesce(col("data_point.total_tests_per_thousand"), lit(null)).cast(DataTypes.DoubleType).as("total_tests_per_thousand"),
                coalesce(col("data_point.new_tests_per_thousand"), lit(null)).cast(DataTypes.DoubleType).as("new_tests_per_thousand"),
                coalesce(col("data_point.new_tests_smoothed"), lit(null)).cast(DataTypes.LongType).as("new_tests_smoothed"),
                coalesce(col("data_point.new_tests_smoothed_per_thousand"), lit(null)).cast(DataTypes.DoubleType).as("new_tests_smoothed_per_thousand"),
                coalesce(col("data_point.positive_rate"), lit(null)).cast(DataTypes.DoubleType).as("positive_rate"),
                coalesce(col("data_point.tests_per_case"), lit(null)).cast(DataTypes.DoubleType).as("tests_per_case"),
                coalesce(col("data_point.tests_units"), lit(null)).cast(DataTypes.StringType).as("tests_units"),
                
                // Vaccination data
                coalesce(col("data_point.total_vaccinations"), lit(null)).cast(DataTypes.LongType).as("total_vaccinations"),
                coalesce(col("data_point.people_vaccinated"), lit(null)).cast(DataTypes.LongType).as("people_vaccinated"),
                coalesce(col("data_point.people_fully_vaccinated"), lit(null)).cast(DataTypes.LongType).as("people_fully_vaccinated"),
                coalesce(col("data_point.total_boosters"), lit(null)).cast(DataTypes.LongType).as("total_boosters"),
                coalesce(col("data_point.new_vaccinations"), lit(null)).cast(DataTypes.LongType).as("new_vaccinations"),
                coalesce(col("data_point.new_vaccinations_smoothed"), lit(null)).cast(DataTypes.LongType).as("new_vaccinations_smoothed"),
                coalesce(col("data_point.total_vaccinations_per_hundred"), lit(null)).cast(DataTypes.DoubleType).as("total_vaccinations_per_hundred"),
                coalesce(col("data_point.people_vaccinated_per_hundred"), lit(null)).cast(DataTypes.DoubleType).as("people_vaccinated_per_hundred"),
                coalesce(col("data_point.people_fully_vaccinated_per_hundred"), lit(null)).cast(DataTypes.DoubleType).as("people_fully_vaccinated_per_hundred"),
                coalesce(col("data_point.total_boosters_per_hundred"), lit(null)).cast(DataTypes.DoubleType).as("total_boosters_per_hundred"),
                coalesce(col("data_point.new_vaccinations_smoothed_per_million"), lit(null)).cast(DataTypes.DoubleType).as("new_vaccinations_smoothed_per_million"),
                coalesce(col("data_point.new_people_vaccinated_smoothed"), lit(null)).cast(DataTypes.LongType).as("new_people_vaccinated_smoothed"),
                coalesce(col("data_point.new_people_vaccinated_smoothed_per_hundred"), lit(null)).cast(DataTypes.DoubleType).as("new_people_vaccinated_smoothed_per_hundred"),
                
                // Excess mortality data
                coalesce(col("data_point.excess_mortality"), lit(null)).cast(DataTypes.DoubleType).as("excess_mortality"),
                coalesce(col("data_point.excess_mortality_cumulative"), lit(null)).cast(DataTypes.DoubleType).as("excess_mortality_cumulative"),
                coalesce(col("data_point.excess_mortality_cumulative_absolute"), lit(null)).cast(DataTypes.DoubleType).as("excess_mortality_cumulative_absolute"),
                coalesce(col("data_point.excess_mortality_cumulative_per_million"), lit(null)).cast(DataTypes.DoubleType).as("excess_mortality_cumulative_per_million"),
                
                // Data source
                lit("our_world_in_data").as("data_source")
            );

            System.out.println("DEBUG: Schema for " + countryCode + " after explicit column selection:");
            df.printSchema();

            // Step 3: Ensure all required columns are present by adding missing ones with null values
            for (String colName : FINAL_OUTPUT_COLS_ORDER) {
                if (!Arrays.asList(df.columns()).contains(colName)) {
                    System.out.println("DEBUG: Adding missing column '" + colName + "' for " + countryCode);
                    df = df.withColumn(colName, lit(null));
                }
            }

            // Step 4: Reorder columns to match FINAL_OUTPUT_COLS_ORDER exactly
            Column[] finalOrderCols = Arrays.stream(FINAL_OUTPUT_COLS_ORDER)
                .map(functions::col)
                .toArray(Column[]::new);
            df = df.select(finalOrderCols);

            System.out.println("DEBUG: Final schema for " + countryCode + " (columns: " + df.columns().length + "):");
            df.printSchema();
            System.out.println("DEBUG: Column names for " + countryCode + ": " + Arrays.toString(df.columns()));

            return df;

        } catch (Exception e) {
            System.err.println("Error in processCountryData for " + countryCode + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
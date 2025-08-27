package com.debopam.tablesaw.table;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.median;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.aggregate.AggregateFunctions.range;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestReadCSVTableData {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestReadCSVTableData.class);

    static Table airlinesflightsdata;
    static Table carsData;

    @BeforeAll
    static void setup() {
        // Initialize any required resources or configurations for the tests
        carsData = Table.read().csv("src/test/resources/Cars_Datasets_2025.csv");
        airlinesflightsdata = Table.read().csv("src/test/resources/airlines_flights_data.csv");
    }

    // Add test methods here to validate the functionality of reading CSV data into Tables
    // For example, you can test the structure, data types, and contents of the loaded
    // tables, ensuring they match expected values or formats.
    // Example test method (to be implemented):

    @Test
    @Order(1)
    public void testCarsDataValues() {
        LOGGER.info("Cars data :{}", carsData);

        assertEquals(11, carsData.columnCount(), "Cars data should have 10 columns.");
        assertEquals("Company Names", carsData.column(0).name(), "First column should be 'Make'.");
        assertEquals("Cars Names", carsData.column(1).name(), "Second column should be 'Model'.");
        LOGGER.info("Cars data loaded successfully with {} rows and {} columns.",
                carsData.rowCount(), carsData.columnCount());
    }

    @Test
    @Order(2)
    public void testAirlinesFlightsDataValues() {
        LOGGER.info("Airlines data :{}", airlinesflightsdata);
        assertEquals(12, airlinesflightsdata.columnCount(), "Airlines flights data should have 10 columns.");
        assertEquals("airline", airlinesflightsdata.column(1).name(), "Second column should be 'airline'.");
        assertEquals("source_city", airlinesflightsdata.column(3).name(), "Third column should be 'source_city'.");
        assertEquals("destination_city", airlinesflightsdata.column(7).name(), "Seventh column should be destination_city.");

        LOGGER.info("Airlines flights data loaded successfully with {} rows and {} columns.",
                airlinesflightsdata.rowCount(), airlinesflightsdata.columnCount());
    }

    // Additional test methods can be added to validate specific data characteristics,
    // such as checking for missing values, data types, or specific data entries.
    // For example, you can check if certain expected values exist in the data,
    // or if the data types of each column match the expected types.

    @Test
    @Order(3)
    public void testCarsDataStructure() {
        Table carsDataTableStrcuture = carsData.structure();
        LOGGER.info("Cars data structure: {}", carsDataTableStrcuture);
    }

    @Test
    @Order(4)
    public void testAddColumnWithType() {
        Table airlinesDataStructure = airlinesflightsdata.structure();
        LOGGER.info("Airlines data structure: {}", airlinesDataStructure);
        Column<?> originalColumn = airlinesflightsdata.column("price");
        DoubleColumn newPriceColumn = DoubleColumn.create("new_price_column", originalColumn.size());
        for (int i = 0; i < originalColumn.size(); i++) {
            try {
                newPriceColumn.set(i, Double.parseDouble(originalColumn.getString(i)));
                //Alternatively, cast to double directly because the column is already numeric
                // newPriceColumn.append((double)originalColumn.get(i));
            } catch (NumberFormatException e) {
                LOGGER.warn("Skipping invalid number format at index {}: {}", i, originalColumn.getString(i));
                newPriceColumn.setMissing(i);
            }
        }
        LOGGER.info("Table rows: {}, columns: {}", airlinesflightsdata.rowCount(), airlinesflightsdata.columnCount());
        LOGGER.info("Adding new column 'new_price_column' to airlines flights data. rows:{}", newPriceColumn.size());
        airlinesflightsdata.addColumns(newPriceColumn);
        LOGGER.info("New column 'new_price_column' added to airlines flights data.");
        assertEquals("new_price_column", airlinesflightsdata.column("new_price_column").name(), "New column should be 'new_price_column'.");
        assertEquals(DoubleColumn.class, airlinesflightsdata.column("new_price_column").getClass(),
                "New column should be of type DoubleColumn.");
        LOGGER.info("New column 'new_price_column' added successfully with {} rows.",
                airlinesflightsdata.column(12).size());
        Table structure = airlinesflightsdata.structure();
        LOGGER.info("Updated airlines flights data structure: {}", structure);
        LOGGER.info("Airlines updated data: {}", airlinesflightsdata);
    }

    @Test
    @Order(5)
    public void testCleanDataAndSummerizeNumericColumn() {
        StringColumn carsPricesColumn = carsData.stringColumn("Cars Prices");
        DoubleColumn newPriceColumn = DoubleColumn.create("Updated Car Price", carsPricesColumn.size());
        for (int r = 0; r < carsData.rowCount(); r++) {
            String[] carPrices = carsPricesColumn.get(r).split("[-/�]");
            String lowerBoundStr = carPrices[0].trim().replaceAll("[^0-9]", "");
            String upperBoundStr = (carPrices.length == 2) ? carPrices[1].trim().replaceAll("[^0-9]", "") : "0";
            double lowerBound = (!lowerBoundStr.isEmpty()) ? Double.parseDouble(lowerBoundStr) : 0;
            double upperBound = (!upperBoundStr.isEmpty()) ? Double.parseDouble(upperBoundStr) : 0;
            LOGGER.trace("Row {}: Car Prices='{}', Lower Bound='{}', Upper Bound='{}'", r, carsPricesColumn.get(r), lowerBoundStr, upperBoundStr);
            if (carPrices.length == 0 || (lowerBound == 0 && upperBound == 0)) {
                LOGGER.warn("Skipping empty or null price at row {}: {}", r, carPrices);
                newPriceColumn.setMissing(r);
            } else {
                int computedPrice = (upperBound == 0 || lowerBound == 0) ? (int) lowerBound + (int) upperBound : ((int) lowerBound + (int) upperBound) / 2;
                newPriceColumn.set(r, computedPrice);
            }
        }
        carsData.addColumns(newPriceColumn);
        Table carsSummary = carsData.summarize("Updated Car Price", mean, median, range, max, min).apply();
        LOGGER.info("Cars data summary: {}", carsSummary);
        LOGGER.info("Cars data after adding 'Updated Car Price' column: {}", carsData.sortDescendingOn("Updated Car Price"));
        assertTrue(carsSummary.rowCount() > 0, "Cars data summary should have rows.");
        assertTrue(carsSummary.columnCount() > 0, "Cars data summary should have columns.");
    }
}

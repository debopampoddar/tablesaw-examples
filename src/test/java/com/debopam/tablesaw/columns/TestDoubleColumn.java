package com.debopam.tablesaw.columns;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.selection.Selection;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Tablesaw DoubleColumn class.
 * <p>
 * This class verifies the core functionalities of the DoubleColumn, including:
 * <ul>
 *     <li>Column creation with and without data</li>
 *     <li>Filtering and selection operations</li>
 *     <li>Mapping and transformation of values</li>
 *     <li>Statistical calculations (mean, sum, standard deviation)</li>
 *     <li>Handling and removal of missing values</li>
 *     <li>Direct modification of column values</li>
 * </ul>
 * Each test method is designed to validate a specific aspect of DoubleColumn behavior.
 */
public class TestDoubleColumn {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestDoubleColumn.class);

    // A DoubleColumn pre-populated with values 1 through 10, used as a base for most tests.
    private static final DoubleColumn DEFAULT_DOUBLE_COLUMN = DoubleColumn.create("default_double_column", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // An empty DoubleColumn with only a name, used to test column creation and size.
    private static final DoubleColumn DEFAULT_EMPTY_DOUBLE_COLUMN = DoubleColumn.create("Empty Column");

    // An empty DoubleColumn with a specified initial size (10), used to test size and value modification.
    private static final DoubleColumn DEFAULT_SIZED_DOUBLE_COLUMN = DoubleColumn.create("Sized Column", 10);

    /**
     * Verifies that a DoubleColumn can be created with a specific name and is initially empty.
     * Ensures the column's name and size are as expected.
     */
    @Test
    public void testColumdoubleColumnreationWithName() {
        DoubleColumn column = DEFAULT_EMPTY_DOUBLE_COLUMN.copy();
        assertEquals("Empty Column", column.name(), "Column name should match the specified name.");
        assertEquals(0, column.size(), "Newly created column should have size 0.");
        LOGGER.info("Column created with name: {}", column.name());
    }

    /**
     * Checks that a DoubleColumn created with initial data has the correct name, size, and values.
     * Also ensures that copying the column produces a distinct object.
     */
    @Test
    public void testColumdoubleColumnreationWithData() {
        DoubleColumn doubleColumn = DEFAULT_DOUBLE_COLUMN.copy();
        assertNotEquals(doubleColumn, DEFAULT_DOUBLE_COLUMN, "Copy should not be the same object as the original.");
        assertEquals("default_double_column", doubleColumn.name(), "Column name should match the original.");
        assertEquals(10, doubleColumn.size(), "Column should contain 10 elements.");
        assertEquals(1.0, doubleColumn.get(0), 0.001, "First value should be 1.0.");
        LOGGER.info("Column data: {}", doubleColumn.print());
    }

    /**
     * Tests filtering a DoubleColumn to include only values greater than 8.
     * Verifies the filtered column contains exactly 9.0 and 10.0.
     */
    @Test
    public void testFilterColumnData() {
        DoubleColumn doubleColumn = DEFAULT_DOUBLE_COLUMN.copy();
        DoubleColumn filtered = doubleColumn.where(doubleColumn.isGreaterThan(8));
        assertEquals(2, filtered.size(), "Filtered column should have 2 values.");
        assertTrue(Arrays.equals(new Double[]{9.0, 10.0}, filtered.asObjectArray()), "Filtered values should be 9.0 and 10.0.");
        LOGGER.info("Filtered column data: {}", filtered.print());
    }

    /**
     * Tests filtering with multiple conditions: values greater than 2 and not equal to 5 or 6.
     * Ensures the resulting column contains only the expected values.
     */
    @Test
    public void testFilterColumnDataUsingMutipleConditions() {
        DoubleColumn doubleColumn = DEFAULT_DOUBLE_COLUMN.copy();
        DoubleColumn filtered = doubleColumn.where(doubleColumn.isGreaterThan(2).and(doubleColumn.isNotIn(5, 6)));
        assertEquals(6, filtered.size(), "Filtered column should have 6 values.");
        assertTrue(Arrays.equals(new Double[]{3.0, 4.0, 7.0, 8.0, 9.0, 10.0}, filtered.asObjectArray()), "Filtered values should match expected.");
        LOGGER.info("Filtered column data with multiple filtering selection: {}", filtered.print());
    }

    /**
     * Validates mapping operations by multiplying all values less than 5 by 2.
     * Checks that the resulting values are as expected.
     */
    @Test
    public void testColumnMapFudoubleColumntions() {
        DoubleColumn doubleColumn = DEFAULT_DOUBLE_COLUMN.where(DEFAULT_DOUBLE_COLUMN.isLessThan(5.0)).copy();
        DoubleColumn newMultipliedColumn = doubleColumn.multiply(2);
        assertEquals(4, newMultipliedColumn.size(), "Should have 4 values after filtering.");
        assertTrue(Arrays.equals(new Double[]{2.0, 4.0, 6.0, 8.0}, newMultipliedColumn.asObjectArray()), "Values should be doubled.");
        LOGGER.info("Column value after multipiled by 2: {}", newMultipliedColumn.asDoubleColumn().print());
    }

    /**
     * Tests selection of specific indices and ranges from a DoubleColumn.
     * Verifies that the correct values are selected for both index-based and range-based selection.
     */
    @Test
    public void testColumnDataSelection() {
        DoubleColumn doubleColumn = DEFAULT_DOUBLE_COLUMN.copy();
        // Select values at index 0 and 4 (should be 1.0 and 5.0)
        assertTrue(Arrays.equals(new Double[]{1.0, 5.0}, doubleColumn.where(Selection.with(0, 4)).asObjectArray()), "Selected values should be 1.0 and 5.0.");
        LOGGER.info("Column values of index 0 and 4: {}", doubleColumn.where(Selection.with(0, 4)).print());
        // Select values from index 1 to 3 (should be 2.0, 3.0, 4.0)
        assertTrue(Arrays.equals(new Double[]{2.0, 3.0, 4.0}, doubleColumn.where(Selection.withRange(1, 4)).asObjectArray()), "Selected values should be 2.0, 3.0, 4.0.");
        LOGGER.info("Column values from 1 to 3: {}", doubleColumn.where(Selection.withRange(1, 4)).print());
    }

    /**
     * Verifies statistical calculations on a DoubleColumn, including mean, sum, and standard deviation.
     * Appends data to a sized column and checks the results of each calculation.
     */
    @Test
    public void testColumnStatisticalMethods() {
        DoubleColumn dataColumn = DEFAULT_SIZED_DOUBLE_COLUMN.copy();
        dataColumn.append(DEFAULT_DOUBLE_COLUMN);

        assertEquals(20.0, dataColumn.size(), "Column should have 20 elements after appending.");

        LOGGER.info("Column data: {}", dataColumn.print());
        double mean = dataColumn.mean();
        LOGGER.info("Mean: {}", mean);
        assertEquals(5.5, mean, 0.001, "Mean should be 5.5.");

        double sum = dataColumn.sum();
        LOGGER.info("Sum: {}", sum);
        assertEquals(55, sum, 0.001, "Sum should be 55.");

        double stdDev = dataColumn.standardDeviation();
        assertEquals(3.027, stdDev, 0.001, "Standard deviation should be approximately 3.027.");
        LOGGER.info("Standard Deviation: {}", stdDev);
    }

    /**
     * Tests direct modification of values in a DoubleColumn.
     * Ensures that values can be set and retrieved as expected, and that nulls are handled.
     */
    @Test
    public void testModifyValuesOfDoubleColumn() {
        DoubleColumn dataColumn = DEFAULT_SIZED_DOUBLE_COLUMN.copy();
        assertEquals(10.0, dataColumn.size(), "Column should have 10 elements.");
        LOGGER.info("Column data: {}", dataColumn.print());
        assertNull(dataColumn.get(0), "First value should be null before setting.");
        dataColumn.set(0, 120.0);
        assertEquals(120.0, dataColumn.get(0), "First value should be updated to 120.0.");
    }

    /**
     * Validates handling of missing (NaN) values in a DoubleColumn.
     * Checks that missing values can be detected and removed, and that the column size decreases after removal.
     */
    @Test
    public void testMissingValuesOfDoubleColumn() {
        double[] data = {10.5, 12.3, 8.9, 15.1, Double.NaN, 9.2};
        DoubleColumn prices = DoubleColumn.create("Price", data);
        Selection missingSelection = prices.isMissing();
        LOGGER.info("Missing Columns: {}", missingSelection);
        assertEquals(6, prices.size(), "Column should have 6 elements.");
        LOGGER.info("Prices Columns Before removing missing data: {}", prices.print());
        DoubleColumn afterRemovingMIssing = prices.removeMissing();
        LOGGER.info("Modified view After removing missing data: {}", afterRemovingMIssing.print());
        LOGGER.info("Prices Columns After removing missing data: {}", prices.print());
        assertTrue(prices.size() > afterRemovingMIssing.size(), "Size should decrease after removing missing values.");
    }
}

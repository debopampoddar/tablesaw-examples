package com.debopam.tablesaw.columns;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStringColumn {
    // Default StringColumn used in multiple tests
    private static StringColumn DEFAULT_STRING_COLUMN = StringColumn.create("default_string_column", "a","b", "c", "d", "e", "f");

    /**
     * Test creation of a StringColumn with a specific name.
     */
    @Test
    public void testColumnCreationWithName() {
        // Create an empty StringColumn with a name
        StringColumn column = StringColumn.create("My Column");
        assertEquals("My Column", column.name());
        assertEquals(0, column.size());
    }
}

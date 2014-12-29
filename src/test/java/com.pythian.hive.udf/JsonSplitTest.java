package com.pythian.hive.udf;

import groovy.json.JsonException;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class JsonSplitTest{

    @Test
    public void basicJsonArrayTest() throws IOException, JsonException {
        String jsonString = "[\"a\",\"b\",\"c\"]";
        JsonSplitUDF udf = new JsonSplitUDF();
        ArrayList<Object[]> splits = udf.splitJsonString(jsonString);
        org.junit.Assert.assertEquals(3, splits.size());
        // Test elements are unpacked correctly
        org.junit.Assert.assertEquals("a", splits.get(0)[1]);
        org.junit.Assert.assertEquals("b", splits.get(1)[1]);
        org.junit.Assert.assertEquals("c", splits.get(2)[1]);
        // Test indices are assigned for each element
        org.junit.Assert.assertEquals(0, splits.get(0)[0]);
        org.junit.Assert.assertEquals(1, splits.get(1)[0]);
        org.junit.Assert.assertEquals(2, splits.get(2)[0]);
    }

    @Test
    public void nestedJsonArrayTest() throws IOException, JsonException {
        String jsonString = "[{\"a\":1},{\"b\":\"c\"}]";
        JsonSplitUDF udf = new JsonSplitUDF();
        ArrayList<Object[]> splits = udf.splitJsonString(jsonString);
        // Nested objects are just strings
        org.junit.Assert.assertEquals("{\"a\":1}", splits.get(0)[1]);
        org.junit.Assert.assertEquals("{\"b\":\"c\"}", splits.get(1)[1]);
    }

    @Test
    public void escapedQuoteStringTest() throws IOException, JsonException {
        String jsonString = "[\"Hi there, \\\"quotes\\\"\"]";
        JsonSplitUDF udf = new JsonSplitUDF();
        ArrayList<Object[]> splits = udf.splitJsonString(jsonString);
        // Escaped quotes are handled
        org.junit.Assert.assertEquals("Hi there, \"quotes\"", splits.get(0)[1]);
    }
}
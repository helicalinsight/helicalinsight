package com.helicalinsight.instant.ai.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PatternExtractorTest {

    @Test
    public void extractFromPatternReturnsCapturedGroup() {
        String result = PatternExtractor.extractFromPattern("1. Revenue trend:", "^\\d+\\.\\s*(.*?):");
        assertEquals("Revenue trend", result);
    }

    @Test
    public void extractFromPatternReturnsOriginalWhenNoMatch() {
        String original = "No numbered prefix here";
        String result = PatternExtractor.extractFromPattern(original, "^\\d+\\.\\s*(.*?):");
        assertEquals(original, result);
    }

    @Test
    public void extractFromPatternTrimsCapturedValue() {
        String result = PatternExtractor.extractFromPattern("2.   Sales by region  :", "^\\d+\\.\\s*(.*?):");
        assertEquals("Sales by region", result);
    }
}

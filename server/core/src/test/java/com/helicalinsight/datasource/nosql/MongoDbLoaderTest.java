package com.helicalinsight.datasource.nosql;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MongoDbLoaderTest {

    @Test
    public void rejectsIncompleteConnectionDetails() {
        assertFalse(MongoDbLoader.testConnection(null, "demo", "customers"));
        assertFalse(MongoDbLoader.testConnection("mongodb://localhost:27017", null, "customers"));
        assertFalse(MongoDbLoader.testConnection("mongodb://localhost:27017", "demo", ""));
    }

    @Test
    public void acceptsOnlyPositiveSampleLimit() {
        assertTrue(MongoDbLoader.sample(null, "demo", "customers", 0).isEmpty());
        assertTrue(MongoDbLoader.sample("", "demo", "customers", 10).isEmpty());
    }
}

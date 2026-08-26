package com.helicalinsight.datasource.nosql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoDbLoaderTest {

    @Test
    void rejectsIncompleteConnectionDetails() {
        assertFalse(MongoDbLoader.testConnection(null, "demo", "customers"));
        assertFalse(MongoDbLoader.testConnection("mongodb://localhost:27017", null, "customers"));
        assertFalse(MongoDbLoader.testConnection("mongodb://localhost:27017", "demo", ""));
    }

    @Test
    void acceptsOnlyPositiveSampleLimit() {
        assertTrue(MongoDbLoader.sample(null, "demo", "customers", 0).isEmpty());
        assertTrue(MongoDbLoader.sample("", "demo", "customers", 10).isEmpty());
    }
}

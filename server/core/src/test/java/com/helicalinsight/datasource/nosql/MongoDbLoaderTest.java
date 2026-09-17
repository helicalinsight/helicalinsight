package com.helicalinsight.datasource.nosql;

import org.junit.Test;

public class MongoDbLoaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankUri() {
        MongoDbLoader.testConnection(null, "demo", "customers");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankDatabaseName() {
        MongoDbLoader.testConnection("mongodb://localhost:27017", null, "customers");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankCollectionName() {
        MongoDbLoader.testConnection("mongodb://localhost:27017", "demo", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidSampleLimit() {
        MongoDbLoader.sample("mongodb://localhost:27017", "demo", "customers", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankSampleUri() {
        MongoDbLoader.sample("", "demo", "customers", 10);
    }
}

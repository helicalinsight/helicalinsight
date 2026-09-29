package com.helicalinsight.datasource.managed;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConnectionProviderUtilityTest {

    @Test
    public void mongoJdbcUrlProvidesDatabaseProperty() {
        Properties properties = new Properties();

        ConnectionProviderUtility.setMongoDatabaseProperty(
                properties,
                "jdbc:mongodb://cluster.example.mongodb.net/analytics?ssl=true&authSource=admin");

        assertEquals("analytics", properties.getProperty("database"));
    }

    @Test
    public void mongoJdbcUrlPreservesExplicitDatabaseProperty() {
        Properties properties = new Properties();
        properties.setProperty("database", "configured");

        ConnectionProviderUtility.setMongoDatabaseProperty(
                properties,
                "jdbc:mongodb://cluster.example.mongodb.net/analytics?ssl=true");

        assertEquals("configured", properties.getProperty("database"));
    }

    @Test
    public void nonMongoJdbcUrlDoesNotAddDatabaseProperty() {
        Properties properties = new Properties();

        ConnectionProviderUtility.setMongoDatabaseProperty(
                properties,
                "jdbc:postgresql://localhost:5432/analytics");

        assertFalse(properties.containsKey("database"));
    }
}
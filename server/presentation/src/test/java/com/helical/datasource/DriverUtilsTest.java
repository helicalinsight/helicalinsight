package com.helical.datasource;

import org.junit.After;
import org.junit.Test;

import com.helicalinsight.efw.framework.PluginUtils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DriverUtilsTest {

    /**
     * Dummy JDBC Driver for testing
     */
    public static class DummyDriver implements Driver {

        @Override
        public Connection connect(String url, Properties info) {
            return null;
        }

        @Override
        public boolean acceptsURL(String url) {
            return false;
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
            return new DriverPropertyInfo[0];
        }

        @Override
        public int getMajorVersion() {
            return 1;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public boolean jdbcCompliant() {
            return false;
        }

        @Override
        public Logger getParentLogger() {
            return Logger.getGlobal();
        }
    }

    private final DummyDriver dummyDriver = new DummyDriver();

    @After
    public void cleanup() throws SQLException {
        try {
            DriverManager.deregisterDriver(dummyDriver);
        } catch (SQLException ignored) {
            // Ignore if already deregistered / never registered
        }
    }

    @Test
    public void shouldContainRegisteredDriver() throws SQLException {
        // Arrange
        DriverManager.registerDriver(dummyDriver);

        // Act
        List<String> loadedClasses = PluginUtils.getLoadedClasses();

        // Assert
        assertTrue(
                "Registered driver should be present in loaded classes",
                loadedClasses.contains(DummyDriver.class.getName())
        );
    }

    @Test
    public void shouldNotContainUnregisteredDriver() {
        List<String> loadedClasses = PluginUtils.getLoadedClasses();
        assertFalse(
                "Unregistered driver should not be present in loaded classes",
                loadedClasses.contains(DummyDriver.class.getName())
        );
    }
}
package com.helicalinsight.datasource;

import java.sql.*;
import java.util.Properties;

/**
 * DriverShim implements {@link Driver}
 * Used for the database connection
 *
 * @author Unknown
 * @since 1.0
 */
public class DriverShim implements Driver {
	/**
	 * getDriverName()
	 * @return driver name
	 */
    public String getDriverName() {
        if (this.driver == null) {
            return "null";
        }
        return this.driver.getClass().getName();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        DriverShim shim = (DriverShim) other;

        if ((driver == null)) {
            return shim.driver == null;
        } else {
            return (shim.driver != null) && (driver.getClass().getName().equals(shim.driver.getClass().getName()));
        }
    }

    @Override
    public int hashCode() {
        return driver != null ? driver.hashCode() : 0;
    }

    /**
     * The JDBC driver class object
     */
    private final Driver driver;

    /**
     * DriverShim(Driver driver)
     * Sets the the JDBC driver class object
     *
     * @param driver The JDBC driver class object
     */
    public DriverShim(Driver driver) {
        this.driver = driver;
    }

    /**
     * connect(String url, Properties info)
     * Connects to the database
     *
     * @param url  		url as a string
     * @param info 		{@code java.util.Properties} object
     * @return The JDBC connection object
     * @throws SQLException If some thing goes wrong ):
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.driver.connect(url, info);
    }

    /**
     * acceptsURL(String url)
     * Returns true if the url is accepted otherwise false
     *
     * @param url 			 url as a string
     * @return true if the url is accepted otherwise false
     * @throws SQLException If some thing goes wrong ):
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return this.driver.acceptsURL(url);
    }

    /**
     * getPropertyInfo(String url, Properties info)
     * Returns DriverPropertyInfo array
     *
     * @param url  			url as a string
     * @param info 			{@code java.util.Properties} object
     * @return DriverPropertyInfo array
     * @throws SQLException If some thing goes wrong ):
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return this.driver.getPropertyInfo(url, info);
    }

    /**
     * getMajorVersion()
     * Returns the version number as an integer
     * @return The major version number as an integer
     */
    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    /**
     * getMinorVersion()
     * Returns the version number as an integer
     * @return The minor version number as an integer
     */
    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    /**
     * jdbcCompliant()
     * Returns true if jdbc compliant
     * @return true if jdbc compliant
     */
    @Override
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}

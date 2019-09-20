/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource;

import java.sql.*;
import java.util.Properties;

/**
 * Used for the database connection
 *
 * @author Unknown
 */
public class DriverShim implements Driver {

    /**
     * The JDBC driver class object
     */
    private final Driver driver;

    /**
     * Sets the the JDBC driver class object
     *
     * @param driver The JDBC driver class object
     */
    public DriverShim(Driver driver) {
        this.driver = driver;
    }

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
     * Connects to the database
     *
     * @param url  The url as a string
     * @param info The java.util.Properties object
     * @return The JDBC connection object
     * @throws SQLException If some thing goes wrong ):
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.driver.connect(url, info);
    }

    /**
     * Returns true if the url is accepted otherwise false
     *
     * @param url The url as a string
     * @return true if the url is accepted otherwise false
     * @throws SQLException If some thing goes wrong ):
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return this.driver.acceptsURL(url);
    }

    /**
     * Returns DriverPropertyInfo array
     *
     * @param url  The url as a string
     * @param info The java.util.Properties object
     * @return DriverPropertyInfo array
     * @throws SQLException If some thing goes wrong ):
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return this.driver.getPropertyInfo(url, info);
    }

    /**
     * Returns the version number as an integer
     *
     * @return The major version number as an integer
     */
    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    /**
     * Returns the version number as an integer
     *
     * @return The minor version number as an integer
     */
    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    /**
     * Returns true if jdbc compliant
     *
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

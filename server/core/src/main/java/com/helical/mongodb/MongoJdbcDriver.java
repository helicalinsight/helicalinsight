package com.helical.mongodb;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Helical Insight MongoDB JDBC driver adapter.
 *
 * Converts Helical Insight MongoDB URLs (mongodb:// and mongodb+srv://)
 * into the JDBC URL format expected by the MongoDB JDBC driver.
 */
public class MongoJdbcDriver implements Driver {

    private final com.mongodb.jdbc.MongoDriver delegate;

    public MongoJdbcDriver() {
        this.delegate = new com.mongodb.jdbc.MongoDriver();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            return null;
        }

        String jdbcUrl = toJdbcUrl(url);

        return delegate.connect(jdbcUrl, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }

        return url.startsWith("mongodb://")
                || url.startsWith("mongodb+srv://")
                || delegate.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
            throws SQLException {

        if (url != null) {
            url = toJdbcUrl(url);
        }

        return delegate.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return delegate.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }

    private String toJdbcUrl(String url) {
        if (url.startsWith("mongodb://")) {
            return "jdbc:" + url;
        }

        if (url.startsWith("mongodb+srv://")) {
            return "jdbc:" + url;
        }

        return url;
    }
}

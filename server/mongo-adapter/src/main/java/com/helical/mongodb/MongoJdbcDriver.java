package com.helical.mongodb;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class MongoJdbcDriver implements Driver {

    private final Driver delegate;

    public MongoJdbcDriver() {
        this.delegate = new com.mongodb.jdbc.MongoDriver();
    }

    private String adaptUrl(String url) {
        if (url == null) {
            return null;
        }

        if (url.startsWith("mongodb://")
                || url.startsWith("mongodb+srv://")) {
            return "jdbc:" + url;
        }

        return url;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return delegate.connect(adaptUrl(url), info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return delegate.acceptsURL(adaptUrl(url));
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(
            String url, Properties info) throws SQLException {
        return delegate.getPropertyInfo(adaptUrl(url), info);
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
    public Logger getParentLogger()
            throws SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }

    static {
        try {
            DriverManager.registerDriver(new MongoJdbcDriver());
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

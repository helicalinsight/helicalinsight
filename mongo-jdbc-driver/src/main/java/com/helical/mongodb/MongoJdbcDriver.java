package com.helical.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class MongoJdbcDriver implements Driver {

    private static final String JDBC_MONGODB_PREFIX = "jdbc:mongodb://";
    private static final String JDBC_MONGODB_SRV_PREFIX = "jdbc:mongodb+srv://";
    private static final String MONGODB_PREFIX = "mongodb://";
    private static final String MONGODB_SRV_PREFIX = "mongodb+srv://";

    static {
        try {
            DriverManager.registerDriver(new MongoJdbcDriver());
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Connection connect(String url, Properties info)
            throws SQLException {

        if (!acceptsURL(url)) {
            return null;
        }

        // MongoClientURI expects a standard MongoDB connection string,
        // so remove the JDBC prefix when one is present.
        String mongoUrl = url;

        if (url.startsWith("jdbc:")) {
            mongoUrl = url.substring("jdbc:".length());
        }

        MongoClientURI mongoUri =
                new MongoClientURI(mongoUrl);

        MongoClient mongoClient =
                new MongoClient(mongoUri);

        String databaseName =
                mongoUri.getDatabase();

        if (databaseName == null || databaseName.isBlank()) {
            mongoClient.close();

            throw new SQLException(
                    "MongoDB JDBC URL must specify a database"
            );
        }

        return new MongoConnection(mongoClient, databaseName, url);
    }

    @Override
    public boolean acceptsURL(String url) {
        return url != null &&
                (url.startsWith(JDBC_MONGODB_PREFIX)
                        || url.startsWith(JDBC_MONGODB_SRV_PREFIX)
                        || url.startsWith(MONGODB_PREFIX)
                        || url.startsWith(MONGODB_SRV_PREFIX));
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(
            String url,
            Properties info) {

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
    public Logger getParentLogger()
            throws SQLFeatureNotSupportedException {

        throw new SQLFeatureNotSupportedException(
                "Parent logger is not supported"
        );
    }
}
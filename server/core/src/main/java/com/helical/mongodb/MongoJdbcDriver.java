package com.helical.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;


public class MongoJdbcDriver implements Driver {

    public static final String URL_PREFIX_MONGODB = "mongodb://";
    public static final String URL_PREFIX_MONGODB_SRV = "mongodb+srv://";
    public static final String JDBC_PREFIX = "jdbc:";

    static {
        try {
            DriverManager.registerDriver(new MongoJdbcDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to register the Helical MongoDB JDBC driver.", e);
        }
    }


    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        String effectiveUri = buildEffectiveUri(url, info);

        try {
            MongoClientURI mongoClientURI = new MongoClientURI(effectiveUri);
            MongoClient mongoClient = new MongoClient(mongoClientURI);

            String databaseName = mongoClientURI.getDatabase();
            if (isBlank(databaseName) && info != null) {
                databaseName = info.getProperty("database");
            }
            if (isBlank(databaseName)) {
                databaseName = "admin";
            }

            DB db = mongoClient.getDB(databaseName);
            //Eagerly validate the connection/credentials, exactly like a real JDBC driver would
            //fail fast on DriverManager.getConnection(...) for a bad host/user/password.
            db.command(new BasicDBObject("ping", 1));

            return new MongoJdbcConnection(mongoClient, db, url, databaseName);
        }  catch (Exception ex) {
            throw new SQLException("Unable to connect to the MongoDB server: " + ex.getMessage(), ex);
        }
    }


    @Override
    public boolean acceptsURL(String url) {
        if (url == null) {
            return false;
        }
        String normalized = url.startsWith(JDBC_PREFIX) ? url.substring(JDBC_PREFIX.length()) : url;
        return normalized.startsWith(URL_PREFIX_MONGODB) || normalized.startsWith(URL_PREFIX_MONGODB_SRV);
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
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("java.util.logging is not used by the Helical MongoDB JDBC driver.");
    }

    private static String buildEffectiveUri(String rawUrl, Properties info) {
        String url = rawUrl.startsWith(JDBC_PREFIX) ? rawUrl.substring(JDBC_PREFIX.length()) : rawUrl;

        String username = info != null ? info.getProperty("user") : null;
        String password = info != null ? info.getProperty("password") : null;
        if (isBlank(username)) {
            return url;
        }

        String scheme;
        if (url.startsWith(URL_PREFIX_MONGODB_SRV)) {
            scheme = URL_PREFIX_MONGODB_SRV;
        } else if (url.startsWith(URL_PREFIX_MONGODB)) {
            scheme = URL_PREFIX_MONGODB;
        } else {
            return url;
        }

        String rest = url.substring(scheme.length());
        if (rest.contains("@")) {
            //Credentials are already embedded in the url, do not override them.
            return url;
        }

        return scheme + encode(username) + ":" + encode(password == null ? "" : password) + "@" + rest;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}

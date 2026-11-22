package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * NoSQLLoader implementation for MongoDB that talks to MongoDB directly through the
 * official MongoDB Java driver (already an existing project dependency), without routing
 * through Apache Drill. This is the implementation backing the always-available "Mongodb"
 * tile under the "No SQL & Big Data" category (see DataSourcesList.groovy).
 * <p>
 * Registered under the Spring bean name "com.helicalinsight.nosql.mongo.native" - this name
 * is what gets stored as the "subType"/"driverName" of the NoSql datasource and is looked up
 * via {@link com.helicalinsight.efw.utility.NoSqlUtils#getNoSqlImplementation(String)}.
 */
@Component("com.helicalinsight.nosql.mongo.native")
@Scope("prototype")
public class MongoNativeLoader extends NoSQLLoader {

    private static final Logger logger = LoggerFactory.getLogger(MongoNativeLoader.class);

    /**
     * MongoDB connections do not need to be registered with any external middleware
     * (unlike the Apache Drill based implementation). The connection details submitted by
     * the user are already persisted generically by NoSqlDataSourceProperties/DSTypeNoSQL,
     * so there is nothing extra to push anywhere else.
     */
    @Override
    public boolean loadToMiddleWare(JsonObject formData) {
        String jdbcUrl = GsonUtility.optString(formData, "jdbcUrl");
        String host = GsonUtility.optString(formData, "host");
        if (StringUtils.isEmpty(jdbcUrl) && StringUtils.isEmpty(host)) {
            throw new EfwServiceException("Either a MongoDB connection URL or a host must be provided.");
        }
        return true;
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String uri = GsonUtility.optString(formData, "jdbcUrl");
        String database = GsonUtility.optString(formData, "database");
        if (StringUtils.isEmpty(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }
        String username = GsonUtility.optString(formData, "userName");
        String password = GsonUtility.optString(formData, "password");

        String host = extractHostPort(uri);

        MongoClientOptions connectOptions = MongoClientOptions.builder()
                .connectTimeout(5000)
                .serverSelectionTimeout(5000)
                .build();

        MongoClient mongoClient = null;
        try {
            if (!StringUtils.isEmpty(host) && uri != null && uri.toLowerCase().startsWith("mongodb://")) {
                if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                    mongoClient = new MongoClient(new ServerAddress(host), connectOptions);
                } else {
                    MongoCredential credential = MongoCredential.createCredential(
                            username, StringUtils.isEmpty(database) ? "admin" : database, password.toCharArray());
                    mongoClient = new MongoClient(new ServerAddress(host), Collections.singletonList(credential), connectOptions);
                }
            } else if (!StringUtils.isEmpty(uri)) {
                mongoClient = new MongoClient(new MongoClientURI(uri, MongoClientOptions.builder(connectOptions)));
            } else {
                throw new EfwServiceException(
                        "A valid MongoDB connection URL (jdbc:// / mongodb://) is required to test the connection.");
            }

            // A lightweight, universally available call that forces the driver to talk to
            // the server and surface authentication/network failures immediately.
            mongoClient.listDatabaseNames().first();
            return true;
        } catch (Exception e) {
            logger.error("MongoDB connection test failed", e);
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    private String extractHostPort(String uri) {
        if (StringUtils.isEmpty(uri)) {
            return null;
        }
        try {
            String[] splitArray = uri.split(":");
            if (splitArray.length >= 3) {
                String hostName = splitArray[1].replace("//", "");
                String port = splitArray[2].contains("/")
                        ? splitArray[2].substring(0, splitArray[2].indexOf("/"))
                        : splitArray[2];
                return hostName + ":" + port;
            }
        } catch (Exception ignored) {
            // Falls through to using the full URI via MongoClientURI instead.
        }
        return null;
    }
}

package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientOptions;
import com.mongodb.DB;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MongoDB NoSQL datasource implementation.
 */
@Component("mongo")
@Scope("prototype")
public class MongoDrillLoader extends NoSQLLoader {

    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {

        JsonObject mongo = new JsonObject();

        String username = GsonUtility.optString(formDataJson, "userName");
        String password = GsonUtility.optString(formDataJson, "password");
        String jdbcUrl = GsonUtility.optString(formDataJson, "jdbcUrl");

        String host = getHostPort(jdbcUrl, true);
        String port = getHostPort(jdbcUrl, false);

        String storageName = GsonUtility.optString(formDataJson, "name");
        String theId = GsonUtility.optString(formDataJson, "theId");

        mongo.addProperty("type", "mongo");

        String connectionString;

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            connectionString = "mongodb://" + host + ":" + port;
        } else {
            connectionString =
                    "mongodb://" +
                    username +
                    ":" +
                    password +
                    "@" +
                    host +
                    ":" +
                    port +
                    "/?authMechanism=SCRAM-SHA-1";
        }

        mongo.addProperty("connection", connectionString);
        mongo.addProperty("enabled", true);

        String drillStorageUrl = DrillCsvDataSourceCreator.getUrlOfDrill();

        String resourceUrl =
                drillStorageUrl +
                "/storage/" +
                storageName +
                "_" +
                theId +
                ".json";

        JsonObject storageJson = new JsonObject();

        storageJson.addProperty(
                "name",
                storageName + "_" + theId
        );

        storageJson.add("config", mongo);

        String result =
                DrillCsvDataSourceCreator.drillRestApiCall(
                        resourceUrl,
                        "POST",
                        storageJson.toString()
                );

        if (result == null) {
            throw new EfwServiceException(
                    "There was some problem creating drill mongo connection"
            );
        }

        try {
            new Gson().fromJson(result, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new EfwServiceException(
                    "There was a problem " + result
            );
        }

        return true;
    }

    /**
     * Extract host or port from a MongoDB URI.
     */
    private String getHostPort(String uri, boolean isHost) {

        if (StringUtils.isBlank(uri)) {
            return "";
        }

        Pattern pattern =
                Pattern.compile(
                        "^mongodb(?:\\+srv)?://(?:[^@]+@)?([^/:]+)(?::(\\d+))?.*"
                );

        Matcher matcher = pattern.matcher(uri);

        if (matcher.matches()) {

            if (isHost) {
                return matcher.group(1);
            }

            String port = matcher.group(2);

            if (port != null) {
                return port;
            }

            return "27017";
        }

        return "";
    }

    @Override
    public boolean testConnection(JsonObject formData) {

        String uri =
                GsonUtility.optString(
                        formData,
                        "jdbcUrl"
                );

        String database =
                GsonUtility.optString(
                        formData,
                        "database"
                );

        if (StringUtils.isBlank(database)) {
            database =
                    GsonUtility.optString(
                            formData,
                            "databaseName"
                    );
        }

        String username =
                GsonUtility.optString(
                        formData,
                        "userName"
                );

        String password =
                GsonUtility.optString(
                        formData,
                        "password"
                );

        int timeout =
                GsonUtility.optInt(
                        formData,
                        "timeOut"
                );

        int maxWait =
                GsonUtility.optInt(
                        formData,
                        "maxWait"
                );

        return testMongoConnection(
                uri,
                database,
                username,
                password,
                timeout,
                maxWait
        );
    }

    /**
     * Tests MongoDB connectivity using the MongoDB connection URI.
     */
    private boolean testMongoConnection(
            String uri,
            String database,
            String username,
            String password,
            int timeout,
            int maxWait) {

        MongoClient mongoClient = null;

        try {

            if (StringUtils.isBlank(uri)) {
                throw new IllegalArgumentException(
                        "MongoDB connection URL is required"
                );
            }

            MongoClientURI mongoClientURI =
                    new MongoClientURI(uri);

            MongoClientOptions options =
                    mongoClientURI.getOptions();

            MongoClientURI finalUri = mongoClientURI;

            if (timeout > 0 || maxWait > 0) {

                MongoClientOptions.Builder builder =
                        MongoClientOptions.builder(options);

                if (timeout > 0) {
                    builder.connectTimeout(timeout);
                }

                if (maxWait > 0) {
                    builder.maxWaitTime(maxWait);
                }

                finalUri =
                        new MongoClientURI(
                                uri,
                                builder
                        );
            }

            mongoClient =
                    new MongoClient(finalUri);

            if (StringUtils.isBlank(database)) {

                database =
                        extractDatabaseName(uri);
            }

            if (StringUtils.isBlank(database)) {

                throw new IllegalArgumentException(
                        "MongoDB database name is required"
                );
            }

            DB db =
                    mongoClient.getDB(database);

            /*
             * Force MongoDB to actually contact the server.
             * getDB() alone does not necessarily validate connectivity.
             */
            db.getStats();

            return db != null;

        } finally {

            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    /**
     * Extract database name from MongoDB URI.
     */
    private String extractDatabaseName(String uri) {

        if (StringUtils.isBlank(uri)) {
            return "";
        }

        MongoClientURI mongoClientURI =
                new MongoClientURI(uri);

        String database = mongoClientURI.getURI();

if (database == null) {
    return "";
}

int slashIndex = database.lastIndexOf("/");

if (slashIndex == -1 || slashIndex == database.length() - 1) {
    return "";
}

database = database.substring(slashIndex + 1);

int queryIndex = database.indexOf("?");

if (queryIndex != -1) {
    database = database.substring(0, queryIndex);
}

        if (database == null) {
            return "";
        }

        database =
                database.replaceFirst("^/", "");

        return database;
    }
}
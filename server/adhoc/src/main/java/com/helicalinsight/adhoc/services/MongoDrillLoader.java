package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Somen
 * Created on 11/15/2017.
 */

@Component("com.helicalinsight.nosql.mongo")
@Scope("prototype")
@Deprecated
public class MongoDrillLoader extends NoSQLLoader {
    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {
        JsonObject mongo = new JsonObject();
        String jdbcUrl = GsonUtility.optString(formDataJson, "jdbcUrl");
        String username = GsonUtility.optString(formDataJson, "userName");
        String password = GsonUtility.optString(formDataJson, "password");
        String database = GsonUtility.optString(formDataJson, "database");
        if (StringUtils.isBlank(database)) {
            database = GsonUtility.optString(formDataJson, "databaseName");
        }
        String authMechanism = GsonUtility.optString(formDataJson, "authMechanism");
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();
        mongo.addProperty("type", "mongo");
        mongo.addProperty("connection", buildConnectionUri(jdbcUrl, username, password, database, authMechanism));
        mongo.addProperty("enabled", true);

        String drillStorageUrl = DrillCsvDataSourceCreator.getUrlOfDrill();
        String resourceUrl = drillStorageUrl + "/storage/" + storageName + "_" + theId + ".json";

        JsonObject storageJson = new JsonObject();
        storageJson.addProperty("name", storageName + "_" + theId);
        storageJson.add("config", mongo);

        String result = DrillCsvDataSourceCreator.drillRestApiCall(resourceUrl, "POST", storageJson.toString());
        if (result == null) {
            throw new EfwServiceException("There was some problem creating drill mongo connection");
        }
        try {
            new Gson().fromJson(result, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new EfwServiceException("There was a problem " + result);
        }
        return true;
    }

    static String buildConnectionUri(String uri, String username, String password, String database,
                                     String authMechanism) {
        if (StringUtils.isBlank(uri)) {
            throw new EfwServiceException("MongoDB connection URI is required.");
        }

        String connectionUri = uri.trim();
        MongoClientURI parsedUri = new MongoClientURI(connectionUri);
        boolean hasUsername = StringUtils.isNotBlank(username);
        boolean hasPassword = StringUtils.isNotBlank(password);
        boolean hasCredentials = parsedUri.getCredentials() != null;
        if (!hasCredentials && (hasUsername || hasPassword)) {
            if (!hasUsername || !hasPassword) {
                throw new EfwServiceException("Both MongoDB username and password are required.");
            }

            int authorityStart = connectionUri.indexOf("://") + 3;
            String userInfo = encodeUriComponent(username) + ":" + encodeUriComponent(password) + "@";
            connectionUri = connectionUri.substring(0, authorityStart) + userInfo
                    + connectionUri.substring(authorityStart);
            hasCredentials = true;
        }

        if (hasCredentials && StringUtils.isNotBlank(database) && !hasUriOption(connectionUri, "authSource")) {
            connectionUri = appendUriOption(connectionUri, "authSource", database);
        }
        if (StringUtils.isNotBlank(authMechanism) && !hasUriOption(connectionUri, "authMechanism")) {
            connectionUri = appendUriOption(connectionUri, "authMechanism", normalizeAuthMechanism(authMechanism));
        }

        new MongoClientURI(connectionUri);
        return connectionUri;
    }

    private static String normalizeAuthMechanism(String authMechanism) {
        if ("MongoCR".equalsIgnoreCase(authMechanism)) {
            return "MONGODB-CR";
        }
        if ("ScramSha1".equalsIgnoreCase(authMechanism)) {
            return "SCRAM-SHA-1";
        }
        if ("ScramSha256".equalsIgnoreCase(authMechanism)) {
            return "SCRAM-SHA-256";
        }
        if ("Plain".equalsIgnoreCase(authMechanism)) {
            return "PLAIN";
        }
        return authMechanism;
    }

    private static String encodeUriComponent(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static boolean hasUriOption(String uri, String optionName) {
        int queryStart = uri.indexOf('?');
        if (queryStart < 0) {
            return false;
        }
        int fragmentStart = uri.indexOf('#', queryStart);
        String query = uri.substring(queryStart + 1, fragmentStart < 0 ? uri.length() : fragmentStart);
        for (String option : query.split("&")) {
            int equalsIndex = option.indexOf('=');
            String name = equalsIndex < 0 ? option : option.substring(0, equalsIndex);
            if (optionName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private static String appendUriOption(String uri, String name, String value) {
        int fragmentStart = uri.indexOf('#');
        String fragment = fragmentStart < 0 ? "" : uri.substring(fragmentStart);
        String baseUri = fragmentStart < 0 ? uri : uri.substring(0, fragmentStart);
        String separator = baseUri.contains("?") ? "&" : "?";
        return baseUri + separator + encodeUriComponent(name) + "=" + encodeUriComponent(value) + fragment;
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String uri = GsonUtility.optString(formData, "jdbcUrl");
        String database = GsonUtility.optString(formData, "database");
        if (StringUtils.isBlank(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }
        String connectionUri = buildConnectionUri(uri, GsonUtility.optString(formData, "userName"),
                GsonUtility.optString(formData, "password"), database,
                GsonUtility.optString(formData, "authMechanism"));
        MongoClientURI mongoUri = new MongoClientURI(connectionUri);
        String targetDatabase = StringUtils.defaultIfBlank(database, mongoUri.getDatabase());
        if (StringUtils.isBlank(targetDatabase)) {
            throw new EfwServiceException("MongoDB database name is required.");
        }

        try (MongoClient mongo = new MongoClient(mongoUri)) {
            mongo.getDatabase(targetDatabase).runCommand(new BasicDBObject("ping", 1));
            return true;
        }
    }
}

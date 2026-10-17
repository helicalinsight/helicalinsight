package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("com.helicalinsight.nosql.mongo")
@Scope("prototype")
@Deprecated
public class MongoDrillLoader extends NoSQLLoader {

    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {
        JsonObject mongo = new JsonObject();

        String username = GsonUtility.optString(formDataJson, "userName");
        String password = GsonUtility.optString(formDataJson, "password");
        String jdbcUrl = GsonUtility.optString(formDataJson, "jdbcUrl");
        String host = getHostPort(jdbcUrl, true);
        String port = getHostPort(jdbcUrl, false);
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();

        mongo.addProperty("type", "mongo");

        String connectionString;
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            connectionString = "mongodb://" + host + ":" + port;
        } else {
            connectionString = "mongodb://" + username + ":" + password
                    + "@" + host + ":" + port + "/?authMechanism=SCRAM-SHA-1";
        }

        mongo.addProperty("connection", connectionString);
        mongo.addProperty("enabled", true);

        String drillStorageUrl = DrillCsvDataSourceCreator.getUrlOfDrill();
        String resourceUrl = drillStorageUrl + "/storage/" + storageName
                + "_" + theId + ".json";

        JsonObject storageJson = new JsonObject();
        storageJson.addProperty("name", storageName + "_" + theId);
        storageJson.add("config", mongo);

        String result = DrillCsvDataSourceCreator.drillRestApiCall(
                resourceUrl, "POST", storageJson.toString());

        if (result == null) {
            throw new EfwServiceException(
                    "There was some problem creating drill mongo connection");
        }

        try {
            new Gson().fromJson(result, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new EfwServiceException("There was a problem " + result);
        }

        return true;
    }

    private String getHostPort(String uri, boolean isHost) {
        if (uri == null || uri.trim().isEmpty()) {
            return "";
        }

        String[] splitArray = uri.split(":");

        if (splitArray.length >= 3) {
            if (isHost) {
                return splitArray[1].replace("//", "");
            }

            int slashIndex = splitArray[2].indexOf("/");
            return slashIndex >= 0
                    ? splitArray[2].substring(0, slashIndex)
                    : splitArray[2];
        }

        return "";
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String host = GsonUtility.optString(formData, "host");
        String uri = GsonUtility.optString(formData, "jdbcUrl");
        String database = GsonUtility.optString(formData, "database");
        String username = GsonUtility.optString(formData, "userName");
        String password = GsonUtility.optString(formData, "password");

        if (StringUtils.isEmpty(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }

        MongoModel mongoModel = new MongoModel();

        if (uri != null && !uri.trim().isEmpty()) {
            mongoModel.setUri(uri);
        }

        if (host != null && !host.trim().isEmpty()) {
            mongoModel.setHost(host);
        }

        mongoModel.setDatabase(database);
        mongoModel.setUsername(username);
        mongoModel.setPassword(password);
        mongoModel.setAuthMechanism(
                GsonUtility.optString(formData, "authMechanism"));

        mongoModel.setTimeout(GsonUtility.optInt(formData, "timeOut"));
        mongoModel.setMaxWait(GsonUtility.optInt(formData, "maxWait"));

        mongoModel.setSsl(GsonUtility.optString(formData, "ssl"));
        mongoModel.setKeyStorePath(
                GsonUtility.optString(formData, "keyStorePath"));
        mongoModel.setKeyStorePassword(
                GsonUtility.optString(formData, "keyStorePassword"));

        return mongoModel.testConnection();
    }
}

class MongoModel {

    private String host;
    private String uri;
    private String database;
    private String username;
    private String password;
    private int timeout;
    private int maxWait;
    private String authMechanism;
    private String ssl;
    private String keyStorePath;
    private String keyStorePassword;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public String getAuthMechanism() {
        return authMechanism;
    }

    public void setAuthMechanism(String authMechanism) {
        this.authMechanism = authMechanism;
    }

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public boolean testConnection() {
        MongoClient mongo = null;

        try {
            MongoClientOptions.Builder options = MongoClientOptions.builder()
                    .socketKeepAlive(true);

            // Apply SSL/TLS when explicitly enabled.
            if ("true".equalsIgnoreCase(ssl)) {
                options.sslEnabled(true);
            }

            if (timeout > 0) {
                options.connectTimeout(timeout);
            }

            if (maxWait > 0) {
                options.serverSelectionTimeout(maxWait);
            }

            MongoClientURI parsedUri = null;

            if (uri != null && !uri.trim().isEmpty()) {
                parsedUri = new MongoClientURI(uri, options);
            }

            String targetDatabase = database;

            if ((targetDatabase == null || targetDatabase.trim().isEmpty())
                    && parsedUri != null) {
                targetDatabase = parsedUri.getDatabase();
            }

            if (targetDatabase == null || targetDatabase.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "MongoDB database is required. Specify it in the URI or database field.");
            }

            if (parsedUri != null
                    && (parsedUri.getCredentials() != null
                    || username == null
                    || username.trim().isEmpty())) {

                // Use credentials and connection options supplied in the URI.
                mongo = new MongoClient(parsedUri);

            } else if (parsedUri != null) {

                // Use the URI hosts with separately supplied credentials.
                List<ServerAddress> seeds = new ArrayList<>();

                for (String seed : parsedUri.getHosts()) {
                    seeds.add(new ServerAddress(seed));
                }

                List<MongoCredential> credentials =
                        createCredentials(targetDatabase);

                mongo = credentials.isEmpty()
                        ? new MongoClient(seeds, options.build())
                        : new MongoClient(seeds, credentials, options.build());

            } else {

                if (host == null || host.trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "MongoDB host is required when a connection URI is not provided.");
                }

                List<MongoCredential> credentials =
                        createCredentials(targetDatabase);

                mongo = credentials.isEmpty()
                        ? new MongoClient(new ServerAddress(host), options.build())
                        : new MongoClient(
                                new ServerAddress(host),
                                credentials,
                                options.build());
            }

            // MongoClient connects lazily; ping verifies actual connectivity.
            mongo.getDatabase(targetDatabase)
                    .runCommand(new org.bson.Document("ping", 1));

            return true;

        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "MongoDB connection failed. Check the URI, host/port, database, "
                            + "credentials, SSL settings, and server availability.",
                    ex);
        } finally {
            if (mongo != null) {
                mongo.close();
            }
        }
    }

    private List<MongoCredential> createCredentials(String targetDatabase) {
        List<MongoCredential> credentials = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            return credentials;
        }

        if (password == null) {
            throw new IllegalArgumentException(
                    "MongoDB password is required when a username is supplied.");
        }

        if ("MongoCR".equalsIgnoreCase(authMechanism)) {
            credentials.add(MongoCredential.createMongoCRCredential(
                    username, targetDatabase, password.toCharArray()));

        } else if ("ScramSha1".equalsIgnoreCase(authMechanism)
                || "SCRAM-SHA-1".equalsIgnoreCase(authMechanism)) {
            credentials.add(MongoCredential.createScramSha1Credential(
                    username, targetDatabase, password.toCharArray()));

        } else if ("Plain".equalsIgnoreCase(authMechanism)) {
            credentials.add(MongoCredential.createPlainCredential(
                    username, targetDatabase, password.toCharArray()));

        } else {
            credentials.add(MongoCredential.createCredential(
                    username, targetDatabase, password.toCharArray()));
        }

        return credentials;
    }
}
package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MongoDrillLoader implements NoSQLLoader for MongoDB data sources in Helical Insight.
 * Handles active MongoDB connection testing and middleware synchronization.
 */
@Component("com.helicalinsight.nosql.mongo")
@Scope("prototype")
public class MongoDrillLoader extends NoSQLLoader {

    private static final Logger logger = LoggerFactory.getLogger(MongoDrillLoader.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_PORT = 27017;

    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {
        String storageName = GsonUtility.optString(formDataJson, "name");
        String theId = GsonUtility.optString(formDataJson, "theId");
        String username = GsonUtility.optString(formDataJson, "userName");
        String password = GsonUtility.optString(formDataJson, "password");
        String jdbcUrl = GsonUtility.optString(formDataJson, "jdbcUrl");

        String host = getHostPort(jdbcUrl, true);
        String port = getHostPort(jdbcUrl, false);
        if (StringUtils.isBlank(host)) {
            host = GsonUtility.optStringValue(formDataJson, "hostName", "localhost");
        }
        if (StringUtils.isBlank(port)) {
            port = String.valueOf(GsonUtility.optIntValue(formDataJson, "port", DEFAULT_PORT));
        }

        JsonObject mongo = new JsonObject();
        mongo.addProperty("type", "mongo");

        String connectionString;
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            connectionString = "mongodb://" + host + ":" + port;
        } else {
            connectionString = "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authMechanism=SCRAM-SHA-1";
        }
        mongo.addProperty("connection", connectionString);
        mongo.addProperty("enabled", true);

        try {
            String drillStorageUrl = DrillCsvDataSourceCreator.getUrlOfDrill();
            if (StringUtils.isNotBlank(drillStorageUrl) && !drillStorageUrl.contains("://:")) {
                String resourceUrl = drillStorageUrl + "/storage/" + storageName + "_" + theId + ".json";
                JsonObject storageJson = new JsonObject();
                storageJson.addProperty("name", storageName + "_" + theId);
                storageJson.add("config", mongo);

                String result = DrillCsvDataSourceCreator.drillRestApiCall(resourceUrl, "POST", storageJson.toString());
                if (result != null) {
                    try {
                        new Gson().fromJson(result, JsonObject.class);
                    } catch (JsonSyntaxException e) {
                        logger.warn("Non-JSON response from Drill storage configuration: {}", result);
                    }
                }
            } else {
                logger.info("Apache Drill middleware is not active; saved standalone MongoDB data source: {}", storageName);
            }
        } catch (Exception ex) {
            logger.warn("Could not register with Drill middleware (standalone MongoDB mode active): {}", ex.getMessage());
        }

        return true;
    }

    private String getHostPort(String uri, boolean isHost) {
        if (StringUtils.isBlank(uri)) {
            return "";
        }
        try {
            String cleanUri = uri.replace("mongodb://", "").replace("mongodb+srv://", "");
            if (cleanUri.contains("@")) {
                cleanUri = cleanUri.substring(cleanUri.indexOf("@") + 1);
            }
            if (cleanUri.contains("/")) {
                cleanUri = cleanUri.substring(0, cleanUri.indexOf("/"));
            }
            if (cleanUri.contains("?")) {
                cleanUri = cleanUri.substring(0, cleanUri.indexOf("?"));
            }
            String[] parts = cleanUri.split(":");
            if (isHost) {
                return parts[0];
            } else {
                return parts.length > 1 ? parts[1] : String.valueOf(DEFAULT_PORT);
            }
        } catch (Exception e) {
            logger.debug("Could not parse host/port from URI: {}", uri);
            return "";
        }
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String host = GsonUtility.optString(formData, "hostName");
        if (StringUtils.isBlank(host)) {
            host = GsonUtility.optString(formData, "host");
        }
        int port = GsonUtility.optIntValue(formData, "port", DEFAULT_PORT);
        String uri = GsonUtility.optString(formData, "jdbcUrl");
        if (StringUtils.isBlank(uri)) {
            uri = GsonUtility.optString(formData, "url");
        }
        String database = GsonUtility.optString(formData, "database");
        if (StringUtils.isBlank(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }
        String username = GsonUtility.optString(formData, "userName");
        if (StringUtils.isBlank(username)) {
            username = GsonUtility.optString(formData, "username");
        }
        String password = GsonUtility.optString(formData, "password");
        String authMechanism = GsonUtility.optString(formData, "authMechanism");
        String collection = GsonUtility.optString(formData, "collection");

        int timeout = GsonUtility.optIntValue(formData, "timeOut", DEFAULT_TIMEOUT_MS);
        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT_MS;
        }

        MongoModel mongoModel = new MongoModel();
        mongoModel.setHost(host);
        mongoModel.setPort(port);
        mongoModel.setUri(uri);
        mongoModel.setDatabase(database);
        mongoModel.setUsername(username);
        mongoModel.setPassword(password);
        mongoModel.setAuthMechanism(authMechanism);
        mongoModel.setCollection(collection);
        mongoModel.setTimeout(timeout);

        return mongoModel.testConnection();
    }
}

class MongoModel {

    private static final Logger logger = LoggerFactory.getLogger(MongoModel.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_PORT = 27017;

    private String host;
    private int port = DEFAULT_PORT;
    private String uri;
    private String database;
    private String username;
    private String password;
    private String authMechanism;
    private String collection;
    private int timeout = DEFAULT_TIMEOUT_MS;
    private String ssl;

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAuthMechanism() { return authMechanism; }
    public void setAuthMechanism(String authMechanism) { this.authMechanism = authMechanism; }
    public String getCollection() { return collection; }
    public void setCollection(String collection) { this.collection = collection; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public String getSsl() { return ssl; }
    public void setSsl(String ssl) { this.ssl = ssl; }

    public boolean testConnection() {
        MongoClient mongo = null;
        try {
            MongoClientOptions.Builder builder = MongoClientOptions.builder()
                    .connectTimeout(timeout)
                    .socketTimeout(timeout)
                    .serverSelectionTimeout(timeout)
                    .maxWaitTime(timeout);

            if ("true".equalsIgnoreCase(ssl)) {
                builder.sslEnabled(true).sslInvalidHostNameAllowed(true);
            }

            if (StringUtils.isNotBlank(uri)) {
                if (!uri.startsWith("mongodb://") && !uri.startsWith("mongodb+srv://")) {
                    logger.warn("Invalid MongoDB URI protocol (must start with mongodb:// or mongodb+srv://): {}", uri);
                    return false;
                }
                MongoClientURI mongoURI = new MongoClientURI(uri, builder);
                mongo = new MongoClient(mongoURI);
                if (StringUtils.isBlank(database)) {
                    database = mongoURI.getDatabase();
                }
            } else {
                String targetHost = StringUtils.isNotBlank(host) ? host.trim() : "localhost";
                if (targetHost.contains(":")) {
                    String[] parts = targetHost.split(":");
                    targetHost = parts[0];
                    try {
                        port = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (port <= 0) {
                    port = DEFAULT_PORT;
                }

                ServerAddress serverAddress = new ServerAddress(targetHost, port);
                List<MongoCredential> credentials = new ArrayList<>();

                if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                    String authDb = StringUtils.isNotBlank(database) ? database : "admin";
                    if ("MongoCR".equalsIgnoreCase(authMechanism)) {
                        credentials.add(MongoCredential.createMongoCRCredential(username, authDb, password.toCharArray()));
                    } else if ("ScramSha1".equalsIgnoreCase(authMechanism) || "SCRAM-SHA-1".equalsIgnoreCase(authMechanism)) {
                        credentials.add(MongoCredential.createScramSha1Credential(username, authDb, password.toCharArray()));
                    } else if ("ScramSha256".equalsIgnoreCase(authMechanism) || "SCRAM-SHA-256".equalsIgnoreCase(authMechanism)) {
                        credentials.add(MongoCredential.createScramSha256Credential(username, authDb, password.toCharArray()));
                    } else if ("Plain".equalsIgnoreCase(authMechanism) || "PLAIN".equalsIgnoreCase(authMechanism)) {
                        credentials.add(MongoCredential.createPlainCredential(username, authDb, password.toCharArray()));
                    } else {
                        credentials.add(MongoCredential.createCredential(username, authDb, password.toCharArray()));
                    }
                    mongo = new MongoClient(serverAddress, credentials, builder.build());
                } else {
                    mongo = new MongoClient(serverAddress, builder.build());
                }
            }

            String targetDatabase = StringUtils.isNotBlank(database) ? database : "admin";
            MongoDatabase db = mongo.getDatabase(targetDatabase);

            Document pingResult = db.runCommand(new Document("ping", 1));
            if (pingResult == null) {
                return false;
            }
            Object ok = pingResult.get("ok");
            if (ok instanceof Number) {
                if (((Number) ok).intValue() != 1) {
                    return false;
                }
            } else if (!"1".equals(String.valueOf(ok))) {
                return false;
            }

            if (StringUtils.isNotBlank(collection)) {
                try {
                    db.getCollection(collection).estimatedDocumentCount();
                } catch (Exception ex) {
                    logger.debug("Collection access check exception (non-fatal for ping): {}", ex.getMessage());
                }
            }

            return true;
        } catch (MongoException ex) {
            logger.warn("MongoDB connection test failed: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            logger.warn("Unexpected error during MongoDB connection test: {}", ex.getMessage());
            return false;
        } finally {
            if (mongo != null) {
                try {
                    mongo.close();
                } catch (Exception e) {
                    logger.debug("Error closing MongoClient: {}", e.getMessage());
                }
            }
        }
    }
}

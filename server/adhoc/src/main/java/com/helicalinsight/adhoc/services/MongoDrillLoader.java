
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
        String username = formDataJson.get("userName").getAsString();
        String password = formDataJson.get("password").getAsString();
        String jdbcUrl = GsonUtility.optString(formDataJson, "jdbcUrl");
        String host = getHostPort(jdbcUrl, true);
        String port = getHostPort(jdbcUrl, false);
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();
        mongo.addProperty("type", "mongo");

        String connectionString = null;
        if (username.isEmpty() || password.isEmpty()) {

            connectionString = "mongodb://" + host + ":" + port;
        } else {
            connectionString = "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authMechanism=SCRAM-SHA-1";
        }
        mongo.addProperty("connection", connectionString);
        mongo.addProperty("enabled", true);

        String drillStorageUrl = DrillCsvDataSourceCreator.getUrlOfDrill();

        String resourceUrl = drillStorageUrl + "/storage/" + storageName + "_" + theId + ".json";

        JsonObject storageJson = new JsonObject();
        storageJson.addProperty("name", storageName + "_" + theId);
        storageJson.add("config", mongo);

        String result = DrillCsvDataSourceCreator.drillRestApiCall(resourceUrl, "POST", storageJson.toString());
        if (result == null) {
            throw new EfwServiceException("There was some problem creating drill mongo connection");
        } else {
            try {
                JsonObject resultJSON = new Gson().fromJson(result,JsonObject.class);

            } catch (JsonSyntaxException e) {
                throw new EfwServiceException("There was a problem " + result);
            }
        }
        return true;
    }

    private String getHostPort(String uri, boolean isHost) {
        String splitArray[] = uri.split(":");
        if (splitArray.length >= 3) {
            if (isHost)
                return splitArray[1].replace("//", "");
            else
                return splitArray[2].substring(0, splitArray[2].indexOf("/"));
        }
        return "";
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String host = GsonUtility.optString(formData, "host");
        String uri = GsonUtility.optString(formData,"jdbcUrl");
        String database = GsonUtility.optString(formData,"database");
        String username = GsonUtility.optString(formData,"userName");
        String password = GsonUtility.optString(formData,"password");
        if (StringUtils.isEmpty(database)) {
            database = GsonUtility.optString(formData,"databaseName");
        }
        MongoModel mongoModel = new MongoModel();
        String splitArray[] = uri.split(":");
        if (splitArray.length >= 3) {
            String hostName = splitArray[1].replace("//", "");
            String port = splitArray[2].substring(0, splitArray[2].indexOf("/"));
            mongoModel.setHost(hostName + ":" + port);
            mongoModel.setUri(uri);
        }

        int timeout = GsonUtility.optInt(formData, "timeOut");
        int maxWait = GsonUtility.optInt(formData, "maxWait");
        String authMechanism = GsonUtility.optString(formData,"authMechanism");
        mongoModel.setDatabase(database);
        mongoModel.setUsername(username);
        mongoModel.setPassword(password);
        mongoModel.setAuthMechanism(authMechanism);
        mongoModel.setTimeout(timeout);
        mongoModel.setMaxWait(maxWait);
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


    private String ssl;
    private String keyStorePath;
    private String keyStorePassword;

    public boolean testConnection() {
        List<MongoCredential> credentials = new ArrayList<>();
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        MongoClient mongo = null;
        try {
            if ((username == null) || (password == null) || (authMechanism == null)) {
                mongo = new MongoClient(host);
                this.mongoDb = mongo.getDB(database);
            } else {
                List<ServerAddress> seeds = new ArrayList<>();
                seeds.add(new ServerAddress(host));
                if (authMechanism.equalsIgnoreCase("MongoCR")) {
                    credentials.add(MongoCredential.createMongoCRCredential(username, database, password.toCharArray()));
                } else if (authMechanism.equalsIgnoreCase("ScramSha1")) {
                    credentials.add(MongoCredential.createScramSha1Credential(username, database, password.toCharArray()));
                } else if (authMechanism.equalsIgnoreCase("Plain")) {
                    credentials.add(MongoCredential.createPlainCredential(username, database, password.toCharArray()));
                } else {
                    credentials.add(MongoCredential.createCredential(username, database, password.toCharArray()));
                }
                if (ssl != null) {
                    if (notNullOrBlank(keyStorePassword)) {
                        System.setProperty("jakarta.net.ssl.trustStore", keyStorePath);
                        System.setProperty("jakarta.net.ssl.trustStorePassword", keyStorePassword);
                    }
                    builder.sslEnabled(true).sslInvalidHostNameAllowed(true).build();
                }
                if (timeout > 0 && maxWait > 0) {
                    builder.connectTimeout(timeout);
                    builder.maxWaitTime(maxWait);
                }
                builder.socketKeepAlive(true);
                MongoClientOptions mongoClientOptions = builder.build();
                if (uri != null && uri.length() > 0) {
                    MongoClientURI mongoURI = new MongoClientURI(uri);
                    mongo = new MongoClient(mongoURI);
                } else {
                    mongo = new MongoClient(seeds, credentials, mongoClientOptions);
                }
                if (database.isEmpty()) {
                    database = uri.substring(uri.lastIndexOf("/"));
                }
                this.mongoDb = mongo.getDB(database);

                mongo.getAddress();
                return this.mongoDb != null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            if (mongo != null) {
                mongo.close();
            }
        }
        return false;
    }

    private boolean notNullOrBlank(String trustStorePassword) {
        return !((trustStorePassword == null) || (trustStorePassword.isEmpty()));
    }

    DB mongoDb;

}


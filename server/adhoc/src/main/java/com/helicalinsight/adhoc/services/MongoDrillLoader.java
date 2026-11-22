
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

import org.bson.Document;

import java.net.URLEncoder;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


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
        String connectionString = MongoModel.from(formDataJson).getConnectionUri();
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();
        mongo.addProperty("type", "mongo");
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

    @Override
    public boolean testConnection(JsonObject formData) {
        return MongoModel.from(formData).testConnection();
    }
}

class MongoModel {

    private String uri;
    private String database;
    private String username;
    private String password;
    private int timeout;
    private int maxWait;
    private String authMechanism;
    private String ssl;

    static MongoModel from(JsonObject formData) {
        MongoModel model = new MongoModel();
        model.uri = GsonUtility.optString(formData, "jdbcUrl");
        model.database = GsonUtility.optString(formData, "database");
        if (StringUtils.isBlank(model.database)) {
            model.database = GsonUtility.optString(formData, "databaseName");
        }
        model.username = GsonUtility.optString(formData, "userName");
        model.password = GsonUtility.optString(formData, "password");
        model.authMechanism = GsonUtility.optString(formData, "authMechanism");
        model.timeout = GsonUtility.optInt(formData, "timeOut");
        model.maxWait = GsonUtility.optInt(formData, "maxWait");
        model.ssl = GsonUtility.optString(formData, "ssl");
        return model;
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

    public boolean testConnection() {
        MongoClient mongo = null;
        try {
            String connectionUri = getConnectionUri();
            MongoClientOptions.Builder options = MongoClientOptions.builder();
            if (timeout > 0) {
                options.connectTimeout(timeout);
            }
            if (maxWait > 0) {
                options.maxWaitTime(maxWait);
            }
            if (Boolean.parseBoolean(ssl)) {
                options.sslEnabled(true);
            }

            MongoClientURI mongoUri = new MongoClientURI(connectionUri, options);
            String databaseName = StringUtils.defaultIfBlank(database, mongoUri.getDatabase());
            if (StringUtils.isBlank(databaseName)) {
                return false;
            }

            mongo = new MongoClient(mongoUri);
            mongo.getDatabase(databaseName).runCommand(new Document("ping", 1));
            return true;
        } catch (MongoException | IllegalArgumentException exception) {
            return false;
        } finally {
            if (mongo != null) {
                mongo.close();
            }
        }
    }

    String getConnectionUri() {
        if (StringUtils.isBlank(uri)) {
            throw new IllegalArgumentException("MongoDB URI is required");
        }
        URI parsedUri = URI.create(uri);
        String connectionUri = uri;
        if (parsedUri.getRawUserInfo() == null && StringUtils.isNotBlank(username)
                && StringUtils.isNotBlank(password)) {
            int schemeEnd = connectionUri.indexOf("://");
            if (schemeEnd < 0) {
                throw new IllegalArgumentException("MongoDB URI must include a scheme");
            }
            String credentials = encode(username) + ":" + encode(password) + "@";
            connectionUri = connectionUri.substring(0, schemeEnd + 3) + credentials
                    + connectionUri.substring(schemeEnd + 3);
            } else if (parsedUri.getRawUserInfo() == null
                && (StringUtils.isNotBlank(username) || StringUtils.isNotBlank(password))) {
                throw new IllegalArgumentException("Both MongoDB username and password are required");
        }

        if (StringUtils.isNotBlank(authMechanism)
                && !connectionUri.toLowerCase(Locale.ROOT).contains("authmechanism=")) {
            String mechanism = authMechanism;
            if ("MongoCR".equalsIgnoreCase(mechanism)) {
                mechanism = "MONGODB-CR";
            } else if ("ScramSha1".equalsIgnoreCase(mechanism)) {
                mechanism = "SCRAM-SHA-1";
            }
            connectionUri = appendOption(connectionUri, "authMechanism", mechanism);
        }
        if (Boolean.parseBoolean(ssl)
                && !connectionUri.toLowerCase(Locale.ROOT).contains("tls=")
                && !connectionUri.toLowerCase(Locale.ROOT).contains("ssl=")) {
            connectionUri = appendOption(connectionUri, "tls", "true");
        }
        return connectionUri;
    }

    private static String appendOption(String connectionUri, String name, String value) {
        return connectionUri + (connectionUri.contains("?") ? "&" : "?") + name + "=" + value;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

}


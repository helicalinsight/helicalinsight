package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component("com.helicalinsight.nosql.mongo")
@Scope("prototype")
@Deprecated
public class MongoDrillLoader extends NoSQLLoader {
    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {
        JsonObject mongo = new JsonObject();
        String jdbcUrl = getConnectionUri(formDataJson);
        String connectionString = withCredentials(jdbcUrl,
                GsonUtility.optString(formDataJson, "userName"),
                GsonUtility.optString(formDataJson, "password"));
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();
        mongo.addProperty("type", "mongo");
        mongo.addProperty("connection", connectionString);
        mongo.addProperty("enabled", true);

        String resourceUrl = DrillCsvDataSourceCreator.getUrlOfDrill()
                + "/storage/" + storageName + "_" + theId + ".json";
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
            throw new EfwServiceException("There was a problem creating the MongoDB data source");
        }
        return true;
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String database = GsonUtility.optString(formData, "database");
        if (StringUtils.isBlank(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }
        MongoClient mongo = null;
        try {
            String uri = withCredentials(getConnectionUri(formData),
                    GsonUtility.optString(formData, "userName"),
                    GsonUtility.optString(formData, "password"));
            MongoClientURI mongoUri = new MongoClientURI(uri);
            mongo = new MongoClient(mongoUri);
            String targetDatabase = StringUtils.isNotBlank(database) ? database : mongoUri.getDatabase();
            if (StringUtils.isBlank(targetDatabase)) {
                return false;
            }
            DBObject result = mongo.getDB(targetDatabase).command(new BasicDBObject("ping", 1));
            Object ok = result.get("ok");
            return ok instanceof Number && ((Number) ok).doubleValue() == 1.0;
        } catch (Exception e) {
            return false;
        } finally {
            if (mongo != null) {
                mongo.close();
            }
        }
    }

    private String getConnectionUri(JsonObject formData) {
        String uri = GsonUtility.optString(formData, "jdbcUrl");
        if (StringUtils.isBlank(uri)) {
            String host = GsonUtility.optString(formData, "hostName");
            String port = GsonUtility.optString(formData, "port");
            String database = GsonUtility.optString(formData, "database");
            uri = "mongodb://" + host + (StringUtils.isBlank(port) ? "" : ":" + port)
                    + "/" + (database == null ? "" : database);
        }
        if (!(uri.startsWith("mongodb://") || uri.startsWith("mongodb+srv://"))) {
            throw new IllegalArgumentException("MongoDB URI must start with mongodb:// or mongodb+srv://");
        }
        return uri.trim();
    }

    private String withCredentials(String uri, String username, String password) {
        int authorityStart = uri.indexOf("://") + 3;
        int authorityEnd = uri.indexOf('/', authorityStart);
        if (authorityEnd < 0) {
            authorityEnd = uri.indexOf('?', authorityStart);
        }
        if (authorityEnd < 0) {
            authorityEnd = uri.length();
        }
        if (uri.substring(authorityStart, authorityEnd).contains("@")) {
            throw new IllegalArgumentException("Enter MongoDB credentials in the username and password fields");
        }
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
            return uri;
        }
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Both MongoDB username and password are required");
        }
        String encodedUsername = encodeCredential(username);
        String encodedPassword = encodeCredential(password);
        return uri.substring(0, authorityStart) + encodedUsername + ":" + encodedPassword + "@"
                + uri.substring(authorityStart);
    }

    private String encodeCredential(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}


package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.DrillConfigUrlContext;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * MongoDB NoSQL datasource implementation.
 *
 * <p>The native MongoDB driver is used to validate the supplied connection
 * details. When Apache Drill is enabled, the same connection string is also
 * registered as a Drill storage plugin so it can be queried by Helical
 * Insight's SQL-based reporting pipeline.</p>
 */

@Component("com.helicalinsight.nosql.mongo")
@Scope("prototype")
public class MongoDrillLoader extends NoSQLLoader {
    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {
        if (!DrillConfigUrlContext.isEnabled()) {
            // Saving and testing a MongoDB connection must not depend on the
            // optional Drill SQL bridge being installed.
            return true;
        }

        JsonObject mongo = new JsonObject();
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();
        mongo.addProperty("type", "mongo");
        mongo.addProperty("connection", buildConnectionString(formDataJson));
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
        String connectionString = buildConnectionString(formData);
        ConnectionString parsedConnectionString = new ConnectionString(connectionString);
        String databaseName = parsedConnectionString.getDatabase();
        if (StringUtils.isBlank(databaseName)) {
            databaseName = databaseName(formData);
        }
        if (StringUtils.isBlank(databaseName)) {
            throw new EfwServiceException("A MongoDB database name is required.");
        }

        int connectTimeout = positiveOrDefault(GsonUtility.optInt(formData, "timeOut"), 10_000);
        int maxWait = positiveOrDefault(GsonUtility.optInt(formData, "maxWait"), 10_000);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(parsedConnectionString)
                .applyToClusterSettings(builder -> builder.serverSelectionTimeout(connectTimeout, TimeUnit.MILLISECONDS))
                .applyToSocketSettings(builder -> builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS))
                .applyToConnectionPoolSettings(builder -> builder.maxWaitTime(maxWait, TimeUnit.MILLISECONDS))
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception exception) {
            throw new EfwServiceException("Could not connect to MongoDB: " + exception.getMessage(), exception);
        }
    }

    static String buildConnectionString(JsonObject formData) {
        String uri = GsonUtility.optString(formData, "jdbcUrl").trim();
        if (StringUtils.isBlank(uri)) {
            throw new EfwServiceException("A MongoDB connection string is required.");
        }
        if (!uri.startsWith("mongodb://") && !uri.startsWith("mongodb+srv://")) {
            throw new EfwServiceException("MongoDB URLs must start with mongodb:// or mongodb+srv://.");
        }

        String username = GsonUtility.optString(formData, "userName");
        String password = GsonUtility.optString(formData, "password");
        int authorityEnd = uri.indexOf('/', uri.indexOf("//") + 2);
        String authority = authorityEnd == -1 ? uri.substring(uri.indexOf("//") + 2) : uri.substring(uri.indexOf("//") + 2, authorityEnd);
        if (StringUtils.isNotBlank(username) && !authority.contains("@")) {
            String scheme = uri.substring(0, uri.indexOf("//") + 2);
            String remainder = uri.substring(uri.indexOf("//") + 2);
            uri = scheme + encodeUserInfo(username) + ":" + encodeUserInfo(password) + "@" + remainder;
        }

        if (!hasDatabaseName(uri) && StringUtils.isNotBlank(databaseName(formData))) {
            uri = appendDatabase(uri, databaseName(formData));
        }
        return uri;
    }

    private static boolean hasDatabaseName(String uri) {
        int authorityEnd = uri.indexOf('/', uri.indexOf("//") + 2);
        if (authorityEnd == -1) {
            return false;
        }
        int queryIndex = uri.indexOf('?', authorityEnd);
        String path = uri.substring(authorityEnd + 1, queryIndex == -1 ? uri.length() : queryIndex);
        return StringUtils.isNotBlank(path);
    }

    private static String appendDatabase(String uri, String database) {
        int queryIndex = uri.indexOf('?');
        String query = queryIndex == -1 ? "" : uri.substring(queryIndex);
        String base = queryIndex == -1 ? uri : uri.substring(0, queryIndex);
        return base.endsWith("/") ? base + database + query : base + "/" + database + query;
    }

    private static String databaseName(JsonObject formData) {
        String database = GsonUtility.optString(formData, "database");
        return StringUtils.isBlank(database) ? GsonUtility.optString(formData, "databaseName") : database;
    }

    private static String encodeUserInfo(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static int positiveOrDefault(int value, int defaultValue) {
        return value > 0 ? value : defaultValue;
    }
}

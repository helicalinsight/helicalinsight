
package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.Document;
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
public class MongoDrillLoader extends NoSQLLoader {
    @Override
    public boolean loadToMiddleWare(JsonObject formDataJson) {
        JsonObject mongo = new JsonObject();
        String username = GsonUtility.optString(formDataJson, "userName");
        String password = GsonUtility.optString(formDataJson, "password");
        String jdbcUrl = GsonUtility.optString(formDataJson, "jdbcUrl");
        String storageName = GsonUtility.optString(formDataJson, "name");
        String theId = GsonUtility.optString(formDataJson, "theId");
        mongo.addProperty("type", "mongo");

        mongo.addProperty("connection", addCredentials(jdbcUrl, username, password));
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

    static String addCredentials(String uri, String username, String password) {
        if (StringUtils.isBlank(uri) || StringUtils.isBlank(username) || uri.contains("@")) {
            return uri;
        }
        int schemeEnd = uri.indexOf("://");
        if (schemeEnd < 0) {
            return uri;
        }
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8).replace("+", "%20");
        return uri.substring(0, schemeEnd + 3) + encodedUsername + ":" + encodedPassword + "@"
                + uri.substring(schemeEnd + 3);
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String uri = GsonUtility.optString(formData,"jdbcUrl");
        String database = GsonUtility.optString(formData,"database");
        String username = GsonUtility.optString(formData,"userName");
        String password = GsonUtility.optString(formData,"password");
        if (StringUtils.isEmpty(database)) {
            database = GsonUtility.optString(formData,"databaseName");
        }
        return MongoModel.testConnection(uri, database, username, password,
                GsonUtility.optString(formData,"authMechanism"),
                GsonUtility.optInt(formData, "timeOut"),
                GsonUtility.optInt(formData, "maxWait"));
    }
}

class MongoModel {

    public static boolean testConnection(String uri, String database, String username, String password,
                                         String authMechanism, int timeout, int maxWait) {
        if (StringUtils.isBlank(uri)) {
            throw new EfwServiceException("MongoDB URL is required");
        }
        MongoClient mongo = null;
        try {
            String connectionUri = MongoDrillLoader.addCredentials(uri, username, password);
            connectionUri = addQueryParameter(connectionUri, "authMechanism", toMongoAuthMechanism(authMechanism));
            connectionUri = addQueryParameter(connectionUri, "connectTimeoutMS", timeout > 0 ? String.valueOf(timeout) : null);
            connectionUri = addQueryParameter(connectionUri, "waitQueueTimeoutMS", maxWait > 0 ? String.valueOf(maxWait) : null);
            MongoClientURI clientUri = new MongoClientURI(connectionUri);
            String selectedDatabase = StringUtils.isNotBlank(database) ? database : clientUri.getDatabase();
            if (StringUtils.isBlank(selectedDatabase)) {
                throw new EfwServiceException("MongoDB database is required");
            }

            mongo = new MongoClient(clientUri);
            mongo.getDatabase(selectedDatabase).runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            throw new EfwServiceException("Unable to connect to MongoDB: " + e.getMessage());
        } finally {
            if (mongo != null) {
                mongo.close();
            }
        }
    }

    private static String toMongoAuthMechanism(String authMechanism) {
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

    private static String addQueryParameter(String uri, String name, String value) {
        if (StringUtils.isBlank(uri) || StringUtils.isBlank(value) || uri.contains(name + "=")) {
            return uri;
        }
        return uri + (uri.contains("?") ? "&" : "?") + name + "=" + value;
    }

}


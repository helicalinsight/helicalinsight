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
 * MongoDB datasource integration through the existing Helical Insight NoSQL/Drill middleware path.
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
        String host = getHostPort(jdbcUrl, true);
        String port = getHostPort(jdbcUrl, false);
        String storageName = formDataJson.get("name").getAsString();
        String theId = formDataJson.get("theId").getAsString();
        mongo.addProperty("type", "mongo");

        String connectionString;
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            connectionString = "mongodb://" + host + ":" + port;
        } else {
            connectionString = "mongodb://" + username + ":" + password + "@" + host + ":" + port
                    + "/?authMechanism=SCRAM-SHA-1";
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
        }

        try {
            new Gson().fromJson(result, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new EfwServiceException("There was a problem " + result, e);
        }
        return true;
    }

    private String getHostPort(String uri, boolean isHost) {
        if (StringUtils.isBlank(uri)) {
            throw new EfwServiceException("MongoDB connection URL is required");
        }

        try {
            MongoClientURI mongoURI = new MongoClientURI(uri);
            List<ServerAddress> hosts = new ArrayList<>();
            for (String host : mongoURI.getHosts()) {
                hosts.add(parseServerAddress(host));
            }

            if (hosts.isEmpty()) {
                throw new EfwServiceException("MongoDB connection URL does not contain a host");
            }

            ServerAddress address = hosts.get(0);
            return isHost ? address.getHost() : String.valueOf(address.getPort());
        } catch (EfwServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new EfwServiceException("Invalid MongoDB connection URL", e);
        }
    }

    private ServerAddress parseServerAddress(String host) {
        int separator = host.lastIndexOf(':');
        if (separator > 0 && separator < host.length() - 1) {
            String hostname = host.substring(0, separator);
            String portValue = host.substring(separator + 1);
            try {
                return new ServerAddress(hostname, Integer.parseInt(portValue));
            } catch (NumberFormatException ignored) {
                // Fall through to the default MongoDB port.
            }
        }
        return new ServerAddress(host);
    }

    @Override
    public boolean testConnection(JsonObject formData) {
        String uri = GsonUtility.optString(formData, "jdbcUrl");
        String database = GsonUtility.optString(formData, "database");
        if (StringUtils.isBlank(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }

        if (StringUtils.isBlank(uri)) {
            throw new EfwServiceException("MongoDB connection URL is required");
        }

        MongoClient mongo = null;
        try {
            MongoClientURI mongoURI = new MongoClientURI(uri);
            mongo = new MongoClient(mongoURI);

            if (StringUtils.isBlank(database)) {
                database = mongoURI.getDatabase();
            }
            if (StringUtils.isBlank(database)) {
                throw new EfwServiceException("MongoDB database name is required");
            }

            DB mongoDb = mongo.getDB(database);
            mongoDb.command(new BasicDBObject("ping", 1));
            return true;
        } catch (EfwServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new EfwServiceException("MongoDB connection failed", e);
        } finally {
            if (mongo != null) {
                mongo.close();
            }
        }
    }
}

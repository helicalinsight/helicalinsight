package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.ApplicationProperties;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.Arrays;

/**
 * MongoDriver implements {@link IDriver} for MongoDB data sources.
 * <p>
 * Allows Helical Insight EFWD reports to query a MongoDB collection using the
 * native MongoDB Java driver (no SQL/JDBC shim required).
 * <p>
 * <b>EFWD connection element</b> (type="nosql.mongodb"):
 * <pre>{@code
 * <Connection id="1" type="nosql.mongodb">
 *   <Driver>com.helical.mongodb.MongoDriver</Driver>
 *   <Url>mongodb://localhost:27017/mydb</Url>
 *   <User>admin</User>
 *   <Pass>secret</Pass>
 * </Connection>
 * }</pre>
 *
 * <b>EFWD DataMap query</b> — a JSON object with the following optional fields:
 * <pre>{@code
 * {"collection":"orders","filter":{"status":"active"},"projection":{"_id":0,"name":1},"sort":{"name":1},"limit":500}
 * }</pre>
 *
 * @author Helical Insight
 * @since 7.1
 */
public class MongoDriver implements IDriver {

    private static final Logger logger = LoggerFactory.getLogger(MongoDriver.class);

    /** Default max documents returned when no limit is specified in the query. */
    private static final int DEFAULT_LIMIT = 10_000;

    // -------------------------------------------------------------------------
    // IDriver implementation
    // -------------------------------------------------------------------------

    @Override
    public String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) {
        if (dataMapTagContent.has("Query")) {
            return dataMapTagContent.get("Query").getAsString().trim();
        }
        throw new MongoDriverException("No <Query> element found in the DataMap.");
    }

    @Override
    public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                  JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
        MongoConnectionParams params = extractParams(connectionDetails);
        String queryJson = getQuery(dataMapTagContent, requestParameterJson);
        return executeQuery(params, queryJson);
    }

    /** Not applicable for MongoDB – returns null so callers fall back gracefully. */
    @Override
    public ResultSet getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                      JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
        return null;
    }

    @Override
    public void streamResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                    JsonObject dataMapTagContent, ApplicationProperties applicationProperties,
                                    CallBack<ResultSet> callBack) {
        // Streaming via ResultSet is not supported for MongoDB; no-op.
    }

    // -------------------------------------------------------------------------
    // Connection / query helpers
    // -------------------------------------------------------------------------

    /**
     * Opens a {@link MongoClient}, runs the query described by {@code queryJson}
     * against the database resolved from the connection URL, then closes the client.
     */
    private JsonObject executeQuery(MongoConnectionParams params, String queryJson) {
        MongoClient mongoClient = null;
        try {
            mongoClient = buildClient(params);
            MongoDatabase db = mongoClient.getDatabase(params.database);

            JsonObject queryObject = parseQueryJson(queryJson);
            String collectionName = getRequiredString(queryObject, "collection");
            MongoCollection<Document> collection = db.getCollection(collectionName);

            Document filter = queryObject.has("filter")
                    ? Document.parse(queryObject.get("filter").toString())
                    : new Document();
            Document projection = queryObject.has("projection")
                    ? Document.parse(queryObject.get("projection").toString())
                    : null;
            Document sort = queryObject.has("sort")
                    ? Document.parse(queryObject.get("sort").toString())
                    : null;
            int limit = queryObject.has("limit") ? queryObject.get("limit").getAsInt() : DEFAULT_LIMIT;

            FindIterable<Document> iterable = collection.find(filter);
            if (projection != null) {
                iterable = iterable.projection(projection);
            }
            if (sort != null) {
                iterable = iterable.sort(sort);
            }
            iterable = iterable.limit(limit);

            return buildResultJson(iterable);

        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    /**
     * Converts a {@link FindIterable} of BSON Documents into the standard
     * Helical Insight response format used by JDBC drivers:
     * <pre>{"data":[{...},{...}], "metadata":[{column-map}, {"rows":N}]}</pre>
     */
    private JsonObject buildResultJson(FindIterable<Document> iterable) {
        JsonArray dataArray = new JsonArray();
        JsonObject columnMap = new JsonObject();
        boolean headerCaptured = false;
        int rowCount = 0;
        int colIndex = 1;

        for (Document doc : iterable) {
            // Remove internal Mongo _id from results to keep the output clean
            doc.remove("_id");

            JsonObject row = new JsonObject();
            if (!headerCaptured) {
                for (String key : doc.keySet()) {
                    JsonObject colMeta = new JsonObject();
                    colMeta.addProperty("name", key);
                    colMeta.addProperty("type", "string");
                    columnMap.add(Integer.toString(colIndex++), colMeta);
                }
                headerCaptured = true;
            }
            for (String key : doc.keySet()) {
                Object val = doc.get(key);
                row.addProperty(key, val == null ? "" : val.toString());
            }
            dataArray.add(row);
            rowCount++;
        }

        JsonArray metadataArray = new JsonArray();
        metadataArray.add(columnMap);
        JsonObject rowsMeta = new JsonObject();
        rowsMeta.addProperty("rows", rowCount);
        metadataArray.add(rowsMeta);

        JsonObject result = new JsonObject();
        result.add("data", dataArray);
        result.add("metadata", metadataArray);
        return result;
    }

    // -------------------------------------------------------------------------
    // Client builder
    // -------------------------------------------------------------------------

    /**
     * Builds a {@link MongoClient} from the extracted connection parameters.
     * Supports both URI-style ({@code mongodb://...}) and host/port.
     */
    MongoClient buildClient(MongoConnectionParams params) {
        if (!StringUtils.isBlank(params.url) && params.url.startsWith("mongodb")) {
            if (!StringUtils.isBlank(params.username) && !StringUtils.isBlank(params.password)) {
                // Embed credentials into URI if not already present
                MongoClientURI uri = new MongoClientURI(params.url);
                if (uri.getCredentials() == null) {
                    MongoCredential credential = MongoCredential.createCredential(
                            params.username, params.database, params.password.toCharArray());
                    MongoClientOptions options = MongoClientOptions.builder().build();
                    return new MongoClient(
                            Arrays.asList(new ServerAddress(uri.getHosts().get(0))),
                            credential, options);
                }
            }
            return new MongoClient(new MongoClientURI(params.url));
        }
        // Fallback: host + port
        if (!StringUtils.isBlank(params.username) && !StringUtils.isBlank(params.password)) {
            MongoCredential credential = MongoCredential.createCredential(
                    params.username, params.database, params.password.toCharArray());
            MongoClientOptions options = MongoClientOptions.builder().build();
            return new MongoClient(
                    Arrays.asList(new ServerAddress(params.host, params.port)),
                    credential, options);
        }
        return new MongoClient(params.host, params.port);
    }

    // -------------------------------------------------------------------------
    // Parameter extraction helpers
    // -------------------------------------------------------------------------

    private MongoConnectionParams extractParams(JsonObject connectionDetails) {
        MongoConnectionParams p = new MongoConnectionParams();

        JsonObject conn = connectionDetails.has("connDetails")
                ? connectionDetails.getAsJsonObject("connDetails")
                : connectionDetails;

        p.url = optString(conn, "Url", optString(conn, "url", ""));
        p.username = optString(conn, "User", optString(conn, "user", optString(conn, "userName", "")));
        p.password = optString(conn, "Pass", optString(conn, "pass", optString(conn, "password", "")));

        // Extract database name from URL or dedicated field
        p.database = optString(conn, "Database", optString(conn, "database", ""));
        if (StringUtils.isBlank(p.database)) {
            p.database = extractDatabaseFromUrl(p.url);
        }

        // Parse host/port in case URL is not provided
        String[] hostPort = extractHostPort(p.url);
        p.host = hostPort[0];
        p.port = Integer.parseInt(hostPort[1]);

        if (logger.isDebugEnabled()) {
            logger.debug("MongoDriver connecting to url={} db={}", p.url, p.database);
        }
        return p;
    }

    private String extractDatabaseFromUrl(String url) {
        if (StringUtils.isBlank(url)) return "test";
        try {
            // mongodb://host:port/dbname  or  mongodb+srv://host/dbname
            String path = url.replaceAll("mongodb(\\+srv)?://[^/]+/", "");
            String db = path.split("[?/]")[0].trim();
            return StringUtils.isBlank(db) ? "test" : db;
        } catch (Exception e) {
            return "test";
        }
    }

    private String[] extractHostPort(String url) {
        String host = "localhost";
        String port = "27017";
        if (StringUtils.isBlank(url)) return new String[]{host, port};
        try {
            String stripped = url.replaceAll("mongodb(\\+srv)?://", "").split("/")[0];
            if (stripped.contains("@")) {
                stripped = stripped.substring(stripped.lastIndexOf("@") + 1);
            }
            if (stripped.contains(":")) {
                String[] parts = stripped.split(":");
                host = parts[0];
                port = parts[1];
            } else {
                host = stripped;
            }
        } catch (Exception ignored) {
        }
        return new String[]{host, port};
    }

    private JsonObject parseQueryJson(String queryJson) {
        try {
            return new JsonParser().parse(queryJson).getAsJsonObject();
        } catch (Exception e) {
            throw new MongoDriverException("Invalid MongoDB query JSON: " + queryJson, e);
        }
    }

    private String getRequiredString(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            throw new MongoDriverException("Required field '" + key + "' missing from MongoDB query JSON.");
        }
        return obj.get(key).getAsString();
    }

    private static String optString(JsonObject obj, String key, String defaultValue) {
        return (obj != null && obj.has(key) && !obj.get(key).isJsonNull())
                ? obj.get(key).getAsString()
                : defaultValue;
    }

    // -------------------------------------------------------------------------
    // Inner types
    // -------------------------------------------------------------------------

    /** Holds the parsed MongoDB connection parameters. */
    static class MongoConnectionParams {
        String url;
        String host;
        int port = 27017;
        String username;
        String password;
        String database;
    }

    /** Unchecked exception thrown on driver-level errors. */
    public static class MongoDriverException extends RuntimeException {
        public MongoDriverException(String message) { super(message); }
        public MongoDriverException(String message, Throwable cause) { super(message, cause); }
    }
}

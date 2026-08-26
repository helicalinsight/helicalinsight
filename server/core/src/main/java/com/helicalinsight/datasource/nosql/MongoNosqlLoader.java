package com.helicalinsight.datasource.nosql;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.MongoDriver;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * NoSQL loader that tests and registers a native MongoDB connection.
 * Registered as Spring bean {@code "nosql.mongodb"} so that
 * {@code NoSqlUtils.getNoSqlImplementation("nosql.mongodb")} resolves to this class.
 *
 * <p>Expected {@code formData} keys:
 * <ul>
 *   <li>{@code jdbcUrl} — MongoDB connection URI, e.g. {@code mongodb://localhost:27017/mydb}</li>
 *   <li>{@code userName} — optional username</li>
 *   <li>{@code password} — optional password</li>
 *   <li>{@code database} / {@code databaseName} — target database name (also resolved from URL)</li>
 * </ul>
 *
 * @author Helical Insight
 * @since 7.1
 */
@Component("nosql.mongodb")
@Scope("prototype")
public class MongoNosqlLoader extends NoSQLLoader {

    private static final Logger logger = LoggerFactory.getLogger(MongoNosqlLoader.class);

    /**
     * MongoDB does not require middleware registration; this method is a no-op
     * and always returns {@code true}.
     */
    @Override
    public boolean loadToMiddleWare(JsonObject formData) {
        logger.debug("MongoNosqlLoader.loadToMiddleWare: no middleware registration required for MongoDB.");
        return true;
    }

    /**
     * Tests the MongoDB connection by attempting to list database names.
     *
     * @param formData JSON containing connection details
     * @return {@code true} if connection succeeded, {@code false} otherwise
     */
    @Override
    public boolean testConnection(JsonObject formData) {
        String url = GsonUtility.optString(formData, "jdbcUrl");
        String username = GsonUtility.optString(formData, "userName");
        String password = GsonUtility.optString(formData, "password");
        String database = GsonUtility.optString(formData, "database");
        if (StringUtils.isBlank(database)) {
            database = GsonUtility.optString(formData, "databaseName");
        }

        MongoClient mongoClient = null;
        try {
            MongoDriver.MongoConnectionParams params = new MongoDriver.MongoConnectionParams();
            params.url = url;
            params.username = username;
            params.password = password;
            params.database = StringUtils.isBlank(database) ? "test" : database;

            MongoDriver driver = new MongoDriver();
            mongoClient = driver.buildClient(params);

            // Ping by fetching the first database name; throws on auth/network failure
            mongoClient.listDatabaseNames().first();
            logger.info("MongoNosqlLoader: connection test successful for url={}", url);
            return true;
        } catch (Exception e) {
            logger.warn("MongoNosqlLoader: connection test failed for url={}: {}", url, e.getMessage());
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }
}

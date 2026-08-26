package com.helicalinsight.datasource.nosql;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads MongoDB collection metadata through the MongoDB Java driver.
 *
 * <p>The loader is intentionally independent from the relational JDBC
 * connection layer. It uses the same repository configuration values that
 * are supplied for a database connection: URI, database and collection.</p>
 */
public final class MongoDbLoader {

    private MongoDbLoader() {
    }

    /**
     * Verifies that a MongoDB connection can be opened and that the requested
     * database/collection is reachable.
     *
     * @param uri MongoDB connection URI
     * @param databaseName database name
     * @param collectionName collection name
     * @return true when the server/database/collection can be reached
     * @throws IllegalArgumentException when required configuration is blank
     */
    public static boolean testConnection(String uri, String databaseName, String collectionName) {
        validateInputs(uri, databaseName, collectionName);

        try (MongoClient client = MongoClients.create(uri)) {
            MongoDatabase database = client.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.estimatedDocumentCount();
            return true;
        } catch (MongoException ex) {
            return false;
        }
    }

    /**
     * Returns a small sample of documents from a collection. This is useful
     * for metadata/discovery code without loading an entire collection.
     *
     * @throws IllegalArgumentException when required configuration is blank
     */
    public static List<Document> sample(String uri, String databaseName,
                                        String collectionName, int limit) {
        validateInputs(uri, databaseName, collectionName);
        if (limit <= 0) {
            throw new IllegalArgumentException("MongoDB sample limit must be greater than zero");
        }

        try (MongoClient client = MongoClients.create(uri)) {
            MongoCollection<Document> collection =
                    client.getDatabase(databaseName).getCollection(collectionName);
            List<Document> result = new ArrayList<>();
            collection.find().limit(limit).into(result);
            return result;
        } catch (MongoException ex) {
            throw new IllegalStateException("Unable to sample documents from MongoDB collection", ex);
        }
    }

    private static void validateInputs(String uri, String databaseName, String collectionName) {
        if (StringUtils.isBlank(uri)) {
            throw new IllegalArgumentException("MongoDB URI must not be blank");
        }
        if (StringUtils.isBlank(databaseName)) {
            throw new IllegalArgumentException("MongoDB database name must not be blank");
        }
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("MongoDB collection name must not be blank");
        }
    }
}

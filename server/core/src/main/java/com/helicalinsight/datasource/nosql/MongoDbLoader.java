package com.helicalinsight.datasource.nosql;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
     */
    public static boolean testConnection(String uri, String databaseName, String collectionName) {
        if (isBlank(uri) || isBlank(databaseName) || isBlank(collectionName)) {
            return false;
        }

        try (MongoClient client = MongoClients.create(uri)) {
            MongoDatabase database = client.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.estimatedDocumentCount();
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /**
     * Returns a small sample of documents from a collection. This is useful
     * for metadata/discovery code without loading an entire collection.
     */
    public static List<Document> sample(String uri, String databaseName,
                                        String collectionName, int limit) {
        if (isBlank(uri) || isBlank(databaseName) || isBlank(collectionName) || limit <= 0) {
            return List.of();
        }

        try (MongoClient client = MongoClients.create(uri)) {
            MongoCollection<Document> collection =
                    client.getDatabase(databaseName).getCollection(collectionName);
            List<Document> result = new ArrayList<>();
            collection.find().limit(limit).into(result);
            return result;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

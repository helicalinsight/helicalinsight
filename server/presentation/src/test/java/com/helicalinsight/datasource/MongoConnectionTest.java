package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.services.MongoDrillLoader;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit and integration tests for MongoDB connection handling, active validation,
 * credential parsing, bounded timeouts, invalid credentials, database name handling,
 * and regression verification of existing JDBC connectivity.
 */
public class MongoConnectionTest {

    private MongoDrillLoader mongoLoader;

    @Before
    public void setUp() {
        mongoLoader = new MongoDrillLoader();
    }

    /**
     * Checks whether a MongoDB server is reachable by attempting a plain TCP
     * connection.  Used exclusively by {@link org.junit.Assume#assumeTrue} guards
     * in positive-connection tests so that those tests are reported as SKIPPED
     * (not FAILED) when no MongoDB instance is available (e.g. in CI).
     */
    private static boolean isMongoReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ==========================================
    // POSITIVE TESTS
    // ==========================================
    // These tests require a live MongoDB server.  When one is not available
    // (e.g. in CI without a service container) they are skipped via
    // Assume.assumeTrue so the build does not fail.

    @Test
    public void testPositiveMongoConnectionDirectHostPort() {
        Assume.assumeTrue("MongoDB not available at localhost:27017 — skipping positive connection test",
                isMongoReachable("localhost", 27017));

        JsonObject formData = new JsonObject();
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "helical_test");
        formData.addProperty("collection", "employees");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("subType", "com.helicalinsight.nosql.mongo");
        formData.addProperty("timeOut", 5000);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertTrue("Expected valid MongoDB connection on localhost:27017 to succeed", isConnected);
    }

    @Test
    public void testPositiveMongoConnectionWithUri() {
        // URI is mongodb://localhost:27017/helical_test — probe the same host/port.
        Assume.assumeTrue("MongoDB not available at localhost:27017 — skipping positive URI connection test",
                isMongoReachable("localhost", 27017));

        JsonObject formData = new JsonObject();
        formData.addProperty("jdbcUrl", "mongodb://localhost:27017/helical_test");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("subType", "com.helicalinsight.nosql.mongo");
        formData.addProperty("timeOut", 5000);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertTrue("Expected valid MongoDB connection with URI to succeed", isConnected);
    }

    @Test
    public void testPositiveMongoConnectionWithEmptyCredentials() {
        Assume.assumeTrue("MongoDB not available at localhost:27017 — skipping positive empty-credentials test",
                isMongoReachable("localhost", 27017));

        JsonObject formData = new JsonObject();
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "helical_test");
        formData.addProperty("userName", "");
        formData.addProperty("password", "");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 5000);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertTrue("Expected empty string credentials to be treated as unauthenticated and succeed", isConnected);
    }

    // ==========================================
    // NEGATIVE TESTS (CREDENTIALS & MALFORMED)
    // ==========================================

    @Test
    public void testNegativeMongoConnectionWithInvalidCredentials() {
        // Attempting to authenticate with arbitrary credentials against an unauthenticated
        // server or invalid auth configuration fails authentication cleanly.
        JsonObject formData = new JsonObject();
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "admin");
        formData.addProperty("userName", "nonexistent_admin");
        formData.addProperty("password", "invalid_password_xyz");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 3000);

        long start = System.currentTimeMillis();
        boolean isConnected = mongoLoader.testConnection(formData);
        long duration = System.currentTimeMillis() - start;

        assertFalse("Expected invalid credentials to fail authentication gracefully", isConnected);
        assertTrue("Expected authentication check to return within bounded timeout, took: " + duration + "ms", duration < 6000);
    }

    @Test
    public void testNegativeMongoConnectionClosedPort() {
        JsonObject formData = new JsonObject();
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 59999);
        formData.addProperty("database", "helical_test");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 1500);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertFalse("Expected connection to closed port 59999 to fail gracefully", isConnected);
    }

    @Test
    public void testNegativeMongoConnectionUnreachableHostWithBoundedTimeout() {
        JsonObject formData = new JsonObject();
        // 192.0.2.1 is reserved for documentation (TEST-NET-1) and non-routable
        formData.addProperty("hostName", "192.0.2.1");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "helical_test");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 1500);

        long start = System.currentTimeMillis();
        boolean isConnected = mongoLoader.testConnection(formData);
        long duration = System.currentTimeMillis() - start;

        assertFalse("Expected unreachable host to return false", isConnected);
        assertTrue("Expected connection attempt to respect bounded timeout (< 6000ms), took: " + duration + "ms", duration < 6000);
    }

    @Test
    public void testNegativeMongoConnectionMalformedUri() {
        JsonObject formData = new JsonObject();
        formData.addProperty("jdbcUrl", "invalid://broken_host:::27017");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 1500);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertFalse("Expected malformed URI to return false without throwing uncaught exception", isConnected);
    }

    // ==========================================
    // DATABASE NAME BEHAVIOR TESTS
    // ==========================================

    @Test
    public void testMongoConnectionDatabaseLazyHandling() {
        // In MongoDB, non-existent databases are created lazily upon document write.
        // Connecting and pinging a non-existent database verifies server connectivity.
        Assume.assumeTrue("MongoDB not available at localhost:27017 — skipping lazy-database connection test",
                isMongoReachable("localhost", 27017));

        JsonObject formData = new JsonObject();
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "nonexistent_lazy_db_xyz");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 5000);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertTrue("Expected connection to valid server with lazy database to succeed", isConnected);
    }

    @Test
    public void testNegativeMongoConnectionInvalidDatabaseNameWithIllegalCharacters() {
        JsonObject formData = new JsonObject();
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "invalid\0db\0name");
        formData.addProperty("dataSourceProvider", "noSql");
        formData.addProperty("timeOut", 1500);

        boolean isConnected = mongoLoader.testConnection(formData);
        assertFalse("Expected database name containing null bytes/illegal characters to fail gracefully", isConnected);
    }

    // ==========================================
    // STANDALONE MIDDLEWARE LOADING TEST
    // ==========================================

    @Test
    public void testLoadToMiddleWareStandalone() {
        JsonObject formData = new JsonObject();
        formData.addProperty("name", "TestMongoDS");
        formData.addProperty("theId", "101");
        formData.addProperty("hostName", "localhost");
        formData.addProperty("port", 27017);
        formData.addProperty("database", "helical_test");
        formData.addProperty("userName", "");
        formData.addProperty("password", "");

        boolean loaded = mongoLoader.loadToMiddleWare(formData);
        assertTrue("Expected loadToMiddleWare to complete gracefully in standalone mode", loaded);
    }

    // ==========================================
    // EXISTING DATABASE REGRESSION TEST (JDBC)
    // ==========================================

    @Test
    public void testExistingDerbyDatabaseConnectionRegression() throws Exception {
        // Verify that standard relational JDBC connectivity (e.g. Apache Derby)
        // remains fully functional and unaffected by MongoDB changes.
        String derbyUrl = "jdbc:derby:memory:regressionTestDb;create=true";
        try (Connection connection = DriverManager.getConnection(derbyUrl)) {
            assertNotNull("Expected valid Derby JDBC connection", connection);
            assertFalse("Expected open connection", connection.isClosed());
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE REGRESSION_CHECK (ID INT PRIMARY KEY, NAME VARCHAR(50))");
                statement.execute("INSERT INTO REGRESSION_CHECK VALUES (1, 'HelicalInsight')");
                try (ResultSet rs = statement.executeQuery("SELECT NAME FROM REGRESSION_CHECK WHERE ID = 1")) {
                    assertTrue("Expected query result from Derby", rs.next());
                    assertTrue("HelicalInsight".equals(rs.getString("NAME")));
                }
            }
        }
    }
}

package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MongoDrillLoaderTest {

    @Test
    public void preservesSrvUriOptionsAndEncodesCredentials() {
        JsonObject formData = new JsonObject();
        formData.addProperty("jdbcUrl", "mongodb+srv://cluster.example.test/analytics?retryWrites=true");
        formData.addProperty("userName", "report user");
        formData.addProperty("password", "p@ss:word");

        String uri = MongoModel.from(formData).getConnectionUri();

        assertTrue(uri.startsWith("mongodb+srv://report%20user:p%40ss%3Aword@"));
        assertTrue(uri.contains("?retryWrites=true"));
    }

    @Test
    public void doesNotReplaceCredentialsAlreadyInUri() {
        JsonObject formData = new JsonObject();
        String configuredUri = "mongodb://uri-user:uri-pass@localhost:27017/analytics?authSource=admin";
        formData.addProperty("jdbcUrl", configuredUri);
        formData.addProperty("userName", "form-user");
        formData.addProperty("password", "form-pass");

        assertEquals(configuredUri, MongoModel.from(formData).getConnectionUri());
    }

    @Test
    public void appendsAuthenticationAndTlsOptionsWithoutReplacingUriOptions() {
        JsonObject formData = new JsonObject();
        formData.addProperty("jdbcUrl", "mongodb://localhost:27017/analytics?retryWrites=true");
        formData.addProperty("userName", "reporter");
        formData.addProperty("password", "secret");
        formData.addProperty("authMechanism", "ScramSha1");
        formData.addProperty("ssl", "true");

        String uri = MongoModel.from(formData).getConnectionUri();

        assertTrue(uri.contains("?retryWrites=true&authMechanism=SCRAM-SHA-1&tls=true"));
    }

    @Test
    public void rejectsIncompleteSeparateCredentials() {
        JsonObject formData = new JsonObject();
        formData.addProperty("jdbcUrl", "mongodb://localhost:27017/analytics");
        formData.addProperty("userName", "reporter");

        try {
            MongoModel.from(formData).getConnectionUri();
            fail("Expected incomplete credentials to be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Both MongoDB username and password are required", expected.getMessage());
        }
    }
}
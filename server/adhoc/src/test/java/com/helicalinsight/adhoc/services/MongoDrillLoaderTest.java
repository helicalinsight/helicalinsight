package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoDrillLoaderTest {

    @Test
    public void addsCredentialsAndDatabaseToStandardConnectionString() {
        JsonObject formData = formData("mongodb://localhost:27017", "analytics", "app@user", "p:ass word");

        assertEquals("mongodb://app%40user:p%3Aass%20word@localhost:27017/analytics",
                MongoDrillLoader.buildConnectionString(formData));
    }

    @Test
    public void preservesUriOptionsWhenAddingDatabase() {
        JsonObject formData = formData("mongodb+srv://cluster.example.com/?retryWrites=true", "analytics", "", "");

        assertEquals("mongodb+srv://cluster.example.com/analytics?retryWrites=true",
                MongoDrillLoader.buildConnectionString(formData));
    }

    @Test
    public void doesNotReplaceCredentialsAlreadyInConnectionString() {
        JsonObject formData = formData("mongodb://uri-user:uri-password@localhost:27017/analytics", "other", "form-user", "form-password");

        assertEquals("mongodb://uri-user:uri-password@localhost:27017/analytics",
                MongoDrillLoader.buildConnectionString(formData));
    }

    private JsonObject formData(String uri, String database, String username, String password) {
        JsonObject formData = new JsonObject();
        formData.addProperty("jdbcUrl", uri);
        formData.addProperty("database", database);
        formData.addProperty("userName", username);
        formData.addProperty("password", password);
        return formData;
    }
}

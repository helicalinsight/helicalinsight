package com.helicalinsight.adhoc.services;

import com.mongodb.MongoClientURI;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MongoDrillLoaderTest {
    @Test
    public void preservesUnauthenticatedMongoUri() {
        String uri = "mongodb://localhost:27017/reports?retryWrites=true";

        assertEquals(uri, MongoDrillLoader.buildConnectionUri(uri, "", "", "reports", ""));
    }

    @Test
    public void addsAndEncodesCredentialsAndAuthenticationOptions() {
        String uri = MongoDrillLoader.buildConnectionUri(
                "mongodb://localhost:27017/reports", "report@user", "p@ss word", "reports", "ScramSha1");
        MongoClientURI parsedUri = new MongoClientURI(uri);

        assertEquals("report@user", parsedUri.getCredentials().getUserName());
        assertArrayEquals("p@ss word".toCharArray(), parsedUri.getCredentials().getPassword());
        assertEquals("reports", parsedUri.getCredentials().getSource());
        assertTrue(uri.contains("authMechanism=SCRAM-SHA-1"));
        assertTrue(uri.contains("authSource=reports"));
    }

    @Test
    public void keepsExplicitAuthenticationSourceAndUriCredentials() {
        String uri = "mongodb://existing:secret@localhost:27017/reports?authSource=admin";

        String connectionUri = MongoDrillLoader.buildConnectionUri(uri, "", "", "reports", "Plain");

        assertEquals(uri + "&authMechanism=PLAIN", connectionUri);
    }
}

package com.helicalinsight.efw.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResponseMetadataEnricherTest {

    @Test
    public void enrichJsonBytes_addsMetaToStatusResponseEnvelope() {
        byte[] body = "{\"status\":1,\"response\":{\"message\":\"ok\"}}".getBytes(StandardCharsets.UTF_8);

        byte[] enriched = ResponseMetadataEnricher.enrichJsonBytes(body);
        JsonObject json = JsonParser.parseString(new String(enriched, StandardCharsets.UTF_8)).getAsJsonObject();

        assertTrue(json.has("meta"));
        assertEquals("Helical Insight", json.getAsJsonObject("meta").get("productName").getAsString());
        assertTrue(json.getAsJsonObject("meta").has("version"));
        assertTrue(json.getAsJsonObject("meta").has("build"));
        assertTrue(json.getAsJsonObject("meta").has("licenseType"));
    }

    @Test
    public void enrichJsonBytes_isIdempotent() {
        byte[] body = "{\"status\":0,\"response\":{\"message\":\"error\"}}".getBytes(StandardCharsets.UTF_8);
        byte[] firstPass = ResponseMetadataEnricher.enrichJsonBytes(body);
        byte[] secondPass = ResponseMetadataEnricher.enrichJsonBytes(firstPass);

        assertEquals(new String(firstPass, StandardCharsets.UTF_8), new String(secondPass, StandardCharsets.UTF_8));
    }

    @Test
    public void enrichJsonBytes_skipsNonJsonBody() {
        byte[] html = "<html><body>ok</body></html>".getBytes(StandardCharsets.UTF_8);
        assertEquals(html, ResponseMetadataEnricher.enrichJsonBytes(html));
    }

    @Test
    public void enrichJsonBytes_skipsJsonArray() {
        byte[] array = "[{\"status\":1}]".getBytes(StandardCharsets.UTF_8);
        assertEquals(array, ResponseMetadataEnricher.enrichJsonBytes(array));
    }

    @Test
    public void enrichJsonBytes_skipsEmptyBody() {
        byte[] empty = new byte[0];
        assertEquals(0, ResponseMetadataEnricher.enrichJsonBytes(empty).length);
    }

    @Test
    public void getMetaObject_containsExpectedKeys() {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();

        assertTrue(meta.has("productName"));
        assertTrue(meta.has("version"));
        assertTrue(meta.has("build"));
        assertTrue(meta.has("licenseType"));
        assertFalse(meta.get("productName").getAsString().isEmpty());
    }
}

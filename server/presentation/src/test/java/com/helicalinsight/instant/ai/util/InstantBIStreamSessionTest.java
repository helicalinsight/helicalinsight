package com.helicalinsight.instant.ai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.efw.utility.JsonUtils;
import com.sun.net.httpserver.HttpServer;

import jakarta.servlet.http.HttpServletResponse;

public class InstantBIStreamSessionTest {

    @After
    public void cleanup() {
        RequestRegistryFilter.cancelledRequests.clear();
        RequestContext.clear();
    }

    @Test
    public void wrapCompleteUsesInteractiveEnvelope() {
        InstantBIStreamSession session = new InstantBIStreamSession(mock(HttpServletResponse.class), "req");
        JsonObject python = new JsonObject();
        python.add("chat_response", new JsonObject());
        JsonObject wrapped = session.wrapComplete("/interactive", python);
        assertEquals(1, wrapped.get("status").getAsInt());
        assertTrue(wrapped.getAsJsonObject("response").has("chat_response"));
    }

    @Test
    public void wrapCompleteUsesDataInsightEnvelope() {
        InstantBIStreamSession session = new InstantBIStreamSession(mock(HttpServletResponse.class), "req");
        JsonObject python = new JsonObject();
        python.addProperty("insight", "summary");
        JsonObject wrapped = session.wrapComplete("/data-insight", python);
        assertEquals("summary", wrapped.getAsJsonObject("response").get("insight").getAsString());
    }

    @Test
    public void wrapCompleteUsesConvertDashboardEnvelope() {
        InstantBIStreamSession session = new InstantBIStreamSession(mock(HttpServletResponse.class), "req");
        JsonObject python = new JsonObject();
        python.add("layout", new com.google.gson.JsonArray());
        python.addProperty("templateId", "t1");
        JsonObject wrapped = session.wrapComplete("/convert-dashboard", python);
        assertEquals("t1", wrapped.getAsJsonObject("response").get("templateId").getAsString());
        assertFalse(wrapped.getAsJsonObject("response").has("metadata"));
    }

    @Test
    public void copyEventsForwardsProgressAndWrapsComplete() throws Exception {
        StringWriter output = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(output, true));
        InstantBIStreamSession session = new InstantBIStreamSession(response, "req-copy");

        String sse = "event: begin\ndata: {\"status\":\"STARTED\"}\n\n"
                + "event: progress\ndata: {\"stage\":\"sql\",\"status\":\"started\",\"message\":\"Generating SQL...\"}\n\n"
                + "event: complete\ndata: {\"chat_response\":{\"sql\":\"select 1\"}}\n\n";
        session.copyEvents(new ByteArrayInputStream(sse.getBytes(StandardCharsets.UTF_8)), "/interactive");

        String text = output.toString();
        assertTrue(text.contains("event: begin"));
        assertTrue(text.contains("Generating SQL..."));
        assertTrue(text.contains("event: complete"));
        assertTrue(text.contains("\"status\":1"));
        assertTrue(text.contains("\"sql\":\"select 1\""));
    }

    @Test(expected = EfwServiceException.class)
    public void copyEventsAbortsWhenRequestCancelled() throws Exception {
        StringWriter output = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(output, true));
        InstantBIStreamSession session = new InstantBIStreamSession(response, "req-abort-copy");
        RequestRegistryFilter.cancelledRequests.add("req-abort-copy");

        String sse = "event: begin\ndata: {\"status\":\"STARTED\"}\n\n"
                + "event: complete\ndata: {\"chat_response\":{}}\n\n";
        session.copyEvents(new ByteArrayInputStream(sse.getBytes(StandardCharsets.UTF_8)), "/interactive");
    }

    @Test
    public void abortCancelsRegisteredHttpFuture() {
        CompletableFuture<Object> future = new CompletableFuture<>();
        InstantBIUtils.registerActiveHttpCall("req-abort-session", future);
        InstantBIStreamSession session = new InstantBIStreamSession(mock(HttpServletResponse.class), "req-abort-session");
        session.abort();
        assertTrue(future.isCancelled());
    }

    @Test
    public void forwardFromPythonWritesProgressBeforeComplete() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/interactive", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            exchange.sendResponseHeaders(200, 0);
            java.io.OutputStream body = exchange.getResponseBody();
            body.write("event: begin\ndata: {\"status\":\"STARTED\"}\n\n".getBytes(StandardCharsets.UTF_8));
            body.flush();
            body.write("event: progress\ndata: {\"stage\":\"sql\",\"status\":\"started\",\"message\":\"Generating SQL...\"}\n\n"
                    .getBytes(StandardCharsets.UTF_8));
            body.flush();
            body.write("event: complete\ndata: {\"chat_response\":{}}\n\n".getBytes(StandardCharsets.UTF_8));
            body.close();
            exchange.close();
        });
        server.start();
        try {
            JsonObject settings = new JsonObject();
            JsonObject config = new JsonObject();
            config.addProperty("serviceUrl", "http://127.0.0.1:" + server.getAddress().getPort() + "/");
            settings.add("instantbiConfig", config);

            StringWriter output = new StringWriter();
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(response.getWriter()).thenReturn(new PrintWriter(output, true));

            try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
                jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
                InstantBIStreamSession session = new InstantBIStreamSession(response, "req-live");
                session.forwardFromPython("/interactive", new JsonObject());
            }

            String text = output.toString();
            int beginAt = text.indexOf("event: begin");
            int progressAt = text.indexOf("Generating SQL...");
            int completeAt = text.indexOf("event: complete");
            assertTrue(beginAt >= 0);
            assertTrue(progressAt > beginAt);
            assertTrue(completeAt > progressAt);
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void forwardFromPythonProxiesSseFromPython() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String sse = "event: begin\ndata: {\"status\":\"STARTED\"}\n\n"
                + "event: progress\ndata: {\"stage\":\"execute\",\"status\":\"started\",\"message\":\"Executing SQL...\"}\n\n"
                + "event: complete\ndata: {\"insight\":\"ok\",\"token_usage\":{\"total_tokens\":1}}\n\n";
        AtomicBoolean sawStreamQuery = new AtomicBoolean(false);
        server.createContext("/data-insight", exchange -> {
            sawStreamQuery.set(exchange.getRequestURI().getQuery() != null
                    && exchange.getRequestURI().getQuery().contains("stream=true"));
            byte[] bytes = sse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        try {
            JsonObject settings = new JsonObject();
            JsonObject config = new JsonObject();
            config.addProperty("serviceUrl", "http://127.0.0.1:" + server.getAddress().getPort() + "/");
            settings.add("instantbiConfig", config);

            StringWriter output = new StringWriter();
            HttpServletResponse response = mock(HttpServletResponse.class);
            when(response.getWriter()).thenReturn(new PrintWriter(output, true));

            try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
                jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
                InstantBIStreamSession session = new InstantBIStreamSession(response, "req-forward");
                session.forwardFromPython("/data-insight", new JsonObject());
            }

            String text = output.toString();
            assertTrue(sawStreamQuery.get());
            assertTrue(text.contains("Executing SQL..."));
            assertTrue(text.contains("\"insight\":\"ok\""));
            assertTrue(text.contains("\"status\":1"));
        } finally {
            server.stop(0);
        }
    }
}

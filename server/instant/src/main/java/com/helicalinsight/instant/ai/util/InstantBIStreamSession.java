package com.helicalinsight.instant.ai.util;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * One InstantBI SSE proxy session. Forwards Python activity events and wraps
 * {@code complete} in the same {@code {status:1, response:...}} envelope used by JSON.
 */
public class InstantBIStreamSession {

    private static final Logger logger = LoggerFactory.getLogger(InstantBIStreamSession.class);

    private final HttpServletResponse response;
    private final String requestId;
    private PrintWriter writer;
    private CompletableFuture<HttpResponse<InputStream>> pythonCall;

    public InstantBIStreamSession(HttpServletResponse response, String requestId) {
        this.response = response;
        this.requestId = requestId;
    }

    public void forwardFromPython(String endpoint, JsonObject body) {
        if (InstantBIUtils.isRequestCancelled(requestId)) {
            throw new EfwServiceException("Request has been cancelled.");
        }
        try {
            openSseResponse();
            InputStream pythonStream = openPythonStream(endpoint, body);
            copyEvents(pythonStream, endpoint);
        } catch (EfwServiceException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            abort();
            throw new EfwServiceException("Request has been cancelled.");
        } catch (Exception exception) {
            if (InstantBIUtils.isRequestCancelled(requestId)) {
                abort();
                throw new EfwServiceException("Request has been cancelled.");
            }
            logger.error("InstantBI SSE proxy failed for endpoint={} requestId={}", endpoint, requestId, exception);
            throw new EfwServiceException("problem while loading the call " + endpoint);
        } finally {
            close();
        }
    }

    public void writeEvent(String eventName, JsonObject payload) {
        if (writer == null) {
            openSseResponse();
        }
        writer.write("event: " + eventName + "\n");
        writer.write("data: " + payload.toString() + "\n\n");
        writer.flush();
        flushToClient();
    }

    public void abort() {
        InstantBIUtils.cancelActiveHttpCall(requestId);
        close();
    }

    public void close() {
        InstantBIUtils.unregisterActiveHttpCall(requestId);
        if (pythonCall != null && !pythonCall.isDone()) {
            pythonCall.cancel(true);
        }
        if (writer != null) {
            try {
                writer.flush();
            } catch (Exception ignore) {
                logger.debug("Could not flush InstantBI SSE writer for requestId={}", requestId);
            }
        }
    }

    void copyEvents(InputStream pythonStream, String endpoint) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(pythonStream, StandardCharsets.UTF_8));
        String eventName = null;
        StringBuilder data = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            ensureNotAborted();
            if (line.startsWith("event:")) {
                eventName = line.substring("event:".length()).trim();
            } else if (line.startsWith("data:")) {
                if (data.length() > 0) {
                    data.append('\n');
                }
                data.append(line.substring("data:".length()).trim());
            } else if (StringUtils.isBlank(line)) {
                dispatchEvent(eventName, data.toString(), endpoint);
                eventName = null;
                data.setLength(0);
            }
        }
        if (eventName != null || data.length() > 0) {
            dispatchEvent(eventName, data.toString(), endpoint);
        }
    }

    JsonObject wrapComplete(String endpoint, JsonObject pythonPayload) {
        String raw = pythonPayload.toString();
        JsonObject prepared;
        if ("/data-insight".equals(endpoint)) {
            prepared = InstantBIUtils.prepareDataInsightResponse(raw);
        } else if ("/convert-dashboard".equals(endpoint)) {
            prepared = InstantBIUtils.prepareConvertDashboardResponse(raw);
        } else {
            prepared = InstantBIUtils.prepareResponse("", raw, null);
        }
        JsonObject envelope = new JsonObject();
        envelope.addProperty("status", 1);
        envelope.add("response", prepared);
        return envelope;
    }

    private void dispatchEvent(String eventName, String data, String endpoint) {
        if (StringUtils.isBlank(eventName) && StringUtils.isBlank(data)) {
            return;
        }
        JsonObject payload = parseJsonObject(data);
        if ("complete".equals(eventName)) {
            writeEvent("complete", wrapComplete(endpoint, payload));
            return;
        }
        writeEvent(StringUtils.defaultIfBlank(eventName, "message"), payload);
    }

    private JsonObject parseJsonObject(String data) {
        if (StringUtils.isBlank(data)) {
            return new JsonObject();
        }
        try {
            return GsonUtility.parseString(data, JsonObject.class);
        } catch (Exception exception) {
            JsonObject fallback = new JsonObject();
            fallback.addProperty("error", data);
            return fallback;
        }
    }

    private void openSseResponse() {
        if (writer != null) {
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("X-Accel-Buffering", "no");
        try {
            response.setBufferSize(512);
        } catch (IllegalArgumentException ignore) {
            logger.debug("Could not shrink InstantBI SSE buffer for requestId={}", requestId);
        }
        try {
            writer = response.getWriter();
            flushToClient();
        } catch (IOException exception) {
            throw new EfwServiceException("problem while loading the call stream");
        }
    }

    private void flushToClient() {
        try {
            response.flushBuffer();
        } catch (IOException exception) {
            logger.debug("Could not flush InstantBI SSE buffer for requestId={}", requestId);
        }
    }

    private InputStream openPythonStream(String endpoint, JsonObject body)
            throws InterruptedException, ExecutionException, IOException {
        JsonObject outgoing = body == null ? new JsonObject() : body.deepCopy();
        if (StringUtils.isNotBlank(requestId) && !outgoing.has("requestId")) {
            outgoing.addProperty("requestId", requestId);
        }
        String url = InstantBIUtils.getInstantBIServiceUrl() + stripLeadingSlash(endpoint) + "?stream=true";
        if (StringUtils.isNotBlank(requestId)) {
            url = url + "&requestId=" + requestId;
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream");
        InstantBIUtils.applyForwardedHeaders(requestBuilder, outgoing);
        HttpRequest httpRequest = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(outgoing.toString()))
                .build();

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        pythonCall = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        InstantBIUtils.registerActiveHttpCall(requestId, pythonCall);
        HttpResponse<InputStream> pythonResponse = pythonCall.get();
        ensureNotAborted();
        if (pythonResponse.statusCode() >= 400) {
            throw new EfwServiceException("problem while loading the call " + endpoint);
        }
        return pythonResponse.body();
    }

    private void ensureNotAborted() {
        if (InstantBIUtils.isRequestCancelled(requestId)) {
            abort();
            throw new EfwServiceException("Request has been cancelled.");
        }
    }

    private static String stripLeadingSlash(String endpoint) {
        if (endpoint == null) {
            return "";
        }
        return endpoint.startsWith("/") ? endpoint.substring(1) : endpoint;
    }
}

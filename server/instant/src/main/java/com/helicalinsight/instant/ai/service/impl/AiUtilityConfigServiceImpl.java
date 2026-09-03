package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.payload.IInstantBIPayload;
import com.helicalinsight.instant.ai.payload.UtilityConfigPayload;
import com.helicalinsight.instant.ai.service.IInstantBIService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Proxies Admin InstantBI Settings UI calls to the Python {@code /utility/*} APIs.
 *
 * <p>Payload sources (first match wins):
 * <ol>
 *   <li>{@code body} request parameter containing a JSON object string</li>
 *   <li>Raw {@code application/json} request entity</li>
 *   <li>Remaining form/query parameters (excluding session plumbing keys)</li>
 * </ol>
 */
@Service(InstantBIServiceFactory.UTILITY_CONFIG_SERVICE)
public class AiUtilityConfigServiceImpl implements IInstantBIService {

    private static final Logger logger = LoggerFactory.getLogger(AiUtilityConfigServiceImpl.class);

    private static final Set<String> SKIP_PARAMS = new HashSet<>(Arrays.asList(
            "body", "requestId", "htmlId", "sessionCookie", "username", "userId", "orgId",
            "headers", "requestParams"
    ));

    @Override
    public void execute(IInstantBIPayload instantBIPayload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        UtilityConfigPayload payload = (UtilityConfigPayload) instantBIPayload;
        String utilityPath = payload.getUtilityPath();
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            if (StringUtils.isBlank(utilityPath)
                    || !(utilityPath.startsWith("/utility") || utilityPath.startsWith("/settings"))) {
                throw new IllegalArgumentException("Invalid InstantBI settings path: " + utilityPath);
            }

            JsonObject payloadJson = buildPayload(request);
            InstantBIUtils.addSessionContext(request, payloadJson);

            logger.info("Proxying InstantBI utility path={} keys={}", utilityPath, payloadJson.keySet());
            String botResponse = InstantBIServiceFactory.getHttpService().callHttp(utilityPath, payloadJson);
            JsonObject utilityPayload = GsonUtility.parseString(botResponse, JsonObject.class);

            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", utilityPayload);
            InstantBIUtils.sendResponse(response, isAjax, mainObject);
        } catch (Exception exception) {
            logger.error("Failed InstantBI utility call path={}", utilityPath, exception);
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    JsonObject buildPayload(HttpServletRequest request) throws IOException {
        String bodyParam = request.getParameter("body");
        if (StringUtils.isNotBlank(bodyParam)) {
            JsonObject fromBody = tryParseObject(bodyParam);
            if (fromBody != null) {
                return fromBody;
            }
        }

        String contentType = StringUtils.defaultString(request.getContentType()).toLowerCase();
        if (contentType.contains("application/json")) {
            String raw = readRequestBody(request);
            if (StringUtils.isNotBlank(raw)) {
                JsonObject fromEntity = tryParseObject(raw);
                if (fromEntity != null) {
                    return fromEntity;
                }
            }
        }

        JsonObject payload = new JsonObject();
        Enumeration<String> names = request.getParameterNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            if (SKIP_PARAMS.contains(name)) {
                continue;
            }
            String value = request.getParameter(name);
            if (value != null) {
                payload.addProperty(name, value);
            }
        }
        return payload;
    }

    private JsonObject tryParseObject(String raw) {
        try {
            JsonElement parsed = JsonParser.parseString(raw);
            if (parsed != null && parsed.isJsonObject()) {
                return parsed.getAsJsonObject();
            }
        } catch (Exception exception) {
            logger.debug("Could not parse utility payload as JSON object", exception);
        }
        return null;
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }
}

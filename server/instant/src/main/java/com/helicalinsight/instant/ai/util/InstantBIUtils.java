package com.helicalinsight.instant.ai.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.efw.utility.JsonUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class InstantBIUtils {

    private static final Logger logger = LoggerFactory.getLogger(InstantBIUtils.class);
    private static final Map<String, CompletableFuture<HttpResponse<String>>> ACTIVE_HTTP_CALLS = new ConcurrentHashMap<>();

    /**
     * Hop-by-hop / transport headers that must not be copied onto InstantBI or
     * back to hi-ee. Auth headers such as Authorization, authToken, type, Cookie,
     * and X-Auth-Token are forwarded so JWT/SSO callbacks succeed.
     */
    private static final Set<String> SKIP_HEADER_NAMES = new HashSet<>(Arrays.asList(
            "host", "connection", "content-length", "content-type", "content-encoding",
            "transfer-encoding", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "upgrade", "expect", "accept-encoding"
    ));

    private static final Set<String> SKIP_PARAM_NAMES = new HashSet<>(Arrays.asList(
            "input", "chatid", "chat_sequence_id", "subject", "formData", "model", "domain",
            "topN", "requestId", "htmlId", "dashboardid", "dashboard_sequence_id", "mode",
            "items", "body", "sessionCookie", "username", "userId", "orgId", "headers",
            "requestParams"
    ));

    private static final List<String> AUTH_PARAM_NAMES = Arrays.asList(
            "Authorization", "authToken", "type", "X-Auth-Token"
    );
    private static final List<String> AUTH_CREDENTIAL_NAMES = Arrays.asList(
            "Authorization", "authToken", "X-Auth-Token"
    );

    private InstantBIUtils() {
    }

    @Nullable
    public static String getEncodedElseNormal(String subject) {
        if (subject == null || !Base64.isBase64(subject)) {
            return subject;
        }
        try {
            byte[] decoded = Base64.decodeBase64(subject);
            if (subject.equals(Base64.encodeBase64String(decoded))
                    || subject.equals(Base64.encodeBase64URLSafeString(decoded))) {
                return new String(decoded, ControllerUtils.defaultCharSet());
            }
        } catch (Exception ignore) {
            logger.error("Encoding exception occurred " + ignore);
        }
        return subject;
    }

    public static void sendResponse(HttpServletResponse response, boolean isAjax, JsonObject responseFinal) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        if (isAjax) {
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        } else {
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
        }
        ControllerUtils.handleSuccess(response, isAjax, responseFinal.toString());
    }

    public static JsonArray getHistory(JsonArray inputs, int chatSequenceId) {
        JsonArray history = new JsonArray();

        for (int index = inputs.size() - 1; index >= 0; index--) {
            JsonObject obj = inputs.get(index).getAsJsonObject();

            int seqId = obj.get("chat_sequence_id").getAsInt();

            if (seqId < chatSequenceId) {
                history.add(obj.get("input").getAsString());
            }
        }

        return history;
    }

    @NotNull
    public static JsonObject prepareResponse(String input, String botResponse, JsonObject js) {
        return GsonUtility.parseString(botResponse, JsonObject.class);
    }

    @NotNull
    public static JsonObject prepareDataInsightResponse(String botResponse) {
        JsonObject outputJson = GsonUtility.parseString(botResponse, JsonObject.class);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("insight", GsonUtility.optString(outputJson, "insight"));
        JsonObject tokenUsage = GsonUtility.optJsonObject(outputJson, "token_usage");
        if (tokenUsage != null) {
            responseObject.add("token_usage", tokenUsage);
        }
        return responseObject;
    }

    @NotNull
    public static JsonObject prepareConvertHreportResponse(String botResponse) {
        JsonObject outputJson = GsonUtility.parseString(botResponse, JsonObject.class);
        JsonObject responseObject = new JsonObject();
        JsonObject sqlParts = GsonUtility.optJsonObject(outputJson, "sql_parts");
        JsonObject vizParts = GsonUtility.optJsonObject(outputJson, "viz_parts");
        if (sqlParts != null) {
            responseObject.add("sql_parts", sqlParts);
        }
        if (vizParts != null) {
            responseObject.add("viz_parts", vizParts);
        }
        String error = GsonUtility.optString(outputJson, "error");
        if (StringUtils.isNotBlank(error)) {
            responseObject.addProperty("error", error);
        }
        return responseObject;
    }

    @NotNull
    public static JsonObject prepareConvertDashboardResponse(String botResponse) {
        JsonObject outputJson = GsonUtility.parseString(botResponse, JsonObject.class);
        JsonObject responseObject = new JsonObject();
        String[] keys = {"items", "chatid", "theme", "summary", "sections", "filters", "layout", "templateId", "decorations"};
        for (String key : keys) {
            if (outputJson.has(key)) {
                responseObject.add(key, outputJson.get(key));
            }
        }
        String error = GsonUtility.optString(outputJson, "error");
        if (StringUtils.isNotBlank(error)) {
            responseObject.addProperty("error", error);
        }
        return responseObject;
    }

    public static void addRoleProfile(User loggedInUser, JsonObject js) {
        List<Role> roles = loggedInUser.getRoles();
        JsonArray roleData = new JsonArray();
        for (Role r : roles) {
            JsonObject roleObj = new JsonObject();
            int id = r.getId();
            String roleName = r.getRole_name();
            roleObj.addProperty("id", id);
            roleObj.addProperty("roleName", roleName);
            roleData.add(roleObj);
        }
        js.add("userRole", roleData);
        List<Profile> profiles = loggedInUser.getProfile();
        JsonArray profileArray = new JsonArray();
        for (Profile profile : profiles) {
            JsonObject profileObj = new JsonObject();
            profileObj.addProperty("id", profile.getId());
            profileObj.addProperty("name", profile.getProfile_name());
            profileObj.addProperty("value", profile.getProfile_value());
            profileArray.add(profileObj);
        }
        js.add("userProfile", profileArray);
    }

    /**
     * InstantBI interactive-chat body: session, model, inputString, chatid, chat_seq_id.
     */
    @NotNull
    public static JsonObject buildInteractiveChatRequest(HttpServletRequest request, String input, String chatid,
            String chatSeqId, String subject) {
        return buildConversationRequest(request, input, "chatid", chatid, "chat_seq_id", chatSeqId, subject);
    }

    /**
     * Same envelope as interactive chat, with {@code dashboardid} / {@code dashboard_sequence_id}.
     */
    @NotNull
    public static JsonObject buildAgentDashboardRequest(HttpServletRequest request, String input, String dashboardid,
            String dashboardSeqId, String subject) {
        return buildAgentDashboardRequest(request, input, dashboardid, dashboardSeqId, subject, null);
    }

    @NotNull
    public static JsonObject buildAgentDashboardRequest(HttpServletRequest request, String input, String dashboardid,
            String dashboardSeqId, String subject, String mode) {
        JsonObject js = buildConversationRequest(request, input, "dashboardid", dashboardid, "dashboard_sequence_id",
                dashboardSeqId, subject);
        if (StringUtils.isNotBlank(mode)) {
            js.getAsJsonObject("input").addProperty("mode", mode.trim());
        }
        return js;
    }

    @NotNull
    static JsonObject buildConversationRequest(HttpServletRequest request, String input, String idProperty,
            String idValue, String seqProperty, String seqValue, String subject) {
        JsonObject js = new JsonObject();
        JsonObject userInput = new JsonObject();

        if (StringUtils.isNotBlank(subject)) {
            String decodedSubject = getEncodedElseNormal(subject);
            JsonObject subjectJson = GsonUtility.parseString(decodedSubject, JsonObject.class);
            JsonObject modelJson = subjectJson.get("model").getAsJsonObject();
            userInput.add("model", modelJson);
            userInput.addProperty("reportId", extractJsessionId(request));
        }
        Principal userDetails = AuthenticationUtils.getUserDetails();
        User loggedInUser = userDetails.getLoggedInUser();
        addRoleProfile(loggedInUser, js);

        addSessionContext(request, userInput);
        userInput.addProperty("inputString", input);
        userInput.addProperty(idProperty, idValue);
        userInput.addProperty(seqProperty, seqValue);

        js.add("input", userInput);
        return js;
    }

    public static void addSessionContext(HttpServletRequest request, JsonObject target) {
        JsonObject headers = collectRequestHeaders(request);
        JsonObject requestParams = collectRequestParameters(request, target);
        copyAuthParamsIntoHeaders(headers, requestParams);

        String sessionCookie = extractJsessionId(request);
        if (StringUtils.isBlank(sessionCookie) && !hasAuthCredentials(headers, requestParams)) {
            throw new EfwServiceException("Session cookie not found.");
        }
        Principal userDetails = AuthenticationUtils.getUserDetails();
        User loggedInUser = userDetails.getLoggedInUser();
        if (StringUtils.isNotBlank(sessionCookie)) {
            target.addProperty("sessionCookie", sessionCookie);
        }
        target.addProperty("username", loggedInUser.getUsername());
        target.addProperty("userId", loggedInUser.getId());
        Integer orgId = loggedInUser.getOrg_id();
        if (orgId != null) {
            target.addProperty("orgId", orgId);
        }
        if (headers.size() > 0) {
            target.add("headers", headers);
        }
        if (requestParams.size() > 0) {
            target.add("requestParams", requestParams);
        }
    }

    /**
     * Incoming request headers to copy onto InstantBI and later back to hi-ee.
     */
    @NotNull
    public static JsonObject collectRequestHeaders(HttpServletRequest request) {
        JsonObject headers = new JsonObject();
        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (StringUtils.isBlank(name) || shouldSkipHeader(name)) {
                    continue;
                }
                String value = joinHeaderValues(request.getHeaders(name), isCookieHeader(name) ? "; " : ", ");
                if (StringUtils.isNotBlank(value)) {
                    headers.addProperty(name, value);
                }
            }
        }
        if (!hasHeaderIgnoreCase(headers, "Cookie")) {
            String cookieHeader = buildCookieHeader(request);
            if (StringUtils.isNotBlank(cookieHeader)) {
                headers.addProperty("Cookie", cookieHeader);
            }
        }
        return headers;
    }

    /**
     * Extra request parameters (JWT {@code authToken}, SSO {@code type}, …) that are
     * not already part of the InstantBI payload.
     */
    @NotNull
    public static JsonObject collectRequestParameters(HttpServletRequest request, JsonObject target) {
        JsonObject params = new JsonObject();
        Enumeration<String> names = request.getParameterNames();
        if (names == null) {
            return params;
        }
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (StringUtils.isBlank(name) || SKIP_PARAM_NAMES.contains(name)
                    || (target != null && target.has(name))) {
                continue;
            }
            String value = request.getParameter(name);
            if (value != null) {
                params.addProperty(name, value);
            }
        }
        return params;
    }

    public static void applyForwardedHeaders(HttpRequest.Builder builder, JsonObject body) {
        JsonObject headers = resolveForwardedObject(body, "headers");
        JsonObject requestParams = resolveForwardedObject(body, "requestParams");
        if (headers == null) {
            headers = new JsonObject();
        } else {
            headers = headers.deepCopy();
        }
        copyAuthParamsIntoHeaders(headers, requestParams);
        for (String name : headers.keySet()) {
            if (shouldSkipHeader(name)) {
                continue;
            }
            String value = GsonUtility.optString(headers, name);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            try {
                builder.header(name, value);
            } catch (IllegalArgumentException exception) {
                logger.debug("Skipping restricted InstantBI header {}", name);
            }
        }
    }

    @Nullable
    static JsonObject resolveForwardedObject(JsonObject body, String key) {
        JsonObject value = GsonUtility.optJsonObject(body, key);
        if (value != null) {
            return value;
        }
        JsonObject input = GsonUtility.optJsonObject(body, "input");
        return GsonUtility.optJsonObject(input, key);
    }

    static boolean hasAuthCredentials(JsonObject headers, JsonObject requestParams) {
        for (String name : AUTH_CREDENTIAL_NAMES) {
            if (hasNonBlankIgnoreCase(headers, name) || hasNonBlankIgnoreCase(requestParams, name)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static String extractJsessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Nullable
    public static String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getParameter("requestId");
        if (StringUtils.isBlank(requestId)) {
            Object requestIdAttribute = request.getAttribute("requestId");
            if (requestIdAttribute != null) {
                requestId = requestIdAttribute.toString();
            }
        }
        if (StringUtils.isBlank(requestId)) {
            requestId = RequestContext.get();
        }
        if (StringUtils.isBlank(requestId)) {
            logger.warn("Could not resolve requestId for cancellable instant BI call");
        } else {
            logger.debug("Resolved requestId={} for cancellable instant BI call", requestId);
        }
        return requestId;
    }

    public static boolean isRequestCancelled(@Nullable String requestId) {
        return StringUtils.isNotBlank(requestId)
                && RequestRegistryFilter.cancelledRequests.contains(requestId);
    }

    public static boolean isAbortException(EfwServiceException exception) {
        String message = exception.getMessage();
        return message != null && message.toLowerCase().contains("cancel");
    }

    public static void registerActiveHttpCall(String requestId, CompletableFuture<HttpResponse<String>> responseFuture) {
        if (StringUtils.isNotBlank(requestId)) {
            ACTIVE_HTTP_CALLS.put(requestId, responseFuture);
        }
    }

    public static void unregisterActiveHttpCall(String requestId) {
        if (StringUtils.isNotBlank(requestId)) {
            ACTIVE_HTTP_CALLS.remove(requestId);
        }
    }

    public static void cancelActiveHttpCall(@Nullable String requestId) {
        if (StringUtils.isBlank(requestId)) {
            return;
        }
        CompletableFuture<HttpResponse<String>> activeCall = ACTIVE_HTTP_CALLS.remove(requestId);
        if (activeCall != null) {
            activeCall.cancel(true);
        }
    }

    public static String getInstantBIServiceUrl() {
        JsonObject instantBiConfig = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(), "instantbiConfig");
        String serviceUrl = instantBiConfig == null
                ? "http://pyflask:8000/"
                : GsonUtility.optStringValue(instantBiConfig, "serviceUrl", "http://pyflask:8000/");
        if (!serviceUrl.endsWith("/")) {
            serviceUrl = serviceUrl + "/";
        }
        return serviceUrl;
    }

    static boolean shouldSkipHeader(String name) {
        return name != null && SKIP_HEADER_NAMES.contains(name.toLowerCase(Locale.ROOT));
    }

    private static void copyAuthParamsIntoHeaders(JsonObject headers, JsonObject requestParams) {
        if (headers == null || requestParams == null) {
            return;
        }
        for (String name : AUTH_PARAM_NAMES) {
            if (hasNonBlankIgnoreCase(headers, name)) {
                continue;
            }
            String value = firstNonBlankIgnoreCase(requestParams, name);
            if (StringUtils.isNotBlank(value)) {
                headers.addProperty(name, value);
            }
        }
    }

    private static boolean hasHeaderIgnoreCase(JsonObject headers, String name) {
        return firstNonBlankIgnoreCase(headers, name) != null;
    }

    private static boolean hasNonBlankIgnoreCase(JsonObject object, String name) {
        return StringUtils.isNotBlank(firstNonBlankIgnoreCase(object, name));
    }

    @Nullable
    private static String firstNonBlankIgnoreCase(JsonObject object, String name) {
        if (object == null || StringUtils.isBlank(name)) {
            return null;
        }
        if (object.has(name)) {
            String value = GsonUtility.optString(object, name);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        for (String key : object.keySet()) {
            if (name.equalsIgnoreCase(key)) {
                String value = GsonUtility.optString(object, key);
                if (StringUtils.isNotBlank(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    private static boolean isCookieHeader(String name) {
        return "cookie".equalsIgnoreCase(name);
    }

    @Nullable
    private static String buildCookieHeader(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        StringBuilder cookieHeader = new StringBuilder();
        for (Cookie cookie : cookies) {
            if (cookie == null || StringUtils.isBlank(cookie.getName())) {
                continue;
            }
            if (cookieHeader.length() > 0) {
                cookieHeader.append("; ");
            }
            cookieHeader.append(cookie.getName()).append("=").append(StringUtils.defaultString(cookie.getValue()));
        }
        return cookieHeader.length() == 0 ? null : cookieHeader.toString();
    }

    private static String joinHeaderValues(Enumeration<String> values, String delimiter) {
        if (values == null) {
            return null;
        }
        StringBuilder joined = new StringBuilder();
        while (values.hasMoreElements()) {
            String value = values.nextElement();
            if (StringUtils.isBlank(value)) {
                continue;
            }
            if (joined.length() > 0) {
                joined.append(delimiter);
            }
            joined.append(value);
        }
        return joined.length() == 0 ? null : joined.toString();
    }
}

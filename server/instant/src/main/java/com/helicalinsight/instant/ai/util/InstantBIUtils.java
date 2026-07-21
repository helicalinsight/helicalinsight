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
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class InstantBIUtils {

    private static final Logger logger = LoggerFactory.getLogger(InstantBIUtils.class);
    private static final Map<String, CompletableFuture<HttpResponse<String>>> ACTIVE_HTTP_CALLS = new ConcurrentHashMap<>();

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

    public static void addSessionContext(HttpServletRequest request, JsonObject target) {
        Principal userDetails = AuthenticationUtils.getUserDetails();
        User loggedInUser = userDetails.getLoggedInUser();
        String sessionCookie = extractJsessionId(request);
        if (StringUtils.isBlank(sessionCookie)) {
            throw new EfwServiceException("Session cookie not found.");
        }
        target.addProperty("sessionCookie", sessionCookie);
        target.addProperty("username", loggedInUser.getUsername());
        target.addProperty("userId", loggedInUser.getId());
        Integer orgId = loggedInUser.getOrg_id();
        if (orgId != null) {
            target.addProperty("orgId", orgId);
        }
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
}

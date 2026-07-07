package com.helicalinsight.instant.ai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.efw.utility.JsonUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InstantBIUtilsTest {

    @After
    public void cleanup() {
        RequestRegistryFilter.cancelledRequests.clear();
        RequestContext.clear();
    }

    @Test
    public void getEncodedElseNormalDecodesBase64Input() {
        String encoded = Base64.encodeBase64String("decoded-value".getBytes(StandardCharsets.UTF_8));
        assertEquals("decoded-value", InstantBIUtils.getEncodedElseNormal(encoded));
    }

    @Test
    public void getEncodedElseNormalReturnsPlainTextUnchanged() {
        assertEquals("plain-text", InstantBIUtils.getEncodedElseNormal("plain-text"));
    }

    @Test
    public void getEncodedElseNormalReturnsNullForNullInput() {
        assertNull(InstantBIUtils.getEncodedElseNormal(null));
    }

    @Test
    public void getHistoryReturnsInputsBeforeSequenceId() {
        JsonArray inputs = new JsonArray();
        inputs.add(chatInput(1, "first"));
        inputs.add(chatInput(3, "second"));
        inputs.add(chatInput(5, "third"));

        JsonArray history = InstantBIUtils.getHistory(inputs, 5);

        assertEquals(2, history.size());
        assertEquals("second", history.get(0).getAsString());
        assertEquals("first", history.get(1).getAsString());
    }

    @Test
    public void prepareResponseParsesBotResponseJson() {
        JsonObject parsed = InstantBIUtils.prepareResponse("ignored", "{\"answer\":\"ok\"}", null);
        assertEquals("ok", parsed.get("answer").getAsString());
    }

    @Test
    public void prepareDataInsightResponseExtractsInsightAndTokenUsage() {
        String botResponse = "{\"insight\":\"summary\",\"token_usage\":{\"total\":10}}";
        JsonObject response = InstantBIUtils.prepareDataInsightResponse(botResponse);

        assertEquals("summary", response.get("insight").getAsString());
        assertEquals(10, response.getAsJsonObject("token_usage").get("total").getAsInt());
    }

    @Test
    public void prepareDataInsightResponseOmitsTokenUsageWhenMissing() {
        JsonObject response = InstantBIUtils.prepareDataInsightResponse("{\"insight\":\"only\"}");
        assertEquals("only", response.get("insight").getAsString());
        assertFalse(response.has("token_usage"));
    }

    @Test
    public void addRoleProfileAddsRolesAndProfiles() {
        Role role = new Role();
        role.setId(1);
        role.setRole_name("Admin");

        Profile profile = new Profile();
        profile.setId(2);
        profile.setProfile_name("dept");
        profile.setProfile_value("sales");

        User user = mock(User.class);
        whenRolesAndProfiles(user, Collections.singletonList(role), Collections.singletonList(profile));

        JsonObject target = new JsonObject();
        InstantBIUtils.addRoleProfile(user, target);

        assertEquals(1, target.getAsJsonArray("userRole").size());
        assertEquals("Admin", target.getAsJsonArray("userRole").get(0).getAsJsonObject().get("roleName").getAsString());
        assertEquals("sales", target.getAsJsonArray("userProfile").get(0).getAsJsonObject().get("value").getAsString());
    }

    @Test
    public void addSessionContextAddsCookieAndUsername() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "abc123")});

        Principal principal = mock(Principal.class);
        when(principal.getUsername()).thenReturn("tester");

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);

            JsonObject target = new JsonObject();
            InstantBIUtils.addSessionContext(request, target);

            assertEquals("abc123", target.get("sessionCookie").getAsString());
            assertEquals("tester", target.get("username").getAsString());
        }
    }

    @Test(expected = EfwServiceException.class)
    public void addSessionContextThrowsWhenCookieMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[0]);

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(mock(Principal.class));
            InstantBIUtils.addSessionContext(request, new JsonObject());
        }
    }

    @Test
    public void extractJsessionIdReturnsCookieValue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});
        assertEquals("session-1", InstantBIUtils.extractJsessionId(request));
    }

    @Test
    public void extractJsessionIdReturnsNullWhenCookieAbsent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);
        assertNull(InstantBIUtils.extractJsessionId(request));
    }

    @Test
    public void resolveRequestIdUsesParameterThenAttributeThenContext() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("requestId")).thenReturn("param-id");
        assertEquals("param-id", InstantBIUtils.resolveRequestId(request));

        when(request.getParameter("requestId")).thenReturn(null);
        when(request.getAttribute("requestId")).thenReturn("attr-id");
        assertEquals("attr-id", InstantBIUtils.resolveRequestId(request));

        when(request.getAttribute("requestId")).thenReturn(null);
        RequestContext.set("context-id");
        assertEquals("context-id", InstantBIUtils.resolveRequestId(request));
    }

    @Test
    public void isRequestCancelledReturnsTrueForCancelledRequest() {
        RequestRegistryFilter.cancelledRequests.add("cancelled-id");
        assertTrue(InstantBIUtils.isRequestCancelled("cancelled-id"));
        assertFalse(InstantBIUtils.isRequestCancelled("active-id"));
        assertFalse(InstantBIUtils.isRequestCancelled(null));
    }

    @Test
    public void isAbortExceptionDetectsCancelMessage() {
        assertTrue(InstantBIUtils.isAbortException(new EfwServiceException("Request has been cancelled.")));
        assertFalse(InstantBIUtils.isAbortException(new EfwServiceException("other error")));
        assertFalse(InstantBIUtils.isAbortException(new EfwServiceException((String) null)));
    }

    @Test
    public void cancelActiveHttpCallCancelsRegisteredFuture() {
        CompletableFuture<HttpResponse<String>> future = new CompletableFuture<>();
        InstantBIUtils.registerActiveHttpCall("req-1", future);

        InstantBIUtils.cancelActiveHttpCall("req-1");

        assertTrue(future.isCancelled());
    }

    @Test
    public void unregisterActiveHttpCallRemovesRegisteredFuture() {
        CompletableFuture<HttpResponse<String>> future = new CompletableFuture<>();
        InstantBIUtils.registerActiveHttpCall("req-2", future);
        InstantBIUtils.unregisterActiveHttpCall("req-2");
        InstantBIUtils.cancelActiveHttpCall("req-2");
        assertFalse(future.isCancelled());
    }

    @Test
    public void getInstantBIServiceUrlUsesConfiguredValueAndEnsuresTrailingSlash() {
        JsonObject settings = new JsonObject();
        JsonObject config = new JsonObject();
        config.addProperty("serviceUrl", "http://instantbi:8000");
        settings.add("instantbiConfig", config);

        try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            assertEquals("http://instantbi:8000/", InstantBIUtils.getInstantBIServiceUrl());
        }
    }

    @Test
    public void getInstantBIServiceUrlUsesDefaultWhenConfigMissing() {
        try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(new JsonObject());
            assertEquals("http://pyflask:8000/", InstantBIUtils.getInstantBIServiceUrl());
        }
    }

    @Test
    public void sendResponseDelegatesToControllerUtils() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        JsonObject payload = new JsonObject();
        payload.addProperty("status", 1);

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class)) {
            InstantBIUtils.sendResponse(response, true, payload);
            controllerUtils.verify(() -> ControllerUtils.handleSuccess(response, true, payload.toString()));
        }
    }

    private static JsonObject chatInput(int sequenceId, String input) {
        JsonObject obj = new JsonObject();
        obj.addProperty("chat_sequence_id", sequenceId);
        obj.addProperty("input", input);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private static void whenRolesAndProfiles(User user, java.util.List<Role> roles, java.util.List<Profile> profiles) {
        when(user.getRoles()).thenReturn(roles);
        when(user.getProfile()).thenReturn(profiles);
    }
}

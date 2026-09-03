package com.helicalinsight.instant.ai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpRequest;
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
    public void prepareConvertHreportResponsePassesSqlAndVizParts() {
        String botResponse = "{\"sql_parts\":{\"columns\":[{\"column\":\"region\"}],\"location\":\"/meta\",\"metadataFileName\":\"pg.metadata\"},\"viz_parts\":{\"mark\":\"Chart\",\"viz\":\"Bar\"},\"metadata\":{\"tables\":{\"t\":{\"alias\":\"t\"}}}}";
        JsonObject response = InstantBIUtils.prepareConvertHreportResponse(botResponse);

        assertEquals("region", response.getAsJsonObject("sql_parts")
                .getAsJsonArray("columns").get(0).getAsJsonObject().get("column").getAsString());
        assertEquals("Chart", response.getAsJsonObject("viz_parts").get("mark").getAsString());
        assertEquals("/meta", response.getAsJsonObject("sql_parts").get("location").getAsString());
        assertEquals("pg.metadata", response.getAsJsonObject("sql_parts").get("metadataFileName").getAsString());
        assertFalse(response.has("metadata"));
        assertFalse(response.has("insight"));
    }

    @Test
    public void prepareConvertHreportResponseIncludesPythonError() {
        JsonObject response = InstantBIUtils.prepareConvertHreportResponse("{\"error\":\"No SQL found\"}");
        assertEquals("No SQL found", response.get("error").getAsString());
    }

    @Test
    public void prepareConvertDashboardResponsePassesLayoutParts() {
        String botResponse = "{\"items\":[{\"id\":\"seq-3\"}],\"layout\":[{\"itemId\":\"seq-3\",\"x\":0,\"y\":2,\"w\":6,\"h\":4}],\"theme\":{\"color\":\"#1677ff\"}}";
        JsonObject response = InstantBIUtils.prepareConvertDashboardResponse(botResponse);
        assertEquals("seq-3", response.getAsJsonArray("items").get(0).getAsJsonObject().get("id").getAsString());
        assertEquals("seq-3", response.getAsJsonArray("layout").get(0).getAsJsonObject().get("itemId").getAsString());
        assertEquals("#1677ff", response.getAsJsonObject("theme").get("color").getAsString());
        assertFalse(response.has("gridItemsData"));
        assertTrue(response.has("items"));
    }

    @Test
    public void prepareConvertDashboardResponsePassesTemplateId() {
        String botResponse = "{\"layout\":[],\"templateId\":\"executive-kpi-first\"}";
        JsonObject response = InstantBIUtils.prepareConvertDashboardResponse(botResponse);
        assertEquals("executive-kpi-first", response.get("templateId").getAsString());
    }

    @Test
    public void prepareConvertDashboardResponsePassesDecorations() {
        String botResponse = "{\"layout\":[],\"decorations\":[{\"kind\":\"separator\",\"w\":12,\"h\":1}]}";
        JsonObject response = InstantBIUtils.prepareConvertDashboardResponse(botResponse);
        assertEquals("separator", response.getAsJsonArray("decorations").get(0).getAsJsonObject().get("kind").getAsString());
    }

    @Test
    public void prepareConvertDashboardResponseOmitsMetadata() {
        String botResponse = "{\"layout\":[],\"metadata\":{\"tables\":{\"t\":{\"alias\":\"t\"}}}}";
        JsonObject response = InstantBIUtils.prepareConvertDashboardResponse(botResponse);
        assertFalse(response.has("metadata"));
        assertTrue(response.has("layout"));
    }

    @Test
    public void prepareConvertDashboardResponseIncludesPythonError() {
        JsonObject response = InstantBIUtils.prepareConvertDashboardResponse("{\"error\":\"No visualizations were provided\"}");
        assertEquals("No visualizations were provided", response.get("error").getAsString());
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
    public void buildInteractiveChatRequestMatchesInteractiveEnvelope() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getOrg_id()).thenReturn(5);
        when(user.getRoles()).thenReturn(Collections.emptyList());
        when(user.getProfile()).thenReturn(Collections.emptyList());
        when(principal.getLoggedInUser()).thenReturn(user);

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);

            JsonObject body = InstantBIUtils.buildInteractiveChatRequest(
                    request,
                    "Show sales by region",
                    "chat-1",
                    "3",
                    "{\"model\":{\"dir\":\"MyFolder\",\"file\":\"Sales.agent\"}}");

            JsonObject input = body.getAsJsonObject("input");
            assertEquals("Show sales by region", input.get("inputString").getAsString());
            assertEquals("chat-1", input.get("chatid").getAsString());
            assertEquals("3", input.get("chat_seq_id").getAsString());
            assertEquals("session-1", input.get("sessionCookie").getAsString());
            assertEquals("tester", input.get("username").getAsString());
            assertEquals("MyFolder", input.getAsJsonObject("model").get("dir").getAsString());
            assertEquals("Sales.agent", input.getAsJsonObject("model").get("file").getAsString());
            assertEquals("session-1", input.get("reportId").getAsString());
        }
    }

    @Test
    public void buildAgentDashboardRequestUsesDashboardidNotChatid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getOrg_id()).thenReturn(5);
        when(user.getRoles()).thenReturn(Collections.emptyList());
        when(user.getProfile()).thenReturn(Collections.emptyList());
        when(principal.getLoggedInUser()).thenReturn(user);

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);

            JsonObject body = InstantBIUtils.buildAgentDashboardRequest(
                    request,
                    "Build a sales dashboard",
                    "dash-1",
                    "1",
                    "{\"model\":{\"dir\":\"MyFolder\",\"file\":\"Sales.agent\"}}");

            JsonObject input = body.getAsJsonObject("input");
            assertEquals("Build a sales dashboard", input.get("inputString").getAsString());
            assertEquals("dash-1", input.get("dashboardid").getAsString());
            assertFalse(input.has("chatid"));
            assertEquals("1", input.get("dashboard_sequence_id").getAsString());
            assertFalse(input.has("chat_seq_id"));
            assertFalse(input.has("max_sub_questions"));
            assertEquals("MyFolder", input.getAsJsonObject("model").get("dir").getAsString());
        }
    }

    @Test
    public void addSessionContextAddsCookieAndUsername() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "abc123")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getOrg_id()).thenReturn(5);
        when(principal.getLoggedInUser()).thenReturn(user);

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);

            JsonObject target = new JsonObject();
            InstantBIUtils.addSessionContext(request, target);

            assertEquals("abc123", target.get("sessionCookie").getAsString());
            assertEquals("tester", target.get("username").getAsString());
            assertEquals(42, target.get("userId").getAsInt());
            assertEquals(5, target.get("orgId").getAsInt());
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
    public void addSessionContextForwardsJwtHeadersWhenSessionCookieMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(
                java.util.Arrays.asList("Authorization", "type", "Host", "Content-Type")));
        when(request.getHeaders("Authorization"))
                .thenReturn(Collections.enumeration(Collections.singletonList("Bearer jwt-token")));
        when(request.getHeaders("type"))
                .thenReturn(Collections.enumeration(Collections.singletonList("jwt")));
        when(request.getHeaders("Host"))
                .thenReturn(Collections.enumeration(Collections.singletonList("hi.example")));
        when(request.getHeaders("Content-Type"))
                .thenReturn(Collections.enumeration(Collections.singletonList("application/x-www-form-urlencoded")));

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("jwt-user");
        when(user.getId()).thenReturn(7);
        when(user.getOrg_id()).thenReturn(null);
        when(principal.getLoggedInUser()).thenReturn(user);

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);

            JsonObject target = new JsonObject();
            InstantBIUtils.addSessionContext(request, target);

            org.junit.Assert.assertFalse(target.has("sessionCookie"));
            JsonObject headers = target.getAsJsonObject("headers");
            assertEquals("Bearer jwt-token", headers.get("Authorization").getAsString());
            assertEquals("jwt", headers.get("type").getAsString());
            org.junit.Assert.assertFalse(headers.has("Host"));
            org.junit.Assert.assertFalse(headers.has("Content-Type"));
        }
    }

    @Test
    public void addSessionContextCopiesAuthTokenRequestParamIntoHeaders() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});
        when(request.getParameterNames())
                .thenReturn(Collections.enumeration(java.util.Arrays.asList("authToken", "input", "type")));
        when(request.getParameter("authToken")).thenReturn("Bearer sso-token");
        when(request.getParameter("input")).thenReturn("show sales");
        when(request.getParameter("type")).thenReturn("token");

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getOrg_id()).thenReturn(5);
        when(principal.getLoggedInUser()).thenReturn(user);

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);

            JsonObject target = new JsonObject();
            target.addProperty("input", "show sales");
            InstantBIUtils.addSessionContext(request, target);

            JsonObject params = target.getAsJsonObject("requestParams");
            assertEquals("Bearer sso-token", params.get("authToken").getAsString());
            assertEquals("token", params.get("type").getAsString());
            org.junit.Assert.assertFalse(params.has("input"));
            assertEquals("Bearer sso-token", target.getAsJsonObject("headers").get("authToken").getAsString());
        }
    }

    @Test
    public void applyForwardedHeadersCopiesNestedInputHeaders() {
        JsonObject headers = new JsonObject();
        headers.addProperty("Authorization", "Bearer nested");
        headers.addProperty("Host", "should-skip");
        JsonObject input = new JsonObject();
        input.add("headers", headers);
        JsonObject body = new JsonObject();
        body.add("input", input);

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://instantbi/interactive"));
        InstantBIUtils.applyForwardedHeaders(builder, body);
        HttpRequest request = builder.POST(HttpRequest.BodyPublishers.noBody()).build();

        assertEquals("Bearer nested", request.headers().firstValue("Authorization").orElse(null));
        org.junit.Assert.assertFalse(request.headers().firstValue("Host").isPresent());
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

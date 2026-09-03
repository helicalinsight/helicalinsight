package com.helicalinsight.instant.ai.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.instant.ai.payload.AgentDashboardPayload;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiAgentDashboardServiceImplTest {

    private final AiAgentDashboardServiceImpl service = new AiAgentDashboardServiceImpl();

    @Test
    public void executeSendsAgentDashboardResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(principal.getUsername()).thenReturn("tester");
        when(principal.getLoggedInUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(java.util.Collections.emptyList());
        when(user.getProfile()).thenReturn(java.util.Collections.emptyList());

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/agent-dashboard")))
                .thenReturn("{\"final_answer\":\"overview\",\"dashboard\":{}}");

        JsonObject settings = new JsonObject();
        settings.addProperty("BaseUrl", "http://localhost/hi.html");

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new AgentDashboardPayload("question", "dash-1", "1", null), request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1,\"response\":{\"final_answer\":\"overview\",\"dashboard\":{}}}")));
        }
    }

    @Test
    public void executeForwardsDashboardEnvelopeWithoutMaxSubQuestions() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(principal.getLoggedInUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getOrg_id()).thenReturn(null);
        when(user.getRoles()).thenReturn(java.util.Collections.emptyList());
        when(user.getProfile()).thenReturn(java.util.Collections.emptyList());

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/agent-dashboard")))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    Callable<JsonObject> bodyPreparer = invocation.getArgument(1);
                    JsonObject body = bodyPreparer.call();
                    JsonObject input = body.getAsJsonObject("input");
                    org.junit.Assert.assertEquals("question", input.get("inputString").getAsString());
                    org.junit.Assert.assertEquals("dash-1", input.get("dashboardid").getAsString());
                    org.junit.Assert.assertFalse(input.has("chatid"));
                    org.junit.Assert.assertEquals("1", input.get("dashboard_sequence_id").getAsString());
                    org.junit.Assert.assertFalse(input.has("chat_seq_id"));
                    org.junit.Assert.assertFalse(input.has("max_sub_questions"));
                    org.junit.Assert.assertEquals("session-1", input.get("sessionCookie").getAsString());
                    org.junit.Assert.assertEquals("d", input.getAsJsonObject("model").get("dir").getAsString());
                    return "{\"final_answer\":\"ok\"}";
                });

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(
                    new AgentDashboardPayload("question", "dash-1", "1",
                            "{\"model\":{\"dir\":\"d\",\"file\":\"f.agent\"}}"),
                    request, response);
        }
    }

    @Test
    public void executeSwallowsAbortException() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("requestId")).thenReturn("req-abort");

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/agent-dashboard")))
                .thenThrow(new com.helicalinsight.efw.exceptions.EfwServiceException("Request has been cancelled."));

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new AgentDashboardPayload("question", "dash-1", "1", null), request, response);

            controllerUtils.verifyNoInteractions();
        }
    }
}

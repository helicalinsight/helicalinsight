package com.helicalinsight.instant.ai.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.instant.ai.payload.DataInsightPayload;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiDataInsightServiceImplTest {

    private final AiDataInsightServiceImpl service = new AiDataInsightServiceImpl();


    @Test
    public void executeWithInputParamSendsInsightResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(principal.getUsername()).thenReturn("tester");
        when(principal.getLoggedInUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getRoles()).thenReturn(java.util.Collections.emptyList());
        when(user.getProfile()).thenReturn(java.util.Collections.emptyList());

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/data-insight")))
                .thenReturn("{\"insight\":\"summary\",\"token_usage\":{\"total\":5}}");

        JsonObject settings = new JsonObject();
        settings.addProperty("BaseUrl", "http://localhost/hi.html");

        String subject = "{\"model\":{\"path\":\"/model\"}}";

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new DataInsightPayload("1", "chat-1", "insight question", null, subject, "/data-insight"),
                    request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1,\"response\":{\"insight\":\"summary\",\"token_usage\":{\"total\":5}}}")));
        }
    }

    @Test
    public void executeWithInstantToHrEndpointSendsConvertResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(principal.getUsername()).thenReturn("tester");
        when(principal.getLoggedInUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getRoles()).thenReturn(java.util.Collections.emptyList());
        when(user.getProfile()).thenReturn(java.util.Collections.emptyList());

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/instant-to-hr")))
                .thenReturn("{\"sql_parts\":{\"columns\":[{\"column\":\"region\"}]},\"viz_parts\":{\"mark\":\"Chart\"}}");

        JsonObject settings = new JsonObject();
        settings.addProperty("BaseUrl", "http://localhost/hi.html");

        String subject = "{\"model\":{\"path\":\"/model\"}}";

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new DataInsightPayload("1", "chat-1", "convert question", null, subject, "/instant-to-hr"),
                    request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1,\"response\":{\"sql_parts\":{\"columns\":[{\"column\":\"region\"}]},\"viz_parts\":{\"mark\":\"Chart\"}}}")));
        }
    }

    @Test
    public void executeHandlesAbortExceptionWithoutFailureResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(principal.getUsername()).thenReturn("tester");
        when(principal.getLoggedInUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getRoles()).thenReturn(java.util.Collections.emptyList());
        when(user.getProfile()).thenReturn(java.util.Collections.emptyList());

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/data-insight")))
                .thenThrow(new com.helicalinsight.efw.exceptions.EfwServiceException("Request has been cancelled."));

        JsonObject settings = new JsonObject();
        settings.addProperty("BaseUrl", "http://localhost/hi.html");
        String subject = "{\"model\":{\"path\":\"/model\"}}";

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new DataInsightPayload("1", "chat-1", "insight question", null, subject, "/data-insight"),
                    request, response);

            controllerUtils.verifyNoInteractions();
        }
    }
}

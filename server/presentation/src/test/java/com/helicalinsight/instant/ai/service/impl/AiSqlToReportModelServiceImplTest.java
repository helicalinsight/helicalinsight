package com.helicalinsight.instant.ai.service.impl;

import static org.junit.Assert.assertEquals;
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
import com.helicalinsight.instant.ai.payload.SqlToReportModelPayload;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiSqlToReportModelServiceImplTest {

    private final AiSqlToReportModelServiceImpl service = new AiSqlToReportModelServiceImpl();

    @Test
    public void executeForwardsSqlLocationAndMetadataFile() throws Exception {
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
        when(httpService.executeCancellableCall(eq(request), any(), eq("/sql-to-report-model")))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    Callable<JsonObject> bodyPreparer = invocation.getArgument(1);
                    JsonObject body = bodyPreparer.call();
                    JsonObject input = body.getAsJsonObject("input");
                    assertEquals("SELECT region FROM t", input.get("sql").getAsString());
                    assertEquals("/meta", input.get("location").getAsString());
                    assertEquals("metadata.json", input.get("metadataFileName").getAsString());
                    return "{\"report_model\":{\"data_model\":{\"columns\":[{\"alias\":\"region\"}]},\"viz_model\":{\"chart\":{\"viz\":\"Bar\"}}}}";
                });

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

            service.execute(new SqlToReportModelPayload(
                    "SELECT region FROM t", "/meta", "metadata.json"), request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1,\"response\":{\"report_model\":{\"data_model\":{\"columns\":[{\"alias\":\"region\"}]},\"viz_model\":{\"chart\":{\"viz\":\"Bar\"}}}}}")
            ));
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
        when(httpService.executeCancellableCall(eq(request), any(), eq("/sql-to-report-model")))
                .thenThrow(new com.helicalinsight.efw.exceptions.EfwServiceException("Request has been cancelled."));

        JsonObject settings = new JsonObject();
        settings.addProperty("BaseUrl", "http://localhost/hi.html");

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new SqlToReportModelPayload("SELECT 1", "/meta", "m.metadata"), request, response);

            controllerUtils.verifyNoInteractions();
        }
    }
}

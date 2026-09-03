package com.helicalinsight.instant.ai.service.impl;

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
import com.helicalinsight.instant.ai.payload.ChatContextPayload;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiChatContextServiceImplTest {

    private final AiChatContextServiceImpl service = new AiChatContextServiceImpl();


    @Test
    public void executeSendsChatContextResponse() throws Exception {
        HttpServletRequest request = mockRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        Principal principal = mockPrincipal();

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.callHttp(eq("/chat"), any(JsonObject.class)))
                .thenReturn("{\"context\":\"general\",\"message\":\"ok\"}");

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new ChatContextPayload("show sales"), request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"output\":{\"context\":\"general\",\"message\":\"ok\"}}")));
        }
    }

    @Test
    public void executeHandlesInvalidChatOutputGracefully() throws Exception {
        HttpServletRequest request = mockRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        Principal principal = mockPrincipal();

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.callHttp(eq("/chat"), any(JsonObject.class))).thenReturn("not-json");

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute(new ChatContextPayload("show sales"), request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(eq(response), eq(true), any(String.class)));
        }
    }

    private static HttpServletRequest mockRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});
        return request;
    }

    private static Principal mockPrincipal() {
        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(principal.getLoggedInUser()).thenReturn(user);
        return principal;
    }
}

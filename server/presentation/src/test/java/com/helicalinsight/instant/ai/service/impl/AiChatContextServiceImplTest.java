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
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiChatContextServiceImplTest {

    private final AiChatContextServiceImpl service = new AiChatContextServiceImpl();

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(service.isThreadSafeToCache());
    }

    @Test
    public void executeSendsChatContextResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.callHttp(eq("/chat"), any(JsonObject.class)))
                .thenReturn("{\"context\":\"general\",\"message\":\"ok\"}");

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute("show sales", request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"output\":{\"context\":\"general\",\"message\":\"ok\"}}")));
        }
    }

    @Test
    public void executeHandlesInvalidChatOutputGracefully() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.callHttp(eq("/chat"), any(JsonObject.class))).thenReturn("not-json");

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute("show sales", request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(eq(response), eq(true), any(String.class)));
        }
    }
}

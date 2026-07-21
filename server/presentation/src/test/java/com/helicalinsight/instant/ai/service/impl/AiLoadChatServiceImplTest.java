package com.helicalinsight.instant.ai.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiLoadChatServiceImplTest {

    private final AiLoadChatServiceImpl service = new AiLoadChatServiceImpl();

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(service.isThreadSafeToCache());
    }

    @Test
    public void executeLoadsChatAndSendsResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(principal.getUsername()).thenReturn("tester");
        when(principal.getLoggedInUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(java.util.Collections.emptyList());
        when(user.getProfile()).thenReturn(java.util.Collections.emptyList());

        HIResourceServiceDB resourceService = mock(HIResourceServiceDB.class);
        HIResource resource = mock(HIResource.class);
        HIResourceInstantReport instantReport = mock(HIResourceInstantReport.class);
        when(resourceService.getResourceByUrl("reports/sales.report")).thenReturn(resource);
        when(resource.getHiResourceInstantReport()).thenReturn(instantReport);
        when(instantReport.getState()).thenReturn(buildReportState());

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.executeCancellableCall(eq(request), any(), eq("/load-chat")))
                .thenReturn("{\"result\":\"loaded\"}");

        JsonObject settings = new JsonObject();
        settings.addProperty("BaseUrl", "http://localhost/hi.html");

        String formData = "{\"input\":\"question\",\"location\":\"reports\",\"fileName\":\"sales.report\"}";

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
             MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
            context.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(resourceService);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            service.execute("2", formData, request, response);

            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1,\"response\":{\"result\":\"loaded\"}}")));
        }
    }

    private String buildReportState() {
        JsonObject state = new JsonObject();
        JsonObject subject = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("path", "/model");
        subject.add("model", model);
        state.add("subject", subject);
        state.addProperty("activeChatId", "chat-1");

        JsonArray inputs = new JsonArray();
        inputs.add(chatInput(1, "first"));
        inputs.add(chatInput(2, "second"));
        state.add("inputs", inputs);

        JsonArray chatResponses = new JsonArray();
        chatResponses.add(chatResponse(2, "answer"));
        state.add("chat_responses", chatResponses);

        return state.toString();
    }

    private JsonObject chatInput(int sequenceId, String input) {
        JsonObject obj = new JsonObject();
        obj.addProperty("chat_sequence_id", sequenceId);
        obj.addProperty("input", input);
        return obj;
    }

    private JsonObject chatResponse(int sequenceId, String output) {
        JsonObject obj = new JsonObject();
        obj.addProperty("chat_sequence_id", sequenceId);
        obj.addProperty("output", output);
        return obj;
    }
}

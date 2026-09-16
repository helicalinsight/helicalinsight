package com.helicalinsight.adhoc.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.report.InstantReportForEditReaderComponent;

import jakarta.servlet.http.Cookie;

public class InstantReportForEditReaderComponentTest {

    @After
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void unwrapReportDataUsesInnerDataEnvelope() {
        JsonObject inner = new JsonObject();
        inner.addProperty("reportName", "Sales");
        JsonObject envelope = new JsonObject();
        envelope.add("data", inner);

        assertEquals(inner, InstantReportForEditReaderComponent.unwrapReportData(envelope));
        assertEquals(inner, InstantReportForEditReaderComponent.unwrapReportData(inner));
    }

    @Test
    public void resolveChatIdPrefersActiveChatId() {
        JsonObject state = new JsonObject();
        state.addProperty("activeChatId", "saved-chat");
        state.addProperty("activeChatID", "legacy-id");
        JsonObject report = new JsonObject();
        report.add("state", state);

        assertEquals("saved-chat", InstantReportForEditReaderComponent.resolveChatId(report));
    }

    @Test
    public void aliasActiveChatIdCopiesSavedIdForUiLookup() {
        JsonObject state = new JsonObject();
        state.addProperty("activeChatId", "c80a03e1-9981-460b-a616-5bbdef276bb2");
        JsonObject data = new JsonObject();
        data.add("state", state);
        JsonObject envelope = new JsonObject();
        envelope.add("data", data);

        InstantReportForEditReaderComponent.aliasActiveChatId(envelope);

        assertEquals("c80a03e1-9981-460b-a616-5bbdef276bb2", state.get("activeChatId").getAsString());
        assertEquals("c80a03e1-9981-460b-a616-5bbdef276bb2", state.get("activeChatID").getAsString());
    }

    @Test
    public void executeComponentHydratesMemoryWithChatIdBeforeReturning() {
        InstantReportForEditReaderComponent component = new InstantReportForEditReaderComponent();
        AdhocReport adhocReport = mock(AdhocReport.class);
        JsonObject formJson = new JsonObject();
        formJson.addProperty("dir", "dir");
        formJson.addProperty("file", "file.instant");

        JsonObject state = new JsonObject();
        state.addProperty("activeChatId", "chat-edit-1");
        JsonObject data = new JsonObject();
        data.addProperty("reportName", "Instant_1_edit");
        data.add("state", state);
        JsonObject reportContent = new JsonObject();
        reportContent.add("data", data);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("JSESSIONID", "abc123"));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Principal principal = mock(Principal.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("tester");
        when(user.getId()).thenReturn(42);
        when(user.getOrg_id()).thenReturn(5);
        when(principal.getLoggedInUser()).thenReturn(user);

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
        when(httpService.callHttp(eq("/updateMemory"), any(JsonObject.class)))
                .thenReturn("{\"status\":1,\"memory\":{\"chatid\":\"chat-edit-1\",\"turns\":1}}");

        try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
                    .thenReturn(adhocReport);
            reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            String result = component.executeComponent(formJson.toString());
            JsonObject resultState = JsonParser.parseString(result).getAsJsonObject()
                    .getAsJsonObject("data").getAsJsonObject("state");
            assertEquals("chat-edit-1", resultState.get("activeChatId").getAsString());
            assertEquals("chat-edit-1", resultState.get("activeChatID").getAsString());

            ArgumentCaptor<JsonObject> bodyCaptor = ArgumentCaptor.forClass(JsonObject.class);
            verify(httpService).callHttp(eq("/updateMemory"), bodyCaptor.capture());
            JsonObject input = bodyCaptor.getValue().getAsJsonObject("input");
            assertEquals("chat-edit-1", input.get("chatid").getAsString());
            assertEquals("abc123", input.get("sessionCookie").getAsString());
            JsonObject report = input.getAsJsonObject("report");
            assertTrue(report.has("state"));
            assertFalse(report.has("data"));
            assertEquals("Instant_1_edit", report.get("reportName").getAsString());
        }
    }

    @Test
    public void executeComponentStillReturnsReportWhenRequestContextMissing() {
        InstantReportForEditReaderComponent component = new InstantReportForEditReaderComponent();
        AdhocReport adhocReport = mock(AdhocReport.class);
        JsonObject formJson = new JsonObject();
        formJson.addProperty("dir", "dir");
        formJson.addProperty("file", "file.instant");
        JsonObject reportContent = new JsonObject();
        reportContent.addProperty("data", "ok");

        IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);

        try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class);
             MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
            reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
                    .thenReturn(adhocReport);
            reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);
            factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

            String result = component.executeComponent(formJson.toString());
            assertEquals(reportContent.toString(), result);
            verify(httpService, never()).callHttp(anyString(), any());
        }
    }
}

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

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.report.InstantReportReaderComponent;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.Cookie;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstantReportReaderComponentTest {

	@After
	public void tearDown() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a1_test_executeComponent() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file");
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			mockedStatic.when(()-> ReportOpenHelper.getInstantReportDb(anyString(), anyString())).thenReturn(null);
			component.executeComponent(formJson.toString());
		}
		
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file");
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			mockedStatic.when(()-> ReportOpenHelper.getInstantReportDb(anyString(), anyString())).thenReturn(adhocReport);
			mockedStatic.when(()-> ReportOpenHelper.reportContentAsJson(any())).thenReturn(formJson);
			
			String executeComponent = component.executeComponent(formJson.toString());
			assertEquals(formJson.toString(), executeComponent);
		}
		
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		boolean threadSafeToCache = component.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test
	public void ut_a4_executeComponentHydratesMemoryIncludingThinkSteps() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file.instant");

		JsonObject state = new JsonObject();
		state.addProperty("activeChatId", "chat-open-1");
		JsonObject data = new JsonObject();
		data.addProperty("reportName", "Instant_1_open");
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
				.thenReturn("{\"status\":1,\"memory\":{\"chatid\":\"chat-open-1\",\"turns\":1}}");

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
			assertEquals("chat-open-1", resultState.get("activeChatId").getAsString());
			assertEquals("chat-open-1", resultState.get("activeChatID").getAsString());

			ArgumentCaptor<JsonObject> bodyCaptor = ArgumentCaptor.forClass(JsonObject.class);
			verify(httpService).callHttp(eq("/updateMemory"), bodyCaptor.capture());
			JsonObject input = bodyCaptor.getValue().getAsJsonObject("input");
			assertEquals("chat-open-1", input.get("chatid").getAsString());
			assertEquals("abc123", input.get("sessionCookie").getAsString());
			JsonObject report = input.getAsJsonObject("report");
			assertTrue(report.has("state"));
			assertFalse(report.has("data"));
			assertEquals("Instant_1_open", report.get("reportName").getAsString());
		}
	}

	@Test
	public void ut_a5_executeComponentStillReturnsReportWhenRequestContextMissing() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
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

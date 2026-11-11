package com.helicalinsight.adhoc.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceAIModel;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
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

import com.google.gson.JsonArray;
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

	@Test
	public void ut_a6_blankVfTemplateIsLeftUnchanged() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file.instant");

		JsonObject viz = new JsonObject();
		viz.addProperty("vf_template", "");
		viz.addProperty("chart_name", "bar");
		JsonObject chat = new JsonObject();
		chat.addProperty("chat_sequence_id", 1);
		chat.add("viz", viz);
		JsonObject sql = new JsonObject();
		sql.addProperty("raw_sql", "SELECT 1");
		chat.add("sql", sql);
		JsonArray responses = new JsonArray();
		responses.add(chat);
		JsonObject state = new JsonObject();
		state.add("chat_responses", responses);
		JsonObject data = new JsonObject();
		data.add("state", state);
		JsonObject reportContent = new JsonObject();
		reportContent.add("data", data);

		IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
		try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class);
			 MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
			reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
					.thenReturn(adhocReport);
			reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);
			factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

			String result = component.executeComponent(formJson.toString());
			JsonObject resultChat = JsonParser.parseString(result).getAsJsonObject()
					.getAsJsonObject("data").getAsJsonObject("state")
					.getAsJsonArray("chat_responses").get(0).getAsJsonObject();
			assertEquals("bar", resultChat.getAsJsonObject("viz").get("chart_name").getAsString());
			assertFalse(resultChat.has("report_model"));
			assertFalse(resultChat.has("hreportId"));
			verify(httpService, never()).callHttp(anyString(), any());
		}
	}

	@Test
	public void ut_a7_nonNullVfTemplateConvertsSqlToTable() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file.instant");

		JsonObject stepViz = new JsonObject();
		stepViz.addProperty("chart_name", "bar");
		JsonObject stepChat = new JsonObject();
		stepChat.add("viz", stepViz);
		stepChat.addProperty("vf_string", "function Draw(){ return null; }");
		JsonObject stepSql = new JsonObject();
		stepSql.addProperty("raw_sql", "SELECT region FROM t");
		stepSql.addProperty("dialect", "postgresql");
		stepChat.add("sql", stepSql);
		JsonObject step = new JsonObject();
		step.addProperty("chat_seq_id", "1-1");
		step.add("chat_response", stepChat);

		JsonArray history = new JsonArray();
		history.add(step);
		JsonObject response = new JsonObject();
		response.addProperty("chat_sequence_id", 1);
		response.add("question_history", history);

		JsonObject directViz = new JsonObject();
		directViz.add("vf_template", com.google.gson.JsonNull.INSTANCE);
		JsonObject direct = new JsonObject();
		direct.addProperty("chat_sequence_id", 2);
		direct.add("viz", directViz);
		direct.addProperty("vf", "function DrawCost(){ return 1; }");
		direct.addProperty("sql", "SELECT cost FROM t");
		direct.addProperty("hreportId", "already-set-hreport");

		JsonArray responses = new JsonArray();
		responses.add(response);
		responses.add(direct);

		JsonObject dashItem = new JsonObject();
		dashItem.addProperty("itemId", "1-1");
		dashItem.addProperty("kind", "instant");
		JsonArray items = new JsonArray();
		items.add(dashItem);
		JsonObject dashboard = new JsonObject();
		dashboard.add("items", items);

		JsonObject state = new JsonObject();
		state.addProperty("activeChatId", "chat-vf-1");
		state.add("chat_responses", responses);
		state.add("dashboard_model", dashboard);
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "Instant_BI_01");
		metadata.addProperty("metadataFileName", "PG_Metadata.metadata");
		JsonObject data = new JsonObject();
		data.add("metadata", metadata);
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
		when(httpService.callHttp(eq("/updateMemory"), any(JsonObject.class))).thenReturn("{\"status\":1}");
		when(httpService.callHttp(eq("/sql-to-report-model"), any(JsonObject.class))).thenReturn(
				"{\"report_model\":{\"data_model\":{\"sql\":\"SELECT region FROM t\",\"columns\":[]},"
						+ "\"viz_model\":{\"data\":{\"rows\":[\"region\"],\"columns\":[]},"
						+ "\"chart\":{\"viz\":\"Bar\",\"mark\":\"Chart\",\"dualaxis\":false,\"measures\":[]}}}}");

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
			JsonObject resultStep = resultState.getAsJsonArray("chat_responses").get(0).getAsJsonObject()
					.getAsJsonArray("question_history").get(0).getAsJsonObject();
			JsonObject resultStepChat = resultStep.getAsJsonObject("chat_response");
			assertEquals("Table", resultStepChat.getAsJsonObject("report_model")
					.getAsJsonObject("viz_model").getAsJsonObject("chart").get("mark").getAsString());
			assertEquals("", resultStepChat.getAsJsonObject("report_model")
					.getAsJsonObject("viz_model").getAsJsonObject("chart").get("viz").getAsString());
			assertFalse(resultStepChat.has("vf_string"));
			assertTrue(resultStepChat.getAsJsonObject("viz").get("vf_template").getAsString().length() > 0);
			String stepHreportId = resultStep.get("hreportId").getAsString();
			assertTrue(stepHreportId.matches(
					"[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"));
			assertEquals(stepHreportId, resultStepChat.get("hreportId").getAsString());
			assertFalse(resultState.getAsJsonArray("chat_responses").get(0).getAsJsonObject().has("hreportId"));
			assertEquals("Table", resultStep.getAsJsonObject("report_model")
					.getAsJsonObject("viz_model").getAsJsonObject("chart").get("mark").getAsString());

			JsonObject resultDirect = resultState.getAsJsonArray("chat_responses").get(1).getAsJsonObject();
			assertEquals("Table", resultDirect.getAsJsonObject("report_model")
					.getAsJsonObject("viz_model").getAsJsonObject("chart").get("mark").getAsString());
			assertFalse(resultDirect.has("vf"));
			assertEquals("already-set-hreport", resultDirect.get("hreportId").getAsString());
			JsonObject resultItem = resultState.getAsJsonObject("dashboard_model")
					.getAsJsonArray("items").get(0).getAsJsonObject();
			assertEquals("Table", resultItem.getAsJsonObject("report_model").getAsJsonObject("viz_model")
					.getAsJsonObject("chart").get("mark").getAsString());
			assertEquals(stepHreportId, resultItem.get("hreportId").getAsString());

			ArgumentCaptor<JsonObject> bodyCaptor = ArgumentCaptor.forClass(JsonObject.class);
			verify(httpService, org.mockito.Mockito.atLeastOnce())
					.callHttp(eq("/sql-to-report-model"), bodyCaptor.capture());
			JsonObject sent = bodyCaptor.getAllValues().get(0).getAsJsonObject("input");
			assertEquals("table", sent.get("defaultChart").getAsString());
			assertEquals("Instant_BI_01", sent.get("location").getAsString());
			assertEquals("PG_Metadata.metadata", sent.get("metadataFileName").getAsString());
			assertEquals("SELECT region FROM t", sent.get("sql").getAsString());
			assertEquals("postgresql", sent.get("dialect").getAsString());

			ArgumentCaptor<JsonObject> memoryCaptor = ArgumentCaptor.forClass(JsonObject.class);
			verify(httpService).callHttp(eq("/updateMemory"), memoryCaptor.capture());
			JsonObject memoryInput = memoryCaptor.getValue().getAsJsonObject("input");
			assertEquals("chat-vf-1", memoryInput.get("chatid").getAsString());
			JsonObject memoryState = memoryInput.getAsJsonObject("report").getAsJsonObject("state");
			assertEquals("Table", memoryState.getAsJsonArray("chat_responses").get(1).getAsJsonObject()
					.getAsJsonObject("report_model").getAsJsonObject("viz_model")
					.getAsJsonObject("chart").get("mark").getAsString());
			assertEquals("SELECT cost FROM t", memoryState.getAsJsonArray("chat_responses").get(1)
					.getAsJsonObject().get("sql").getAsString());
		}
	}

	@Test
	public void ut_a8_legacyChatsWithoutMetadataBecomeInputsAndResponses() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "8047");
		formJson.addProperty("file", "Instant_1_123.instant");

		JsonObject bot = new JsonObject();
		bot.addProperty("id", "bot-1");
		bot.addProperty("isUser", false);
		bot.addProperty("text", "Flights cost the most.");
		bot.addProperty("vf", "function DrawBar(){ return 1; }");
		bot.addProperty("sql", "```sql\nSELECT \"travel_medium\" FROM \"travel_details\"\n```");
		JsonObject user = new JsonObject();
		user.addProperty("id", "user-1");
		user.addProperty("isUser", true);
		user.addProperty("text", "Show travel cost");
		JsonArray messages = new JsonArray();
		messages.add(bot);
		messages.add(user);
		JsonObject chat = new JsonObject();
		chat.addProperty("chatID", "chat-old");
		chat.add("messageList", messages);
		JsonArray chats = new JsonArray();
		chats.add(chat);

		JsonObject column = new JsonObject();
		column.addProperty("name", "travel_medium");
		column.addProperty("type", "text");
		JsonObject columns = new JsonObject();
		columns.add("1", column);
		JsonObject previewMetadata = new JsonObject();
		previewMetadata.add("columns", columns);
		JsonArray metadataList = new JsonArray();
		metadataList.add(previewMetadata);
		JsonObject preview = new JsonObject();
		preview.addProperty("id", "5d3b23bd-9c9e-48f9-8011-3dcfac013e77");
		preview.addProperty("chatID", "chat-old");
		preview.add("metadata", metadataList);
		JsonArray previews = new JsonArray();
		previews.add(preview);

		JsonObject state = new JsonObject();
		state.addProperty("activeChatID", "chat-old");
		state.add("chats", chats);
		state.add("previews", previews);
		JsonObject metadata = new JsonObject();
		metadata.add("location", com.google.gson.JsonNull.INSTANCE);
		metadata.add("metadataFileName", com.google.gson.JsonNull.INSTANCE);
		JsonObject data = new JsonObject();
		data.addProperty("reportName", "Instant_1-123");
		data.add("metadata", metadata);
		data.add("state", state);
		JsonObject reportContent = new JsonObject();
		reportContent.add("data", data);

		IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
		try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class);
			 MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
			reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
					.thenReturn(adhocReport);
			reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);
			factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

			String result = component.executeComponent(formJson.toString());
			JsonObject resultData = JsonParser.parseString(result).getAsJsonObject().getAsJsonObject("data");
			JsonObject resultMetadata = resultData.getAsJsonObject("metadata");
			assertTrue(resultMetadata.get("location").isJsonNull());
			assertTrue(resultMetadata.get("metadataFileName").isJsonNull());
			JsonObject resultState = resultData.getAsJsonObject("state");
			JsonObject resultInput = resultState.getAsJsonArray("inputs").get(0).getAsJsonObject();
			assertEquals(1, resultInput.get("chat_sequence_id").getAsInt());
			assertEquals("Show travel cost", resultInput.get("input").getAsString());
			JsonObject resultResponse = resultState.getAsJsonArray("chat_responses").get(0).getAsJsonObject();
			assertEquals(2, resultResponse.get("chat_sequence_id").getAsInt());
			assertEquals("Flights cost the most.", resultResponse.getAsJsonObject("summary").get("insight").getAsString());
			assertEquals("SELECT \"travel_medium\" FROM \"travel_details\"",
					resultResponse.getAsJsonObject("sql").get("raw_sql").getAsString());
			assertFalse(resultResponse.has("hreportId"));
			assertFalse(resultResponse.has("report_model"));
			assertEquals("chat-old", resultState.get("activeChatID").getAsString());
			verify(httpService, never()).callHttp(anyString(), any());
		}
	}

	@Test
	public void ut_a9_legacyChatsWithMetadataKeepChartStructure() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "8047");
		formJson.addProperty("file", "Instant_1_123.instant");

		JsonObject bot = new JsonObject();
		bot.addProperty("isUser", false);
		bot.addProperty("text", "Flights cost the most.");
		bot.addProperty("vf", "function DrawBar(xField, yField){ return 1; }");
		bot.addProperty("sql", "SELECT travel_medium, SUM(travel_cost) AS total_cost FROM travel_details");
		JsonArray messages = new JsonArray();
		messages.add(bot);
		JsonObject chat = new JsonObject();
		chat.addProperty("chatID", "chat-old");
		chat.add("messageList", messages);
		JsonArray chats = new JsonArray();
		chats.add(chat);

		JsonObject medium = new JsonObject();
		medium.addProperty("name", "travel_medium");
		medium.addProperty("type", "text");
		JsonObject cost = new JsonObject();
		cost.addProperty("name", "total_cost");
		cost.addProperty("type", "numeric");
		JsonObject columns = new JsonObject();
		columns.add("1", medium);
		columns.add("2", cost);
		JsonObject previewMetadata = new JsonObject();
		previewMetadata.add("columns", columns);
		JsonArray metadataList = new JsonArray();
		metadataList.add(previewMetadata);
		JsonObject preview = new JsonObject();
		preview.addProperty("id", "5d3b23bd-9c9e-48f9-8011-3dcfac013e77");
		preview.add("metadata", metadataList);
		JsonArray previews = new JsonArray();
		previews.add(preview);

		JsonObject state = new JsonObject();
		state.addProperty("activeChatId", "chat-old");
		state.add("chats", chats);
		state.add("previews", previews);
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "8047");
		metadata.addProperty("metadataFileName", "Meta111.metadata");
		JsonObject data = new JsonObject();
		data.add("metadata", metadata);
		data.add("state", state);
		JsonObject reportContent = new JsonObject();
		reportContent.add("data", data);

		try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class)) {
			reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
					.thenReturn(adhocReport);
			reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);

			String result = component.executeComponent(formJson.toString());
			JsonObject resultData = JsonParser.parseString(result).getAsJsonObject().getAsJsonObject("data");
			assertEquals("Meta111.metadata", resultData.getAsJsonObject("metadata").get("metadataFileName").getAsString());
			JsonObject resultResponse = resultData.getAsJsonObject("state")
					.getAsJsonArray("chat_responses").get(0).getAsJsonObject();
			assertEquals("5d3b23bd-9c9e-48f9-8011-3dcfac013e77", resultResponse.get("hreportId").getAsString());
			assertEquals("Flights cost the most.", resultResponse.getAsJsonObject("summary").get("insight").getAsString());
			assertTrue(resultResponse.getAsJsonObject("viz").get("vf_template").getAsString().length() > 0);
			JsonObject chart = resultResponse.getAsJsonObject("report_model").getAsJsonObject("viz_model")
					.getAsJsonObject("chart");
			assertEquals("Chart", chart.get("mark").getAsString());
			assertEquals("Bar", chart.get("viz").getAsString());
			JsonObject dataModel = resultResponse.getAsJsonObject("report_model").getAsJsonObject("data_model");
			assertEquals("8047", dataModel.get("location").getAsString());
			assertEquals("Meta111.metadata", dataModel.get("metadataFileName").getAsString());
			assertEquals("chat-old", resultData.getAsJsonObject("state").get("activeChatID").getAsString());
		}
	}

	@Test
	public void ut_a10_agentFileIsNoLongerSupported() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "Postgress_Instant");
		formJson.addProperty("file", "Instant_1.instant");

		JsonObject agent = new JsonObject();
		agent.addProperty("file", "Agent_1.agent");
		agent.addProperty("dir", "Postgress_Instant");
		JsonObject subject = new JsonObject();
		subject.add("agent", agent);
		JsonObject state = new JsonObject();
		state.add("subject", subject);
		JsonObject metadata = new JsonObject();
		metadata.add("location", com.google.gson.JsonNull.INSTANCE);
		metadata.add("metadataFileName", com.google.gson.JsonNull.INSTANCE);
		JsonObject data = new JsonObject();
		data.add("metadata", metadata);
		data.add("state", state);
		JsonObject reportContent = new JsonObject();
		reportContent.add("data", data);

		try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class)) {
			reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
					.thenReturn(adhocReport);
			reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);
			try {
				component.executeComponent(formJson.toString());
				fail("Opening a .agent reference should be rejected");
			} catch (EfwServiceException exception) {
				assertEquals("This file is no longer supported", exception.getMessage());
			}
		}
	}

	@Test
	public void ut_a11_modelFileSuppliesMetadata() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "saved");
		formJson.addProperty("file", "Instant_1.instant");

		JsonObject agent = new JsonObject();
		agent.addProperty("file", "Agent_1.model");
		agent.addProperty("dir", "Postgress_Instant");
		JsonObject subject = new JsonObject();
		subject.add("agent", agent);
		JsonObject state = new JsonObject();
		state.add("subject", subject);
		JsonObject metadata = new JsonObject();
		metadata.add("location", com.google.gson.JsonNull.INSTANCE);
		metadata.add("metadataFileName", com.google.gson.JsonNull.INSTANCE);
		JsonObject data = new JsonObject();
		data.add("metadata", metadata);
		data.add("state", state);
		JsonObject reportContent = new JsonObject();
		reportContent.add("data", data);

		HIResourceServiceDB resources = mock(HIResourceServiceDB.class);
		HIResource modelResource = mock(HIResource.class);
		HIResourceAIModel model = mock(HIResourceAIModel.class);
		HIResource metadataResource = mock(HIResource.class);
		when(resources.getResourceByUrl("Postgress_Instant/Agent_1.model")).thenReturn(modelResource);
		when(modelResource.getAiModel()).thenReturn(model);
		when(model.getHiResourceMetadata()).thenReturn(15);
		when(resources.getHIResourceById(15)).thenReturn(metadataResource);
		when(metadataResource.getResourceURL()).thenReturn("Postgress_Instant/Travel.metadata");
		when(metadataResource.getResourcePath()).thenReturn("Travel");

		try (MockedStatic<ReportOpenHelper> reportOpen = mockStatic(ReportOpenHelper.class);
			 MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
			reportOpen.when(() -> ReportOpenHelper.getInstantReportDb(anyString(), anyString()))
					.thenReturn(adhocReport);
			reportOpen.when(() -> ReportOpenHelper.reportContentAsJson(any())).thenReturn(reportContent);
			context.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(resources);

			String result = component.executeComponent(formJson.toString());
			JsonObject resultMetadata = JsonParser.parseString(result).getAsJsonObject()
					.getAsJsonObject("data").getAsJsonObject("metadata");
			assertEquals("Postgress_Instant", resultMetadata.get("location").getAsString());
			assertEquals("Travel.metadata", resultMetadata.get("metadataFileName").getAsString());
		}
	}
}

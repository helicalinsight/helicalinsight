package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.JsonUtils;


public class HttpExportServiceHandlerTest {

	private static JsonObject exportSettingsWithPort(int port) {
		JsonObject export = new JsonObject();
		export.addProperty("phantomPort", port);
		JsonObject settings = new JsonObject();
		settings.add("export", export);
		return settings;
	}

	private static JsonObject samplePrintRequest() {
		JsonObject object = new JsonObject();
		object.addProperty("domain", "domain");
		JsonArray arr = new JsonArray();
		arr.add("pdf");
		object.add("format", arr);
		object.addProperty("reportSourceUri", "uri");
		object.addProperty("destinationFile", "dummyFile");
		return object;
	}

	@Test(expected = ExportException.class)
	public void ut_a1_testHandlePhantomjs() {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class);
				MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
			jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(exportSettingsWithPort(3000));
			exportUtils.when(ExportUtils::getReportDirectory).thenReturn("Reports");
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn("Reports/ExportTemplates");
			mockedStatic.when(HttpClients::createDefault).thenReturn(httpClient);
			// No execute stub → response stays null → ExportException from isResponseNull
			exportServiceHandler.handlePhantomjs(samplePrintRequest());
		}
	}
	
	@Test
	public void ut_a2_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(200);
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class);
				MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
			jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(exportSettingsWithPort(3000));
			exportUtils.when(ExportUtils::getReportDirectory).thenReturn("Reports");
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn("Reports/ExportTemplates");
			mockedStatic.when(HttpClients::createDefault).thenReturn(httpClient);
			
			List<String> handlePhantomjs = exportServiceHandler.handlePhantomjs(samplePrintRequest());
			Assert.assertEquals(1, handlePhantomjs.size());
		}
		
	}
	
	@Test
	public void ut_a3_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity responseEntity = mock(HttpEntity.class);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(100);
		when(response.getEntity()).thenReturn(responseEntity);
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class);
				MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
			jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(exportSettingsWithPort(3000));
			exportUtils.when(ExportUtils::getReportDirectory).thenReturn("Reports");
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn("Reports/ExportTemplates");
			mockedStatic.when(HttpClients::createDefault).thenReturn(httpClient);
			
			List<String> handlePhantomjs = exportServiceHandler.handlePhantomjs(samplePrintRequest());
			Assert.assertEquals(2, handlePhantomjs.size());
		}
	}
	@Test(expected = ExportException.class)
	public void ut_a4_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity responseEntity = mock(HttpEntity.class);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(100);
		when(response.getEntity()).thenReturn(responseEntity);
		
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class);
				MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class);
				MockedStatic<EntityUtils> entityUtil = mockStatic(EntityUtils.class)) {
			jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(exportSettingsWithPort(3000));
			exportUtils.when(ExportUtils::getReportDirectory).thenReturn("Reports");
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn("Reports/ExportTemplates");
			mockedStatic.when(HttpClients::createDefault).thenReturn(httpClient);
			entityUtil.when(() -> EntityUtils.toString(responseEntity)).thenThrow(new ParseException());
			List<String> handlePhantomjs = exportServiceHandler.handlePhantomjs(samplePrintRequest());
			Assert.assertEquals(2, handlePhantomjs.size());
		}
	}
	
	@Test(expected = ExportException.class)
	public void ut_a5_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		when(httpClient.execute(any(HttpPost.class))).thenThrow(new RuntimeException("boom"));
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class);
				MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
			jsonUtils.when(JsonUtils::newGetSettingsJson).thenReturn(exportSettingsWithPort(3000));
			exportUtils.when(ExportUtils::getReportDirectory).thenReturn("Reports");
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn("Reports/ExportTemplates");
			mockedStatic.when(HttpClients::createDefault).thenReturn(httpClient);
			// execute exception is swallowed → null response → ExportException
			exportServiceHandler.handlePhantomjs(samplePrintRequest());
		}
	}
}

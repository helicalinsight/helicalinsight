package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ExportException;


public class HttpExportServiceHandlerTest {

	@Test(expected = ExportException.class)
	public void ut_a1_testHandlePhantomjs() {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		JsonObject object = new JsonObject();
		object.addProperty("domain", "domain");
		JsonArray arr = new JsonArray();
		arr.add("pdf");
		object.add("format", arr);
		object.addProperty("reportSourceUri", "uri");
		object.addProperty("destinationFile", "dummyFile");

		exportServiceHandler.handlePhantomjs(object);

	}
	
	@Test
	public void ut_a2_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		JsonObject object = new JsonObject();
		object.addProperty("domain", "domain");
		JsonArray arr = new JsonArray();
		arr.add("pdf");
		object.add("format", arr);
		object.addProperty("reportSourceUri", "uri");
		object.addProperty("destinationFile", "dummyFile");
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(200);
		try(MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)){
			mockedStatic.when(()-> HttpClients.createDefault()).thenReturn(httpClient);
			
			List<String> handlePhantomjs = exportServiceHandler.handlePhantomjs(object);
			Assert.assertEquals(1, handlePhantomjs.size());
			
			
		}
		
	}
	
	@Test
	public void ut_a3_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		JsonObject object = new JsonObject();
		object.addProperty("domain", "domain");
		JsonArray arr = new JsonArray();
		arr.add("pdf");
		object.add("format", arr);
		object.addProperty("reportSourceUri", "uri");
		object.addProperty("destinationFile", "dummyFile");
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity responseEntity = mock(HttpEntity.class);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(100);
		when( response.getEntity()).thenReturn(responseEntity);
		try(MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)){
			mockedStatic.when(()-> HttpClients.createDefault()).thenReturn(httpClient);
			
			List<String> handlePhantomjs = exportServiceHandler.handlePhantomjs(object);
			Assert.assertEquals(2, handlePhantomjs.size());
		}
	}
	@Test(expected = ExportException.class)
	public void ut_a4_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		JsonObject object = new JsonObject();
		object.addProperty("domain", "domain");
		JsonArray arr = new JsonArray();
		arr.add("pdf");
		object.add("format", arr);
		object.addProperty("reportSourceUri", "uri");
		object.addProperty("destinationFile", "dummyFile");
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity responseEntity = mock(HttpEntity.class);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(100);
		when( response.getEntity()).thenReturn(responseEntity);
		
		try(MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)){
			mockedStatic.when(()-> HttpClients.createDefault()).thenReturn(httpClient);
			try(MockedStatic<EntityUtils> entityUtil = mockStatic(EntityUtils.class)){
			entityUtil.when(()->EntityUtils.toString(responseEntity)).thenThrow(new ParseException());
			List<String> handlePhantomjs = exportServiceHandler.handlePhantomjs(object);
			System.out.println(handlePhantomjs);
			Assert.assertEquals(2, handlePhantomjs.size());
		}
		}
	}
	
	@Test(expected = ExportException.class)
	public void ut_a5_testHandlePhantomjs() throws ClientProtocolException, IOException {
		HttpExportServiceHandler exportServiceHandler = new HttpExportServiceHandler();
		JsonObject object = new JsonObject();
		object.addProperty("domain", "domain");
		JsonArray arr = new JsonArray();
		arr.add("pdf");
		object.add("format", arr);
		object.addProperty("reportSourceUri", "uri");
		object.addProperty("destinationFile", "dummyFile");
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity responseEntity = mock(HttpEntity.class);
		when(httpClient.execute(any(HttpPost.class))).thenThrow(new RuntimeException());
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(100);
		when( response.getEntity()).thenReturn(responseEntity);
		URIBuilder uriBuilder = mock(URIBuilder.class);
		try(MockedConstruction<URIBuilder> builder = mockConstruction(URIBuilder.class,(mock,context)->{
			when(mock.build()).thenThrow(new URISyntaxException("input","reason"));
			when(mock.setScheme("http")).thenReturn(uriBuilder);
			when(uriBuilder.setHost("localhost")).thenReturn(uriBuilder);
			when(uriBuilder.setPort(3000)).thenReturn(uriBuilder);
			when(uriBuilder.setPath("/")).thenReturn(uriBuilder);
			
		})){
		
		try(MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)){
			mockedStatic.when(()-> HttpClients.createDefault()).thenReturn(httpClient);
			
			exportServiceHandler.handlePhantomjs(object);
			
		}
		}
	}
}

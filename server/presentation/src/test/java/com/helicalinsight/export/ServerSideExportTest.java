package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;

public class ServerSideExportTest {

	@Test
	public void ut_a1_testListOfLocations() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("key", "value");
		String reportParameters = jsonObject.toString();
		JsonObject newSettings = new JsonObject();
		JsonObject credentials = new JsonObject();
		credentials.addProperty("username", "helical");
		credentials.addProperty("password", "insight");
		ServerSideExport export = new ServerSideExport("format", "reportName", reportParameters, "report", "dir", "reportFileName", newSettings);
		
		try(MockedConstruction<ReportsProcessor> construction = mockConstruction(ReportsProcessor.class,(mock,context)->{
			when(mock.phantomCredentials()).thenReturn(credentials);
		})){
			List<String> listOfLocations = export.listOfLocations();
			List<String> list = new ArrayList<>();
			Assert.assertEquals(list, listOfLocations);
		}
	}
	
	@Test
	public void ut_a2_testListOfLocations() {
		JsonObject jsonObject = new JsonObject();
		//jsonObject.addProperty("key", "value");
		String reportParameters = jsonObject.toString();
		JsonObject newSettings = new JsonObject();
		JsonObject credentials = new JsonObject();
		credentials.addProperty("username", "helical");
		credentials.addProperty("password", "insight");
		ServerSideExport export = new ServerSideExport("format", "reportName", reportParameters, "reportType", "dir", "reportFileName", newSettings);
		
		try(MockedConstruction<ReportsProcessor> construction = mockConstruction(ReportsProcessor.class,(mock,context)->{
			when(mock.phantomCredentials()).thenReturn(credentials);
		})){
			try(MockedStatic<URLEncoder> mockedStatic = mockStatic(URLEncoder.class)){
				mockedStatic.when(()-> URLEncoder.encode(anyString(),anyString())).thenThrow(new UnsupportedEncodingException());
				List<String> listOfLocations = export.listOfLocations();
				List<String> list = new ArrayList<>();
				Assert.assertEquals(list, listOfLocations);
			}
		}
	}
}

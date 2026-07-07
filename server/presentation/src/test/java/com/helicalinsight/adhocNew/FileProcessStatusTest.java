package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.FileProcessStatus;
import com.helicalinsight.datasource.ActiveQueryRegistry;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileProcessStatusTest {

	@Test
	public void ut_a1_test_executeComponent() {
		FileProcessStatus fileProcessStatus = new FileProcessStatus();
		
		JsonObject formJson = new JsonObject();
		String executeComponent = fileProcessStatus.executeComponent(formJson.toString());
		JsonObject response = JsonParser.parseString(executeComponent).getAsJsonObject();
		assertTrue(response.entrySet().isEmpty());
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		FileProcessStatus fileProcessStatus = new FileProcessStatus();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("requestId", "11");
		JsonObject fileProcessedPercentage = new JsonObject();
		fileProcessedPercentage.addProperty("key", "value");
		
		ActiveQueryRegistry queryRegistry = mock(ActiveQueryRegistry.class);
		when(queryRegistry.getFileProcessedPercentage(anyString())).thenReturn(fileProcessedPercentage);
		
		try(MockedStatic<ActiveQueryRegistry> mockedStatic = mockStatic(ActiveQueryRegistry.class)){
			mockedStatic.when(()-> ActiveQueryRegistry.getRegistry()).thenReturn(queryRegistry);
			String executeComponent = fileProcessStatus.executeComponent(formJson.toString());
			JsonObject response = JsonParser.parseString(executeComponent).getAsJsonObject();
			assertTrue(!response.entrySet().isEmpty());
		}
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		FileProcessStatus fileProcessStatus = new FileProcessStatus();
		boolean threadSafeToCache = fileProcessStatus.isThreadSafeToCache();
		assertFalse(threadSafeToCache);
	}
}

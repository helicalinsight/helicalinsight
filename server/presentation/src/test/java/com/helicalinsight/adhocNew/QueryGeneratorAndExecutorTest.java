package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.DatabaseQueryExecutor;
import com.helicalinsight.adhoc.QueryGeneratorAndExecutor;
import com.helicalinsight.adhoc.VisualizationComponent;
import com.helicalinsight.efw.exceptions.EfwServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QueryGeneratorAndExecutorTest {

	
	@Test(expected = EfwServiceException.class)
	public void ut_a1_test_executeComponent() {
		QueryGeneratorAndExecutor executor =new QueryGeneratorAndExecutor();
		JsonObject formData = new JsonObject();
		formData.addProperty("query", "");
		executor.executeComponent(formData.toString());
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		QueryGeneratorAndExecutor executor =new QueryGeneratorAndExecutor();
		JsonObject formData = new JsonObject();
		formData.addProperty("query", "No Columns Selected");
		String executeComponent = executor.executeComponent(formData.toString());
		assertEquals("No Columns Selected", executeComponent);
	}
	
	@Test
	public void ut_a3_test_executeComponent() {
		QueryGeneratorAndExecutor executor =new QueryGeneratorAndExecutor();
		JsonObject formData = new JsonObject();
		formData.addProperty("query", "sample_query");
		
		
		try(MockedConstruction<DatabaseQueryExecutor> construction = mockConstruction(DatabaseQueryExecutor.class,(mock,context)->{
			when(mock.executeComponent(anyString())).thenReturn("sample_query");
		})){
			String executeComponent = executor.executeComponent(formData.toString());
			assertEquals("sample_query", executeComponent);
		}
	}
	
	@Test
	public void ut_a4_test_executeComponent() {
		QueryGeneratorAndExecutor executor =new QueryGeneratorAndExecutor();
		JsonObject formData = new JsonObject();
		formData.addProperty("query", "sample_query");
		formData.addProperty("vf", true);
		formData.addProperty("provideQuery", true);
		JsonObject jsonResult = new JsonObject();
		JsonArray metadata = new JsonArray();
		jsonResult.add("metadata", metadata);
		jsonResult.add("data", metadata);
		JsonObject scriptJson = new JsonObject();
		scriptJson.addProperty("script", "script");
		try(MockedConstruction<DatabaseQueryExecutor> construction = mockConstruction(DatabaseQueryExecutor.class,(mock,context)->{
			when(mock.executeComponent(anyString())).thenReturn(jsonResult.toString());
		})){
			try(MockedConstruction<VisualizationComponent> construction2 = mockConstruction(VisualizationComponent.class,(mock,context)->{
				when(mock.executeComponent(anyString())).thenReturn(scriptJson.toString());
			})){
				
			String executeComponent = executor.executeComponent(formData.toString());
			assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().getAsJsonArray("metadata").isEmpty());
		}
	}
	}
	
	@Test
	public void ut_a5_test_isThreadSafeToCache() {
		QueryGeneratorAndExecutor executor =new QueryGeneratorAndExecutor();
		boolean threadSafeToCache = executor.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}

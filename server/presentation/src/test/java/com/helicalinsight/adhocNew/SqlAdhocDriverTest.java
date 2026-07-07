package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.SqlAdhocDriver;
import com.helicalinsight.adhoc.services.QueryGeneratorAndExecutorService;
import com.helicalinsight.datasource.EfwdQueryProcessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlAdhocDriverTest {

	@Test
	public void ut_a1_test_getQuery() {
		SqlAdhocDriver adhocDriver = new SqlAdhocDriver();
		JsonObject dataMapTagContent = new JsonObject();
		JsonObject requestParameterJson = new JsonObject();
	
		try(MockedConstruction<EfwdQueryProcessor> construction = mockConstruction(EfwdQueryProcessor.class,(mock,context)->{
			when(mock.getQuery(any(JsonObject.class), any(JsonObject.class))).thenReturn("Query");
		})){
			String query = adhocDriver.getQuery(dataMapTagContent, requestParameterJson);
			assertEquals("Query", query);
		}
	}
	
	@Test
	public void ut_a2_test_getJSONData() {
		SqlAdhocDriver adhocDriver = new SqlAdhocDriver();
		JsonObject dataMapTagContent = new JsonObject();
		JsonObject requestParameterJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("location", "loc");
		connectionDetails.addProperty("metadataFileName", "metadataFileName");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("response", null);
		
		try(MockedConstruction<EfwdQueryProcessor> construction = mockConstruction(EfwdQueryProcessor.class,(mock,context)->{
			when(mock.getQuery(any(JsonObject.class), any(JsonObject.class))).thenReturn(dataMapTagContent.toString());
		})){
			try(MockedConstruction<QueryGeneratorAndExecutorService> construction2 = mockConstruction(QueryGeneratorAndExecutorService.class,(mock,context)->{
				when(mock.doService(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonObject.toString());
			})){
			JsonObject object = adhocDriver.getJSONData(requestParameterJson,connectionDetails, dataMapTagContent, null);
			assertTrue(object.entrySet().isEmpty());
			}
		}
	}
	
	@Test
	public void ut_a3_test_getJSONData() {
		SqlAdhocDriver adhocDriver = new SqlAdhocDriver();
		JsonObject dataMapTagContent = new JsonObject();
		JsonObject requestParameterJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("location", "loc");
		connectionDetails.addProperty("metadataFileName", "metadataFileName");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("response", new JsonObject());
		
		try(MockedConstruction<EfwdQueryProcessor> construction = mockConstruction(EfwdQueryProcessor.class,(mock,context)->{
			when(mock.getQuery(any(JsonObject.class), any(JsonObject.class))).thenReturn(dataMapTagContent.toString());
		})){
			try(MockedConstruction<QueryGeneratorAndExecutorService> construction2 = mockConstruction(QueryGeneratorAndExecutorService.class,(mock,context)->{
				when(mock.doService(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonObject.toString());
			})){
			JsonObject object = adhocDriver.getJSONData(requestParameterJson,connectionDetails, dataMapTagContent, null);
			assertTrue(object.entrySet().isEmpty());
			}
		}
	}
	
	@Test
	public void ut_a4_test_getJSONData() {
		SqlAdhocDriver adhocDriver = new SqlAdhocDriver();
		JsonObject dataMapTagContent = new JsonObject();
		JsonObject requestParameterJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("location", "loc");
		connectionDetails.addProperty("metadataFileName", "metadataFileName");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("response", connectionDetails);
		
		try(MockedConstruction<EfwdQueryProcessor> construction = mockConstruction(EfwdQueryProcessor.class,(mock,context)->{
			when(mock.getQuery(any(JsonObject.class), any(JsonObject.class))).thenReturn(dataMapTagContent.toString());
		})){
			try(MockedConstruction<QueryGeneratorAndExecutorService> construction2 = mockConstruction(QueryGeneratorAndExecutorService.class,(mock,context)->{
				when(mock.doService(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonObject.toString());
			})){
			JsonObject object = adhocDriver.getJSONData(requestParameterJson,connectionDetails, dataMapTagContent, null);
			assertEquals(connectionDetails,object);
			}
		}
	}
}

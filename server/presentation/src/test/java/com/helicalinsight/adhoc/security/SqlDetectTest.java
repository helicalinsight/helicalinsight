package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlDetectTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		SqlDetect detect = new SqlDetect();
		boolean threadSafeToCache = detect.isThreadSafeToCache();
		assertFalse(threadSafeToCache);
	}
	
	@Test
	public void ut_a2_test_checkForSqlString() {
		SqlDetect detect = new SqlDetect();
		Object checkForSqlString = detect.checkForSqlString("ALTER");
		assertNull(checkForSqlString);
		
	}
	
	@Test
	public void ut_a3_test_checkForSqlString() {
		SqlDetect detect = new SqlDetect();
		Object checkForSqlString = detect.checkForSqlString("value");
		assertEquals("value",checkForSqlString);
		
	}
	
	@Test(expected = SecurityException.class)
	public void ut_a4_test_checkSqlInjection() throws Exception {
		SqlDetect detect = new SqlDetect();
		JsonObject formData = new JsonObject();
		JsonObject json = new JsonObject();
		json.addProperty("temp", "ALTER");
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read(anyString())).thenReturn("temp");
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
			
			JsonObject checkForSqlString = detect.checkSqlInjection(formData, "key", json);
			System.out.println(checkForSqlString);
			
		}
	}
	
	
	@Test(expected = SecurityException.class)
	public void ut_a5_test_checkSqlInjection() throws Exception {
		SqlDetect detect = new SqlDetect();
		JsonObject formData = new JsonObject();
		JsonObject json = new JsonObject();
		json.add("ALTER", JsonNull.INSTANCE);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read(anyString())).thenReturn("ALTER");
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
			
			JsonObject checkForSqlString = detect.checkSqlInjection(formData, "key", json);
			System.out.println(checkForSqlString);
			
		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_a6_test_checkSqlInjection() throws Exception {
		SqlDetect detect = new SqlDetect();
		JsonObject formData = new JsonObject();
		JsonArray json = new JsonArray();
	
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read(anyString())).thenReturn("ALTER");
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
			
			JsonObject checkForSqlString = detect.checkSqlInjection(formData, "key", json);
			System.out.println(checkForSqlString);
			
		}
	}
	
	@Test
	public void ut_a7_test_checkSqlInjection() throws Exception {
		SqlDetect detect = new SqlDetect();
		JsonObject formData = new JsonObject();
		JsonArray json = new JsonArray();
	
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read(anyString())).thenReturn(new JsonArray());
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
			
			JsonObject checkForSqlString = detect.checkSqlInjection(formData, "key", json);
			assertTrue(checkForSqlString.entrySet().isEmpty());
			
		}
	}
}

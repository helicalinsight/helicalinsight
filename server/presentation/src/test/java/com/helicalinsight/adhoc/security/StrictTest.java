package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertFalse;
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
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StrictTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		Strict strict = new Strict();
		boolean threadSafeToCache = strict.isThreadSafeToCache();
		assertFalse(threadSafeToCache);
	}
	@Test(expected = Exception.class)
	public void ut_a2_test_checkSqlInjection() throws Exception {
		Strict strict = new Strict();
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray jsonArrayColumns = new JsonArray();
		jsonArrayColumns.add("");
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		
		JsonObject formData = new JsonObject();
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
	
			strict.checkSqlInjection(formData, "key", new Object());
		}
		
	}
	
	@Test(expected = Exception.class)
	public void ut_a3_test_checkSqlInjection() throws Exception {
		Strict strict = new Strict();
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonObject jsonObj = new JsonObject();
		
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonObj);
		
		JsonObject formData = new JsonObject();
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
	
			strict.checkSqlInjection(formData, "key", new Object());
		}
		
	}
	
	@Test
	public void ut_a4_test_checkSqlInjection() throws Exception {
		Strict strict = new Strict();
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		
		when(jsonDocumentContext.read(anyString())).thenReturn("");
		
		JsonObject formData = new JsonObject();
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
			.thenReturn(jsonDocumentContext);
	
			JsonObject checkSqlInjection = strict.checkSqlInjection(formData, "key", new Object());
			assertTrue(checkSqlInjection.entrySet().isEmpty());
		}
		
	}
}

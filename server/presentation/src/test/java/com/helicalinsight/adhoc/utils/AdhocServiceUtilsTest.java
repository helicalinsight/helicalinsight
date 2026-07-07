package com.helicalinsight.adhoc.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocServiceUtilsTest {

	@Test(expected = IllegalArgumentException.class)
	public void ut_a1_test_getArray() {
		AdhocServiceUtils adhocServiceUtils = new AdhocServiceUtils();
		AdhocServiceUtils.getArray(null, null);
	}

	@Test(expected = MalformedJsonException.class)
	public void ut_a2_test_getArray() {
		JsonObject json = new JsonObject();
		json.addProperty("keyName", "kName");
		AdhocServiceUtils.getArray(json, "keyName");
	}

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a3_test_getArray() {
		JsonObject json = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		json.add("keyName", jsonArray);
		AdhocServiceUtils.getArray(json, "keyName");
	}

	@Test
	public void ut_a4_test_getArray() {
		JsonObject json = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("helical");
		json.add("keyName", jsonArray);
		JsonArray array = AdhocServiceUtils.getArray(json, "keyName");
		assertEquals(jsonArray, array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a5_test_getObject() {
		AdhocServiceUtils.getObject(null, null);
	}

	@Test(expected = MalformedJsonException.class)
	public void ut_a6_test_getObject() {
		JsonObject json = new JsonObject();
		json.addProperty("keyName", "kName");
		AdhocServiceUtils.getObject(json, "keyName");
	}

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a7_test_getObject() {
		JsonObject json = new JsonObject();
		JsonObject jsonObj = new JsonObject();
		json.add("keyName", jsonObj);
		AdhocServiceUtils.getObject(json, "keyName");
	}

	@Test
	public void ut_a8_test_getObject() {
		JsonObject json = new JsonObject();
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("helical", "insight");
		json.add("keyName", jsonObj);
		JsonObject array = AdhocServiceUtils.getObject(json, "keyName");
		assertEquals(jsonObj, array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a9_test_addExtraDataForNormalProcess() {
		JsonObject formJson = new JsonObject();
		AdhocServiceUtils.addExtraDataForNormalProcess(formJson, null);
	}

	@Test
	public void ut_b1_test_addExtraDataForNormalProcess() {
		JsonObject formJson = new JsonObject();
		try (MockedStatic<DataSourceUtils> mockedStatic = mockStatic(DataSourceUtils.class)) {
			AdhocServiceUtils.addExtraDataForNormalProcess(formJson, "dataSourceType");

		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_b2_test_addExtraDataForNormalProcessDB() {
		Map<String, Object> formMap = new HashMap<>();
		AdhocServiceUtils.addExtraDataForNormalProcessDB(formMap, null);

	}

	@Test
	public void ut_b3_test_addExtraDataForNormalProcessDB() {
		Map<String, Object> formMap = new HashMap<>();
		try (MockedStatic<DataSourceUtils> mockedStatic = mockStatic(DataSourceUtils.class)) {
			AdhocServiceUtils.addExtraDataForNormalProcessDB(formMap, "dataSourceType");

		}
	}
	
	@Test
	public void ut_b4_test_prepareExtensions() {
		JsonArray prepareExtensions = AdhocServiceUtils.prepareExtensions();
		assertFalse(prepareExtensions.isEmpty());
	}
	@Test
	public void ut_b5_test_prepareFormData() {
		JsonObject object = AdhocServiceUtils.prepareFormData();
		assertNotNull(object);
	}
	
	@Test
	public void ut_b6_test_getSpecificExtension() {
		JsonArray specificExtension = AdhocServiceUtils.getSpecificExtension(null, "extension");
		assertTrue(specificExtension.isEmpty());
	}
	
	@Test
	public void ut_b7_test_getSpecificExtension() {
		JsonArray arr = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("reportPath", "reportPath.ext");
		arr.add(jsonObject);
		
		JsonArray specificExtension = AdhocServiceUtils.getSpecificExtension(arr, "ext");
		assertFalse(specificExtension.isEmpty());
	}
	@Test
	public void ut_b8_test_testURL() throws IOException {
		HttpURLConnection urlConn = mock(HttpURLConnection.class);
		when(urlConn.getResponseCode()).thenReturn(200);
		try(MockedConstruction<URL> construction = mockConstruction(URL.class,(mock,context)->{
			when(mock.openConnection()).thenReturn(urlConn);
		})){
			boolean testURL = AdhocServiceUtils.testURL("");
			assertTrue(testURL);
		}
		
	}
	@Test
	public void ut_b9_test_testURL() throws IOException {
		HttpURLConnection urlConn = mock(HttpURLConnection.class);
		when(urlConn.getResponseCode()).thenReturn(300);
		try(MockedConstruction<URL> construction = mockConstruction(URL.class,(mock,context)->{
			when(mock.openConnection()).thenReturn(urlConn);
		})){
			boolean testURL = AdhocServiceUtils.testURL("");
			assertFalse(testURL);
		}
		
	}
	
	@Test
	public void ut_c1_test_prepareUrlForDrill() throws IOException {
		JsonObject drillJson = new JsonObject();
		drillJson.addProperty("host", "host");
		drillJson.addProperty("port", "port");
		
		String prepareUrlForDrill = AdhocServiceUtils.prepareUrlForDrill(drillJson);
		assertEquals("http://host:port",prepareUrlForDrill);
	}
}

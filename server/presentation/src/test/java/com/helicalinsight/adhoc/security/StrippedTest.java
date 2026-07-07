package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StrippedTest {

	@Test(expected = Exception.class)
	public void ut_a1_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		JsonObject updatedJSON = new JsonObject();
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(null);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			stripped.checkSqlInjection(formData, "key", obj);
		}

	}

	@Test
	public void ut_a2_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		JsonObject updatedJSON = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		JsonArray jsonArr = new JsonArray();
		jsonArr.add("string");
		jsonArrayColumns.add(jsonArr);
		JsonObject jsonObject = new JsonObject();
		JsonArray finalFilterArray = new JsonArray();

		jsonObject.add("filters", finalFilterArray);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.filters", obj);
			assertTrue(checkSqlInjection.entrySet().isEmpty());
		}

	}

	@Test
	public void ut_a3_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		JsonObject updatedJSON = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		JsonArray jsonArr = new JsonArray();
		jsonArr.add("string");
		jsonArrayColumns.add(jsonArr);
		JsonObject jsonObject = new JsonObject();
		JsonArray finalFilterArray = new JsonArray();
		finalFilterArray.add("");
		jsonObject.add("filters", finalFilterArray);
		Statement stmt = mock(Statement.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(stmt);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.filters", obj);
				assertTrue(checkSqlInjection.entrySet().isEmpty());
			}
		}
	}
	
	@Test
	public void ut_a4_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		JsonObject updatedJSON = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		JsonArray jsonArr = new JsonArray();
		jsonArr.add("string");
		jsonArrayColumns.add(jsonArr);
		JsonObject jsonObject = new JsonObject();
		JsonArray finalFilterArray = new JsonArray();
		
		finalFilterArray.add("filters");
		finalFilterArray.add("customFilterExpression");
		finalFilterArray.add("customHavingExpression");
		
		jsonObject.add("filters", finalFilterArray);
		jsonObject.addProperty("customFilterExpression","customFilterExpression");
		jsonObject.addProperty("customHavingExpression","customHavingExpression" );
		jsonObject.addProperty("customFilterExpression","customFilterExpression" );
		jsonObject.addProperty("customHavingExpression","customHavingExpression" );
		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.filters", obj);
				assertTrue(checkSqlInjection.has("filters"));
			}
		}
	}

	@Test
	public void ut_a5_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		JsonObject updatedJSON = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		JsonArray jsonArr = new JsonArray();
		jsonArr.add("string");
		jsonArrayColumns.add(jsonArr);
		JsonObject jsonObject = new JsonObject();
		JsonArray finalFilterArray = new JsonArray();
		
		finalFilterArray.add("filters");
		finalFilterArray.add("customFilterExpression");
		finalFilterArray.add("customHavingExpression");
		
		jsonObject.add("filters", finalFilterArray);
//		jsonObject.addProperty("customFilterExpression","customFilterExpression");
//		jsonObject.addProperty("customHavingExpression","customHavingExpression" );
//		jsonObject.addProperty("customFilterExpression","customFilterExpression" );
//		jsonObject.addProperty("customHavingExpression","customHavingExpression" );
//		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.filters", obj);
				assertTrue(checkSqlInjection.has("filters"));
			}
		}
	}
	@Test
	public void ut_a6_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		JsonObject updatedJSON = new JsonObject();
		JsonObject object = new JsonObject();
		JsonArray jsonArr = new JsonArray();
		jsonArr.add("string");
		object.add("values",jsonArr);
		JsonObject jsonObject = new JsonObject();
		JsonArray finalFilterArray = new JsonArray();
		
		finalFilterArray.add("filters");
		finalFilterArray.add("customFilterExpression");
		finalFilterArray.add("customHavingExpression");
		
		jsonObject.add("filters", finalFilterArray);
		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(object);
		when(jsonDocumentContext.json()).thenReturn(object);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.filters", obj);
				assertTrue(checkSqlInjection.has("values"));
			}
		}
	}
	
	@Test
	public void ut_a7_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		
		JsonObject object = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		
		JsonObject json = new JsonObject();
		json.addProperty("alias", "columnName");
		jsonArrayColumns.add(json);
		JsonObject jsonObject = new JsonObject();
		JsonObject functionObject = new JsonObject();
		JsonArray aggregate = new JsonArray();
		functionObject.add("aggregate", aggregate);
		JsonObject updatedJSON = new JsonObject();
		updatedJSON.add("functions", functionObject);
		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		when(jsonDocumentContext.json()).thenReturn(updatedJSON);
		when(jsonDocumentContext.delete(anyString())).thenReturn(jsonDocumentContext);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.columns", obj);
				assertTrue(checkSqlInjection.has("functions"));
			}
		}
	}

	@Test
	public void ut_a8_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		
		JsonObject object = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		JsonObject alias = new JsonObject();
		alias.addProperty("alias", "columnName");
		jsonArrayColumns.add(alias);
		JsonObject jsonObject = new JsonObject();
		JsonObject functionObject = new JsonObject();
		JsonArray aggregate = new JsonArray();
		functionObject.add("aggregate", aggregate);
		JsonArray groupByArray = new JsonArray();
		JsonObject groupBy = new JsonObject();
		groupBy.addProperty("column", "columnName");
		groupByArray.add(groupBy);
		functionObject.add("groupBy", groupByArray);
		JsonObject updatedJSON = new JsonObject();
		updatedJSON.add("functions", functionObject);
		JsonArray orderBy = new JsonArray();
		orderBy.add(alias);
		functionObject.add("orderBy", orderBy);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		when(jsonDocumentContext.json()).thenReturn(updatedJSON);
		when(jsonDocumentContext.delete(anyString())).thenReturn(jsonDocumentContext);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.columns", obj);
				assertTrue(checkSqlInjection.has("functions"));
			}
		}
	}
	
	@Test
	public void ut_a9_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		
		JsonObject object = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		
		JsonObject jsonObject = new JsonObject();
		JsonObject functionObject = new JsonObject();
		JsonArray aggregate = new JsonArray();
		functionObject.add("aggregate", aggregate);
		JsonArray groupByArray = new JsonArray();
		JsonObject groupBy = new JsonObject();
		groupBy.addProperty("column", "columnName");
		groupByArray.add(groupBy);
		functionObject.add("groupBy", groupByArray);
		JsonObject updatedJSON = new JsonObject();
		updatedJSON.add("functions", functionObject);
		JsonArray orderBy = new JsonArray();
		functionObject.add("orderBy", orderBy);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		when(jsonDocumentContext.json()).thenReturn(updatedJSON);
		when(jsonDocumentContext.delete(anyString())).thenReturn(jsonDocumentContext);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "$.columns", obj);
				assertTrue(checkSqlInjection.has("functions"));
			}
		}
	}

	@Test
	public void ut_b1_test_checkSqlInjection() throws Exception {
		Stripped stripped = new Stripped();
		Object obj = new Object();
		JsonObject formData = new JsonObject();
		
		JsonObject object = new JsonObject();
		JsonArray jsonArrayColumns = new JsonArray();
		
		JsonObject jsonObject = new JsonObject();
		JsonObject functionObject = new JsonObject();
		JsonArray aggregate = new JsonArray();
		functionObject.add("aggregate", aggregate);
		JsonArray groupByArray = new JsonArray();
		JsonObject groupBy = new JsonObject();
		groupBy.addProperty("column", "columnName");
		groupByArray.add(groupBy);
		functionObject.add("groupBy", groupByArray);
		JsonObject updatedJSON = new JsonObject();
		updatedJSON.add("functions", functionObject);
		JsonArray orderBy = new JsonArray();
		functionObject.add("orderBy", orderBy);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.jsonString()).thenReturn(updatedJSON.toString());
		when(jsonDocumentContext.read(anyString())).thenReturn(jsonArrayColumns);
		when(jsonDocumentContext.json()).thenReturn(jsonObject);
		when(jsonDocumentContext.json()).thenReturn(updatedJSON);
		when(jsonDocumentContext.delete(anyString())).thenReturn(jsonDocumentContext);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				JsonObject checkSqlInjection = stripped.checkSqlInjection(formData, "rows", obj);
				assertTrue(checkSqlInjection.has("functions"));
			}
		}
	}

	@Test
	public void ut_b2_test_isThreadSafeToCache() {
		Stripped stripped = new Stripped();
		boolean threadSafeToCache = stripped.isThreadSafeToCache();
		assertFalse(threadSafeToCache);
	}

	@Test
	public void ut_b3_test_check_for_custom_expression() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("check_for_custom_expression", String.class,String.class);
		method.setAccessible(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("filters", "filters");
		jsonObject.addProperty("customFilterExpression", "customFilterExpression");
		JsonObject invoke = (JsonObject) method.invoke(stripped, jsonObject.toString(), "FILTERS");
		assertTrue(invoke.has("filters"));
	}

	@Test
	public void ut_b4_test_check_for_custom_expression() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("check_for_custom_expression", String.class,String.class);
		method.setAccessible(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("having", "having");
		jsonObject.addProperty("customHavingExpression", "customHavingExpression");
		JsonObject invoke = (JsonObject) method.invoke(stripped, jsonObject.toString(), "HAVING");
		assertTrue(invoke.has("having"));
	}
	
	@Test
	public void ut_b5_test_check_for_custom_expression() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("check_for_custom_expression", String.class,String.class);
		method.setAccessible(true);
		
		JsonObject jsonObject = new JsonObject();
		JsonArray having = new JsonArray();
		jsonObject.add("having", having);
		
		JsonObject invoke = (JsonObject) method.invoke(stripped, jsonObject.toString(), "having");
		assertTrue(invoke.entrySet().isEmpty());
	}
	
	@Test
	public void ut_b6_test_check_for_custom_expression() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("check_for_custom_expression", String.class,String.class);
		method.setAccessible(true);
		
		JsonObject jsonObject = new JsonObject();
		JsonArray having = new JsonArray();
		jsonObject.add("filters", having);
		
		JsonObject invoke = (JsonObject) method.invoke(stripped, jsonObject.toString(), "filters");
		assertTrue(invoke.entrySet().isEmpty());
	}
	
	@Test
	public void ut_b7_test_check_for_custom_expression() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("check_for_custom_expression", String.class,String.class);
		method.setAccessible(true);
		
		JsonObject jsonObject = new JsonObject();
		JsonArray having = new JsonArray();
		having.add("");
		jsonObject.add("filters", having);
		
		JsonObject invoke = (JsonObject) method.invoke(stripped, jsonObject.toString(), "filters");
		assertTrue(invoke.has("filters"));
	}
	
	@Test
	public void ut_b8_test_check_for_custom_expression() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("check_for_custom_expression", String.class,String.class);
		method.setAccessible(true);
		
		JsonObject jsonObject = new JsonObject();
		JsonArray having = new JsonArray();
		having.add("");
		jsonObject.add("having", having);
		
		JsonObject invoke = (JsonObject) method.invoke(stripped, jsonObject.toString(), "having");
		assertTrue(invoke.has("having"));
	}
	@Test
	public void ut_b9_test_userRequestedColumns() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Stripped stripped = new Stripped();
		Method method = Stripped.class.getDeclaredMethod("userRequestedColumns", JsonArray.class);
		method.setAccessible(true);
		JsonArray columns = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", "column");
		columns.add(json);
		method.invoke(stripped, columns);
		
	}

	
}

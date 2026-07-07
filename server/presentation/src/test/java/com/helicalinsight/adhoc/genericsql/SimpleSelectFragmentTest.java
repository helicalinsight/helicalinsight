package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleSelectFragmentTest {

	@Test
	public void ut_a1_test_select() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Map<String, String> columnsMap = new HashMap<>();
		List<String> derivedTableColumns = new ArrayList<>();
		
		when(context.getColumnsMap()).thenReturn(columnsMap);
		when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
		SimpleSelectFragment fragment = new SimpleSelectFragment(context);
		JsonObject json = new JsonObject();
		json.addProperty("hidden", "true");
		String select = fragment.select("query", json);
		assertEquals("query", select);
		
		
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a2_test_select() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Map<String, String> columnsMap = new HashMap<>();
		List<String> derivedTableColumns = new ArrayList<>();
		
		when(context.getColumnsMap()).thenReturn(columnsMap);
		when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
		SimpleSelectFragment fragment = new SimpleSelectFragment(context);
		JsonObject json = new JsonObject();
		json.addProperty("hidden", "true");
		json.addProperty("includeInResultset", "true");
		json.addProperty("custom", "false");
		json.addProperty("column", "columnName");
		String select = fragment.select("query", json);
		assertEquals("query", select);
		
		
	}
	
	@Test
	public void ut_a3_test_select() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Map<String, String> columnsMap = new HashMap<>();
		List<String> derivedTableColumns = new ArrayList<>();
		
		when(context.getColumnsMap()).thenReturn(columnsMap);
		when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
				
		SimpleSelectFragment fragment = new SimpleSelectFragment(context);
		JsonObject json = new JsonObject();
		json.addProperty("hidden", "true");
		json.addProperty("includeInResultset", "true");
		json.addProperty("custom", "true");
		json.addProperty("column", "columnName");
		json.addProperty("alias", "aliasName");
		json.addProperty("databaseFunction", "databaseFunction");
		
		when(context.databaseFunction(any())).thenReturn("customColumn");
		JsonObject formData = new JsonObject();
		JsonObject selectedColumnsMap = new JsonObject();
		
		formData.add("selectedColumnsMap", selectedColumnsMap);
		when(context.getFormData()).thenReturn(formData);
		when(context.as(anyString())).thenReturn("as");
		String select = fragment.select("query", json);
		assertTrue(select.contains("customColumn"));
		
		
	}
	
	@Test
	public void ut_a4_test_select() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Map<String, String> columnsMap = new HashMap<>();
		List<String> derivedTableColumns = new ArrayList<>();
		
		when(context.getColumnsMap()).thenReturn(columnsMap);
		when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
				
		SimpleSelectFragment fragment = new SimpleSelectFragment(context);
		JsonObject json = new JsonObject();
		json.addProperty("hidden", "true");
		json.addProperty("includeInResultset", "true");
		json.addProperty("custom", "true");
		json.addProperty("column", "columnName");
		json.addProperty("databaseFunction", "databaseFunction");
		
		when(context.databaseFunction(any())).thenReturn("customColumn");
		JsonObject formData = new JsonObject();
		JsonObject selectedColumnsMap = new JsonObject();
		
		formData.add("selectedColumnsMap", selectedColumnsMap);
		when(context.getFormData()).thenReturn(formData);
		when(context.as(anyString())).thenReturn("as");
		String select = fragment.select("query", json);
		assertTrue(select.contains("customColumn"));
		
		
	}
	
	@Test
	public void ut_a5_test_select() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Map<String, String> columnsMap = new HashMap<>();
		columnsMap.put("columnName", "columnName");
		List<String> derivedTableColumns = new ArrayList<>();
		derivedTableColumns.add("columnName");
		when(context.getColumnsMap()).thenReturn(columnsMap);
		when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
				
		SimpleSelectFragment fragment = new SimpleSelectFragment(context);
		JsonObject json = new JsonObject();
		json.addProperty("hidden", "true");
		json.addProperty("includeInResultset", "true");
		
		json.addProperty("column", "columnName");
		json.addProperty("databaseFunction", "databaseFunction");
		
		when(context.databaseFunction(any())).thenReturn("customColumn");
		JsonObject formData = new JsonObject();
		JsonObject selectedColumnsMap = new JsonObject();
		
		formData.add("selectedColumnsMap", selectedColumnsMap);
		when(context.getFormData()).thenReturn(formData);
		when(context.as(anyString())).thenReturn("as");
		when(context.quotes(anyString())).thenReturn("temp");
		String select = fragment.select("query", json);
		assertTrue(select.contains("temp"));
		
		
	}
	
	@Test
	public void ut_a6_test_select() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Map<String, String> columnsMap = new HashMap<>();
		columnsMap.put("columnName", "columnName");
		List<String> derivedTableColumns = new ArrayList<>();
		derivedTableColumns.add("columnName");
		when(context.getColumnsMap()).thenReturn(columnsMap);
		when(context.getDerivedTableColumns()).thenReturn(derivedTableColumns);
				
		SimpleSelectFragment fragment = new SimpleSelectFragment(context);
		JsonObject json = new JsonObject();
		json.addProperty("hidden", "true");
		json.addProperty("includeInResultset", "true");
		json.addProperty("alias", "aliasName");
		
		json.addProperty("column", "columnName");
		json.addProperty("databaseFunction", "databaseFunction");
		
		when(context.databaseFunction(any())).thenReturn("customColumn");
		JsonObject formData = new JsonObject();
		JsonObject selectedColumnsMap = new JsonObject();
		
		formData.add("selectedColumnsMap", selectedColumnsMap);
		when(context.getFormData()).thenReturn(formData);
		when(context.as(anyString())).thenReturn("as");
		when(context.quotes(anyString())).thenReturn("temp");
		String select = fragment.select("query", json);
		assertTrue(select.contains("temp"));
		
		
	}
}

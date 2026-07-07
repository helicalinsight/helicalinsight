package com.helicalinsight.adhoc.genericsql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectFragmentWithAggregateTest {

	@Test(expected = QueryBuilderException.class)
	public void ut_a1_test_prepareAggregates() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> list = new ArrayList<>();
		JsonObject formData = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray aggregate = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("alias", "alias");
		json.addProperty("column", "column");
		json.addProperty("function", "function");
		json.addProperty("custom", "false");
		
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formData.add("functions", functions);
		
		
		when(context.getFormData()).thenReturn(formData);
		when(context.getDerivedTableColumns()).thenReturn(list);
		SelectFragmentWithAggregate selectFragmentWithAggregate = new SelectFragmentWithAggregate(context);
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a2_test_prepareAggregates() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		JsonObject formData = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray aggregate = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("alias", "alias");
		json.addProperty("column", "column");
		json.addProperty("function", "function");
		json.addProperty("custom", "true");
		
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formData.add("functions", functions);
		JsonArray columns = new JsonArray();
		JsonObject json2 = new JsonObject();
		json2.addProperty("hidden", "true");
		json2.addProperty("column", "column");
		json2.addProperty("aggregate", "true");
		JsonObject json3 = new JsonObject();
		json3.addProperty("hidden", "false");
		json3.addProperty("column", "null");
		json3.addProperty("aggregate", "true");
		columns.add(json2);
		columns.add(json3);
		formData.add("columns", columns);
		when(context.getFormData()).thenReturn(formData);
		when(context.getDerivedTableColumns()).thenReturn(list);
		when(context.databaseFunction(any())).thenReturn("column");
		SelectFragmentWithAggregate selectFragmentWithAggregate = new SelectFragmentWithAggregate(context);
		selectFragmentWithAggregate.select();
	}
	
	@Test
	public void ut_a3_test_prepareAggregates() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		JsonObject formData = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray aggregate = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", "column");
		json.addProperty("function", "function_function");
		json.addProperty("custom", "true");
		json.addProperty("applyBeforeAggregate", "true");
		
		
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formData.add("functions", functions);
		JsonArray columns = new JsonArray();
		JsonObject json2 = new JsonObject();
		json2.addProperty("hidden", "true");
		json2.addProperty("column", "column");
		json2.addProperty("aggregate", "true");
		JsonObject json3 = new JsonObject();
		json3.addProperty("hidden", "false");
		json3.addProperty("column", "column");
		json3.addProperty("aggregate", "true");
		json3.addProperty("databaseFunction", "databaseFunction");
		columns.add(json2);
		columns.add(json3);
		formData.add("columns", columns);
		formData.add("selectedColumnsMap", new JsonObject());
		when(context.getFormData()).thenReturn(formData);
		when(context.getDerivedTableColumns()).thenReturn(list);
		when(context.databaseFunction(any())).thenReturn("column");
		when(context.quotes(anyString())).thenReturn("sample");
		when(context.as(anyString())).thenReturn("alias");
		SelectFragmentWithAggregate selectFragmentWithAggregate = new SelectFragmentWithAggregate(context);
		selectFragmentWithAggregate.select();
	}
	
	@Test
	public void ut_a4_test_prepareAggregates() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		JsonObject formData = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray aggregate = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", "column");
		json.addProperty("function", "function_function");
		json.addProperty("applyBeforeAggregate", "true");
		
		
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formData.add("functions", functions);
		JsonArray columns = new JsonArray();
		JsonObject json2 = new JsonObject();
		json2.addProperty("hidden", "true");
		json2.addProperty("column", "column");
		json2.addProperty("aggregate", "true");
		JsonObject json3 = new JsonObject();
		json3.addProperty("hidden", "false");
		json3.addProperty("column", "column");
		json3.addProperty("aggregate", "true");
		json3.addProperty("databaseFunction", "databaseFunction");
		columns.add(json2);
		columns.add(json3);
		formData.add("columns", columns);
		formData.add("selectedColumnsMap", new JsonObject());
		when(context.getFormData()).thenReturn(formData);
		when(context.getDerivedTableColumns()).thenReturn(list);
		when(context.databaseFunction(any())).thenReturn("column");
		when(context.quotes(anyString())).thenReturn("sample");
		when(context.as(anyString())).thenReturn("alias");
		SelectFragmentWithAggregate selectFragmentWithAggregate = new SelectFragmentWithAggregate(context);
		selectFragmentWithAggregate.select();
	}
	
	
	@Test
	public void ut_a5_test_prepareAggregates() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		JsonObject formData = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray aggregate = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("alias", "alias");
		json.addProperty("column", "column");
		json.addProperty("function", "function");
		json.addProperty("custom", "true");
		json.addProperty("applyBeforeAggregate", "true");
		
		
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formData.add("functions", functions);
		JsonArray columns = new JsonArray();
		JsonObject json2 = new JsonObject();
		json2.addProperty("hidden", "true");
		json2.addProperty("column", "column");
		json2.addProperty("aggregate", "true");
		JsonObject json3 = new JsonObject();
		json3.addProperty("hidden", "false");
		json3.addProperty("column", "column");
		json3.addProperty("aggregate", "true");
		json3.addProperty("databaseFunction", "databaseFunction");
		columns.add(json2);
		columns.add(json3);
		formData.add("columns", columns);
		formData.add("selectedColumnsMap", new JsonObject());
		when(context.getFormData()).thenReturn(formData);
		when(context.getDerivedTableColumns()).thenReturn(list);
		when(context.databaseFunction(any())).thenReturn("column");
		when(context.quotes(anyString())).thenReturn("sample");
		when(context.as(anyString())).thenReturn("alias");
		SelectFragmentWithAggregate selectFragmentWithAggregate = new SelectFragmentWithAggregate(context);
		selectFragmentWithAggregate.select();
	}
	
}

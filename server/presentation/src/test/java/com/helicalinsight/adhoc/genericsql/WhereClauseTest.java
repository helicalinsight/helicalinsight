package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WhereClauseTest {

	@Test(expected = QueryBuilderException.class)
	public void ut_a1_test_whereClause() {
		new WhereClause();
		SqlQueryContext context = mock(SqlQueryContext.class);
		JsonObject formData = new JsonObject();
		formData.add("filters", new JsonArray());
		when(context.getFormData()).thenReturn(formData);
		
		new WhereClause(context);
		
	}
	
	@Test
	public void ut_a2_test_whereClause() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		WhereClause whereClause = new WhereClause();
		List<Filter> filterList = new ArrayList<>();
		JsonArray ignoreFilters = new JsonArray();
		JsonObject ignorableFilter = new JsonObject();
		ignorableFilter.addProperty("id", 12);
		ignoreFilters.add(ignorableFilter);
		
		Field field = WhereClause.class.getDeclaredField("filterList");
		field.setAccessible(true);
		field.set(whereClause, filterList);
		
		Field field1 =  WhereClause.class.getDeclaredField("ignoreFilters");
		field1.setAccessible(true);
		field1.set(whereClause, ignoreFilters);
		
		String where = whereClause.where();
		assertEquals("",where);
	}
	

//	@Test
	public void ut_a3_test_whereClause() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		WhereClause whereClause = new WhereClause();
		Filter filter = mock(Filter.class);
		SqlQueryContext context= mock(SqlQueryContext.class);
		
		List<Filter> filterList = new ArrayList<>();
		filterList.add(filter);
		filterList.add(filter);
		JsonArray ignoreFilters = new JsonArray();
		JsonObject ignorableFilter = new JsonObject();
		ignorableFilter.addProperty("id", 12);
		ignoreFilters.add(ignorableFilter);
		String customExpression = "${0}";
		
		when(filter.getColumn()).thenReturn("column");
		when(filter.isIgnore()).thenReturn(true).thenReturn(false);
		
		Field field = WhereClause.class.getDeclaredField("filterList");
		field.setAccessible(true);
		field.set(whereClause, filterList);
		
		Field field1 =  WhereClause.class.getDeclaredField("ignoreFilters");
		field1.setAccessible(true);
		field1.set(whereClause, ignoreFilters);
		
		Field field2 =  WhereClause.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(whereClause, context);
		
		Field field3 =  WhereClause.class.getDeclaredField("customExpression");
		field3.setAccessible(true);
		field3.set(whereClause, customExpression);
		
		String where = whereClause.where();
		assertEquals("",where);
	}
	
	@Test
	public void ut_a4_test_whereClause() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		WhereClause whereClause = new WhereClause();
		Filter filter = mock(Filter.class);
		SqlQueryContext context= mock(SqlQueryContext.class);
		
		List<Filter> filterList = new ArrayList<>();
		filterList.add(filter);
		filterList.add(filter);
		JsonArray ignoreFilters = new JsonArray();
		JsonObject ignorableFilter = new JsonObject();
		ignorableFilter.addProperty("id", 12);
		ignoreFilters.add(ignorableFilter);
		String customExpression = "";
		
		when(filter.getColumn()).thenReturn("column");
		when(filter.isIgnore()).thenReturn(true).thenReturn(false);
		
		Field field = WhereClause.class.getDeclaredField("filterList");
		field.setAccessible(true);
		field.set(whereClause, filterList);
		
		Field field1 =  WhereClause.class.getDeclaredField("ignoreFilters");
		field1.setAccessible(true);
		field1.set(whereClause, ignoreFilters);
		
		Field field2 =  WhereClause.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(whereClause, context);
		
		Field field3 =  WhereClause.class.getDeclaredField("customExpression");
		field3.setAccessible(true);
		field3.set(whereClause, customExpression);
		
	
		String where = whereClause.where();
		assertEquals("",where);
	}
}

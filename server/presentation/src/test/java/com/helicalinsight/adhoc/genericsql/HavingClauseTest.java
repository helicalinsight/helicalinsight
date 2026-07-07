package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HavingClauseTest {

	@Test(expected = QueryBuilderException.class)
	public void ut_a1_test_HavingClause() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		JsonObject formData = new JsonObject();
		formData.add("having", new JsonArray());
		when(context.getFormData()).thenReturn(formData);
		new HavingClause(context);
	}

	@Test
	public void ut_a2_test_HavingClause() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("customHavingExpression", "");
		JsonArray jsonArray = new JsonArray();
		JsonObject filterJsonItem = new JsonObject();
		filterJsonItem.addProperty("column", "column");
		filterJsonItem.addProperty("ignore", "true");
		filterJsonItem.addProperty("function", "function");
		filterJsonItem.addProperty("condition", "condition");
		filterJsonItem.addProperty("dataType", "java.lang.Object");
		filterJsonItem.addProperty("isCustomValue", true);

		jsonArray.add(filterJsonItem);
		formData.add("having", jsonArray);
		when(context.getFormData()).thenReturn(formData);

		try (MockedStatic<SqlQueryUtilities> mockedStatic = mockStatic(SqlQueryUtilities.class)) {
			mockedStatic.when(() -> SqlQueryUtilities.condition(any(), any())).thenReturn("actualCondition");
			new HavingClause(context);
		}

	}

	@Test(expected = QueryBuilderException.class)
	public void ut_a3_test_HavingClause() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("customHavingExpression", "");
		JsonArray jsonArray = new JsonArray();
		JsonObject filterJsonItem = new JsonObject();
		filterJsonItem.addProperty("column", "column");
		filterJsonItem.addProperty("ignore", "true");
		filterJsonItem.addProperty("function", "function");
		filterJsonItem.addProperty("condition", "condition");
		filterJsonItem.addProperty("dataType", "java.lang.Object");
		filterJsonItem.addProperty("isCustomValue", true);

		jsonArray.add(filterJsonItem);
		formData.add("having", jsonArray);
		when(context.getFormData()).thenReturn(formData);

		
		new HavingClause(context);

	}

	@Test
	public void ut_a4_test_HavingClause() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("customHavingExpression", "");
		JsonArray jsonArray = new JsonArray();
		JsonObject filterJsonItem = new JsonObject();
		filterJsonItem.addProperty("column", "column");
		filterJsonItem.addProperty("ignore", "true");
		filterJsonItem.addProperty("function", "function");
		filterJsonItem.addProperty("condition", "condition");
		filterJsonItem.addProperty("dataType", "java.lang.Object");
		filterJsonItem.addProperty("isCustomValue", true);
		filterJsonItem.addProperty("id", 123);
		jsonArray.add(filterJsonItem);
		formData.add("having", jsonArray);
		when(context.getFormData()).thenReturn(formData);

		try (MockedStatic<SqlQueryUtilities> mockedStatic = mockStatic(SqlQueryUtilities.class)) {
			mockedStatic.when(() -> SqlQueryUtilities.condition(any(), any())).thenReturn("actualCondition");
			HavingClause havingClause = new HavingClause(context);
			String having = havingClause.having();
			assertTrue(having.isEmpty());
		}

	}

	
}

package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.mockito.stubbing.Answer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CustomColumnFilterTest {

	@Test
	public void whereInlinesNestedCustomFormulaNotAlias() {
		SqlQueryContext context = mockContext();
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());
		formData.add("filters", filterArray(nestedCustomFilter("\"booking_platform\"", "bk_pf",
				"'Makemytrip')", "java.lang.String")));
		formData.addProperty("customFilterExpression", " ${0} ");
		when(context.getFormData()).thenReturn(formData);

		String where = new WhereClause(context).where();
		assertTrue(where.contains("\"booking_platform\""));
		assertFalse(where.contains("\"bk_pf\""));
		assertTrue(where.contains("Makemytrip"));
	}

	@Test
	public void whereWorksWhenCustomColumnIsAlsoInSelect() {
		SqlQueryContext context = mockContext();
		JsonObject formData = formDataWithSelectCustom("\"booking_platform\"", "bk_pf", false);
		formData.add("filters", filterArray(nestedCustomFilter("\"booking_platform\"", "bk_pf",
				"'Makemytrip')", "java.lang.String")));
		formData.addProperty("customFilterExpression", " ${0} ");
		when(context.getFormData()).thenReturn(formData);

		String where = new WhereClause(context).where();
		assertTrue(where.contains("\"booking_platform\""));
		assertFalse(where.contains("\"bk_pf\""));
	}

	@Test
	public void havingWrapsSumAroundNestedCustomFormula() {
		SqlQueryContext context = mockContext();
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());
		JsonObject havingItem = nestedCustomFilter("\"source_id\"", "s_id", "501501)", "java.lang.Integer");
		havingItem.addProperty("function", "sum");
		formData.add("having", filterArray(havingItem));
		formData.addProperty("customHavingExpression", " ${0} ");
		when(context.getFormData()).thenReturn(formData);

		String having = new HavingClause(context).having();
		assertTrue(having.contains("sum(\"source_id\")"));
		assertFalse(having.contains("sum(\"s_id\")"));
		assertFalse(having.contains("sum(sum("));
		assertFalse(having.contains("\"s_id\""));
	}

	@Test
	public void typicalFormData_whereUsesCustomFormulaAndDatabaseFunction() {
		SqlQueryContext context = mockContext();
		JsonObject formData = prepareForQueryBuilder(typicalFormData());
		when(context.getFormData()).thenReturn(formData);
		when(context.databaseFunction(any(JsonObject.class))).thenAnswer((Answer<String>) invocation -> {
			JsonObject item = invocation.getArgument(0);
			JsonObject databaseFunction = item.getAsJsonObject("databaseFunction");
			if (databaseFunction != null && "sql.text.concat".equals(databaseFunction.get("functionName").getAsString())) {
				return "concat(\"source\",\"destination\")";
			}
			return "db_fn()";
		});

		String where = new WhereClause(context).where();
		assertTrue(where.contains("\"booking_platform\""));
		assertFalse(where.contains("\"bk_pf\""));
		assertTrue(where.contains("Makemytrip"));
		assertTrue(where.contains("concat(\"source\",\"destination\")"));
		assertTrue(where.contains("AgraMumbai"));
		assertTrue(where.contains("Credit"));
	}

	@Test
	public void typicalFormData_havingUsesCustomFormulaWithAggregateFunction() {
		SqlQueryContext context = mockContext();
		JsonObject formData = prepareForQueryBuilder(typicalFormData());
		when(context.getFormData()).thenReturn(formData);

		String having = new HavingClause(context).having();
		assertTrue(having.contains("sum(\"source_id\")"));
		assertFalse(having.contains("sum(\"s_id\")"));
		assertFalse(having.contains("sum(sum("));
		assertFalse(having.contains("\"s_id\""));
		assertTrue(having.contains("sum("));
		assertTrue(having.contains("travel_id"));
		assertTrue(having.contains("501501"));
	}

	@Test
	public void whereCustomWithDatabaseFunctionUsesFunctionSqlNotAlias() {
		SqlQueryContext context = mockContext();
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());
		JsonObject filter = nestedCustomFilter("\"meeting_date\"", "mtg_yr", "2015)", "java.lang.Integer");
		JsonObject databaseFunction = new JsonObject();
		databaseFunction.addProperty("functionName", "sql.dateTime.year");
		databaseFunction.addProperty("dataType", "numeric");
		JsonObject parameters = new JsonObject();
		parameters.addProperty("datetime", "meeting_details.meeting_date");
		databaseFunction.add("parameters", parameters);
		filter.add("databaseFunction", databaseFunction);
		formData.add("filters", filterArray(filter));
		formData.addProperty("customFilterExpression", " ${0} ");
		when(context.getFormData()).thenReturn(formData);
		when(context.databaseFunction(any(JsonObject.class))).thenReturn("year(\"HIUSER\".\"meeting_details\".\"meeting_date\")");

		String where = new WhereClause(context).where();
		assertTrue(where.contains("year(\"HIUSER\".\"meeting_details\".\"meeting_date\")"));
		assertFalse(where.contains("\"mtg_yr\""));
		assertTrue(where.contains("2015"));
	}

	@Test
	public void havingCustomWithDatabaseFunctionThenAggregate() {
		SqlQueryContext context = mockContext();
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());
		JsonObject havingItem = nestedCustomFilter("\"source_id\"", "s_id", "501501)", "java.lang.Integer");
		havingItem.addProperty("function", "sum");
		JsonObject databaseFunction = new JsonObject();
		databaseFunction.addProperty("functionName", "sql.numeric.abs");
		databaseFunction.addProperty("dataType", "numeric");
		JsonObject parameters = new JsonObject();
		parameters.addProperty("number", "source_id");
		databaseFunction.add("parameters", parameters);
		havingItem.add("databaseFunction", databaseFunction);
		formData.add("having", filterArray(havingItem));
		formData.addProperty("customHavingExpression", " ${0} ");
		when(context.getFormData()).thenReturn(formData);
		when(context.databaseFunction(any(JsonObject.class))).thenReturn("abs(\"source_id\")");

		String having = new HavingClause(context).having();
		assertTrue(having.contains("sum(abs(\"source_id\"))"));
		assertFalse(having.contains("\"s_id\""));
	}

	@Test
	public void filterToStringDoesNotQuoteCustomFormulaWithoutParentheses() {
		SqlQueryContext context = mockContext();
		Filter filter = new Filter("\"booking_platform\"", " IN (", true, "java.lang.String", "true",
				Collections.singletonList("'Makemytrip')"), true, context, false);
		String sql = filter.toString();
		assertTrue(sql.startsWith("\"booking_platform\""));
		assertFalse(sql.startsWith("\"\"booking_platform\"\""));
	}

	@Test(expected = QueryBuilderException.class)
	public void nestedCustomWithoutInnerFormulaIsRejected() {
		SqlQueryContext context = mockContext();
		JsonObject formData = new JsonObject();
		JsonObject filter = nestedCustomFilter("\"booking_platform\"", "bk_pf", "'x')", "java.lang.String");
		JsonObject column = new JsonObject();
		column.addProperty("alias", "bk_pf");
		column.addProperty("custom", true);
		filter.add("column", column);
		formData.add("filters", filterArray(filter));
		formData.addProperty("customFilterExpression", " ${0} ");
		when(context.getFormData()).thenReturn(formData);

		new WhereClause(context);
	}

	private static SqlQueryContext mockContext() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		when(context.getDerivedTableColumns()).thenReturn(new ArrayList<String>());
		when(context.quotes(anyString())).thenAnswer((Answer<String>) invocation -> {
			String column = invocation.getArgument(0);
			return "\"" + column + "\"";
		});
		return context;
	}

	private static JsonObject formDataWithSelectCustom(String expression, String alias, boolean aggregate) {
		JsonObject formData = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject customColumn = new JsonObject();
		customColumn.addProperty("column", expression);
		customColumn.addProperty("alias", alias);
		customColumn.addProperty("custom", true);
		if (aggregate) {
			customColumn.addProperty("aggregate", true);
		}
		columns.add(customColumn);
		formData.add("columns", columns);
		return formData;
	}

	private static JsonArray filterArray(JsonObject item) {
		JsonArray array = new JsonArray();
		array.add(item);
		return array;
	}

	private static JsonObject nestedCustomFilter(String formula, String alias, String value, String dataType) {
		JsonObject item = new JsonObject();
		JsonArray values = new JsonArray();
		values.add(value);
		item.add("values", values);
		item.addProperty("mode", "auto");
		item.addProperty("operator", "AND");
		item.addProperty("dataType", dataType);
		item.addProperty("customCondition", " IN (");
		item.addProperty("encloseInQuotes", false);
		item.addProperty("alias", alias);
		item.addProperty("label", alias);
		item.addProperty("isCustomValue", true);
		item.addProperty("custom", true);
		JsonObject nested = new JsonObject();
		nested.addProperty("column", formula);
		nested.addProperty("alias", alias);
		item.add("column", nested);
		item.addProperty("id", 0);
		item.addProperty("condition", "CUSTOM");
		return item;
	}

	/**
	 * Same shape as HReportTest sample metadata, plus nested custom filter/having.
	 * Regular columns keep {name, id}. Custom items use item-level custom:true and nested {column, alias}.
	 */
	private static JsonObject typicalFormData() {
		return JsonParser.parseString("""
				{
					"location": "HReportTest",
					"metadataFileName": "Metadata_HReportTest.metadata",
					"columns": [
						{
							"column": {
								"name": "HIUSER.travel_details.mode_of_payment",
								"id": "1073"
							},
							"alias": "payment mode",
							"floatingType": "discrete"
						},
						{
							"column": "\\"booking_platform\\"",
							"alias": "bk_pf",
							"custom": true,
							"floatingType": "discrete"
						},
						{
							"column": "\\"source_id\\"",
							"alias": "s_id",
							"custom": true,
							"aggregate": true,
							"aggregateList": [
								"db.generic.aggregate.sum"
							],
							"floatingType": "discrete"
						}
					],
					"functions": {
						"aggregate": [
							{
								"column": "\\"source_id\\"",
								"function": "db.generic.aggregate.sum",
								"alias": "s_id",
								"custom": true
							}
						],
						"groupBy": [
							{
								"column": "payment mode",
								"custom": true
							},
							{
								"column": "bk_pf",
								"custom": true
							}
						]
					},
					"filters": [
						{
							"values": [
								"'Credit')"
							],
							"mode": "auto",
							"operator": "AND",
							"dataType": "java.lang.String",
							"customCondition": " IN (",
							"encloseInQuotes": false,
							"alias": "payment mode",
							"label": "payment mode",
							"isCustomValue": true,
							"column": {
								"name": "HIUSER.travel_details.mode_of_payment",
								"id": "1073"
							},
							"id": 0,
							"condition": "CUSTOM"
						},
						{
							"values": [
								"'Makemytrip')"
							],
							"mode": "auto",
							"operator": "AND",
							"dataType": "java.lang.String",
							"customCondition": " IN (",
							"encloseInQuotes": false,
							"alias": "bk_pf",
							"label": "bk_pf",
							"isCustomValue": true,
							"custom": true,
							"column": {
								"column": "\\"booking_platform\\"",
								"alias": "bk_pf"
							},
							"id": 1,
							"condition": "CUSTOM"
						},
						{
							"values": [
								"'AgraMumbai')"
							],
							"mode": "auto",
							"operator": "AND",
							"dataType": "java.lang.String",
							"customCondition": " IN (",
							"encloseInQuotes": false,
							"alias": "sr-dest",
							"databaseFunction": {
								"functionName": "sql.text.concat",
								"dataType": "text",
								"parameters": {
									"string1": "travel_details.source",
									"string2": "travel_details.destination"
								}
							},
							"label": "sr-dest",
							"isCustomValue": true,
							"column": {
								"name": "HIUSER.travel_details.source",
								"id": "1069"
							},
							"id": 2,
							"condition": "CUSTOM"
						}
					],
					"having": [
						{
							"values": [
								"501501)"
							],
							"mode": "auto",
							"operator": "AND",
							"dataType": "java.lang.Integer",
							"customCondition": " IN (",
							"encloseInQuotes": false,
							"alias": "sum_travel_id",
							"label": "sum_travel_id",
							"isCustomValue": true,
							"column": {
								"name": "HIUSER.travel_details.travel_id",
								"id": "1064"
							},
							"function": "db.generic.aggregate.sum",
							"id": 1,
							"condition": "CUSTOM"
						},
						{
							"values": [
								"10)"
							],
							"mode": "auto",
							"operator": "AND",
							"dataType": "java.lang.Integer",
							"customCondition": " IN (",
							"encloseInQuotes": false,
							"alias": "s_id",
							"label": "s_id",
							"isCustomValue": true,
							"custom": true,
							"column": {
								"column": "\\"source_id\\"",
								"alias": "s_id"
							},
							"function": "db.generic.aggregate.sum",
							"id": 2,
							"condition": "CUSTOM"
						}
					],
					"customFilterExpression": " ${0} AND ${1} AND ${2} ",
					"customHavingExpression": " ${0} AND ${1} ",
					"limitBy": 10,
					"prependTableNameToAlias": false
				}
				""").getAsJsonObject();
	}

	private static JsonObject prepareForQueryBuilder(JsonObject formData) {
		flattenMetadataColumns(formData.getAsJsonArray("filters"));
		flattenMetadataColumns(formData.getAsJsonArray("having"));
		flattenMetadataColumns(formData.getAsJsonArray("columns"));
		return formData;
	}

	private static void flattenMetadataColumns(JsonArray array) {
		if (array == null) {
			return;
		}
		for (JsonElement element : array) {
			JsonObject item = element.getAsJsonObject();
			if (item.has("function")) {
				String function = item.get("function").getAsString();
				int lastDot = function.lastIndexOf('.');
				if (lastDot >= 0) {
					item.addProperty("function", function.substring(lastDot + 1));
				}
			}
			JsonElement column = item.get("column");
			if (column == null || !column.isJsonObject()) {
				continue;
			}
			if (item.has("custom")) {
				continue;
			}
			JsonObject columnObject = column.getAsJsonObject();
			if (columnObject.has("name")) {
				item.addProperty("column", columnObject.get("name").getAsString());
			}
		}
	}
}

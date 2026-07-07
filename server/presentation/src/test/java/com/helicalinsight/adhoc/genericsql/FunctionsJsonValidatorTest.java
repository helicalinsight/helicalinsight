package com.helicalinsight.adhoc.genericsql;

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
public class FunctionsJsonValidatorTest {

	@Test(expected = QueryBuilderException.class)
	public void ut_a1_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
	
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(null);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	@Test(expected = QueryBuilderException.class)
	public void ut_a2_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("functions", "functions");
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(null);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a3_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		functions.add("groupBy", groupBy);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(null);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a4_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(null);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a5_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", "column");
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		List<String> list = new ArrayList<>();
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(list);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	@Test(expected = QueryBuilderException.class)
	public void ut_a6_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", "column");
		json.addProperty("function", "f_name");
		
		
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		JsonArray aggregate = new JsonArray();
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(list);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test
	public void ut_a7_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", "column");
		json.addProperty("function", "db.generic.aggregate.count");
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		JsonArray aggregate = new JsonArray();
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(list);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a8_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray usedColumns = new JsonArray();
		usedColumns.add("usedColumns");
		json.add("usedColumns", usedColumns);
		json.addProperty("column", "column");
		json.addProperty("function", "db.generic.aggregate.count_db.generic.groupBy.group");
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		JsonArray orderBy = new JsonArray();
		JsonObject json2 = new JsonObject();
		orderBy.add(json2);
		functions.add("orderBy", orderBy);
		JsonArray aggregate = new JsonArray();
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		list.add("usedColumns");
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(list);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_a9_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray usedColumns = new JsonArray();
		usedColumns.add("usedColumns");
		json.add("usedColumns", usedColumns);
		json.addProperty("column", "column");
		json.addProperty("function", "db.generic.aggregate.count_db.generic.groupBy.group");
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		JsonArray orderBy = new JsonArray();
		JsonObject json2 = new JsonObject();
		json2.addProperty("alias", "alias");
		json2.addProperty("column", "column");
		json2.addProperty("order", "ascending");
		orderBy.add(json2);
		functions.add("orderBy", orderBy);
		JsonArray aggregate = new JsonArray();
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		list.add("usedColumns");
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(list);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
	
	@Test
	public void ut_b1_test_validateJson() {
		JsonObject formDataJson = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray usedColumns = new JsonArray();
		usedColumns.add("usedColumns");
		json.add("usedColumns", usedColumns);
		json.addProperty("column", "column");
		json.addProperty("function", "db.generic.aggregate.count_db.generic.groupBy.group");
		groupBy.add(json);
		functions.add("groupBy", groupBy);
		JsonArray orderBy = new JsonArray();
		JsonObject json2 = new JsonObject();
		json2.addProperty("alias", "column");
		json2.addProperty("column", "column");
		orderBy.add(json2);
		functions.add("orderBy", orderBy);
		JsonArray aggregate = new JsonArray();
		aggregate.add(json);
		functions.add("aggregate", aggregate);
		formDataJson.add("functions", functions);
		IMetadataStore iMetadataStore = mock(IMetadataStore.class);
		List<String> list = new ArrayList<>();
		list.add("column");
		list.add("usedColumns");
		when(iMetadataStore.getFullyQualifiedColumnsList()).thenReturn(list);
		FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, iMetadataStore);
		functionsJsonValidator.validateJson();
	}
}

package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkflowMongoTemplateTest {

	@Test
	public void ut_a1_test_getDatabase() {
		WorkflowMongoTemplate mongoTemplate = new WorkflowMongoTemplate();
		WorkflowDatabaseTemplate databaseTemplate = mock(WorkflowDatabaseTemplate.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		
		JsonObject formData = new JsonObject();
		JsonObject singleCatalog = new JsonObject();
		singleCatalog.addProperty("catalog", "catalog");
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "s_name");
		JsonArray tablesJson = new JsonArray();
		schemaJson.add("tables", tablesJson);
		schemas.add(schemaJson);
		singleCatalog.add("schemas", schemas);
		formData.add("singleCatalog", singleCatalog);
		
		List<Table> listOfTables = new ArrayList<>();
		when(databaseTemplate.getListOfTables(any(), any(),any())).thenReturn(listOfTables);
		when(databaseTemplate.getJsonObject(formData)).thenReturn(formData);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()->  ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
			mockedStatic.when(()->  ApplicationContextAccessor.getBean(Tables.class)).thenReturn(tables);
			Database database2 = mongoTemplate.getDatabase(formData);
			assertEquals(database, database2);
			
		}
	}
	
	@Test(expected = EfwServiceException.class)
	public void ut_a2_test_getDatabase() {
		WorkflowMongoTemplate mongoTemplate = new WorkflowMongoTemplate();
		WorkflowDatabaseTemplate databaseTemplate = mock(WorkflowDatabaseTemplate.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		
		JsonObject formData = new JsonObject();
		JsonObject singleCatalog = new JsonObject();
		singleCatalog.addProperty("catalog", "catalog");
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "s_name");
		JsonArray tablesJson = new JsonArray();
		schemaJson.add("tables", tablesJson);
		
		singleCatalog.add("schemas", schemas);
		formData.add("singleCatalog", singleCatalog);
		
		List<Table> listOfTables = new ArrayList<>();
		when(databaseTemplate.getListOfTables(any(), any(),any())).thenReturn(listOfTables);
		when(databaseTemplate.getJsonObject(formData)).thenReturn(formData);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()->  ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
			mockedStatic.when(()->  ApplicationContextAccessor.getBean(Tables.class)).thenReturn(tables);
			mongoTemplate.getDatabase(formData);
			
		}
	}
}

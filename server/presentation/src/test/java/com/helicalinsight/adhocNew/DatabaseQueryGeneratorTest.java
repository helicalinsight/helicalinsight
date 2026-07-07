package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.AbstractSecurityRules;
import com.helicalinsight.adhoc.DatabaseQueryGenerator;
import com.helicalinsight.adhoc.genericsql.IQueryGenerator;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JaxbUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseQueryGeneratorTest {

	@Test(expected = ConfigurationException.class)
	public void ut_a1_test_executeComponent() {
		DatabaseQueryGenerator databaseQueryGenerator = new DatabaseQueryGenerator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("queryGeneratorImplementation", "queryGeneratorImplementation");
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("fetchMode", "cache");
		JsonObject driverClass = new JsonObject();
		connectionDetails.add("driverClass", driverClass);
		metadataFileJson.add("connectionDetails", connectionDetails);
		metadataFileJson.addProperty("isCached", true);
		JsonObject database = new JsonObject();
		JsonObject tables = new JsonObject();
		JsonArray tableList = new JsonArray();
		JsonObject table = new JsonObject();
		table.addProperty("name", "t-Name.metadata");
		table.addProperty("aliasName", "aName");

		tableList.add(table);
		tables.add("tableList", tableList);
		database.add("tables", tables);
		metadataFileJson.add("database", database);
		JsonArray columns = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("column", "column-Name.metadata");
		columns.add(object);
		formDataJson.add("columns", columns);
		formDataJson.add("metadataFileJson", metadataFileJson);
		JsonObject functions = new JsonObject();
		JsonArray aggregateArray = new JsonArray();
		aggregateArray.add(object);
		functions.add("aggregate", aggregateArray);
		formDataJson.add("functions", functions);
		formDataJson.add("filters", aggregateArray);
		formDataJson.add("having", aggregateArray);

		try (MockedStatic<AppStatistics> mockedStatic = mockStatic(AppStatistics.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				mockedStatic2.when(() -> FactoryMethodWrapper.getTypedInstance("queryGeneratorImplementation",
						IQueryGenerator.class)).thenReturn(null);

				mockedStatic.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
				mockedStatic.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(true);

				databaseQueryGenerator.executeComponent(formDataJson.toString());
			}
		}
	}

	@Test
	public void ut_a2_test_executeComponent() throws Exception {
		DatabaseQueryGenerator databaseQueryGenerator = new DatabaseQueryGenerator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("queryGeneratorImplementation", "queryGeneratorImplementation");
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("fetchMode", "cache");
		JsonObject driverClass = new JsonObject();
		connectionDetails.add("driverClass", driverClass);
		metadataFileJson.add("connectionDetails", connectionDetails);
		metadataFileJson.addProperty("isCached", true);
		JsonObject database = new JsonObject();
		JsonObject tables = new JsonObject();
		JsonArray tableList = new JsonArray();
		JsonObject table = new JsonObject();
		table.addProperty("name", "t-Name.metadata");
		table.addProperty("aliasName", "aName");

		tableList.add(table);
		tables.add("tableList", tableList);
		database.add("tables", tables);
		metadataFileJson.add("database", database);
		JsonArray columns = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("column", "column-Name.metadata");
		columns.add(object);
		formDataJson.add("columns", columns);
		formDataJson.add("metadataFileJson", metadataFileJson);
		JsonObject functions = new JsonObject();
		JsonArray aggregateArray = new JsonArray();
		aggregateArray.add(object);
		functions.add("aggregate", aggregateArray);
		formDataJson.add("functions", functions);
		formDataJson.add("filters", aggregateArray);
		formDataJson.add("having", aggregateArray);
		formDataJson.addProperty("classifier", "classifier");
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.key", "value");
		
		
		IQueryGenerator queryGenerator = mock(IQueryGenerator.class);
		when(queryGenerator.prepareQuery(anyString(), anyString())).thenReturn("query");

		AbstractSecurityRules rules = mock(AbstractSecurityRules.class);
		when(rules.checkSqlInjection(any(JsonObject.class), any(String.class), any(Object.class))).thenReturn(formDataJson);
		Metadata metadata = mock(Metadata.class);
		try (MockedStatic<AppStatistics> mockedStatic = mockStatic(AppStatistics.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				try (MockedStatic<ApplicationUtilities> mockedStatic3 = mockStatic(ApplicationUtilities.class)) {
					try(MockedStatic<AbstractSecurityRules> mockedStatic4 = mockStatic(AbstractSecurityRules.class)){
						try(MockedStatic<JaxbUtils> mockedStatic5 = mockStatic(JaxbUtils.class)){
							mockedStatic5.when(()-> JaxbUtils.jsonStringToObject(any(),any())).thenReturn(metadata);
						
					mockedStatic4.when(()-> AbstractSecurityRules.getAdhocSecurityRulesClass(anyString())).thenReturn("className");	
					
					mockedStatic3.when(() -> ApplicationUtilities.getDefaultsMap()).thenReturn(propsMap);
					mockedStatic3.when(() -> ApplicationUtilities.isClass(anyString())).thenReturn(true);
					
					mockedStatic2.when(() -> FactoryMethodWrapper.getTypedInstance("queryGeneratorImplementation",
							IQueryGenerator.class)).thenReturn(queryGenerator);
					mockedStatic2.when(() -> FactoryMethodWrapper.getUntypedInstance(anyString())).thenReturn(rules);
					mockedStatic.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
					mockedStatic.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(true);
					
					String executeComponent = databaseQueryGenerator.executeComponent(formDataJson.toString());
					JsonObject response = JsonParser.parseString(executeComponent).getAsJsonObject();		
					assertEquals("classifier",response.get("classifier").getAsString());
					
						}
					}
				}
			}
		}
	}

	
	@Test(expected = SecurityException.class)
	public void ut_a3_test_executeComponent() throws Exception {
		DatabaseQueryGenerator databaseQueryGenerator = new DatabaseQueryGenerator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("queryGeneratorImplementation", "queryGeneratorImplementation");
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("fetchMode", "cache");
		JsonObject driverClass = new JsonObject();
		connectionDetails.add("driverClass", driverClass);
		metadataFileJson.add("connectionDetails", connectionDetails);
		metadataFileJson.addProperty("isCached", true);
		JsonObject database = new JsonObject();
		JsonObject tables = new JsonObject();
		JsonArray tableList = new JsonArray();
		JsonObject table = new JsonObject();
		table.addProperty("name", "t-Name.metadata");
		table.addProperty("aliasName", "aName");

		tableList.add(table);
		tables.add("tableList", tableList);
		database.add("tables", tables);
		metadataFileJson.add("database", database);
		JsonArray columns = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("column", "column-Name.metadata");
		columns.add(object);
		formDataJson.add("columns", columns);
		formDataJson.add("metadataFileJson", metadataFileJson);
		JsonObject functions = new JsonObject();
		JsonArray aggregateArray = new JsonArray();
		aggregateArray.add(object);
		functions.add("aggregate", aggregateArray);
		formDataJson.add("functions", functions);
		formDataJson.add("filters", aggregateArray);
		formDataJson.add("having", aggregateArray);
		formDataJson.addProperty("classifier", "classifier");
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.key", "value");
		
		
		IQueryGenerator queryGenerator = mock(IQueryGenerator.class);
		when(queryGenerator.prepareQuery(anyString(), anyString())).thenReturn("query");

		AbstractSecurityRules rules = mock(AbstractSecurityRules.class);
		when(rules.checkSqlInjection(any(JsonObject.class), any(String.class), any(Object.class))).thenThrow(new Exception());
		Metadata metadata = mock(Metadata.class);
		try (MockedStatic<AppStatistics> mockedStatic = mockStatic(AppStatistics.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				try (MockedStatic<ApplicationUtilities> mockedStatic3 = mockStatic(ApplicationUtilities.class)) {
					try(MockedStatic<AbstractSecurityRules> mockedStatic4 = mockStatic(AbstractSecurityRules.class)){
						try(MockedStatic<JaxbUtils> mockedStatic5 = mockStatic(JaxbUtils.class)){
							mockedStatic5.when(()-> JaxbUtils.jsonStringToObject(any(),any())).thenReturn(metadata);
						
					mockedStatic4.when(()-> AbstractSecurityRules.getAdhocSecurityRulesClass(anyString())).thenReturn("className");	
					
					mockedStatic3.when(() -> ApplicationUtilities.getDefaultsMap()).thenReturn(propsMap);
					mockedStatic3.when(() -> ApplicationUtilities.isClass(anyString())).thenReturn(true);
					
					mockedStatic2.when(() -> FactoryMethodWrapper.getTypedInstance("queryGeneratorImplementation",
							IQueryGenerator.class)).thenReturn(queryGenerator);
					mockedStatic2.when(() -> FactoryMethodWrapper.getUntypedInstance(anyString())).thenReturn(rules);
					mockedStatic.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
					mockedStatic.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(true);

					databaseQueryGenerator.executeComponent(formDataJson.toString());
						}
					}
				}
			}
		}
	}
	
	@Test
	public void ut_a4_test_executeComponent() throws Exception {
		DatabaseQueryGenerator databaseQueryGenerator = new DatabaseQueryGenerator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("queryGeneratorImplementation", "queryGeneratorImplementation");
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("fetchMode", "cache");
		JsonObject driverClass = new JsonObject();
		connectionDetails.add("driverClass", driverClass);
		metadataFileJson.add("connectionDetails", connectionDetails);
		metadataFileJson.addProperty("isCached", true);
		JsonObject database = new JsonObject();
		JsonObject tables = new JsonObject();
		JsonArray tableList = new JsonArray();
		JsonObject table = new JsonObject();
		table.addProperty("name", "t-Name.metadata");
		table.addProperty("aliasName", "aName");

		tableList.add(table);
		tables.add("tableList", tableList);
		database.add("tables", tables);
		metadataFileJson.add("database", database);
		JsonArray columns = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("column", "column-Name.metadata");
		columns.add(object);
		formDataJson.add("columns", columns);
		formDataJson.add("metadataFileJson", metadataFileJson);
		JsonObject functions = new JsonObject();
		JsonArray aggregateArray = new JsonArray();
		aggregateArray.add(object);
		functions.add("aggregate", aggregateArray);
		formDataJson.add("functions", functions);
		formDataJson.add("filters", aggregateArray);
		formDataJson.add("having", aggregateArray);
		formDataJson.addProperty("classifier", "classifier");
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.key", "value");
		
		
		IQueryGenerator queryGenerator = mock(IQueryGenerator.class);
		when(queryGenerator.prepareQuery(anyString(), anyString())).thenReturn("query");

		AbstractSecurityRules rules = mock(AbstractSecurityRules.class);
		when(rules.checkSqlInjection(any(JsonObject.class), any(String.class), any(Object.class))).thenReturn(formDataJson);
		Metadata metadata = mock(Metadata.class);
		try (MockedStatic<AppStatistics> mockedStatic = mockStatic(AppStatistics.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				try (MockedStatic<ApplicationUtilities> mockedStatic3 = mockStatic(ApplicationUtilities.class)) {
					try(MockedStatic<AbstractSecurityRules> mockedStatic4 = mockStatic(AbstractSecurityRules.class)){
						try(MockedStatic<JaxbUtils> mockedStatic5 = mockStatic(JaxbUtils.class)){
							mockedStatic5.when(()-> JaxbUtils.jsonStringToObject(any(),any())).thenReturn(metadata);
						
					mockedStatic4.when(()-> AbstractSecurityRules.getAdhocSecurityRulesClass(anyString())).thenReturn("className");	
					
					mockedStatic3.when(() -> ApplicationUtilities.getDefaultsMap()).thenReturn(propsMap);
					mockedStatic3.when(() -> ApplicationUtilities.isClass(anyString())).thenReturn(true);
					
					mockedStatic2.when(() -> FactoryMethodWrapper.getTypedInstance("queryGeneratorImplementation",
							IQueryGenerator.class)).thenReturn(queryGenerator);
					mockedStatic2.when(() -> FactoryMethodWrapper.getUntypedInstance(anyString())).thenReturn(rules);
					mockedStatic2.when(() -> FactoryMethodWrapper.getClass(anyString())).thenThrow(new ClassNotFoundException());
					
					mockedStatic.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
					mockedStatic.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(true);

					databaseQueryGenerator.executeComponent(formDataJson.toString());
						}
					}
				}
			}
		}
	}
	
	@Test
	public void ut_a5_test_isThreadSafeToCache() {
		DatabaseQueryGenerator databaseQueryGenerator = new DatabaseQueryGenerator();
		boolean threadSafeToCache = databaseQueryGenerator.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}

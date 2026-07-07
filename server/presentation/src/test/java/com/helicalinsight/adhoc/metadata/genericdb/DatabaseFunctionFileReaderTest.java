package com.helicalinsight.adhoc.metadata.genericdb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.DatabaseFunctions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseFunctionFileReaderTest {

	@Test
	public void ut_a1_test_addDatabaseSpecificFunctions() {
		DatabaseFunctionFileReader databaseFunctionFileReader = new DatabaseFunctionFileReader();
		JsonObject response = new JsonObject();
		DatabaseFunctions databaseFunctions = mock(DatabaseFunctions.class);
		JsonArray groups = null;

		databaseFunctionFileReader.addDatabaseSpecificFunctions(response, databaseFunctions, groups);

	}

	@Test
	public void ut_a2_test_addDatabaseSpecificFunctions() {
		DatabaseFunctionFileReader databaseFunctionFileReader = new DatabaseFunctionFileReader();
		JsonObject response = new JsonObject();
		JsonArray groups = new JsonArray();
		groups.add("group");
		DatabaseFunctions databaseFunctions = mock(DatabaseFunctions.class);
		DatabaseFunctions.DbFunction dbFunction = mock(DatabaseFunctions.DbFunction.class);

		List<DatabaseFunctions.DbFunction> listOfDbFunctions = new ArrayList<>();
		listOfDbFunctions.add(dbFunction);

		when(databaseFunctions.getDbFunctions()).thenReturn(listOfDbFunctions);
		when(dbFunction.getGroup()).thenReturn("group");
		databaseFunctionFileReader.addDatabaseSpecificFunctions(response, databaseFunctions, groups);

	}
	
	@Test
	public void ut_a3_test_addDatabaseSpecificFunctions() {
		DatabaseFunctionFileReader databaseFunctionFileReader = new DatabaseFunctionFileReader();
		JsonObject response = new JsonObject();
		JsonArray groups = new JsonArray();
		groups.add("group");
		DatabaseFunctions databaseFunctions = mock(DatabaseFunctions.class);
		DatabaseFunctions.DbFunction dbFunction = mock(DatabaseFunctions.DbFunction.class);
		DatabaseFunctions.Parameters dbFunctionParameters = mock(DatabaseFunctions.Parameters.class);
		DatabaseFunctions.Parameter parameter = mock(DatabaseFunctions.Parameter.class);
		
		List<DatabaseFunctions.DbFunction> listOfDbFunctions = new ArrayList<>();
		listOfDbFunctions.add(dbFunction);
		List<DatabaseFunctions.Parameter> parameterList = new ArrayList<>();
		parameterList.add(parameter);
		
		when(databaseFunctions.getDbFunctions()).thenReturn(listOfDbFunctions);
		when(dbFunction.getGroup()).thenReturn("group");
		when(dbFunction.getDescription()).thenReturn("description");
		when(dbFunction.getReturns()).thenReturn("returns");
		when(dbFunction.getId()).thenReturn("id");
		when(dbFunction.getName()).thenReturn("name");
		when(dbFunction.getParameters()).thenReturn(dbFunctionParameters);
		when(dbFunctionParameters.getParameterList()).thenReturn(parameterList);
		when(parameter.getName()).thenReturn("parameterName");
		when(parameter.getType()).thenReturn("parameterType");
		when(parameter.getColumn()).thenReturn("true");
		when(parameter.getDefaultValue()).thenReturn("defaultValue");
		
		
		databaseFunctionFileReader.addDatabaseSpecificFunctions(response, databaseFunctions, groups);

	}
	
	
	@Test
	public void ut_a4_test_addDatabaseSpecificFunctions() {
		DatabaseFunctionFileReader databaseFunctionFileReader = new DatabaseFunctionFileReader();
		JsonObject response = new JsonObject();
		JsonArray groups = new JsonArray();
		groups.add("group");
		DatabaseFunctions databaseFunctions = mock(DatabaseFunctions.class);
		DatabaseFunctions.DbFunction dbFunction = mock(DatabaseFunctions.DbFunction.class);
		DatabaseFunctions.Parameters dbFunctionParameters = mock(DatabaseFunctions.Parameters.class);
		DatabaseFunctions.Parameter parameter = mock(DatabaseFunctions.Parameter.class);
		
		List<DatabaseFunctions.DbFunction> listOfDbFunctions = new ArrayList<>();
		listOfDbFunctions.add(dbFunction);
		List<DatabaseFunctions.Parameter> parameterList = new ArrayList<>();
		parameterList.add(parameter);
		
		when(databaseFunctions.getDbFunctions()).thenReturn(listOfDbFunctions);
		when(dbFunction.getGroup()).thenReturn("group");
		when(dbFunction.getDescription()).thenReturn("description");
		when(dbFunction.getReturns()).thenReturn("returns");
		when(dbFunction.getId()).thenReturn("id");
		when(dbFunction.getName()).thenReturn("name");
		when(dbFunction.getParameters()).thenReturn(dbFunctionParameters);
		when(dbFunctionParameters.getParameterList()).thenReturn(parameterList);
		when(parameter.getName()).thenReturn("parameterName");
		when(parameter.getType()).thenReturn("parameterType");
		when(parameter.getColumn()).thenReturn("false");
		when(parameter.getDefaultValue()).thenReturn("defaultValue");
		
		
		databaseFunctionFileReader.addDatabaseSpecificFunctions(response, databaseFunctions, groups);

	}
}

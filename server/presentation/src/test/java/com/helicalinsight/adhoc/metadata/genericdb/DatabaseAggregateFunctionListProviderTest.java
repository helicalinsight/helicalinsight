package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonParser;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.SqlQueryUtilities;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.DatabaseFunctions;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseAggregateFunctionListProviderTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		DatabaseAggregateFunctionListProvider listProvider = new DatabaseAggregateFunctionListProvider();
		boolean threadSafeToCache = listProvider.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a2_test_executeComponent() {
		DatabaseAggregateFunctionListProvider listProvider = new DatabaseAggregateFunctionListProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "location");

		listProvider.executeComponent(formJson.toString());

	}

	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a3_test_executeComponent() {
		DatabaseAggregateFunctionListProvider listProvider = new DatabaseAggregateFunctionListProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("metadataFileName", "metadataFileName");
		listProvider.executeComponent(formJson.toString());

	}

	@Test
	public void ut_a4_test_executeComponent() {
		DatabaseAggregateFunctionListProvider listProvider = new DatabaseAggregateFunctionListProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "location");
		formJson.addProperty("metadataFileName", "metadataFileName");
		DriverClass driverClass = mock(DriverClass.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getFetchMode()).thenReturn("cache");
		when(metadata.getCached()).thenReturn(true);

		try (MockedStatic<MetadataDBUtility> mockedStatic = mockStatic(MetadataDBUtility.class)) {
			try (MockedStatic<SqlQueryUtilities> mockedStatic2 = mockStatic(SqlQueryUtilities.class)) {
				mockedStatic2.when(() -> SqlQueryUtilities.driverClass(metadata)).thenReturn(driverClass);

				mockedStatic.when(() -> MetadataDBUtility.getMetadata(anyString(), anyString())).thenReturn(metadata);
				String executeComponent = listProvider.executeComponent(formJson.toString());
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("databaseFunctions"));
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a5_test_executeComponent() {
		DatabaseAggregateFunctionListProvider listProvider = new DatabaseAggregateFunctionListProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "location");
		formJson.addProperty("metadataFileName", "metadataFileName");
		formJson.addProperty("uniqueId", "uniqueId");
		JsonArray groups = new JsonArray();
		formJson.add("groups", groups);
		;
		DriverClass driverClass = mock(DriverClass.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getFetchMode()).thenReturn("cache");
		when(metadata.getCached()).thenReturn(true);

		try (MockedStatic<MetadataDBUtility> mockedStatic = mockStatic(MetadataDBUtility.class)) {
			try (MockedStatic<SqlQueryUtilities> mockedStatic2 = mockStatic(SqlQueryUtilities.class)) {
				mockedStatic2.when(() -> SqlQueryUtilities.driverClass(metadata)).thenReturn(driverClass);

				mockedStatic.when(() -> MetadataDBUtility.getMetadata(anyString(), anyString())).thenReturn(metadata);
				listProvider.executeComponent(formJson.toString());
			
			}
		}
	}

	@Test
	public void ut_a6_test_executeComponent() throws IOException {
		DatabaseAggregateFunctionListProvider listProvider = new DatabaseAggregateFunctionListProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "location");
		formJson.addProperty("metadataFileName", "metadataFileName");
		formJson.addProperty("uniqueId", "uniqueId");
		JsonArray groups = new JsonArray();
		formJson.add("groups", groups);
		DriverClass driverClass = mock(DriverClass.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		 DatabaseFunctions databaseFunctions = mock( DatabaseFunctions.class);
		 
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getFetchMode()).thenReturn("xml");
		when(metadata.getCached()).thenReturn(true);

		String path = TempDirectoryCleaner.getTempDirectory()+File.separator+"uniqueId.metadata";
		File file = new File(path);
		file.createNewFile();
		try (MockedStatic<MetadataDBUtility> mockedStatic = mockStatic(MetadataDBUtility.class)) {
			try (MockedStatic<SqlQueryUtilities> mockedStatic2 = mockStatic(SqlQueryUtilities.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<AppStatistics> mockedStatic4 = mockStatic(AppStatistics.class)) {
						mockedStatic4.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
						mockedStatic4.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(true);

						mockedStatic.when(() -> MetadataDBUtility.getMetadata(anyString(), anyString()))
								.thenReturn(metadata);

						mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(metadata).thenReturn(databaseFunctions);
						mockedStatic2.when(() -> SqlQueryUtilities.driverClass(metadata)).thenReturn(driverClass);
						String executeComponent = listProvider.executeComponent(formJson.toString());
						assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("functions"));
					}
				}
			}
		} finally {
			file.delete();
		}
	}
}

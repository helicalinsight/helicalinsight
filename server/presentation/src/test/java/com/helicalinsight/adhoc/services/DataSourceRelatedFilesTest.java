package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataSourceRelatedFilesTest {

	@Test
	public void ut_a1_test_executeComponent() {
		DataSourceRelatedFiles dataSourceRelatedFiles = new DataSourceRelatedFiles();
		IComponent reportStatsProvider = mock(IComponent.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("dataSourceId", "12");
		formDataJson.addProperty("classifier", "efwd");
		formDataJson.addProperty("location", "location");

		
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray metadataJsonArray = new JsonArray();
		JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("reportPath", "reportPath");
		jsonItem.addProperty("dir", "dir");
		jsonItem.addProperty("reportPath", "reportPath");
		jsonItem.addProperty("lastModified", "lastModified");
		jsonItem.addProperty("title", "title");
		jsonItem.addProperty("logicalPath", "logicalPath");
		jsonItem.addProperty("file", "file");
		metadataJsonArray.add(jsonItem);
		
		when(connectionDetails.getDirectory()).thenReturn("location");
		when(connectionDetails.getConnectionId()).thenReturn("12");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(reportStatsProvider.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		try (MockedStatic<FactoryMethodWrapper> mockedStatic1 = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<ReportsRelatedToMetadata> mockedStatic4 = mockStatic(
							ReportsRelatedToMetadata.class)) {

						mockedStatic4.when(() -> ReportsRelatedToMetadata
								.extractFilesBasedOnMetadataFileName(anyString(), anyString(), any()))
								.thenReturn(allFilesAvailableToLoggedInUser);

						mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(metadata);

						mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(new JsonObject());
						mockedStatic2.when(() -> AdhocServiceUtils.getSpecificExtension(any(), anyString()))
								.thenReturn(metadataJsonArray);
						mockedStatic1.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
								.thenReturn(reportStatsProvider);
						
						String executeComponent = dataSourceRelatedFiles.executeComponent(formDataJson.toString());
						assertFalse(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());
					}
				}
			}
		}

	}

	
	@Test
	public void ut_a2_test_executeComponent() {
		DataSourceRelatedFiles dataSourceRelatedFiles = new DataSourceRelatedFiles();
		IComponent reportStatsProvider = mock(IComponent.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("dataSourceId", "12");
		formDataJson.addProperty("classifier", "classifier");
		formDataJson.addProperty("location", "location");

		
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray metadataJsonArray = new JsonArray();
		JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("reportPath", "reportPath");
		jsonItem.addProperty("dir", "dir");
		jsonItem.addProperty("reportPath", "reportPath");
		jsonItem.addProperty("lastModified", "lastModified");
		jsonItem.addProperty("title", "title");
		jsonItem.addProperty("logicalPath", "logicalPath");
		jsonItem.addProperty("file", "file");
		metadataJsonArray.add(jsonItem);
		
		when(metadata.getConnectionType()).thenReturn("global.jdbc");
		when(connectionDetails.getDirectory()).thenReturn("location");
		when(connectionDetails.getConnectionId()).thenReturn("12");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(reportStatsProvider.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		try (MockedStatic<FactoryMethodWrapper> mockedStatic1 = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<ReportsRelatedToMetadata> mockedStatic4 = mockStatic(
							ReportsRelatedToMetadata.class)) {

						mockedStatic4.when(() -> ReportsRelatedToMetadata
								.extractFilesBasedOnMetadataFileName(anyString(), anyString(), any()))
								.thenReturn(allFilesAvailableToLoggedInUser);

						mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(metadata);

						mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(new JsonObject());
						mockedStatic2.when(() -> AdhocServiceUtils.getSpecificExtension(any(), anyString()))
								.thenReturn(metadataJsonArray);
						mockedStatic1.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
								.thenReturn(reportStatsProvider);
						
						String executeComponent = dataSourceRelatedFiles.executeComponent(formDataJson.toString());
						assertFalse(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());
					}
				}
			}
		}

	}

	
	@Test
	public void ut_a3_test_executeComponent() {
		DataSourceRelatedFiles dataSourceRelatedFiles = new DataSourceRelatedFiles();
		IComponent reportStatsProvider = mock(IComponent.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("dataSourceId", "12");
		formDataJson.addProperty("classifier", "classifier");
		formDataJson.addProperty("location", "location");

		
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray metadataJsonArray = new JsonArray();
		JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("reportPath", "reportPath");
		jsonItem.addProperty("dir", "dir");
		jsonItem.addProperty("reportPath", "reportPath");
		jsonItem.addProperty("lastModified", "lastModified");
		jsonItem.addProperty("title", "title");
		jsonItem.addProperty("logicalPath", "logicalPath");
		jsonItem.addProperty("file", "file");
		metadataJsonArray.add(jsonItem);
		
		when(metadata.getConnectionType()).thenReturn("global.jdbc");
		when(connectionDetails.getDirectory()).thenReturn("location");
		when(connectionDetails.getConnectionId()).thenReturn("12");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(reportStatsProvider.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		try (MockedStatic<FactoryMethodWrapper> mockedStatic1 = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<ReportsRelatedToMetadata> mockedStatic4 = mockStatic(
							ReportsRelatedToMetadata.class)) {

						mockedStatic4.when(() -> ReportsRelatedToMetadata
								.extractFilesBasedOnMetadataFileName(anyString(), anyString(), any()))
								.thenReturn(allFilesAvailableToLoggedInUser);

						mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any())).thenThrow(new RuntimeException());

						mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(new JsonObject());
						mockedStatic2.when(() -> AdhocServiceUtils.getSpecificExtension(any(), anyString()))
								.thenReturn(metadataJsonArray);
						mockedStatic1.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
								.thenReturn(reportStatsProvider);
						
						String executeComponent = dataSourceRelatedFiles.executeComponent(formDataJson.toString());
						assertFalse(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());
					}
				}
			}
		}

	}
	@Test
	public void ut_a4_test_isThreadSafeToCache() {
		DataSourceRelatedFiles dataSourceRelatedFiles = new DataSourceRelatedFiles();
		boolean threadSafeToCache = dataSourceRelatedFiles.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
		
	}
}

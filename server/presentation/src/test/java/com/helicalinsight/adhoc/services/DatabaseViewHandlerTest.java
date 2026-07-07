package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.Properties;

import org.hibernate.dialect.Dialect;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.utility.DialectHelper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseViewHandlerTest {

	@Test(expected = MalformedJsonException.class)
	public void ut_a1_test_executeComponent() {
		DatabaseViewHandler databaseViewHandler = new DatabaseViewHandler();
		JsonObject formData = new JsonObject();
		formData.addProperty("location", "location");
		formData.add("metadataFileName", new JsonObject());
		databaseViewHandler.executeComponent(formData.toString());
	}

	@Test(expected = EfwdServiceException.class)
	public void ut_a2_test_executeComponent() {
		DatabaseViewHandler databaseViewHandler = new DatabaseViewHandler();
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("location", "location");
		formData.addProperty("metadataFileName", "metadataFileName");

		when(connectionDetails.getDirectory()).thenReturn(null);
		when(connectionDetails.getSubType()).thenReturn("subType");
		when(connectionDetails.getConnectionId()).thenReturn("123");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(driverConnection.getConnection()).thenReturn(null);
		try (MockedStatic<MetadataDBUtility> mockedStatic = mockStatic(MetadataDBUtility.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {
				try (MockedStatic<AdhocServiceUtils> mockedStatic3 = mockStatic(AdhocServiceUtils.class)) {
					try (MockedStatic<ConnectionProviderFactory> mockedStatic4 = mockStatic(
							ConnectionProviderFactory.class)) {
						mockedStatic4.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
								.thenReturn(driverConnection);

						mockedStatic2.when(() -> MetadataUtils.parameter(any(), anyString())).thenReturn("type");

						mockedStatic.when(() -> MetadataDBUtility.getMetadata(anyString(), anyString()))
								.thenReturn(metadata);

						databaseViewHandler.executeComponent(formData.toString());
					}
				}
			}
		}
	}

	@Test
	public void ut_a3_test_executeComponent() {
		DatabaseViewHandler databaseViewHandler = new DatabaseViewHandler();
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		Dialect hibernateDialect =mock(Dialect.class);
		
		JsonObject formData = new JsonObject();
		formData.addProperty("location", "location");
		formData.addProperty("metadataFileName", "metadataFileName");

		when(connectionDetails.getDirectory()).thenReturn(null);
		when(connectionDetails.getSubType()).thenReturn("subType");
		when(connectionDetails.getConnectionId()).thenReturn("123");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(driverConnection.getConnection()).thenReturn(connection);
		try (MockedStatic<MetadataDBUtility> mockedStatic = mockStatic(MetadataDBUtility.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {
				try (MockedStatic<AdhocServiceUtils> mockedStatic3 = mockStatic(AdhocServiceUtils.class)) {
					try (MockedStatic<ConnectionProviderFactory> mockedStatic4 = mockStatic(
							ConnectionProviderFactory.class)) {
						try (MockedConstruction<Properties> construction = mockConstruction(Properties.class,
								(mock, context) -> {

								})) {
							try(MockedStatic<DialectHelper> mockedStatic5 = mockStatic(DialectHelper.class)){
								mockedStatic5.when(()->  DialectHelper.getDialect(any())).thenReturn(hibernateDialect);
							
							
							mockedStatic4.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
									.thenReturn(driverConnection);

							mockedStatic2.when(() -> MetadataUtils.parameter(any(), anyString())).thenReturn("type");

							mockedStatic.when(() -> MetadataDBUtility.getMetadata(anyString(), anyString()))
									.thenReturn(metadata);

							String executeComponent = databaseViewHandler.executeComponent(formData.toString());
							String result = JsonParser.parseString(executeComponent).getAsJsonObject().get("location").getAsString();
							assertEquals("location", result);
							}
						}

					}
				}
			}
		}
	}
	
	@Test
	public void ut_a4_test_executeComponent() {
		DatabaseViewHandler databaseViewHandler = new DatabaseViewHandler();
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		Dialect hibernateDialect =mock(Dialect.class);
		
		JsonObject formData = new JsonObject();
		formData.addProperty("location", "location");
		formData.addProperty("metadataFileName", "metadataFileName");

		when(connectionDetails.getDirectory()).thenReturn("dir");
		when(connectionDetails.getSubType()).thenReturn("subType");
		when(connectionDetails.getConnectionId()).thenReturn("123");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(driverConnection.getConnection()).thenReturn(connection);
		try (MockedStatic<MetadataDBUtility> mockedStatic = mockStatic(MetadataDBUtility.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {
				try (MockedStatic<AdhocServiceUtils> mockedStatic3 = mockStatic(AdhocServiceUtils.class)) {
					try (MockedStatic<ConnectionProviderFactory> mockedStatic4 = mockStatic(
							ConnectionProviderFactory.class)) {
						try (MockedConstruction<Properties> construction = mockConstruction(Properties.class,
								(mock, context) -> {

								})) {
							try(MockedStatic<DialectHelper> mockedStatic5 = mockStatic(DialectHelper.class)){
								mockedStatic5.when(()->  DialectHelper.getDialect(any())).thenReturn(hibernateDialect);
							
							
							mockedStatic4.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
									.thenReturn(driverConnection);

							mockedStatic2.when(() -> MetadataUtils.parameter(any(), anyString())).thenReturn("type");

							mockedStatic.when(() -> MetadataDBUtility.getMetadata(anyString(), anyString()))
									.thenReturn(metadata);

							String executeComponent = databaseViewHandler.executeComponent(formData.toString());
							String result = JsonParser.parseString(executeComponent).getAsJsonObject().get("location").getAsString();
							assertEquals("location", result);
							}
						}

					}
				}
			}
		}
	}
	@Test
	public void ut_a5_test_isThreadSafeToCache() {
		DatabaseViewHandler databaseViewHandler = new DatabaseViewHandler();
		boolean threadSafeToCache = databaseViewHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}

package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.sql.Connection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GenericMetadataProducerTest {

	@Test(expected = EfwdServiceException.class)
	public void ut_a1_test_prepareMetadata() {
		GenericMetadataProducer genericMetadataProducer = new GenericMetadataProducer();
		DriverConnection driverConnection = mock(DriverConnection.class);

		JsonObject formData = new JsonObject();
		formData.addProperty("type", "type");
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), any())).thenReturn(driverConnection);
			genericMetadataProducer.prepareMetadata(formData);
		}
	}

	@Test
	public void ut_a2_test_prepareMetadata()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GenericMetadataProducer genericMetadataProducer = new GenericMetadataProducer();
		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionTemplate templateProducer = mock(ConnectionTemplate.class);
		DatabaseTemplate databaseTemplate = mock(DatabaseTemplate.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formData = new JsonObject();
		formData.addProperty("type", "type");
		formData.addProperty("classifier", "classifier");

		Field field = GenericMetadataProducer.class.getDeclaredField("templateProducer");
		field.setAccessible(true);
		field.set(genericMetadataProducer, templateProducer);

		Field field1 = GenericMetadataProducer.class.getDeclaredField("databaseTemplate");
		field1.setAccessible(true);
		field1.set(genericMetadataProducer, databaseTemplate);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<MetadataUtils> mockedStatic3 = mockStatic(MetadataUtils.class)) {
					try (MockedStatic<SecurityUtils> mockedStatic4 = mockStatic(SecurityUtils.class)) {

						mockedStatic2.when(() -> ApplicationContextAccessor.getBean(Metadata.class))
								.thenReturn(metadata);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), any()))
								.thenReturn(driverConnection);
						Metadata prepareMetadata = genericMetadataProducer.prepareMetadata(formData);
						assertEquals(metadata, prepareMetadata);
					}
				}
			}
		}
	}

	@Test
	public void ut_a3_test_prepareMetadata()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GenericMetadataProducer genericMetadataProducer = new GenericMetadataProducer();
		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionTemplate templateProducer = mock(ConnectionTemplate.class);
		DatabaseTemplate databaseTemplate = mock(DatabaseTemplate.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formData = new JsonObject();
		formData.addProperty("type", "type");
		formData.addProperty("classifier", "classifier");
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("selected");
		formData.add("schemaInformation", jsonArray);

		Field field = GenericMetadataProducer.class.getDeclaredField("templateProducer");
		field.setAccessible(true);
		field.set(genericMetadataProducer, templateProducer);

		Field field1 = GenericMetadataProducer.class.getDeclaredField("databaseTemplate");
		field1.setAccessible(true);
		field1.set(genericMetadataProducer, databaseTemplate);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<MetadataUtils> mockedStatic3 = mockStatic(MetadataUtils.class)) {
					try (MockedStatic<SecurityUtils> mockedStatic4 = mockStatic(SecurityUtils.class)) {

						mockedStatic2.when(() -> ApplicationContextAccessor.getBean(Metadata.class))
								.thenReturn(metadata);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), any()))
								.thenReturn(driverConnection);
						Metadata prepareMetadata = genericMetadataProducer.prepareMetadata(formData);
						assertEquals(metadata, prepareMetadata);
					}
				}
			}
		}
	}

	@Test
	public void ut_a4_test_prepareMetadata()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GenericMetadataProducer genericMetadataProducer = new GenericMetadataProducer();
		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionTemplate templateProducer = mock(ConnectionTemplate.class);
		DatabaseTemplate databaseTemplate = mock(DatabaseTemplate.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formData = new JsonObject();
		formData.addProperty("type", "type");
		formData.addProperty("classifier", "classifier");
		JsonArray jsonArray = new JsonArray();
		formData.add("schemaInformation", jsonArray);

		Field field = GenericMetadataProducer.class.getDeclaredField("templateProducer");
		field.setAccessible(true);
		field.set(genericMetadataProducer, templateProducer);

		Field field1 = GenericMetadataProducer.class.getDeclaredField("databaseTemplate");
		field1.setAccessible(true);
		field1.set(genericMetadataProducer, databaseTemplate);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<MetadataUtils> mockedStatic3 = mockStatic(MetadataUtils.class)) {
					try (MockedStatic<SecurityUtils> mockedStatic4 = mockStatic(SecurityUtils.class)) {

						mockedStatic2.when(() -> ApplicationContextAccessor.getBean(Metadata.class))
								.thenReturn(metadata);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), any()))
								.thenReturn(driverConnection);
						Metadata prepareMetadata = genericMetadataProducer.prepareMetadata(formData);
						assertEquals(metadata, prepareMetadata);
					}
				}
			}
		}
	}

}

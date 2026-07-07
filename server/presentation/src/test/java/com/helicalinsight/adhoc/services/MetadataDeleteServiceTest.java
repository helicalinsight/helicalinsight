package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDeleteServiceTest {

	@Test
	public void ut_a1_test_doService() throws IOException {
		MetadataDeleteService deleteService = new MetadataDeleteService();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "System");
		formJson.addProperty("metadataFileName", "dummyMetadata.txt");

		File file = new File(ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator+"dummyMetadata.txt");
		file.createNewFile();
		
		JsonObject metadataFileAsJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("connectionType", "connectionType");
		metadataFileAsJson.add("connection", connection);
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<ServiceUtils> mockedStatic3 = mockStatic(ServiceUtils.class)) {
					mockedStatic3
							.when(() -> ServiceUtils.executeService(anyString(), anyString(), anyString(), anyString()))
							.thenReturn("success");

					mockedStatic.when(() -> JsonUtils.newGetAsJson(any())).thenReturn(metadataFileAsJson);

					String doService = deleteService.doService("type", "serviceType", "service", formJson.toString());
					assertEquals("success", doService);

				}
			}
		}finally {
			file.delete();
		}

	}
	
	@Test(expected =  RuntimeException.class )
	public void ut_a2_test_doService() throws IOException {
		MetadataDeleteService deleteService = new MetadataDeleteService();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "System");
		formJson.addProperty("metadataFileName", "dummyMetadata.txt");

		
		
		JsonObject metadataFileAsJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("connectionType", "connectionType");
		metadataFileAsJson.add("connection", connection);
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<ServiceUtils> mockedStatic3 = mockStatic(ServiceUtils.class)) {
					mockedStatic3
							.when(() -> ServiceUtils.executeService(anyString(), anyString(), anyString(), anyString()))
							.thenReturn("success");

					mockedStatic.when(() -> JsonUtils.newGetAsJson(any())).thenReturn(metadataFileAsJson);

					deleteService.doService("type", "serviceType", "service", formJson.toString());
					

				}
			}
		}
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		MetadataDeleteService deleteService = new MetadataDeleteService();
		boolean threadSafeToCache = deleteService.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}

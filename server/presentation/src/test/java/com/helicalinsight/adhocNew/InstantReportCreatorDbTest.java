package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.instant.InstantReportCreatorDb;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstantReportCreatorDbTest {

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a1_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.add("state", null);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeServiceDB);
			creatorDb.executeComponent(formDataJson.toString());
		}

	}

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a2_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		formDataJson.add("metadata", metadata);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeServiceDB);
			creatorDb.executeComponent(formDataJson.toString());
		}

	}

	@Test(expected = EfwServiceException.class)
	public void ut_a3_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeServiceDB);
			creatorDb.executeComponent(formDataJson.toString());
		}

	}

	@Test(expected = IllegalStateException.class)
	public void ut_a4_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);
		formDataJson.addProperty("uuid", "metadata1");
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource).thenReturn(null);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeServiceDB);
			creatorDb.executeComponent(formDataJson.toString());
		}

	}

	@Test
	public void ut_a5_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		HIResourceInstantReport adhocReport = mock(HIResourceInstantReport.class);
		ResourceInfoUtility bean = mock(ResourceInfoUtility.class);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("insight");
		fileInfo.setPath("path");

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);
		formDataJson.addProperty("uuid", "metadata1");

		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);
		when(metadataResource.getHiResourceInstantReport()).thenReturn(adhocReport);
		when(metadataResource.getResourceURL()).thenReturn("com/helical/");
		when(bean.prepareFileInfo(anyString(), anyString())).thenReturn(fileInfo);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeServiceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceInfoUtility.class)).thenReturn(bean);
			String executeComponent = creatorDb.executeComponent(formDataJson.toString());
			String asString = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
			assertEquals("Successfully saved instant file", asString);
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a6_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		HIResourceInstantReport adhocReport = mock(HIResourceInstantReport.class);
		ResourceInfoUtility bean = mock(ResourceInfoUtility.class);
		Security security = mock(Security.class);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("insight");
		fileInfo.setPath("path");

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);

		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);
		when(metadataResource.getHiResourceInstantReport()).thenReturn(adhocReport);
		when(metadataResource.getResourceURL()).thenReturn("com/helical/");
		when(bean.prepareFileInfo(anyString(), anyString())).thenReturn(fileInfo);
		when(security.getCreatedBy()).thenReturn("1");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<SecurityUtils> mockedStatic2 = mockStatic(SecurityUtils.class)) {
				mockedStatic2.when(() -> SecurityUtils.securityObject()).thenReturn(security);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
						.thenReturn(serviceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
						.thenReturn(resourceTypeServiceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceInfoUtility.class)).thenReturn(bean);
				creatorDb.executeComponent(formDataJson.toString());
				
			}
		}

	}

	@Test(expected = EfwServiceException.class)
	public void ut_a7_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		HIResourceInstantReport adhocReport = mock(HIResourceInstantReport.class);
		ResourceInfoUtility bean = mock(ResourceInfoUtility.class);
		Security security = mock(Security.class);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("insight");
		fileInfo.setPath("path");

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);
		formDataJson.addProperty("reportName", "re");

		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);
		when(metadataResource.getHiResourceInstantReport()).thenReturn(adhocReport);
		when(metadataResource.getResourceURL()).thenReturn("com/helical/");
		when(bean.prepareFileInfo(anyString(), anyString())).thenReturn(fileInfo);
		when(security.getCreatedBy()).thenReturn("1");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<SecurityUtils> mockedStatic2 = mockStatic(SecurityUtils.class)) {
				mockedStatic2.when(() -> SecurityUtils.securityObject()).thenReturn(security);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
						.thenReturn(serviceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
						.thenReturn(resourceTypeServiceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceInfoUtility.class)).thenReturn(bean);
				creatorDb.executeComponent(formDataJson.toString());
				
			}
		}

	}

	// TODO : Configure the character limit in a config file.
//	@Test(expected = EfwServiceException.class)
	public void ut_a8_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		HIResourceInstantReport adhocReport = mock(HIResourceInstantReport.class);
		ResourceInfoUtility bean = mock(ResourceInfoUtility.class);
		Security security = mock(Security.class);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("insight");
		fileInfo.setPath("path");

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);
		String temp = "ThisStringHasMoreThanSixtyCharactersInItThisStringHasMoreThanSixtyCharactersInItThisStringHasMoreThanSixtyCharactersInIt";
		formDataJson.addProperty("reportName", temp);

		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);
		when(metadataResource.getHiResourceInstantReport()).thenReturn(adhocReport);
		when(metadataResource.getResourceURL()).thenReturn("com/helical/");
		when(bean.prepareFileInfo(anyString(), anyString())).thenReturn(fileInfo);
		when(security.getCreatedBy()).thenReturn("1");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<SecurityUtils> mockedStatic2 = mockStatic(SecurityUtils.class)) {
				mockedStatic2.when(() -> SecurityUtils.securityObject()).thenReturn(security);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
						.thenReturn(serviceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
						.thenReturn(resourceTypeServiceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceInfoUtility.class)).thenReturn(bean);
				creatorDb.executeComponent(formDataJson.toString());
				
			}
		}

	}

	
	@Test
	public void ut_a9_test_executeComponent() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		HIResourceInstantReport adhocReport = mock(HIResourceInstantReport.class);
		ResourceInfoUtility bean = mock(ResourceInfoUtility.class);
		Security security = mock(Security.class);
		ResourceType resourceType = mock(ResourceType.class);
		
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("insight");
		fileInfo.setPath("path");

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "hyderbad");
		formDataJson.addProperty("state", "state");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadata1");
		formDataJson.add("metadata", metadata);
		formDataJson.addProperty("reportName", "HReport");

		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);
		when(metadataResource.getHiResourceInstantReport()).thenReturn(adhocReport);
		when(metadataResource.getResourceURL()).thenReturn("com/helical/");
		when(bean.prepareFileInfo(anyString(), anyString())).thenReturn(fileInfo);
		when(security.getCreatedBy()).thenReturn("1");
		when(resourceTypeServiceDB.getResourceTypeByTypeAndExtension(anyString(), anyString())).thenReturn(resourceType);
		
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<SecurityUtils> mockedStatic2 = mockStatic(SecurityUtils.class)) {
				mockedStatic2.when(() -> SecurityUtils.securityObject()).thenReturn(security);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
						.thenReturn(serviceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
						.thenReturn(resourceTypeServiceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceInfoUtility.class)).thenReturn(bean);
				String executeComponent = creatorDb.executeComponent(formDataJson.toString());
				String asString = JsonParser.parseString(executeComponent).getAsJsonObject().get("message")
						.getAsString();
				assertEquals("Successfully saved instant file", asString);
			}
		}

	}
	@Test
	public void ut_b1_test_isThreadSafeToCache() {
		InstantReportCreatorDb creatorDb = new InstantReportCreatorDb();
		boolean threadSafeToCache = creatorDb.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}

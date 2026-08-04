package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.model.HIResourceMetadataDTO;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.service.impl.UserDetailsServiceImpl;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.handler.AbstractResourceWriterHandler;
import com.helicalinsight.export.handler.EfwddWriterHandler;
import com.helicalinsight.export.handler.FolderWriterHandler;
import com.helicalinsight.export.handler.HCRWriterHandler;
import com.helicalinsight.export.handler.HReportWriterHandler;
import com.helicalinsight.export.handler.ImageWriterHandler;
import com.helicalinsight.export.handler.MetadataWriterHandler;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WriterHandlersTest extends ExportUnitTestBase {

	private void injectBaseFields(AbstractResourceWriterHandler handler, HIResourceServiceDB serviceDB,
			ResourceDTOMapper dtoMapper, ManifestUtils manifestUtils, ResourceDataWriter dataWriter, UserService userService)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field serviceField = AbstractResourceWriterHandler.class.getDeclaredField("serviceDB");
		serviceField.setAccessible(true);
		serviceField.set(handler, serviceDB);

		Field mapperField = AbstractResourceWriterHandler.class.getDeclaredField("dtoMapper");
		mapperField.setAccessible(true);
		mapperField.set(handler, dtoMapper);

		Field manifestField = AbstractResourceWriterHandler.class.getDeclaredField("manifestUtils");
		manifestField.setAccessible(true);
		manifestField.set(handler, manifestUtils);

		Field writerField = AbstractResourceWriterHandler.class.getDeclaredField("dataWriter");
		writerField.setAccessible(true);
		writerField.set(handler, dataWriter);
		
		Field userServiceField = AbstractResourceWriterHandler.class.getDeclaredField("userService");
		userServiceField.setAccessible(true);
		userServiceField.set(handler, userService);
	}

	@Test
	public void ut_a1_testEfwddWriterHandler_write()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwddWriterHandler handler = new EfwddWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);
		HReportWriterHandler reportWriterHandler = mock(HReportWriterHandler.class);
		HIResourceConstituentMappingService pathService = mock(HIResourceConstituentMappingService.class);
		ApplicationSettings applicationSettings = mock(ApplicationSettings.class);
		UserService userService = mock(UserService.class);
		injectBaseFields(handler, serviceDB, dtoMapper, manifestUtils, dataWriter, userService);

		Field pathField = AbstractResourceWriterHandler.class.getDeclaredField("pathService");
		pathField.setAccessible(true);
		pathField.set(handler, pathService);

		Field folderField = EfwddWriterHandler.class.getDeclaredField("folderWriterHandlerHandler");
		folderField.setAccessible(true);
		folderField.set(handler, folderWriterHandler);

		Field reportField = EfwddWriterHandler.class.getDeclaredField("reportWriterHandler");
		reportField.setAccessible(true);
		reportField.set(handler, reportWriterHandler);

		HIResourceDTO resource = mock(HIResourceDTO.class);
		HIResource hResource = mock(HIResource.class);
		HIResourceEFWDD efwddResource = mock(HIResourceEFWDD.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();
		JsonObject settingJson = new JsonObject();
		settingJson.addProperty("autoSyncCutPasteDesigner", false);

		when(resource.getResourceId()).thenReturn(1);
		when(resource.getPath()).thenReturn("parent/dashboard");
		when(serviceDB.getResourceByIdIgnoreFilter(1)).thenReturn(hResource);
		when(hResource.getHiResourceEFWDD()).thenReturn(efwddResource);
		when(hResource.getResourceURL()).thenReturn("parent/dashboard");
		when(hResource.getCreatedBy()).thenReturn(4);
		when(efwddResource.getState()).thenReturn("{}");
		when(efwddResource.getCreatedBy()).thenReturn(4);
		when(hResource.getCreatedBy()).thenReturn(4);
		when(pathService.findByParentId(1)).thenReturn(Collections.emptyList());
		when(applicationSettings.getSettingJson()).thenReturn(settingJson);
		when(manifestUtils.pathExists(any(), eq(manifest))).thenReturn(true);
		User user = mock(User.class);
		when(userService.findUser(4)).thenReturn(user);
		when(user.getUsername()).thenReturn("admin");
		
		
		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(ApplicationSettings.class))
					.thenReturn(applicationSettings);
			handler.write(resource, "dir", manifest, options);
		}

		verify(dataWriter).write(any(), eq("dir"), eq(resource), eq(""));
		verify(manifestUtils).insertPath("parent/dashboard", manifest);
		verify(manifestUtils).insertDependency(eq("parent/dashboard"), anyString(), eq(manifest));
	}

	@Test
	public void ut_a2_testHCRWriterHandler_write()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRWriterHandler handler = new HCRWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);
		ImageWriterHandler imageWriterHandler = mock(ImageWriterHandler.class);
		HIResourceConstituentMappingService mappingService = mock(HIResourceConstituentMappingService.class);
		UserService userService = mock(UserDetailsServiceImpl.class);
		
		injectBaseFields(handler, serviceDB, dtoMapper, manifestUtils, dataWriter, userService);

		Field folderField = HCRWriterHandler.class.getDeclaredField("folderWriterHandlerHandler");
		folderField.setAccessible(true);
		folderField.set(handler, folderWriterHandler);

		Field imageField = HCRWriterHandler.class.getDeclaredField("imageWriterHandler");
		imageField.setAccessible(true);
		imageField.set(handler, imageWriterHandler);

		Field mappingField = HCRWriterHandler.class.getDeclaredField("mappingService");
		mappingField.setAccessible(true);
		mappingField.set(handler, mappingService);

		HIResourceDTO report = mock(HIResourceDTO.class);
		HIResource hResource = mock(HIResource.class);
		HIResourceHCR hReport = mock(HIResourceHCR.class);
		Manifest manifest = mock(Manifest.class);
		ResourceOptions options = mock(ResourceOptions.class);

		when(report.getResourceId()).thenReturn(1);
		when(report.getPath()).thenReturn("parent/hcr");
		when(serviceDB.getResourceByIdIgnoreFilter(1)).thenReturn(hResource);
		when(hResource.getHiResourceHCR()).thenReturn(hReport);
		when(hResource.getResourceURL()).thenReturn("parent/hcr");
		when(hResource.getCreatedBy()).thenReturn(3);
		when(hReport.getState()).thenReturn("{}");
		when(hReport.getPreviewFormData()).thenReturn("{}");
		when(hReport.getCreatedBy()).thenReturn(3);
		when(mappingService.findByParentId(1)).thenReturn(Collections.emptyList());
		when(manifestUtils.pathExists(any(), eq(manifest))).thenReturn(true);
		
		User user = mock(User.class);
		when(userService.findUser(3)).thenReturn(user);
		when(user.getUsername()).thenReturn("admin");
		
		handler.write(report, "dir", manifest, options);

		verify(dataWriter).write(any(), eq("dir"), eq(report), eq(""));
		verify(manifestUtils).insertPath("parent/hcr", manifest);
		verify(manifestUtils).insertDependency(eq("parent/hcr"), anyString(), eq(manifest));
	}

	@Test
	public void ut_a3_testHReportWriterHandler_write_success()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportWriterHandler handler = new HReportWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		MetadataWriterHandler mdWriterHandler = mock(MetadataWriterHandler.class);
		FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);
		
		UserService userService = mock(UserDetailsServiceImpl.class);
		injectBaseFields(handler, serviceDB, dtoMapper, manifestUtils, dataWriter, userService);

		Field mdField = HReportWriterHandler.class.getDeclaredField("mdWriterHandler");
		mdField.setAccessible(true);
		mdField.set(handler, mdWriterHandler);

		Field folderField = HReportWriterHandler.class.getDeclaredField("folderWriterHandlerHandler");
		folderField.setAccessible(true);
		folderField.set(handler, folderWriterHandler);

		HIResourceDTO report = mock(HIResourceDTO.class);
		HIResource hResource = mock(HIResource.class);
		HIResourceHReport hReport = mock(HIResourceHReport.class);
		HIResource mdata = mock(HIResource.class);
		HIResourceDTO mdDTO = mock(HIResourceDTO.class);
		HIResourceDTO folderDTO = mock(HIResourceDTO.class);
		HIResource resourceDir = mock(HIResource.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();

		when(report.getResourceId()).thenReturn(1);
		when(report.getPath()).thenReturn("parent/report");
		when(serviceDB.getResourceByIdIgnoreFilter(1)).thenReturn(hResource);
		when(hResource.getHiResourceHReport()).thenReturn(hReport);
		when(hResource.getResourceURL()).thenReturn("parent/report");
		when(hResource.getCreatedBy()).thenReturn(5);
		when(hReport.getHiResourceMetadata()).thenReturn(2);
		when(serviceDB.getResourceByIdIgnoreFilter(2)).thenReturn(mdata);
		when(mdata.getResourceURL()).thenReturn("parent/metadata");
		when(dtoMapper.map(mdata)).thenReturn(mdDTO);
		when(mdDTO.getPath()).thenReturn("parent/metadata");
		when(manifestUtils.pathExists(anyString(), eq(manifest))).thenReturn(false);
		when(serviceDB.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resourceDir);
		when(dtoMapper.map(resourceDir)).thenReturn(folderDTO);
		
		User user = mock(User.class);
		when(userService.findUser(1)).thenReturn(user);
		when(user.getUsername()).thenReturn("admin");
		
		
		
		handler.write(report, "dir", manifest, options);

		verify(mdWriterHandler).write(mdDTO, "dir", manifest, options);
		verify(dataWriter).write(any(), eq("dir"), eq(report), eq(""));
		verify(manifestUtils).insertPath("parent/report", manifest);
	}

	@Test(expected = ResourceExportException.class)
	public void ut_a4_testHReportWriterHandler_write_metadataNotFound()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportWriterHandler handler = new HReportWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		UserService userService = mock(UserDetailsServiceImpl.class);
		injectBaseFields(handler, serviceDB, dtoMapper, manifestUtils, dataWriter, userService);

		HIResourceDTO report = mock(HIResourceDTO.class);
		HIResource hResource = mock(HIResource.class);
		HIResourceHReport hReport = mock(HIResourceHReport.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();

		when(report.getResourceId()).thenReturn(1);
		when(report.getName()).thenReturn("report");
		when(serviceDB.getResourceByIdIgnoreFilter(1)).thenReturn(hResource);
		when(hResource.getHiResourceHReport()).thenReturn(hReport);
		when(hReport.getHiResourceMetadata()).thenReturn(2);
		when(serviceDB.getResourceByIdIgnoreFilter(2)).thenReturn(null);
		User user = mock(User.class);
		when(userService.findUser(1)).thenReturn(user);
		when(user.getUsername()).thenReturn("admin");
		
		handler.write(report, "dir", manifest, options);
	}

	@Test
	public void ut_a5_testMetadataWriterHandler_write()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataWriterHandler handler = new MetadataWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		HIMetadataResourceServiceDB mdServiceDB = mock(HIMetadataResourceServiceDB.class);
		FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);
		
		UserService userService = mock(UserDetailsServiceImpl.class);
		
		injectBaseFields(handler, serviceDB, dtoMapper, manifestUtils, dataWriter, userService);

		Field mdField = AbstractResourceWriterHandler.class.getDeclaredField("mdServiceDB");
		mdField.setAccessible(true);
		mdField.set(handler, mdServiceDB);

		Field folderField = MetadataWriterHandler.class.getDeclaredField("folderWriterHandlerHandler");
		folderField.setAccessible(true);
		folderField.set(handler, folderWriterHandler);

		HIResourceDTO resource = mock(HIResourceDTO.class);
		HIResource folderResource = mock(HIResource.class);
		HIResourceDTO folder = mock(HIResourceDTO.class);
		HIResourceMetadataDTO metadata = mock(HIResourceMetadataDTO.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceDTO hiResourceDTO = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();
		options.setDataSource(true);
		DatasourceHandler dsHandler = mock(DatasourceHandler.class);

		when(resource.getPath()).thenReturn("parent/metadata");
		when(resource.getName()).thenReturn("metadata");
		when(resource.getResourceId()).thenReturn(1);
		when(manifestUtils.pathExists(anyString(), eq(manifest))).thenReturn(false);
		when(serviceDB.getResourceByUrl(anyString(), eq(false))).thenReturn(folderResource);
		when(dtoMapper.map(folderResource)).thenReturn(folder);
		when(mdServiceDB.giveHIResourceMetadataByResId(1)).thenReturn(metadata);
		when(serviceDB.getResourceByIdIgnoreFilter(1)).thenReturn(hiResource);
		when(dtoMapper.map(hiResource)).thenReturn(hiResourceDTO);
		when(metadata.getCreatedBy()).thenReturn(2);
		when(metadata.getConnectionType()).thenReturn("global.jdbc");

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean("globalDSHandler")).thenReturn(dsHandler);
			handler.write(resource, "dir", manifest, options);
		}

		verify(folderWriterHandler).write(folder, "dir", manifest, options);
		verify(dataWriter).write(any(), eq("dir"), eq(resource), eq(""));
		verify(manifestUtils).insertPath("parent/metadata", manifest);
		verify(dsHandler).write(resource, "dir", manifest);
	}

}

package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWSR;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.FolderWriterHandler;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.handler.importres.FolderImportHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.scheduling.handler.ScheduleHandler;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.SchedulesService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScheduleHandlerTest {

	@Test
	public void testWrite_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ScheduleHandler schedulehandler = new ScheduleHandler();
		ResourceIOHandler resourceIOHandler = new ScheduleHandler();
		HIResourceDTO hiResourceDTO = mock(HIResourceDTO.class);
		SchedulesService scheduleService = mock(SchedulesService.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		
		Manifest manifest = mock(Manifest.class);
		List<Schedules> scheduleList = new ArrayList<>();
		Schedules schedules = new Schedules();
		scheduleList.add(schedules);
		when(hiResourceDTO.getExtension()).thenReturn("hr");
		when(manifest.getSchedules()).thenReturn(new HashMap<>());
		ShareHandler shareHandler = mock(ShareHandler.class);

		Field field0 = ResourceIOHandler.class.getDeclaredField("shareHandler");
		field0.setAccessible(true);
		field0.set(schedulehandler, shareHandler);

		Field field1 = ScheduleHandler.class.getDeclaredField("scheduleService");
		field1.setAccessible(true);
		field1.set(schedulehandler, scheduleService);
		when(scheduleService.findSchedulesByResourceId(anyInt())).thenReturn(scheduleList);
		Field field2 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field2.setAccessible(true);
		field2.set(schedulehandler, serviceDb);

		Field field3 = ResourceIOHandler.class.getDeclaredField("dataWriter");
		field3.setAccessible(true);
		field3.set(schedulehandler, dataWriter);

		Field field4 = ResourceIOHandler.class.getDeclaredField("manifestUtils");
		field4.setAccessible(true);
		field4.set(schedulehandler, manifestUtils);

		Field field5 = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		field5.setAccessible(true);
		field5.set(schedulehandler, dtoMapper);
		
		List<HIResource> efwsrResourceFiles = new ArrayList<>();
		HIResource hiResource = new HIResource();
		efwsrResourceFiles.add(hiResource);
		
		when(dtoMapper.map(hiResource)).thenReturn(hiResourceDTO);
		when(hiResourceDTO.getPath()).thenReturn("path");
		when(hiResourceDTO.getName()).thenReturn("helical");
		when(serviceDb.findAllEfwsrFilesByReportDirAndFile("", "path")).thenReturn(efwsrResourceFiles);

		schedulehandler.write(hiResourceDTO, "dir", manifest);
	}

	@Test
	public void testWrite_hcr()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ScheduleHandler schedulehandler = new ScheduleHandler();
		HIResourceDTO hiResourceDTO = mock(HIResourceDTO.class);
		SchedulesService scheduleService = mock(SchedulesService.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);

		List<Schedules> scheduleList = new ArrayList<>();
		scheduleList.add(new Schedules());
		when(hiResourceDTO.getExtension()).thenReturn("hcr");
		when(hiResourceDTO.getPath()).thenReturn("ScheduleTest/report.hcr");
		when(hiResourceDTO.getName()).thenReturn("report.hcr");
		when(hiResourceDTO.getResourceId()).thenReturn(1);
		when(manifest.getSchedules()).thenReturn(new HashMap<>());
		when(scheduleService.findSchedulesByResourceId(anyInt())).thenReturn(scheduleList);

		List<HIResource> efwsrResourceFiles = new ArrayList<>();
		HIResource efwsr = new HIResource();
		efwsrResourceFiles.add(efwsr);
		when(serviceDb.findAllEfwsrFilesByReportDirAndFile("ScheduleTest", "report.hcr"))
				.thenReturn(efwsrResourceFiles);
		when(dtoMapper.map(efwsr)).thenReturn(hiResourceDTO);

		Field field0 = ResourceIOHandler.class.getDeclaredField("shareHandler");
		field0.setAccessible(true);
		field0.set(schedulehandler, shareHandler);
		Field field1 = ScheduleHandler.class.getDeclaredField("scheduleService");
		field1.setAccessible(true);
		field1.set(schedulehandler, scheduleService);
		Field field2 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field2.setAccessible(true);
		field2.set(schedulehandler, serviceDb);
		Field field3 = ResourceIOHandler.class.getDeclaredField("dataWriter");
		field3.setAccessible(true);
		field3.set(schedulehandler, dataWriter);
		Field field4 = ResourceIOHandler.class.getDeclaredField("manifestUtils");
		field4.setAccessible(true);
		field4.set(schedulehandler, manifestUtils);
		Field field5 = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		field5.setAccessible(true);
		field5.set(schedulehandler, dtoMapper);

		schedulehandler.write(hiResourceDTO, "dir", manifest);

		verify(dataWriter).write(eq(scheduleList), eq("dir"), eq(hiResourceDTO), eq(ResourceSuffix.SCHEDULE));
		verify(dataWriter).write(eq(efwsrResourceFiles), eq("dir"), eq(hiResourceDTO),
				eq(ResourceSuffix.SCHEDULE_RESULT));
		verify(manifestUtils, times(1)).insertSchedules(hiResourceDTO, manifest);
	}

	@Test
	public void testWrite_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ScheduleHandler schedulehandler = new ScheduleHandler();
		ResourceIOHandler resourceIOHandler = new ScheduleHandler();
		HIResourceDTO hiResourceDTO = mock(HIResourceDTO.class);
		SchedulesService scheduleService = mock(SchedulesService.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);

		Manifest manifest = mock(Manifest.class);
		List<Schedules> scheduleList = new ArrayList<>();
		Schedules schedules = new Schedules();
		scheduleList.add(schedules);
		when(hiResourceDTO.getExtension()).thenReturn("hr");
		when(manifest.getSchedules()).thenReturn(new HashMap<>());

		Field field1 = ScheduleHandler.class.getDeclaredField("scheduleService");
		field1.setAccessible(true);
		field1.set(schedulehandler, scheduleService);
		when(scheduleService.findSchedulesByResourceId(anyInt())).thenReturn(scheduleList);
		Field field2 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field2.setAccessible(true);
		field2.set(schedulehandler, serviceDb);
		List<HIResource> efwsrResourceFiles = new ArrayList<>();
		when(serviceDb.findAllEfwsrFilesByReportDirAndFile(null, null)).thenReturn(efwsrResourceFiles);

		schedulehandler.write(hiResourceDTO, "dir", manifest);
	}

	@Test
	public void testWrite_a3() {
		ScheduleHandler schedulehandler = new ScheduleHandler();
		HIResourceDTO hiResourceDTO = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		when(hiResourceDTO.getExtension()).thenReturn(null);
		schedulehandler.write(hiResourceDTO, "dir", manifest);
	}

	@Test
	public void testWrite_a4() {
		ScheduleHandler schedulehandler = new ScheduleHandler();
		HIResourceDTO hiResourceDTO = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		when(hiResourceDTO.getExtension()).thenReturn("hr");
		when(hiResourceDTO.getPath()).thenReturn("path");
		Map<String, String> map = new HashMap<>();
		map.put("path", "path");
		when(manifest.getSchedules()).thenReturn(map);
		schedulehandler.write(hiResourceDTO, "dir", manifest);
	}

	@Test
	public void testImportResource_a1() throws IOException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		ScheduleHandler schedulehandler = new ScheduleHandler();
		HIResource hiResource = mock(HIResource.class);
		Manifest manifest = mock(Manifest.class);
		
		String directoryPath = "D:/resources/";
		String filePath = directoryPath + "fileName_schedule";
		File directory = new File(directoryPath);
		File file = new File(filePath);
		directory.mkdirs();
		file.createNewFile();
		
		SchedulesService scheduleService = mock(SchedulesService.class);
		JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		TypeReference<List<Schedules>> typeRef = new TypeReference<List<Schedules>>() {
		};
		ImportManagerContext context = mock(ImportManagerContext.class);

		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(schedulehandler, context);
		
		Field field1 = ScheduleHandler.class.getDeclaredField("scheduleService");
		field1.setAccessible(true);
		field1.set(schedulehandler, scheduleService);
		Field field2 = ResourceIOHandler.class.getDeclaredField("jsonMapperUtils");
		field2.setAccessible(true);
		field2.set(schedulehandler, jsonMapperUtils);
		
		List<Schedules> dbSchedules = new ArrayList<>();
		Schedules schedules = mock(Schedules.class);
		dbSchedules.add(schedules);
		
		when(context.getResourcesDirectory()).thenReturn("D:/resources/");
		when(hiResource.getResourceURL()).thenReturn("");
		when(scheduleService.findSchedulesByResourceId(0)).thenReturn(dbSchedules);
		when(context.getManifest()).thenReturn(manifest);
		when(manifest.getVersion()).thenReturn("1");
		when(context.removeDestination(anyString())).thenReturn("fileName");
		
		try {
			schedulehandler.importResource(hiResource, "fileName", "onConflict");
		} catch (NullPointerException e) {
			file.delete();
			directory.delete();
		}

	}

	@Test
	public void testImportResource_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ScheduleHandler handler = new ScheduleHandler();
		HIResource hiResource = mock(HIResource.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		Manifest manifest = mock(Manifest.class);
		
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(handler, context);

		when(context.getResourcesDirectory()).thenReturn("D:/resources/");
		when(hiResource.getResourceURL()).thenReturn("");
		when(context.getManifest()).thenReturn(manifest);
		when(manifest.getVersion()).thenReturn("1");
		when(context.removeDestination(anyString())).thenReturn("fileName");
		
		handler.importResource(hiResource, "fileName", "onConflict");
	}

	@Test
	public void testWrite_settingsSupportedExtensionsReachScheduleLookup() throws Exception {
		Set<String> supported = JsonUtils.getScheduleSupportedModules();
		assertFalse(supported.isEmpty());
		for (String extension : supported) {
			HandlerMocks mocks = new HandlerMocks();
			mocks.wire();
			HIResourceDTO resource = mock(HIResourceDTO.class);
			Manifest manifest = mock(Manifest.class);
			when(resource.getExtension()).thenReturn(extension);
			when(resource.getPath()).thenReturn("reports/item." + extension);
			when(resource.getResourceId()).thenReturn(3);
			when(manifest.getSchedules()).thenReturn(new HashMap<>());
			when(mocks.scheduleService.findSchedulesByResourceId(3)).thenReturn(new ArrayList<>());

			mocks.handler.write(resource, "dir", manifest);

			verify(mocks.scheduleService).findSchedulesByResourceId(3);
			verifyNoInteractions(mocks.dataWriter);
		}
	}

	@Test
	public void testWrite_unsupportedExtensionSkipsScheduling() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		when(resource.getExtension()).thenReturn("efw");

		mocks.handler.write(resource, "dir", mock(Manifest.class));

		verifyNoInteractions(mocks.scheduleService, mocks.dataWriter, mocks.shareHandler, mocks.folderWriterHandler);
	}

	@Test
	public void testWrite_nullExtensionSkipsScheduling() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		when(resource.getExtension()).thenReturn(null);

		mocks.handler.write(resource, "dir", mock(Manifest.class));

		verifyNoInteractions(mocks.scheduleService);
	}

	@Test
	public void testWrite_alreadyExportedPathSkipsScheduling() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		Map<String, String> schedules = new HashMap<>();
		schedules.put("reports/sales.hcr", "sales.hcr");
		when(resource.getExtension()).thenReturn("hcr");
		when(resource.getPath()).thenReturn("reports/sales.hcr");
		when(manifest.getSchedules()).thenReturn(schedules);

		mocks.handler.write(resource, "dir", manifest);

		verifyNoInteractions(mocks.scheduleService, mocks.dataWriter);
	}

	@Test
	public void testWrite_emptyScheduleListSkipsExport() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		when(resource.getExtension()).thenReturn("hr");
		when(resource.getPath()).thenReturn("reports/sales.hr");
		when(resource.getResourceId()).thenReturn(5);
		when(manifest.getSchedules()).thenReturn(new HashMap<>());
		when(mocks.scheduleService.findSchedulesByResourceId(5)).thenReturn(new ArrayList<>());

		mocks.handler.write(resource, "dir", manifest);

		verify(mocks.scheduleService).findSchedulesByResourceId(5);
		verifyNoInteractions(mocks.dataWriter, mocks.shareHandler, mocks.folderWriterHandler);
	}

	@Test
	public void testWrite_exportsSchedulesResultsAndMissingFolders() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		List<Schedules> scheduleList = new ArrayList<>();
		scheduleList.add(new Schedules(8L));
		HIResource withFolder = new HIResource();
		HIResource withoutFolder = new HIResource();
		HIResource alreadyExported = new HIResource();
		List<HIResource> results = new ArrayList<>();
		results.add(withFolder);
		results.add(withoutFolder);
		results.add(alreadyExported);
		HIResourceDTO withFolderDto = mock(HIResourceDTO.class);
		HIResourceDTO withoutFolderDto = mock(HIResourceDTO.class);
		HIResourceDTO alreadyExportedDto = mock(HIResourceDTO.class);
		HIResource folder = new HIResource();
		HIResourceDTO folderDto = mock(HIResourceDTO.class);

		when(resource.getExtension()).thenReturn("efwdd");
		when(resource.getPath()).thenReturn("reports/sales.efwdd");
		when(resource.getResourceId()).thenReturn(8);
		when(manifest.getSchedules()).thenReturn(new HashMap<>());
		when(manifest.getOptions()).thenReturn(options);
		when(mocks.scheduleService.findSchedulesByResourceId(8)).thenReturn(scheduleList);
		when(mocks.serviceDb.findAllEfwsrFilesByReportDirAndFile("reports", "sales.efwdd")).thenReturn(results);
		when(mocks.dtoMapper.map(withFolder)).thenReturn(withFolderDto);
		when(mocks.dtoMapper.map(withoutFolder)).thenReturn(withoutFolderDto);
		when(mocks.dtoMapper.map(alreadyExported)).thenReturn(alreadyExportedDto);
		when(mocks.dtoMapper.map(folder)).thenReturn(folderDto);
		when(withFolderDto.getPath()).thenReturn("reports/a.efwsr");
		when(withFolderDto.getName()).thenReturn("a.efwsr");
		when(withoutFolderDto.getPath()).thenReturn("reports/b.efwsr");
		when(withoutFolderDto.getName()).thenReturn("b.efwsr");
		when(alreadyExportedDto.getPath()).thenReturn("reports/c.efwsr");
		when(alreadyExportedDto.getName()).thenReturn("c.efwsr");
		when(mocks.serviceDb.getResourceByUrl("reports", false)).thenReturn(folder, null, folder);
		when(mocks.manifestUtils.pathExists("reports" + ResourceExtension.FOLDER.getValue(), manifest))
				.thenReturn(false, false, true);

		mocks.handler.write(resource, "dir", manifest);

		verify(mocks.dataWriter).write(scheduleList, "dir", resource, ResourceSuffix.SCHEDULE);
		verify(mocks.dataWriter).write(results, "dir", resource, ResourceSuffix.SCHEDULE_RESULT);
		verify(mocks.manifestUtils).insertSchedules(resource, manifest);
		verify(mocks.shareHandler).write(withFolder, "dir", manifest);
		verify(mocks.shareHandler).write(withoutFolder, "dir", manifest);
		verify(mocks.shareHandler).write(alreadyExported, "dir", manifest);
		verify(mocks.folderWriterHandler, times(1)).write(folderDto, "dir", manifest, options);
		verify(mocks.serviceDb, times(3)).getResourceByUrl("reports", false);
	}

	@Test
	public void testImportResource_missingSidecarLogsSkip() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> {
			HIResource resource = mock(HIResource.class);
			when(resource.getResourceURL()).thenReturn("reports/sales.hr");
			stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");

			mocks.handler.importResource(resource, "ignored.hr", "skip");

			verify(mocks.context).addLog("reports/sales.hr", "[schedules] No schedule sidecar found; skipped.");
			verify(mocks.context).removeDestination("reports/sales.hr");
			verifyNoInteractions(mocks.scheduleService, mocks.jsonMapperUtils);
		});
	}

	@Test
	public void testImportResource_missingSidecarWithNullUrlDoesNotLog() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> {
			HIResource resource = mock(HIResource.class);
			when(resource.getResourceURL()).thenReturn(null);
			stubVersion(mocks, dir, "1", null, "sales.hr");

			mocks.handler.importResource(resource, "ignored.hr", "skip");

			verify(mocks.context, never()).addLog(any(), any());
			verifyNoInteractions(mocks.scheduleService);
		});
	}

	@Test
	public void testImportResource_nullResourceAndLegacyManifestSkipsQuietly() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> {
			stubVersion(mocks, dir, "0", null, null);

			mocks.handler.importResource(null, "bundle/report.hr", "skip");

			verify(mocks.context, never()).removeDestination(any());
			verify(mocks.context, never()).addLog(any(), any());
			verifyNoInteractions(mocks.scheduleService);
		});
	}

	@Test
	public void testImportResource_legacyManifestEmptySidecarDeletesExistingSchedules() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> {
			HIResource resource = mock(HIResource.class);
			when(resource.getResourceURL()).thenReturn("reports/sales.hr");
			when(resource.getResourceId()).thenReturn(15);
			stubVersion(mocks, dir, "0", "reports/sales.hr", null);
			createScheduleFile(dir, "report.hr");
			List<Schedules> existing = new ArrayList<>();
			existing.add(new Schedules(11L));
			existing.add(new Schedules(12L));
			when(mocks.scheduleService.findSchedulesByResourceId(15)).thenReturn(existing);
			stubSidecars(mocks, new ArrayList<>(), null);

			mocks.handler.importResource(resource, "bundle/report.hr", "skip");

			verify(mocks.context, never()).removeDestination(any());
			verify(mocks.scheduleService).deleteSchedule(11L);
			verify(mocks.scheduleService).deleteSchedule(12L);
			verify(mocks.scheduleService, never()).addSchedule(any());
			verify(mocks.context).addLog("reports/sales.hr", "[schedules] Importing schedules…");
			verify(mocks.context).addLog("reports/sales.hr", "[schedules] Schedule sidecar empty; skipped.");
			verify(mocks.context, times(2)).addLog(anyString(), anyString());
		});
	}

	@Test
	public void testImportResource_nullUrlEmptySidecarDoesNotLog() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> {
			HIResource resource = mock(HIResource.class);
			when(resource.getResourceURL()).thenReturn(null);
			when(resource.getResourceId()).thenReturn(15);
			stubVersion(mocks, dir, "1", null, "sales.hr");
			createScheduleFile(dir, "sales.hr");
			when(mocks.scheduleService.findSchedulesByResourceId(15)).thenReturn(new ArrayList<>());
			stubSidecars(mocks, new ArrayList<>(), null);

			mocks.handler.importResource(resource, "ignored.hr", "skip");

			verify(mocks.context, never()).addLog(any(), any());
			verify(mocks.scheduleService, never()).deleteSchedule(any());
			verify(mocks.scheduleService, never()).addSchedule(any());
		});
	}

	@Test
	public void testImportResource_importsSchedulesAndResultFiles() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = mock(HIResource.class);
			User user = mock(User.class);
			Date importedOn = new Date(1_700_000_000_000L);
			ImportRequest request = mock(ImportRequest.class);
			when(resource.getResourceURL()).thenReturn("reports/sales.hr");
			when(resource.getResourceId()).thenReturn(15);
			when(resource.getCreatedBy()).thenReturn(Integer.valueOf(4), Integer.valueOf(4), null);
			when(mocks.userService.findUser(4)).thenReturn(user);
			when(mocks.context.getDate()).thenReturn(importedOn);
			when(mocks.context.getRequest()).thenReturn(request);
			enableShare(request, true);
			Manifest manifest = stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");
			when(mocks.scheduleService.findSchedulesByResourceId(15)).thenReturn(new ArrayList<>());

			Schedules withUser = new Schedules(21L);
			JobParameters firstParameter = new JobParameters();
			JobParameters secondParameter = new JobParameters();
			withUser.setJobParameters(List.of(firstParameter, secondParameter));
			withUser.setScheduleType(incomingScheduleType());
			Schedules withoutUser = new Schedules(22L);
			withoutUser.setJobParameters(new ArrayList<>());
			withoutUser.setScheduleType(incomingScheduleType());
			List<Schedules> incoming = new ArrayList<>();
			incoming.add(withUser);
			incoming.add(withoutUser);
			ResourceType persistedScheduleType = stubPersistedScheduleType(mocks);

			HIResource created = newResult(RESULT_ONE, true);
			HIResource linked = newResult(RESULT_TWO, true);
			List<HIResource> results = new ArrayList<>();
			results.add(created);
			results.add(linked);
			stubSidecars(mocks, incoming, results);

			Map<String, HIResource> urls = stubDestination(mocks);
			HIResource existingParent = new HIResource();
			existingParent.setResourceId(200);
			urls.put(RESULT_TWO_PARENT, existingParent);
			HIResource importedParent = new HIResource();
			importedParent.setResourceId(100);
			when(mocks.folderImportHandler.importResource(RESULT_ONE_PARENT)).thenReturn(importedParent);
			ResourceType persistedType = savedResultType();
			when(mocks.resourceTypeService.getResourceTypeByTypeAndExtension("Saved Result", "efwsr"))
					.thenReturn(persistedType);

			mocks.handler.importResource(resource, "ignored/original.hr", "skip");

			assertSame(resource, withUser.getHIResource());
			assertSame(resource, withoutUser.getHIResource());
			assertSame(persistedScheduleType, withUser.getScheduleType());
			assertSame(persistedScheduleType, withoutUser.getScheduleType());
			assertSame(user, withUser.getCreatedBy());
			assertNull(withoutUser.getCreatedBy());
			assertEquals(TimeZone.getDefault().getID(), withUser.getTimeZone());
			assertEquals(TimeZone.getDefault().getID(), withoutUser.getTimeZone());
			assertSame(withUser, firstParameter.getScheduleIdOfJobParameter());
			assertSame(withUser, secondParameter.getScheduleIdOfJobParameter());
			verify(mocks.scheduleService, never()).deleteSchedule(any());
			verify(mocks.scheduleService).addSchedule(same(withUser));
			verify(mocks.scheduleService).addSchedule(same(withoutUser));
			verify(mocks.jobParameterService).addJobParameter(same(firstParameter));
			verify(mocks.jobParameterService).addJobParameter(same(secondParameter));
			assertEquals(1, calls.constructed().size());
			EnhancedScheduleProcessCall process = calls.constructed().get(0);
			verify(process).scheduleSpecificJob(same(withUser), eq(""));
			verify(process).scheduleSpecificJob(same(withoutUser), eq(""));

			ArgumentCaptor<HIResource> savedResults = ArgumentCaptor.forClass(HIResource.class);
			verify(mocks.serviceDb, times(2)).addHIResource(savedResults.capture());
			HIResource savedCreated = savedResults.getAllValues().get(0);
			HIResource savedLinked = savedResults.getAllValues().get(1);
			assertEquals(Integer.valueOf(100), savedCreated.getParentId());
			assertEquals(RESULT_ONE_DEST, savedCreated.getResourceURL());
			assertEquals("run.efwsr", savedCreated.getTitle());
			assertEquals("run.efwsr", savedCreated.getResourcePath());
			assertEquals(Integer.valueOf(9), savedCreated.getCreatedBy());
			assertSame(importedOn, savedCreated.getCreated_date());
			assertSame(importedOn, savedCreated.getLastUpdatedTime());
			assertSame(importedOn, savedLinked.getLastUpdatedTime());
			assertFalse(savedCreated.getDeleted());
			assertSame(persistedType, savedCreated.getActualResourceType());
			assertEquals(Long.valueOf(42L), savedCreated.getResourceTypeId());
			assertSame(created.getHiResourceEFWSR(), savedCreated.getHiResourceEFWSR());
			assertSame(importedOn, created.getHiResourceEFWSR().getLastUpdatedTime());
			assertEquals(Integer.valueOf(9), created.getHiResourceEFWSR().getCreatedBy());
			assertEquals(Integer.valueOf(200), savedLinked.getParentId());
			assertEquals(RESULT_TWO_DEST, savedLinked.getResourceURL());
			assertSame(linked.getHiResourceEFWSR(), savedLinked.getHiResourceEFWSR());
			verify(mocks.folderImportHandler).setContext(mocks.context);
			verify(mocks.folderImportHandler).importResource(RESULT_ONE_PARENT);
			verify(mocks.folderImportHandler, never()).importResource(RESULT_TWO_PARENT);
			verify(mocks.serviceDb, never()).editHIResource(any());
			verify(mocks.shareHandler, times(2)).setContext(mocks.context);
			verify(mocks.shareHandler).importResource(same(savedCreated), eq(request), eq(manifest));
			verify(mocks.shareHandler).importResource(same(savedLinked), eq(request), eq(manifest));
			verify(mocks.resourceTypeService, times(2)).getResourceTypeByTypeAndExtension("Report", "hr");
			verify(mocks.resourceTypeService, times(2)).getResourceTypeByTypeAndExtension("Saved Result", "efwsr");
			verify(mocks.context).addLog("reports/sales.hr", "[schedules] Importing schedules…");
			verify(mocks.context).addLog("reports/sales.hr", "[schedules] Imported 2 schedule(s).");

			ArgumentCaptor<File> files = ArgumentCaptor.forClass(File.class);
			verify(mocks.jsonMapperUtils, times(2)).asList(files.capture(), any());
			assertEquals("sales.hr_schedule", files.getAllValues().get(0).getName());
			assertEquals("sales.efwsr", files.getAllValues().get(1).getName());
		}));
	}

	@Test
	public void testImportResource_nullUrlImportsWithoutLogs() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = mock(HIResource.class);
			when(resource.getResourceURL()).thenReturn(null);
			when(resource.getResourceId()).thenReturn(15);
			when(resource.getCreatedBy()).thenReturn(null);
			stubVersion(mocks, dir, "1", null, "sales.hr");
			createScheduleFile(dir, "sales.hr");
			when(mocks.scheduleService.findSchedulesByResourceId(15)).thenReturn(new ArrayList<>());
			Schedules schedule = new Schedules(30L);
			schedule.setJobParameters(new ArrayList<>());
			schedule.setScheduleType(incomingScheduleType());
			List<Schedules> incoming = new ArrayList<>();
			incoming.add(schedule);
			ResourceType persistedScheduleType = stubPersistedScheduleType(mocks);
			stubSidecars(mocks, incoming, new ArrayList<>());

			mocks.handler.importResource(resource, "ignored.hr", "skip");

			assertSame(persistedScheduleType, schedule.getScheduleType());
			assertNull(schedule.getCreatedBy());
			verify(mocks.userService, never()).findUser(anyInt());
			verify(mocks.scheduleService).addSchedule(same(schedule));
			verify(calls.constructed().get(0)).scheduleSpecificJob(same(schedule), eq(""));
			verify(mocks.context, never()).addLog(any(), any());
			verify(mocks.serviceDb, never()).addHIResource(any());
			verifyNoInteractions(mocks.shareHandler, mocks.folderImportHandler);
		}));
	}

	@Test
	public void testImportResource_missingResourceType() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = reportResource();
			prepareSingleSchedule(mocks, resource);
			stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");
			Schedules schedule = scheduleOf(resource);
			ResourceType persistedScheduleType = stubPersistedScheduleType(mocks);
			HIResource result = newResult(RESULT_ONE, false);
			stubSidecars(mocks, List.of(schedule), List.of(result));
			Map<String, HIResource> urls = stubDestination(mocks);
			HIResource parent = new HIResource();
			parent.setResourceId(7);
			urls.put(RESULT_ONE_PARENT, parent);
			when(mocks.context.getDate()).thenReturn(new Date(1_700_000_000_000L));
			stubShare(mocks, true);
			omitCreatedResourceType = true;

			try {
				mocks.handler.importResource(resource, "sales.hr", "skip");
				fail("Expected EfwServiceException");
			} catch (EfwServiceException ex) {
				assertEquals("Resource type should not be null.", ex.getMessage());
			}

			verify(mocks.context).addError(dir.toString(), "Resource type should not be null.");
			assertSame(persistedScheduleType, schedule.getScheduleType());
			verify(mocks.scheduleService).addSchedule(same(schedule));
			verify(mocks.serviceDb, never()).addHIResource(any());
			verify(mocks.shareHandler, never()).importResource(any(HIResource.class), any(ImportRequest.class),
					any(Manifest.class));
			assertEquals(1, calls.constructed().size());
		}));
	}

	@Test
	public void testImportResource_nullScheduleTypeAbortsBeforeSave() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = reportResource();
			when(resource.getCreatedBy()).thenReturn(null);
			prepareSingleSchedule(mocks, resource);
			stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");
			Schedules schedule = new Schedules(40L);
			schedule.setJobParameters(new ArrayList<>());
			HIResource result = newResult(RESULT_ONE, true);
			stubSidecars(mocks, List.of(schedule), List.of(result));
			stubDestination(mocks);

			try {
				mocks.handler.importResource(resource, "sales.hr", "skip");
				fail("Expected EfwServiceException");
			} catch (EfwServiceException ex) {
				assertEquals("Resource type should not be null.", ex.getMessage());
			}

			assertSame(resource, schedule.getHIResource());
			assertEquals(TimeZone.getDefault().getID(), schedule.getTimeZone());
			verify(mocks.context).addError(dir.toString(), "Resource type should not be null.");
			verify(mocks.scheduleService, never()).addSchedule(any());
			verify(mocks.jobParameterService, never()).addJobParameter(any());
			verify(mocks.resourceTypeService, never()).getResourceTypeByTypeAndExtension(anyString(), anyString());
			verify(mocks.serviceDb, never()).addHIResource(any());
			assertEquals(1, calls.constructed().size());
			verify(calls.constructed().get(0), never()).scheduleSpecificJob(any(), any());
		}));
	}

	@Test
	public void testImportResource_unknownScheduleType() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = reportResource();
			when(resource.getCreatedBy()).thenReturn(null);
			prepareSingleSchedule(mocks, resource);
			stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");
			Schedules schedule = scheduleOf(resource);
			ResourceType incoming = schedule.getScheduleType();
			when(mocks.resourceTypeService.getResourceTypeByTypeAndExtension("Report", "hr")).thenReturn(null);
			stubSidecars(mocks, List.of(schedule), List.of(newResult(RESULT_ONE, true)));
			stubDestination(mocks);

			try {
				mocks.handler.importResource(resource, "sales.hr", "skip");
				fail("Expected EfwServiceException");
			} catch (EfwServiceException ex) {
				assertEquals("Could not find ResourceType [Report, hr]", ex.getMessage());
			}

			assertSame(incoming, schedule.getScheduleType());
			verify(mocks.context).addError(dir.toString(), "Could not find ResourceType [Report, hr]");
			verify(mocks.resourceTypeService).getResourceTypeByTypeAndExtension("Report", "hr");
			verify(mocks.scheduleService, never()).addSchedule(any());
			verify(mocks.serviceDb, never()).addHIResource(any());
			verify(calls.constructed().get(0), never()).scheduleSpecificJob(any(), any());
		}));
	}

	@Test
	public void testImportResource_savedResultUsesSecurityOwnerWhenNotShared() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			try (MockedStatic<ApplicationContextAccessor> ignored = mockStatic(ApplicationContextAccessor.class);
					MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
				Security security = new Security();
				security.setCreatedBy("15");
				securityUtils.when(SecurityUtils::securityObject).thenReturn(security);

				HIResource resource = reportResource();
				when(resource.getCreatedBy()).thenReturn(null);
				Date importedOn = new Date(1_700_000_000_000L);
				when(mocks.context.getDate()).thenReturn(importedOn);
				ImportRequest request = stubShare(mocks, false);
				prepareSingleSchedule(mocks, resource);
				Manifest manifest = stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
				createScheduleFile(dir, "sales.hr");
				when(mocks.resourceTypeService.getResourceTypeByTypeAndExtension("Saved Result", "efwsr"))
						.thenReturn(savedResultType());
				HIResource result = newResult(RESULT_ONE, true);
				result.getHiResourceEFWSR().setReportName("Old name");
				stubSidecars(mocks, List.of(scheduleOf(resource)), List.of(result));
				Map<String, HIResource> urls = stubDestination(mocks);
				HIResource parent = new HIResource();
				parent.setResourceId(11);
				urls.put(RESULT_ONE_PARENT, parent);

				mocks.handler.importResource(resource, "sales.hr", "skip");

				ArgumentCaptor<HIResource> savedResults = ArgumentCaptor.forClass(HIResource.class);
				verify(mocks.serviceDb).addHIResource(savedResults.capture());
				HIResource saved = savedResults.getValue();
				assertEquals(Integer.valueOf(15), saved.getCreatedBy());
				assertEquals(RESULT_ONE_DEST, saved.getResourceURL());
				assertEquals("run.efwsr", saved.getTitle());
				assertSame(result.getHiResourceEFWSR(), saved.getHiResourceEFWSR());
				assertEquals("Old name", saved.getHiResourceEFWSR().getReportName());
				assertEquals(Integer.valueOf(15), saved.getHiResourceEFWSR().getCreatedBy());
				assertSame(importedOn, saved.getHiResourceEFWSR().getLastUpdatedTime());
				verify(mocks.shareHandler).importResource(same(saved), eq(request), eq(manifest));
				assertEquals(1, calls.constructed().size());
			}
		}));
	}

	@Test
	public void testImportResource_unknownResourceType() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = reportResource();
			prepareSingleSchedule(mocks, resource);
			stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");
			HIResource result = newResult(RESULT_ONE, true);
			stubSidecars(mocks, List.of(scheduleOf(resource)), List.of(result));
			Map<String, HIResource> urls = stubDestination(mocks);
			HIResource parent = new HIResource();
			parent.setResourceId(7);
			urls.put(RESULT_ONE_PARENT, parent);
			when(mocks.context.getDate()).thenReturn(new Date(1_700_000_000_000L));
			stubShare(mocks, true);
			when(mocks.resourceTypeService.getResourceTypeByTypeAndExtension("Saved Result", "efwsr")).thenReturn(null);

			try {
				mocks.handler.importResource(resource, "sales.hr", "update");
				fail("Expected EfwServiceException");
			} catch (EfwServiceException ex) {
				assertEquals("Could not find ResourceType [Saved Result, efwsr]", ex.getMessage());
			}

			verify(mocks.context).addError(dir.toString(), "Could not find ResourceType [Saved Result, efwsr]");
			verify(mocks.resourceTypeService).getResourceTypeByTypeAndExtension("Saved Result", "efwsr");
			verify(mocks.serviceDb, never()).addHIResource(any());
			verify(mocks.shareHandler, never()).importResource(any(HIResource.class), any(ImportRequest.class),
					any(Manifest.class));
			assertEquals(1, calls.constructed().size());
		}));
	}

	@Test
	public void testImportResource_updatesExistingResultWhenRecovered() throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = reportResource();
			when(resource.getCreatedBy()).thenReturn(4);
			User user = mock(User.class);
			when(mocks.userService.findUser(4)).thenReturn(user);
			Date importedOn = new Date(1_700_000_000_000L);
			ImportRequest request = mock(ImportRequest.class);
			when(mocks.context.getDate()).thenReturn(importedOn);
			when(mocks.context.getRequest()).thenReturn(request);
			enableShare(request, true);
			prepareSingleSchedule(mocks, resource);
			Manifest manifest = stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");

			HIResource incoming = newResult(RESULT_ONE, true);
			incoming.setTitle("Old name");
			incoming.setCreatedBy(9);
			HIResourceEFWSR importedDetail = new HIResourceEFWSR();
			importedDetail.setReportName("Old name");
			importedDetail.setReportParameters("{\"keep\":true}");
			importedDetail.setCreatedBy(3);
			incoming.setHiResourceEFWSR(importedDetail);
			stubSidecars(mocks, List.of(scheduleOf(resource)), List.of(incoming));
			stubDestination(mocks);

			HIResource existing = new HIResource();
			HIResourceEFWSR previousDetail = new HIResourceEFWSR();
			previousDetail.setReportName("Local name");
			previousDetail.setReportParameters("local");
			previousDetail.setCreatedBy(1);
			existing.setHiResourceEFWSR(previousDetail);
			when(mocks.serviceDb.getResourceByUrl(RESULT_ONE_DEST, false)).thenReturn(existing);
			when(mocks.context.recover(existing)).thenReturn(true);
			when(mocks.resourceTypeService.getResourceTypeByTypeAndExtension("Saved Result", "efwsr"))
					.thenReturn(savedResultType());

			mocks.handler.importResource(resource, "sales.hr", "UPDATE");

			assertEquals(importedOn, existing.getLastUpdatedTime());
			assertEquals(Integer.valueOf(9), existing.getCreatedBy());
			assertEquals("Old name", existing.getTitle());
			assertEquals("run.efwsr", existing.getResourcePath());
			assertSame(previousDetail, existing.getHiResourceEFWSR());
			assertEquals("Old name", previousDetail.getReportName());
			assertEquals("{\"keep\":true}", previousDetail.getReportParameters());
			assertEquals(importedOn, previousDetail.getLastUpdatedTime());
			assertEquals(Integer.valueOf(3), previousDetail.getCreatedBy());
			verify(mocks.serviceDb).editHIResource(existing);
			verify(mocks.serviceDb, never()).addHIResource(any());
			verify(mocks.folderImportHandler, never()).importResource(anyString());
			verify(mocks.shareHandler).importResource(same(existing), eq(request), eq(manifest));
			verify(mocks.context).addLog("reports/sales.hr", "[schedules] Imported 1 schedule(s).");
			assertEquals(1, calls.constructed().size());
		}));
	}

	@Test
	public void testImportResource_skipsShareWhenRecoverReturnsFalse() throws Exception {
		assertExistingResultIsNotShared("update", false);
	}

	@Test
	public void testImportResource_skipsShareWhenConflictIsNotUpdate() throws Exception {
		assertExistingResultIsNotShared("skip", true);
	}

	private void assertExistingResultIsNotShared(String onConflict, boolean recover) throws Exception {
		HandlerMocks mocks = new HandlerMocks();
		mocks.wire();
		withResources(dir -> withScheduling(calls -> {
			HIResource resource = reportResource();
			when(resource.getCreatedBy()).thenReturn(null);
			prepareSingleSchedule(mocks, resource);
			stubShare(mocks, true);
			stubVersion(mocks, dir, "1", "reports/sales.hr", "sales.hr");
			createScheduleFile(dir, "sales.hr");
			HIResource incoming = newResult(RESULT_ONE, true);
			stubSidecars(mocks, List.of(scheduleOf(resource)), List.of(incoming));
			stubDestination(mocks);
			HIResource existing = new HIResource();
			when(mocks.serviceDb.getResourceByUrl(RESULT_ONE_DEST, false)).thenReturn(existing);
			when(mocks.context.recover(existing)).thenReturn(recover);

			mocks.handler.importResource(resource, "sales.hr", onConflict);

			if ("update".equalsIgnoreCase(onConflict)) {
				verify(mocks.context).recover(existing);
			} else {
				verify(mocks.context, never()).recover(any(HIResource.class));
			}
			verify(mocks.serviceDb, never()).editHIResource(any());
			verify(mocks.serviceDb, never()).addHIResource(any());
			verify(mocks.shareHandler, never()).importResource(any(HIResource.class), any(ImportRequest.class),
					any(Manifest.class));
			verify(mocks.context).addLog("reports/sales.hr", "[schedules] Imported 1 schedule(s).");
			assertEquals(1, calls.constructed().size());
		}));
	}

	private static HIResource reportResource() {
		HIResource resource = mock(HIResource.class);
		when(resource.getResourceURL()).thenReturn("reports/sales.hr");
		when(resource.getResourceId()).thenReturn(15);
		return resource;
	}

	private static Schedules scheduleOf(HIResource resource) {
		Schedules schedule = new Schedules(40L);
		schedule.setJobParameters(new ArrayList<>());
		schedule.setHIResource(resource);
		schedule.setScheduleType(incomingScheduleType());
		return schedule;
	}

	private static void prepareSingleSchedule(HandlerMocks mocks, HIResource resource) {
		when(mocks.scheduleService.findSchedulesByResourceId(15)).thenReturn(new ArrayList<>());
		stubPersistedScheduleType(mocks);
	}

	private static ResourceType incomingScheduleType() {
		return resourceType(7L, "Report", "hr");
	}

	private static ResourceType resourceType(long id, String name, String extension) {
		ResourceType type = new ResourceType();
		type.setResourceTypeId(id);
		type.setName(name);
		type.setExtension(extension);
		return type;
	}

	private static ResourceType stubPersistedScheduleType(HandlerMocks mocks) {
		ResourceType persisted = resourceType(70L, "Report", "hr");
		when(mocks.resourceTypeService.getResourceTypeByTypeAndExtension("Report", "hr")).thenReturn(persisted);
		return persisted;
	}

	private static HIResource newResult(String resourceUrl, boolean withType) {
		HIResource result = new HIResource();
		result.setResourceURL(resourceUrl);
		result.setResourcePath("run.efwsr");
		result.setTitle("run.efwsr");
		result.setCreatedBy(9);
		result.setHiResourceEFWSR(new HIResourceEFWSR());
		if (withType) {
			ResourceType type = new ResourceType();
			type.setName("Saved Result");
			type.setExtension("efwsr");
			type.setResourceTypeId(1L);
			result.setResourceType(type);
		}
		return result;
	}

	private static ResourceType savedResultType() {
		ResourceType type = new ResourceType();
		type.setName("Saved Result");
		type.setExtension("efwsr");
		type.setResourceTypeId(42L);
		return type;
	}

	private static Manifest stubVersion(HandlerMocks mocks, Path dir, String version, String resourceUrl,
			String resolvedName) {
		Manifest manifest = mock(Manifest.class);
		when(mocks.context.getResourcesDirectory()).thenReturn(dir.toString());
		when(mocks.context.getManifest()).thenReturn(manifest);
		when(manifest.getVersion()).thenReturn(version);
		if (Integer.parseInt(version) > 0) {
			when(mocks.context.removeDestination(resourceUrl)).thenReturn(resolvedName);
		}
		return manifest;
	}

	private static Map<String, HIResource> stubDestination(HandlerMocks mocks) {
		when(mocks.context.addDestination(anyString()))
				.thenAnswer(invocation -> "imported/" + invocation.getArgument(0));
		Map<String, HIResource> urls = new HashMap<>();
		when(mocks.context.getResourceUrlMap()).thenReturn(urls);
		return urls;
	}

	private static void stubSidecars(HandlerMocks mocks, List<Schedules> schedules, List<HIResource> results) {
		if (results == null) {
			doReturn(schedules).when(mocks.jsonMapperUtils).asList(any(File.class), any());
		} else {
			doReturn(schedules).doReturn(results).when(mocks.jsonMapperUtils).asList(any(File.class), any());
		}
	}

	private static File createScheduleFile(Path directory, String resolvedFileName) throws IOException {
		File file = new File(String.join(File.separator, directory.toString(), resolvedFileName + ResourceSuffix.SCHEDULE));
		File parent = file.getParentFile();
		if (parent != null && !parent.exists() && !parent.mkdirs() && !parent.exists()) {
			throw new IOException("Unable to create " + parent);
		}
		if (!file.exists() && !file.createNewFile()) {
			throw new IOException("Unable to create " + file);
		}
		return file;
	}

	private static void withResources(ResourcesTest test) throws Exception {
		Path dir = Files.createTempDirectory("schedule-handler-");
		try {
			test.run(dir);
		} finally {
			deleteRecursively(dir);
		}
	}

	private static boolean omitCreatedResourceType;

	private static void withScheduling(SchedulingTest test) throws Exception {
		omitCreatedResourceType = false;
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<ResourceUtils> resourceUtils = mockStatic(ResourceUtils.class);
				MockedConstruction<EnhancedScheduleProcessCall> calls = mockConstruction(
						EnhancedScheduleProcessCall.class)) {
			jsonUtils.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			jsonUtils.when(JsonUtils::getEFWSRExtension).thenReturn("efwsr");
			resourceUtils.when(() -> ResourceUtils.newHIResource(nullable(String.class), nullable(Date.class),
					nullable(Integer.class), nullable(String.class), nullable(String.class), nullable(String.class),
					nullable(Integer.class), anyBoolean())).thenAnswer(ScheduleHandlerTest::newSavedResult);
			test.run(calls);
		}
	}

	private static HIResource newSavedResult(org.mockito.invocation.InvocationOnMock invocation) {
		HIResource resource = new HIResource();
		Integer ownerId = invocation.getArgument(2);
		boolean isPublic = invocation.getArgument(7);
		if (isPublic) {
			ownerId = null;
		}
		Date date = invocation.getArgument(1);
		resource.setCreatedBy(ownerId);
		resource.setParentId(invocation.getArgument(6));
		resource.setResourceURL(invocation.getArgument(3));
		resource.setResourcePath(invocation.getArgument(4));
		resource.setTitle(invocation.getArgument(5));
		resource.setLastUpdatedTime(date);
		resource.setCreated_date(date);
		resource.setDeleted(false);
		if (!omitCreatedResourceType) {
			resource.setResourceType(resourceType(1L, "Saved Result", "efwsr"));
		}
		return resource;
	}

	private static ImportRequest stubShare(HandlerMocks mocks, boolean share) {
		ImportRequest request = mock(ImportRequest.class);
		enableShare(request, share);
		when(mocks.context.getRequest()).thenReturn(request);
		return request;
	}

	private static void enableShare(ImportRequest request, boolean share) {
		ResourceOptions options = new ResourceOptions();
		options.setShare(share);
		when(request.getOptions()).thenReturn(options);
	}

	private static void deleteRecursively(Path root) {
		if (root == null || !Files.exists(root)) {
			return;
		}
		try (Stream<Path> walk = Files.walk(root)) {
			walk.sorted(Comparator.reverseOrder()).forEach(path -> {
				try {
					Files.deleteIfExists(path);
				} catch (IOException ignored) {
					// Temp files are cleaned up on a best-effort basis.
				}
			});
		} catch (IOException ignored) {
			// Temp files are cleaned up on a best-effort basis.
		}
	}

	private static void setField(Object target, Class<?> owner, String name, Object value) throws Exception {
		Field field = owner.getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}

	@FunctionalInterface
	private interface ResourcesTest {
		void run(Path resourcesDir) throws Exception;
	}

	@FunctionalInterface
	private interface SchedulingTest {
		void run(MockedConstruction<EnhancedScheduleProcessCall> calls) throws Exception;
	}

	private static final class HandlerMocks {
		private final ScheduleHandler handler = new ScheduleHandler();
		private final SchedulesService scheduleService = mock(SchedulesService.class);
		private final JobParametersService jobParameterService = mock(JobParametersService.class);
		private final FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);
		private final FolderImportHandler folderImportHandler = mock(FolderImportHandler.class);
		private final HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		private final ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		private final ManifestUtils manifestUtils = mock(ManifestUtils.class);
		private final ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		private final ShareHandler shareHandler = mock(ShareHandler.class);
		private final JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		private final ImportManagerContext context = mock(ImportManagerContext.class);
		private final UserService userService = mock(UserService.class);
		private final ResourceTypeServiceDB resourceTypeService = mock(ResourceTypeServiceDB.class);

		private void wire() throws Exception {
			setField(handler, ScheduleHandler.class, "scheduleService", scheduleService);
			setField(handler, ScheduleHandler.class, "jobParameterService", jobParameterService);
			setField(handler, ScheduleHandler.class, "folderWriterHandler", folderWriterHandler);
			setField(handler, ScheduleHandler.class, "folderImportHandler", folderImportHandler);
			setField(handler, ResourceIOHandler.class, "serviceDb", serviceDb);
			setField(handler, ResourceIOHandler.class, "dataWriter", dataWriter);
			setField(handler, ResourceIOHandler.class, "manifestUtils", manifestUtils);
			setField(handler, ResourceIOHandler.class, "dtoMapper", dtoMapper);
			setField(handler, ResourceIOHandler.class, "shareHandler", shareHandler);
			setField(handler, ResourceIOHandler.class, "jsonMapperUtils", jsonMapperUtils);
			setField(handler, ResourceIOHandler.class, "context", context);
			setField(handler, ResourceIOHandler.class, "userService", userService);
			setField(handler, ResourceIOHandler.class, "resourceTypeService", resourceTypeService);
		}
	}

	private static final String RESULT_ONE = "out/run.efwsr";
	private static final String RESULT_ONE_DEST = "imported/out/run.efwsr";
	private static final String RESULT_ONE_PARENT = "imported/out.efwfolder";
	private static final String RESULT_TWO = "other/run.efwsr";
	private static final String RESULT_TWO_PARENT = "imported/other.efwfolder";
	private static final String RESULT_TWO_DEST = "imported/other/run.efwsr";

}

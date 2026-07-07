package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.scheduling.handler.ScheduleHandler;
import com.helicalinsight.scheduling.model.Schedules;
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
		when(serviceDb.findAllEfwsrFilesByReportDirAndFile(null, null)).thenReturn(efwsrResourceFiles);

		schedulehandler.write(hiResourceDTO, "dir", manifest);
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

}

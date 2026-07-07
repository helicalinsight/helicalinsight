package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.CubeImportHandler;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CubeImportHandlerTest {

	@Test
	public void ut_a1_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CubeImportHandler cubeImportHandler = new CubeImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(cubeImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(cubeImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(cubeImportHandler, fileReader);

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(request.getOnConflict()).thenReturn("skip");
		HIResource importResource = cubeImportHandler.importResource("url/url");
		Assert.assertEquals(resource, importResource);

	}

	@Test
	public void ut_a2_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CubeImportHandler cubeImportHandler = new CubeImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HICubeDAOService cubeService = mock(HICubeDAOService.class);
		HIMetadataCube existingCube = mock(HIMetadataCube.class);
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(cubeImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(cubeImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(cubeImportHandler, fileReader);

		Field field3 = CubeImportHandler.class.getDeclaredField("cubeService");
		field3.setAccessible(true);
		field3.set(cubeImportHandler, cubeService);

		Field field4 = AbstractResourceImportHandler.class.getDeclaredField("mdServiceDb");
		field4.setAccessible(true);
		field4.set(cubeImportHandler, mdServiceDb);

		Map<String, HIResource> map = new HashMap<>();
		map.put("url.efwfolder", resource);
		map.put("resourceUrl", resource);

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.getResourceUrlMap()).thenReturn(map);
		when(request.getOnConflict()).thenReturn("don't skip");
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(cubeService.findCubeByResourceId(anyInt())).thenReturn(existingCube);
		when(existingCube.getHiResourceMetadata()).thenReturn(hiResourceMetadata);
		when(existingCube.getHiResource()).thenReturn(resource);
		when(hiResourceMetadata.getHiResource()).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("resourceUrl");
		when(resource.getResourceId()).thenReturn(1);
		HIResource importResource = cubeImportHandler.importResource("url/url");
		Assert.assertEquals(resource, importResource);

	}

	@Test
	public void ut_a3_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CubeImportHandler cubeImportHandler = new CubeImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HICubeDAOService cubeService = mock(HICubeDAOService.class);
		HIMetadataCube existingCube = mock(HIMetadataCube.class);
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(cubeImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(cubeImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(cubeImportHandler, fileReader);

		Field field3 = CubeImportHandler.class.getDeclaredField("cubeService");
		field3.setAccessible(true);
		field3.set(cubeImportHandler, cubeService);

		Field field4 = AbstractResourceImportHandler.class.getDeclaredField("mdServiceDb");
		field4.setAccessible(true);
		field4.set(cubeImportHandler, mdServiceDb);

		Map<String, HIResource> map = new HashMap<>();
		map.put("url.efwfolder", resource);
		map.put("resourceUrl", resource);

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.getResourceUrlMap()).thenReturn(map);
		when(request.getOnConflict()).thenReturn("don't skip");
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(cubeService.findCubeByResourceId(anyInt())).thenReturn(existingCube);
		when(existingCube.getHiResourceMetadata()).thenReturn(hiResourceMetadata);
		when(existingCube.getHiResource()).thenReturn(resource);
		when(hiResourceMetadata.getHiResource()).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("resourceUrl");
		when(resource.getResourceId()).thenReturn(1);
        when(fileReader.read(any(), any(), any())).thenReturn(existingCube);
		
		HIResource importResource = cubeImportHandler.importResource("url/url");
		Assert.assertEquals(resource, importResource);

	}

	@Test
	public void ut_a4_testSaveMeasures() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeImportHandler cubeImportHandler = new CubeImportHandler();
		Method method = CubeImportHandler.class.getDeclaredMethod("saveMeasures", HIMetadataCube.class, String.class,
				boolean.class);
		method.setAccessible(true);
		HIMetadataCube cube = mock(HIMetadataCube.class);
		HICubeMeasure cubeMeasure = mock(HICubeMeasure.class);
		ImportManagerContext context = mock(ImportManagerContext.class);

		List<HICubeMeasure> measureList = Arrays.asList(cubeMeasure);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(cubeImportHandler, context);

		Field field2 = CubeImportHandler.class.getDeclaredField("cubeService");
		field2.setAccessible(true);
		HICubeDAOService cubeService = mock(HICubeDAOService.class);
		field2.set(cubeImportHandler, cubeService);

		Map<Integer, Integer> tableIdMap = new HashMap<>();
		tableIdMap.put(1, 10);

		when(context.getTableIdMap(anyString())).thenReturn(tableIdMap);
		when(context.getColumnIdMap(anyString())).thenReturn(tableIdMap);
		when(cubeMeasure.getTableId()).thenReturn("1");
		when(cubeMeasure.getColumnId()).thenReturn("1");
		when(cube.getMeasures()).thenReturn(measureList);
		Object invoke = method.invoke(cubeImportHandler, cube, "string", false);
		Assert.assertEquals(measureList, invoke);
	}

	@Test
	public void ut_a5_testSaveMeasures() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeImportHandler cubeImportHandler = new CubeImportHandler();
		Method method = CubeImportHandler.class.getDeclaredMethod("saveMeasures", HIMetadataCube.class, String.class,
				boolean.class);
		method.setAccessible(true);
		HIMetadataCube cube = mock(HIMetadataCube.class);
		HICubeMeasure cubeMeasure = mock(HICubeMeasure.class);
		ImportManagerContext context = mock(ImportManagerContext.class);

		List<HICubeMeasure> measureList = Arrays.asList(cubeMeasure);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(cubeImportHandler, context);

		Field field2 = CubeImportHandler.class.getDeclaredField("cubeService");
		field2.setAccessible(true);
		HICubeDAOService cubeService = mock(HICubeDAOService.class);
		field2.set(cubeImportHandler, cubeService);

		Map<Integer, Integer> tableIdMap = new HashMap<>();
		tableIdMap.put(1, 10);

		when(context.getTableIdMap(anyString())).thenReturn(tableIdMap);
		when(context.getColumnIdMap(anyString())).thenReturn(tableIdMap);
		when(cubeMeasure.getTableId()).thenReturn("1");
		when(cubeMeasure.getColumnId()).thenReturn("1");
		when(cube.getMeasures()).thenReturn(measureList);
		Object invoke = method.invoke(cubeImportHandler, cube, "string", true);
		Assert.assertEquals(measureList, invoke);
	}

}

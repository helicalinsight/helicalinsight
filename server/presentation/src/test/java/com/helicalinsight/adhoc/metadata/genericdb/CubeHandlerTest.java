package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Dimension;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.service.ProcessDetailsService;
import com.helicalinsight.admin.utils.AuthenticationUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CubeHandlerTest {

	@Test
	public void ut_a1_test_populateDimensions() {
		CubeHandler cubeHandler = new CubeHandler();
		JsonObject item = new JsonObject();
		Dimension dimension = mock(Dimension.class);
		when(dimension.getName()).thenReturn("dName");
		when(dimension.getType()).thenReturn("dType");
		cubeHandler.populateDimensions(item, dimension);
		
	}
	@Test
	public void ut_a2_test_saveToDatabase() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CubeHandler cubeHandler = new CubeHandler();
		Metadata metadata = mock(Metadata.class); 
		ProcessDetails processDetails = mock(ProcessDetails.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		ProcessDetailsService processDetailsService = mock(ProcessDetailsService.class);
		
		List<ProcessDetails> pdList = new ArrayList<>();
		pdList.add(processDetails);
	
		JsonObject response = new JsonObject();
		response.addProperty("uuid", "uuid");
		response.addProperty("location", "location");
		
		Field field = CubeHandler.class.getDeclaredField("processDetailsService");
		field.setAccessible(true);
		field.set(cubeHandler, processDetailsService);;
		
		
		when(driverClass.getDriverClass()).thenReturn("dClass");
		when(connectionDetails.getDriverClass()).thenReturn(driverClass);
		when(metadata.getFileName()).thenReturn("fName");
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		
		try(MockedStatic<AuthenticationUtils> mockedStatic = mockStatic(AuthenticationUtils.class)){
			mockedStatic.when(()-> AuthenticationUtils.getUserId()).thenReturn("12");
			cubeHandler.saveToDatabase(pdList, metadata, response);
			
		}
		
	}
	
	@Test
	public void ut_a3_test_measure() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		Method method = CubeHandler.class.getDeclaredMethod("measure", HIMetadataCube.class, JsonObject.class);
		method.setAccessible(true);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		JsonObject item = new JsonObject();
		item.addProperty("id", "12");
		item.addProperty("action", "delete");
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		
		Object invoke = method.invoke(cubeHandler, hiMetadataCube,item);
		assertNull(invoke);
	
	}
	
	@Test
	public void ut_a4_test_measure() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		Method method = CubeHandler.class.getDeclaredMethod("measure", HIMetadataCube.class, JsonObject.class);
		method.setAccessible(true);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		HICubeMeasure hiCubeMeasure = mock(HICubeMeasure.class);
		JsonObject item = new JsonObject();
		item.addProperty("id", "12");
		item.addProperty("action", "save");
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		when(hiCubeDAOService.findCubeMeasure(any(), any())).thenReturn(hiCubeMeasure);
		Object invoke = method.invoke(cubeHandler, hiMetadataCube,item);
		assertEquals(hiCubeMeasure, invoke);
	
	}
	
	@Test
	public void ut_a5_test_measure() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		Method method = CubeHandler.class.getDeclaredMethod("measure", HIMetadataCube.class, JsonObject.class);
		method.setAccessible(true);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		JsonObject item = new JsonObject();
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		
		Object invoke = method.invoke(cubeHandler, hiMetadataCube,item);
		assertNotNull(invoke);
	
	}
	
	@Test
	public void ut_a6_test_getHiDimensionHierarchy() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		Method method = CubeHandler.class.getDeclaredMethod("getHiDimensionHierarchy", HICubeDimension.class, JsonObject.class);
		method.setAccessible(true);
		HICubeDimension hiCubeDimension = mock(HICubeDimension.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		JsonObject item = new JsonObject();
		item.addProperty("id", "12");
		item.addProperty("action", "delete");
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		
		Object invoke = method.invoke(cubeHandler, hiCubeDimension ,item);
		assertNull(invoke);
	
	}
	
	
	@Test
	public void ut_a7_test_getHiCubeHierarchyLevel() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		Method method = CubeHandler.class.getDeclaredMethod("getHiCubeHierarchyLevel", JsonObject.class, HIDimensionHierarchy.class);
		method.setAccessible(true);
		HIDimensionHierarchy hiDimensionHierarchy = mock(HIDimensionHierarchy.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		JsonObject item = new JsonObject();
		item.addProperty("id", "12");
		item.addProperty("action", "delete");
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		
		Object invoke = method.invoke(cubeHandler,item, hiDimensionHierarchy );
		assertNull(invoke);
	
	}
	
	
	@Test
	public void ut_a8_test_getHiCubeDimension() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		Method method = CubeHandler.class.getDeclaredMethod("getHiCubeDimension", HIMetadataCube.class, JsonObject.class);
		method.setAccessible(true);
		HIMetadataCube  hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		JsonObject item = new JsonObject();
		item.addProperty("id", "12");
		item.addProperty("action", "delete");
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		
		Object invoke = method.invoke(cubeHandler, hiMetadataCube ,item);
		assertNull(invoke);
	
	}
	
	@Test
	public void ut_a9_test_getMetadataCube() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		CubeHandler cubeHandler = new CubeHandler();
		
		Method method = CubeHandler.class.getDeclaredMethod("getMetadataCube", HIResourceMetadata.class, JsonObject.class, HIResource.class);
		method.setAccessible(true);
		
		HIResourceMetadata  hiResourceMetadata = mock(HIResourceMetadata.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		HIResource hiResource = mock(HIResource.class);
		JsonObject item = new JsonObject();
		item.addProperty("id", "12");
		item.addProperty("action", "delete");
		
		Field field = CubeHandler.class.getDeclaredField("hiCubeDAOService");
		field.setAccessible(true);
		field.set(cubeHandler, hiCubeDAOService);
		
		
		Object invoke = method.invoke(cubeHandler, hiResourceMetadata ,item, hiResource);
		assertNull(invoke);
	
	}
}

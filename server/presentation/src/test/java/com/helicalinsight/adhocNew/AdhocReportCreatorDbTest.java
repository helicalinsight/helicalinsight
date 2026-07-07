package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AdhocReportCreatorDb;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.model.HIResourceReport;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocReportCreatorDbTest {

	@Test(expected = EfwServiceException.class)
	public void ut_a1_test_executeComponent() {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("location", "location");
		jsonObject.addProperty("state", "state");
		
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceTypeServiceDB resourceTypeServiceDB = mock(ResourceTypeServiceDB.class);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class)).thenReturn(resourceTypeServiceDB);
			adhocReportCreatorDb.executeComponent(jsonObject.toString());
			
		}
	}
	
	@Test
	public void ut_a2_test_saveReport() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("columns", "columns");
		HIResource hiResource = mock(HIResource.class);
		
		Method method = AdhocReportCreatorDb.class.getDeclaredMethod("saveReport", HIResource.class,Date.class,
				JsonObject.class,String.class,HIResource.class,String.class,String.class);
		method.setAccessible(true);
		method.invoke(adhocReportCreatorDb,hiResource,new Date(),jsonObject,"state",hiResource,"1","reportName");
	}
	@Test
	public void ut_a3_test_adhocReportEdit() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("columns", "columns");
		HIResource hiResource = mock(HIResource.class);
		HIResourceReport adhocReport = mock(HIResourceReport.class);
		when(hiResource.getHiResourceReport()).thenReturn(adhocReport);
		Method method = AdhocReportCreatorDb.class.getDeclaredMethod("adhocReportEdit", HIResource.class,Date.class,
				JsonObject.class,String.class,HIResource.class);
		method.setAccessible(true);
		method.invoke(adhocReportCreatorDb,hiResource,new Date(),jsonObject,"state",hiResource);
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_a4_test_getMetadataLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		JsonObject metadata = new JsonObject();
		jsonObject.add("metadata", metadata);
		HIResource hiResource = mock(HIResource.class);
		HIResourceReport adhocReport = mock(HIResourceReport.class);
		when(hiResource.getHiResourceReport()).thenReturn(adhocReport);
		Method method = AdhocReportCreatorDb.class.getDeclaredMethod("getMetadataLocation", 
				JsonObject.class);
		method.setAccessible(true);
		method.invoke(adhocReportCreatorDb,jsonObject);
	}
	
	@Test
	public void ut_a5_test_getMetadataLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		
		HIResource hiResource = mock(HIResource.class);
		HIResourceReport adhocReport = mock(HIResourceReport.class);
		when(hiResource.getHiResourceReport()).thenReturn(adhocReport);
		Method method = AdhocReportCreatorDb.class.getDeclaredMethod("getMetadataLocation", 
				JsonObject.class);
		method.setAccessible(true);
		Object invoke = method.invoke(adhocReportCreatorDb,jsonObject);
		assertNull(invoke);
	}
	@Test
	public void ut_a6_test_getCubeLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		JsonObject cube = new JsonObject();
		cube.addProperty("location", "location");
		cube.addProperty("fileName", "fileName");
		jsonObject.add("cube", cube);
		Method method = AdhocReportCreatorDb.class.getDeclaredMethod("getCubeLocation", 
				JsonObject.class);
		method.setAccessible(true);
		Object invoke = method.invoke(adhocReportCreatorDb,jsonObject);
		assertEquals("location/fileName",invoke);
		
	}
	@Test
	public void ut_a7_test_coreSaveHR() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocReportCreatorDb adhocReportCreatorDb = new AdhocReportCreatorDb();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("columns", "columns");
		HIResource metadataResource = mock(HIResource.class);
		HIResourceHReport hReport = mock(HIResourceHReport.class);
		Method method = AdhocReportCreatorDb.class.getDeclaredMethod("coreSaveHR",HIResourceHReport.class,Date.class, 
				JsonObject.class,String.class,HIResource.class,HIResource.class);
		method.setAccessible(true);
		method.invoke(adhocReportCreatorDb,hReport,new Date(),jsonObject,"state",metadataResource,metadataResource);
		
	}
	
}

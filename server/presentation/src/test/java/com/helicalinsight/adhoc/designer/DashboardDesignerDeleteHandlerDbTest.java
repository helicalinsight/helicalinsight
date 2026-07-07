package com.helicalinsight.adhoc.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DashboardDesignerDeleteHandlerDbTest {

	
	@Test(expected = IncompleteFormDataException.class)
	public void ut_a1_test_executeComponent() {
		DashboardDesignerDeleteHandlerDb dashboardDesignerDeleteHandlerDb = new DashboardDesignerDeleteHandlerDb();
		JsonObject formData = new JsonObject();
		dashboardDesignerDeleteHandlerDb.executeComponent(formData.toString());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ut_a2_test_executeComponent() {
		DashboardDesignerDeleteHandlerDb dashboardDesignerDeleteHandlerDb = new DashboardDesignerDeleteHandlerDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("dir", "dir");
		formData.addProperty("file", "file");
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(serviceDB);
			dashboardDesignerDeleteHandlerDb.executeComponent(formData.toString());
			
		}
	}
	
	@Test
	public void ut_a3_test_executeComponent() throws IOException {
		DashboardDesignerDeleteHandlerDb dashboardDesignerDeleteHandlerDb = new DashboardDesignerDeleteHandlerDb();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIResource resourceByUrl = mock(HIResource.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("dir", "System");
		formData.addProperty("file", "file.txt");
		
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(resourceByUrl);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(serviceDB);
			String executeComponent = dashboardDesignerDeleteHandlerDb.executeComponent(formData.toString());
			String response = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
			assertEquals("The requested file is deleted successfully", response);
			
		}
		
		
	}
	@Test
	public void ut_a4_test_isThreadsafeToCache() {
		DashboardDesignerDeleteHandlerDb dashboardDesignerDeleteHandlerDb = new DashboardDesignerDeleteHandlerDb();
		boolean threadSafeToCache = dashboardDesignerDeleteHandlerDb.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	
}

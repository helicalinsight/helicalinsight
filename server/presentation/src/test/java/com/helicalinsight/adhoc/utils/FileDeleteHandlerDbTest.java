package com.helicalinsight.adhoc.utils;

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
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileDeleteHandlerDbTest {

	@Test
	public void ut_a1_test_executeComponent() {
		FileDeleteHandlerDb db = new FileDeleteHandlerDb();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("file", "file");
		formJson.addProperty("reportFileName", "reportFileName");
		formJson.addProperty("location", "location");
		HIResource resourceByUrl = mock(HIResource.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(resourceByUrl);
		when(resourceByUrl.getResourceId()).thenReturn(123);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			String executeComponent = db.executeComponent(formJson.toString());
			String response = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
			assertEquals("File deleted successfully.", response);
		}

	}
	@Test
	public void ut_a2_test_isThreadSafeToCache() {
		FileDeleteHandlerDb db = new FileDeleteHandlerDb();
		boolean threadSafeToCache = db.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
		
	}
}

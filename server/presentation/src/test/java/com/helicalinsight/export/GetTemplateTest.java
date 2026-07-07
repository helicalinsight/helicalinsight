package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.export.components.GetTemplate;

public class GetTemplateTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		GetTemplate getTemplate = new GetTemplate();
		boolean threadSafeToCache = getTemplate.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	@Test
	public void ut_a2_testExecuteComponent() {
		GetTemplate getTemplate = new GetTemplate();
		JsonObject formData = new JsonObject();
		formData.addProperty("templateId", "11");
		String json = formData.toString();
		
		JsonObject object = new JsonObject();
		JsonObject body = new JsonObject();
		body.addProperty("key","value");
		object.add("body", body);
		
		try(MockedStatic<ExportUtils> mockedStatic = mockStatic(ExportUtils.class)){
			mockedStatic.when(()-> ExportUtils.getTemplatesDirectory()).thenReturn("directory");
			mockedStatic.when(()-> ExportUtils.getFileAsString(anyString())).thenReturn(object.toString());
			String executeComponent = getTemplate.executeComponent(json);
			Assert.assertNotNull(executeComponent);
		}	
	}
}

package com.helicalinsight.export;

import static org.mockito.Mockito.mockStatic;

import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.export.components.DeleteTemplate;

public class DeleteTemplateTest {
	
	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		DeleteTemplate deleteTemplate = new DeleteTemplate();
		boolean threadSafeToCache = deleteTemplate.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	
	@Test(expected = OperationFailedException.class)
	public void ut_a2_testExecuteComponent() {
		DeleteTemplate deleteTemplate = new DeleteTemplate();
		JsonObject formData = new JsonObject();
		formData.addProperty("templateId", "11");
		String json = formData.toString();
		
		try(MockedStatic<ExportUtils> mockedStatic = mockStatic(ExportUtils.class)){
			mockedStatic.when(()-> ExportUtils.getTemplatesDirectory()).thenReturn("directory");
			deleteTemplate.executeComponent(json);
		}
		
	}
	
	@Test
	public void ut_a3_testExecuteComponent() {
		DeleteTemplate deleteTemplate = new DeleteTemplate();
		JsonObject formData = new JsonObject();
		formData.addProperty("templateId", "11");
		String json = formData.toString();
		
		try(MockedStatic<ExportUtils> mockedStatic = mockStatic(ExportUtils.class)){
			try(MockedStatic<Files> mockedStatic2 = mockStatic(Files.class)){
			mockedStatic.when(()-> ExportUtils.getTemplatesDirectory()).thenReturn("directory");
			String executeComponent = deleteTemplate.executeComponent(json);
			Assert.assertNotNull(executeComponent);
		}
		}
		
	}
}

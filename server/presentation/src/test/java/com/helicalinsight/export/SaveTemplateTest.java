package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.export.components.SaveTemplate;

public class SaveTemplateTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		SaveTemplate saveTemplate = new SaveTemplate();
		boolean threadSafeToCache = saveTemplate.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}

	@Test
	public void ut_a2_testExecuteComponent() throws IOException {
		SaveTemplate saveTemplate = new SaveTemplate();
		JsonObject formData = new JsonObject();
		JsonObject template = new JsonObject();
		JsonObject body = new JsonObject();
		body.addProperty("script", "script");
		template.add("body", body);
		formData.add("template", template);
		String json = formData.toString();
		try {
			String executeComponent = saveTemplate.executeComponent(json);
			Assert.assertNotNull(executeComponent);
		} finally {
			String folderPath = "/home/helical/Performance/hi/hi-repository/System/Reports/ExportTemplates";
			File file = new File(folderPath);

		}
	}

	@Test(expected = RuntimeException.class)
	public void ut_a3_testExecuteComponent() throws IOException {
		SaveTemplate saveTemplate = new SaveTemplate();
		JsonObject formData = new JsonObject();
		JsonObject template = new JsonObject();
		JsonObject body = new JsonObject();
		body.addProperty("script", "script");
		template.add("body", body);
		formData.add("template", template);
		String json = formData.toString();
		try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
			mockedStatic.when(() -> FileUtils.writeStringToFile(any(File.class), anyString(), anyString()))
					.thenThrow(new IOException());
			String executeComponent = saveTemplate.executeComponent(json);
			Assert.assertNotNull(executeComponent);
		}finally {
			String folderPath = "/home/helical/Performance/hi/hi-repository/System/Reports/ExportTemplates";
			File file = new File(folderPath);

		}

	}
}

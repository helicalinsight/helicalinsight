package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
		Path templatesDirectory = Files.createTempDirectory("save-template-test");
		try (MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class)) {
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn(templatesDirectory.toString());
			String executeComponent = new SaveTemplate().executeComponent(templateJson());
			String templateId = JsonParser.parseString(executeComponent).getAsJsonObject().get("templateId")
					.getAsString();
			File jsonFile = new File(templatesDirectory.toFile(), templateId + ExportUtils.JSON_EXTENSION);
			File jsFile = new File(templatesDirectory.toFile(), templateId + ExportUtils.JS_EXTENSION);
			Assert.assertTrue(jsonFile.isFile());
			Assert.assertTrue(jsFile.isFile());
		} finally {
			deleteTemplates(templatesDirectory);
		}
		Assert.assertFalse(templatesDirectory.toFile().exists());
	}

	@Test(expected = RuntimeException.class)
	public void ut_a3_testExecuteComponent() throws IOException {
		Path templatesDirectory = Files.createTempDirectory("save-template-test");
		try (MockedStatic<ExportUtils> exportUtils = mockStatic(ExportUtils.class);
				MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
			exportUtils.when(ExportUtils::getTemplatesDirectory).thenReturn(templatesDirectory.toString());
			mockedStatic.when(() -> FileUtils.writeStringToFile(any(File.class), anyString(), anyString()))
					.thenThrow(new IOException());
			new SaveTemplate().executeComponent(templateJson());
		} finally {
			deleteTemplates(templatesDirectory);
		}
	}

	private String templateJson() {
		JsonObject formData = new JsonObject();
		JsonObject template = new JsonObject();
		JsonObject body = new JsonObject();
		body.addProperty("script", "script");
		template.add("body", body);
		formData.add("template", template);
		return formData.toString();
	}

	private void deleteTemplates(Path templatesDirectory) throws IOException {
		File directory = templatesDirectory.toFile();
		if (directory.exists()) {
			FileUtils.deleteDirectory(directory);
		}
	}
}

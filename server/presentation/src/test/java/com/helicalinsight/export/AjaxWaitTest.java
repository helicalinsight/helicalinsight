package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;

public class AjaxWaitTest {
	
	@Test
	public void ut_a1_testWaitForAjax() {
		
		WebDriver driver = mock(ChromeDriver.class);
		when(((JavascriptExecutor) driver).executeScript(anyString())).thenReturn(true);
		AjaxWait.waitForAjax(driver);
	}
	
	@Test
	public void ut_a2_testWaitForAjax() {
		
		WebDriver driver = mock(ChromeDriver.class);
		when(((JavascriptExecutor) driver).executeScript(anyString())).thenReturn(true);
		JsonObject export = new JsonObject();
		export.addProperty("phantomTimeout", "0");
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("export", export);

		try(MockedStatic<JsonUtils> mock = mockStatic(JsonUtils.class)){
			mock.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(jsonObject);
			AjaxWait.waitForAjax(driver);
		}
	}
	
	@Test
	public void ut_a3_testWaitForAjax() {
		
		WebDriver driver = mock(ChromeDriver.class);
		when(((JavascriptExecutor) driver).executeScript(anyString())).thenReturn(true);
		JsonObject export = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("export", export);

		try(MockedStatic<JsonUtils> mock = mockStatic(JsonUtils.class)){
			mock.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(jsonObject);
			AjaxWait.waitForAjax(driver);
		}
	}
	@Test
	public void ut_a4_testWaitForAjax() {
		AjaxWait ajaxWait = new AjaxWait();
		WebDriver driver = mock(ChromeDriver.class);
		when(((JavascriptExecutor) driver).executeScript(anyString())).thenReturn(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("export", null);

		try(MockedStatic<JsonUtils> mock = mockStatic(JsonUtils.class)){
			mock.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(jsonObject);
			ajaxWait.waitForAjax(driver);
		}
	}
}

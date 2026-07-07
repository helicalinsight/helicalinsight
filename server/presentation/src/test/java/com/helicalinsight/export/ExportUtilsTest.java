package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;

public class ExportUtilsTest {

	@Test
	public void testGetTemplatesDirectory() {
		ExportUtils exportUtils = new ExportUtils();
		String templatesDirectory = ExportUtils.getTemplatesDirectory();
		String test= ExportUtils.getReportDirectory()+ File.separator + "ExportTemplates";
		Assert.assertEquals(test, templatesDirectory);
	}
	@Test
	public void testGetTemplatesTempDirectory() {
		String templatesTempDirectory = ExportUtils.getTemplatesTempDirectory();
		Assert.assertEquals(ExportUtils.getReportDirectory()+File.separator+"ExportTemplates"+File.separator+"Temp", templatesTempDirectory);
	}
	@Test(expected = RuntimeException.class)
	public void testGetFileAsString() {
		ExportUtils.getFileAsString("/home/helical/Performance/hi/hi-repository/System/Reports/ExportTemplates/Temp");
	}
	@Test
	public void testSetPrintOptionsAndDiscardFromReportParameters() {
		
		JsonObject printOptionsJson = new JsonObject();
		JsonObject printOptions = new JsonObject();
		printOptions.addProperty("key", "value");
		printOptionsJson.add("printOptions", printOptions);
		String reportParameters = printOptionsJson.toString();
		String setPrintOptionsAndDiscardFromReportParameters = ExportUtils.setPrintOptionsAndDiscardFromReportParameters(reportParameters, printOptions);
		Assert.assertEquals("{}", setPrintOptionsAndDiscardFromReportParameters);
		
	}
	
	@Test
	public void testIsProcessActive_a1() {
		boolean processActive = ExportUtils.isProcessActive(null);
		Assert.assertFalse(processActive);
	}
	
	@Test
	public void testIsProcessActive_a2() {
		Process process = mock(Process.class);
		boolean processActive = ExportUtils.isProcessActive(process);
		Assert.assertFalse(processActive);
	}
	@Test
	public void testIsProcessActive_a3() {
		Process process = mock(Process.class);
		when(process.exitValue()).thenThrow(new IllegalThreadStateException());
		boolean processActive = ExportUtils.isProcessActive(process);
		Assert.assertTrue(processActive);
	}
	
}

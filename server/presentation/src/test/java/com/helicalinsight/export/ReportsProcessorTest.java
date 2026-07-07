package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.FileStatusInspectionUtility;

public class ReportsProcessorTest {

	@Test
	public void ut_a1_testGetPhantomLocation() {
		String phantomLocation = ReportsProcessor.getPhantomLocation();
		Assert.assertNotNull(phantomLocation);
	}

	@Test(expected = EfwException.class)
	public void ut_a2_testGenerateReportUsingHTMLSource() {
		ReportsProcessor processor = new ReportsProcessor();
		Principal principal = mock(Principal.class);

		when(principal.getOrg_name()).thenReturn("helical");
		try (MockedStatic<AuthenticationUtils> mockedStatic = mockStatic(AuthenticationUtils.class)) {
			mockedStatic.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
			processor.generateReportUsingHTMLSource("html", "format", "reportName");
		}
	}

	@Test
	public void ut_a3_testGenerateReportUsingHTMLSource() {
		ReportsProcessor processor = new ReportsProcessor();
		Principal principal = mock(Principal.class);

		when(principal.getOrg_name()).thenReturn("helical");

		try (MockedConstruction<FileStatusInspectionUtility> construction = mockConstruction(
				FileStatusInspectionUtility.class, (mock, context) -> {
					when(mock.isCompletelyWritten(any(File.class), anyString(), anyString())).thenReturn(false);
				})) {
			try (MockedStatic<AuthenticationUtils> mockedStatic = mockStatic(AuthenticationUtils.class)) {
				mockedStatic.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
				List<String> generateReportUsingHTMLSource = processor.generateReportUsingHTMLSource("html", "format",
						"reportName");
				Assert.assertNull(generateReportUsingHTMLSource);
			}
		}
	}

	@Test
	public void ut_a4_testGenerateReportUsingHTMLSource() {
		ReportsProcessor processor = new ReportsProcessor();
		Principal principal = mock(Principal.class);

		when(principal.getOrg_name()).thenReturn("helical");

		try (MockedStatic<AuthenticationUtils> mockedStatic = mockStatic(AuthenticationUtils.class)) {
			mockedStatic.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
			try (MockedStatic<File> mockedStatic2 = mockStatic(File.class)) {
				mockedStatic2.when(()-> File.createTempFile(anyString(), anyString(), any(File.class))).thenThrow(new IOException());
				List<String> generateReportUsingHTMLSource = processor.generateReportUsingHTMLSource("html", "format",
					"reportName");
				Assert.assertNull(generateReportUsingHTMLSource);
			}
		}

	}
	
	@Test(expected = ExportException.class)
	public void ut_a5_testCannotExport() {
		ReportsProcessor processor = new ReportsProcessor();
		processor.cannotExport("errorString", "inputString");
	}
	
	@Test(expected = ExportException.class)
	public void ut_a6_testCannotExport() {
		ReportsProcessor processor = new ReportsProcessor();
		processor.cannotExport("errorString", "EXPORT ERROR:");
	}
	@Test(expected = ExportException.class)
	public void ut_a7_testCannotExport() {
		ReportsProcessor processor = new ReportsProcessor();
		processor.cannotExport("errorString", null);
	}
	@Test(expected = EfwException.class)
	public void ut_a8_testGetScriptLocation() {
		ReportsProcessor processor = new ReportsProcessor();
		processor.getScriptLocation(true);
	}
	@Test
	public void ut_a9_testGetFileLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ReportsProcessor processor = new ReportsProcessor();
		Method method = ReportsProcessor.class.getDeclaredMethod("getFileLocation", String.class);
		method.setAccessible(true);
		method.invoke(processor, "string\\file");
		
	}
	@Test
	public void ut_b1_testGetFileLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ReportsProcessor processor = new ReportsProcessor();
		Method method = ReportsProcessor.class.getDeclaredMethod("getFileLocation", String.class);
		method.setAccessible(true);
		String inputString = "This is a sample string with a pattern ##(example)## and another one ##(123)##.";

		Object invoke = method.invoke(processor, inputString);
		Assert.assertNotNull(invoke);
	}
	@Test
	public void ut_b2_testGetChromeDriverLocation() {
		String chromeDriverLocation = ReportsProcessor.getChromeDriverLocation();
		Assert.assertNotNull(chromeDriverLocation);
	}
}

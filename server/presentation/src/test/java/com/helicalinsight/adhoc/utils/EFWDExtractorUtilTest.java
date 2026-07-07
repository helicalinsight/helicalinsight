package com.helicalinsight.adhoc.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EFWDExtractorUtilTest {
	
	private static final String TEMP_PATH = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	private static final String SOL_PATH = ApplicationProperties.INSTANCE.getSolutionDirectory();
	private static final String SYS_PATH = ApplicationProperties.INSTANCE.getSystemDirectory();

	@Test
	public void ut_a1_test_readEFWDFile() {
		EFWDExtractorUtil efwdExtractorUtil = new EFWDExtractorUtil();
		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
		IProcessor processor = mock(IProcessor.class);
		when(applicationProperties.getSettingPath()).thenReturn("path");
		JsonObject settings = new JsonObject();
		JsonObject Extentions = new JsonObject();
		Extentions.addProperty("efwd", "efwd");
		settings.add("Extentions", Extentions);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(settings);
		when(applicationProperties.getSolutionDirectory())
				.thenReturn(SOL_PATH);
		try (MockedStatic<ConfigurationFileReader> mockedStatic0 = mockStatic(ConfigurationFileReader.class)) {
			try (MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)) {
				try (MockedStatic<ResourceProcessorFactory> mockedStatic2 = mockStatic(
						ResourceProcessorFactory.class)) {
					try (MockedStatic<ApplicationProperties> mockedStatic3 = mockStatic(ApplicationProperties.class)) {
						mockedStatic3.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
						mockedStatic2.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
						String readEFWDFile = EFWDExtractorUtil.readEFWDFile("System", 12, "type");
						assertNull(readEFWDFile);
					}
				}

			}
		}

	}

//	@Test
	public void ut_a2_test_readEFWDFile() throws IOException {
		IProcessor processor = mock(IProcessor.class);
		JsonObject settings = new JsonObject();
		JsonObject Extentions = new JsonObject();
		Extentions.addProperty("efwd", "type");
		settings.add("Extentions", Extentions);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(settings);
		String path = TEMP_PATH;
		File file = new File(path);
		file.mkdir();
		String path2 = TEMP_PATH+File.separator+"file.type";
		File file2 = new File(path2);
		file2.createNewFile();
		JsonObject json = new JsonObject();
		try (MockedStatic<ConfigurationFileReader> mockedStatic0 = mockStatic(ConfigurationFileReader.class)) {
			try (MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)) {
				try (MockedStatic<ResourceProcessorFactory> mockedStatic2 = mockStatic(
						ResourceProcessorFactory.class)) {
					try(MockedConstruction<QueryExecutor> construction = mockConstruction(QueryExecutor.class,(mock,context)->{
						when(mock.getResultSet()).thenReturn(json);
					})){
						mockedStatic2.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
						String readEFWDFile = EFWDExtractorUtil.readEFWDFile("System/Temp", 12, "file");
						
						assertEquals("{}",readEFWDFile);
					}

				}

			}
		}finally {
			file2.delete();
			file.delete();
		}

	}
	
//	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a3_test_readEFWDFile() throws IOException {
		IProcessor processor = mock(IProcessor.class);
		JsonObject settings = new JsonObject();
		JsonObject Extentions = new JsonObject();
		Extentions.addProperty("efwd", "type");
		settings.add("Extentions", Extentions);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(settings);
		String path = SYS_PATH+File.separator+"Temp";
		File file = new File(path);
		file.mkdir();
		String path2 = TEMP_PATH+File.separator+"file.type";
		File file2 = new File(path2);
		file2.createNewFile();
		JsonObject json = new JsonObject();
		try (MockedStatic<ConfigurationFileReader> mockedStatic0 = mockStatic(ConfigurationFileReader.class)) {
			try (MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)) {
				try (MockedStatic<ResourceProcessorFactory> mockedStatic2 = mockStatic(
						ResourceProcessorFactory.class)) {
					try(MockedConstruction<QueryExecutor> construction = mockConstruction(QueryExecutor.class,(mock,context)->{
						when(mock.getResultSet()).thenReturn(null);
					})){
						mockedStatic2.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
						EFWDExtractorUtil.readEFWDFile("System/Temp", 12, "file");
					}

				}

			}
		}finally {
			file2.delete();
			file.delete();
		}

	}
	
	
	

}

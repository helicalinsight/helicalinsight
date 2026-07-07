package com.helicalinsight.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.manager.EfwvfCacheManager;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.validator.ResourceValidator;
import com.helicalinsight.efw.vf.ChartResource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class EfwvfCacheManagerTest {

	@Mock
	private HttpServletRequest requestMock;

	@Mock
	private HttpServletResponse responseMock;

	@InjectMocks
	private EfwvfCacheManager efwvfCacheManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testgetVfIda() {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();

		cacheManager.getVfId();
	}

	@Test
	public void testgetVfFile() {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
		cacheManager.getVfFile();
	}

	@Test
	public void testsetChartData() {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
		JsonArray data = new JsonArray();
		data.add("data");
		cacheManager.setChartData(data);
	}

	@Test
	public void testServeCachedContent() throws IOException {
		EfwvfCacheManager spy = spy(efwvfCacheManager);
		// Arrange
		JsonObject fileContent = new JsonObject();
		JsonArray dataArray = new JsonArray();
		dataArray.add("Data1");
		dataArray.add("Data2");
		fileContent.add("data", dataArray);

		when(requestMock.getAttribute("lastModifiedCache")).thenReturn(new Date().getTime());

		// Use StringWriter to capture the response output
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		when(responseMock.getWriter()).thenReturn(printWriter);
		when(spy.getScript()).thenReturn("script");
		// Act
		boolean result = spy.serveCachedContent(requestMock, responseMock, fileContent);

		assertTrue(result);

		verify(requestMock, times(1)).getAttribute("lastModifiedCache");

	}

	@Test
	public void testServeCachedContent_exception() throws IOException {
		EfwvfCacheManager spy = spy(efwvfCacheManager);
		// Arrange
		JsonObject fileContent = new JsonObject();
		JsonArray dataArray = new JsonArray();
		dataArray.add("Data1");
		dataArray.add("Data2");
		fileContent.add("data", dataArray);

		when(requestMock.getAttribute("lastModifiedCache")).thenReturn(new Date().getTime());
		when(responseMock.getWriter()).thenThrow(new IOException("Failed to get PrintWriter"));

		when(spy.getScript()).thenReturn("script");

		boolean result = spy.serveCachedContent(requestMock, responseMock, fileContent);

		assertFalse(result);

		verify(requestMock, times(1)).getAttribute("lastModifiedCache");

	}

	// ----------------
	@Test
	public void testGetScript() {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
		ChartResource chartResource = mock(ChartResource.class);
		try {
			Field field = EfwvfCacheManager.class.getDeclaredField("chartResource");
			field.setAccessible(true);
			field.set(cacheManager, chartResource);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(chartResource.getScript()).thenReturn("script");
		String script = cacheManager.getScript();
		assertEquals(script, "script");
	}

	@Test
	public void testSetRequestData_a1() {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
		IProcessor processor = mock(IProcessor.class);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("dir", "AB:");
		jsonObject.addProperty("vf_file", "vfFile");
		jsonObject.addProperty("vf_id", "123");
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		object.add("Charts", array);

		when(processor.getJsonObject(anyString(), any(Boolean.class))).thenReturn(object);
		when(applicationProperties.getSolutionDirectory()).thenReturn("AB:");
		try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				try (MockedConstruction<ResourceValidator> validator = mockConstruction(ResourceValidator.class)) {
					ResourceValidator resourceValidator = new ResourceValidator(object);
					when(resourceValidator.newValidateVf()).thenReturn(false);
					mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
					factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					cacheManager.setRequestData(jsonObject.toString());
				}
			}
		}

	}

	@Test
	public void testSetRequestData_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
			JsonObject object = new JsonObject();
			JsonArray array = new JsonArray();
			JsonObject chart = new JsonObject();
			chart.addProperty("@id", 123);
			JsonObject data = new JsonObject();
			JsonArray arr = new JsonArray();
			arr.add("12");
			data.add("DataSource", arr);
			
			chart.add("prop", data);
			array.add(chart);
			object.add("Charts", array);

			
			
							
			ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
			IProcessor processor = mock(IProcessor.class);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "AB:");
			jsonObject.addProperty("vf_file", "vfFile");
			jsonObject.addProperty("vf_id", "123");

			when(processor.getJsonObject(anyString(), any(Boolean.class))).thenReturn(object);
			when(applicationProperties.getSolutionDirectory()).thenReturn("AB:");
			try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
						mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
					factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					
					cacheManager.setRequestData(jsonObject.toString());
				}
			}
		}

	@Test
	public void testSetRequestData_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
			JsonObject object = new JsonObject();
			JsonArray array = new JsonArray();
			JsonObject chart = new JsonObject();
			chart.addProperty("@id", 123);
			JsonObject data = new JsonObject();
			
			data.addProperty("DataSource", "12");
			
			chart.add("prop", data);
			array.add(chart);
			object.add("Charts", array);

			
			
							
			ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
			IProcessor processor = mock(IProcessor.class);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "AB:");
			jsonObject.addProperty("vf_file", "vfFile");
			jsonObject.addProperty("vf_id", "123");

			when(processor.getJsonObject(anyString(), any(Boolean.class))).thenReturn(object);
			when(applicationProperties.getSolutionDirectory()).thenReturn("AB:");
			try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
						mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
					factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					
					cacheManager.setRequestData(jsonObject.toString());
				}
			}
		}

	@Test
	public void testSetRequestData_a4() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
			JsonObject object = new JsonObject();
			JsonArray array = new JsonArray();
			JsonObject chart = new JsonObject();
			chart.addProperty("@id", 123);
			JsonObject data = new JsonObject();
			
			data.addProperty("DataSource", "12");
			
			chart.add("prop", data);
			array.add(chart);
			object.add("Charts", array);

			
			
							
			ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
			IProcessor processor = mock(IProcessor.class);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "AB:");
			jsonObject.addProperty("vf_file", "vfFile");
			jsonObject.addProperty("vf_id", "1234");

			when(processor.getJsonObject(anyString(), any(Boolean.class))).thenReturn(object);
			when(applicationProperties.getSolutionDirectory()).thenReturn("AB:");
			try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
						mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
					factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					
					cacheManager.setRequestData(jsonObject.toString());
				}
			}
		}

	@Test
	public void testSetRequestData_a5() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwvfCacheManager cacheManager = new EfwvfCacheManager();
			JsonObject object = new JsonObject();
			JsonArray array = new JsonArray();
			
			object.add("Charts", array);

			
			
							
			ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
			IProcessor processor = mock(IProcessor.class);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "AB:");
			jsonObject.addProperty("vf_file", "vfFile");
			jsonObject.addProperty("vf_id", "1234");

			when(processor.getJsonObject(anyString(), any(Boolean.class))).thenReturn(object);
			when(applicationProperties.getSolutionDirectory()).thenReturn("AB:");
			try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
						mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
					factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					
					cacheManager.setRequestData(jsonObject.toString());
				}
			}
		}

}

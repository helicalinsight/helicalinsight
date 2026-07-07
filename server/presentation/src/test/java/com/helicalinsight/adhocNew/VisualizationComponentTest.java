package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.VisualizationComponent;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.validator.ResourceValidator;
import com.helicalinsight.efw.vf.ChartResource;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VisualizationComponentTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		VisualizationComponent component = new VisualizationComponent();
		component.isThreadSafeToCache();
	}
	@Test(expected = EfwServiceException.class)
	public void ut_a2_test_executeComponent() {
		VisualizationComponent component = new VisualizationComponent();
		JsonObject obj  = new JsonObject();
		JsonArray vf = new JsonArray();
		obj.add("vf",vf);
		String jsonFormData = obj.toString() ;
		component.executeComponent(jsonFormData);
	}
	@Test(expected = EfwServiceException.class)
	public void ut_a3_test_executeComponent() {
		VisualizationComponent component = new VisualizationComponent();
		JsonObject obj  = new JsonObject();
		String jsonFormData = obj.toString() ;
		component.executeComponent(jsonFormData);
	}
	
	@Test(expected = EfwServiceException.class)
	public void ut_a4_test_executeComponent() {
		VisualizationComponent component = new VisualizationComponent();
		JsonObject obj  = new JsonObject();
		JsonObject vf = new JsonObject();
		vf.addProperty("dir", "System/Temp");
		vf.addProperty("vf_file", "file.vf");
		vf.addProperty("vf_id", "23");
		obj.add("vf", vf);
		String jsonFormData = obj.toString() ;
		component.executeComponent(jsonFormData);
	}
	
	@Test(expected = EfwServiceException.class)
	public void ut_a5_test_executeComponent() throws IOException {
		VisualizationComponent component = new VisualizationComponent();
		JsonObject obj  = new JsonObject();
		JsonObject vf = new JsonObject();
		vf.addProperty("dir", "System");
		vf.addProperty("vf_file", "Temp");
		vf.addProperty("vf_id", "23");
		obj.add("vf", vf);
		String jsonFormData = obj.toString() ;
		
		String path = "/home/helical/Performance/hi/hi-repository/System/Temp";
		File file = new File(path);
		file.mkdirs();
		
		JsonObject fileAsJson = new JsonObject();
		
				
		IProcessor processor = mock(IProcessor.class);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(fileAsJson);
		try(MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			try(MockedConstruction<ResourceValidator> construction = mockConstruction(ResourceValidator.class,(mock,context)->{
				when(mock.newValidateVf()).thenReturn(false);
			})){
				mockedStatic.when(()-> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				component.executeComponent(jsonFormData);
				
			}
			
		}finally {
			file.delete();
		}
		
	}
	
	
	@Test(expected = EfwServiceException.class)
	public void ut_a6_test_executeComponent() throws IOException {
		VisualizationComponent component = new VisualizationComponent();
		JsonObject obj  = new JsonObject();
		JsonObject vf = new JsonObject();
		vf.addProperty("dir", "System");
		vf.addProperty("vf_file", "Temp");
		vf.addProperty("vf_id", "23");
		obj.add("vf", vf);
		String jsonFormData = obj.toString() ;
		
		String path = "/home/helical/Performance/hi/hi-repository/System/Temp";
		File file = new File(path);
		file.mkdirs();
		
		JsonObject fileAsJson = new JsonObject();
		JsonArray charts = new JsonArray();
		fileAsJson.add("Charts", charts);
		
		IProcessor processor = mock(IProcessor.class);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(fileAsJson);
		try(MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			try(MockedConstruction<ResourceValidator> construction = mockConstruction(ResourceValidator.class,(mock,context)->{
				when(mock.newValidateVf()).thenReturn(true);
			})){
				mockedStatic.when(()-> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				component.executeComponent(jsonFormData);
				
			}
			
		}finally {
			file.delete();
		}
		
	}

	@Test
	public void ut_a7_test_executeComponent() throws IOException {
		VisualizationComponent component = new VisualizationComponent();
		JsonObject obj  = new JsonObject();
		JsonObject vf = new JsonObject();
		vf.addProperty("dir", "System");
		vf.addProperty("vf_file", "Temp");
		vf.addProperty("vf_id", "23");
		obj.add("vf", vf);
		String jsonFormData = obj.toString() ;
		
		String path = "/home/helical/Performance/hi/hi-repository/System/Temp";
		File file = new File(path);
		file.mkdirs();
		
		JsonObject fileAsJson = new JsonObject();
		JsonArray charts = new JsonArray();
		JsonObject chart = new JsonObject();
		chart.addProperty("id", 23);
		JsonObject chartData = new JsonObject();
		chart.add("prop", chartData);
		charts.add(chart);
		fileAsJson.add("Charts", charts);
		
		IProcessor processor = mock(IProcessor.class);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(fileAsJson);
		try(MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			try(MockedConstruction<ResourceValidator> construction = mockConstruction(ResourceValidator.class,(mock,context)->{
				when(mock.newValidateVf()).thenReturn(true);
			})){
				try(MockedConstruction<ChartResource> construction2 = mockConstruction(ChartResource.class,(mock,context)->{
					when(mock.getScript()).thenReturn("script");
				})){
					mockedStatic.when(()-> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					String executeComponent = component.executeComponent(jsonFormData);
					String result = JsonParser.parseString(executeComponent).getAsJsonObject().get("script").getAsString();
					assertEquals("script", result);
				}
				
			}
			
		}finally {
			file.delete();
		}
		
	}
}

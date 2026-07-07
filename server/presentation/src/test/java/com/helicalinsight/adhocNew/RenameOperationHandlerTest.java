package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.RenameOperationHandler;
import com.helicalinsight.adhoc.designer.EfwDashboardDesigner;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.datasource.managed.jaxb.EFWCE;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efw;
import com.helicalinsight.resourcesecurity.jaxb.EfwFolder;
import com.helicalinsight.resourcesecurity.jaxb.EfwResult;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RenameOperationHandlerTest {

	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a1_test_handle() {
		RenameOperationHandler handler = new RenameOperationHandler();
		String sourceArray = "[[]]";
		handler.handle(sourceArray);
	}
	
	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a2_test_handle() {
		RenameOperationHandler handler = new RenameOperationHandler();
		String sourceArray = "[]";
		handler.handle(sourceArray);
	}
	
	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a3_test_handle() {
		RenameOperationHandler handler = new RenameOperationHandler();
		String sourceArray = null;
		handler.handle(sourceArray);
	}
	@Test(expected = OperationFailedException.class)
	public void ut_a4_test_handle() {
		RenameOperationHandler handler = new RenameOperationHandler();
		JsonArray arr = new JsonArray();
		JsonArray arr2 = new JsonArray();
		arr2.add(1);
		arr.add(arr2);
		String sourceArray = arr.toString();
		handler.handle(sourceArray);
		
	}
	
	@Test
	public void ut_a5_test_isSourceArrayValid() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("isSourceArrayValid", String.class);
		method.setAccessible(true);
		JsonArray arr = new JsonArray();
		method.invoke(handler, arr.toString());
	}
	@Test
	public void ut_a6_test_rename() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("rename", Map.class);
		method.setAccessible(true);
		Map<String, String> map = new HashMap<>();
		map.put("System", "Temp");
		
		EfwFolder efwFolder = mock(EfwFolder.class);
		File file = new File(ApplicationProperties.INSTANCE.getSystemDirectory()+File.separator+ "index.efwfolder");
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(EfwFolder.class, file)).thenReturn(efwFolder);
			method.invoke(handler, map);
		}
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_a7_test_rename() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		RenameOperationHandler handler = new RenameOperationHandler();
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("rename", Map.class);
		method.setAccessible(true);
		Map<String, String> map = new HashMap<>();
		map.put("temp.txt", "Temp");
		File f = new File(ApplicationProperties.INSTANCE.getSolutionDirectory() +File.separator+"temp.txt");
		f.createNewFile();
		try{
			method.invoke(handler, map);
		}
		finally {
			f.delete();
		}
	}
	
	@Test
	public void ut_a8_test_rename() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		RenameOperationHandler handler = new RenameOperationHandler();
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("rename", Map.class);
		method.setAccessible(true);
		Map<String, String> map = new HashMap<>();
		map.put("temp.metadata", "Temp");
		File f = new File(ApplicationProperties.INSTANCE.getSolutionDirectory() +File.separator+"temp.metadata");
		f.createNewFile();
		Metadata metadata = mock(Metadata.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(Metadata.class, f)).thenReturn(metadata);
			method.invoke(handler, map);
		}
		finally {
			f.delete();
		}
	}
	
	@Test
	public void ut_a9_test_renameAll() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		RenameOperationHandler handler = new RenameOperationHandler();
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("renameAll", List.class, String.class);
		method.setAccessible(true);
		List<Map<String, String>> listOfMaps = new ArrayList<>();
		JsonArray arr= new JsonArray();
		JsonArray json = new JsonArray();
		json.add("System");
		json.add("temp.efw");
		arr.add(json);
	
		Field field = RenameOperationHandler.class.getDeclaredField("listOfMaps");
		field.setAccessible(true);
		field.set(handler, listOfMaps);;
		
		EfwFolder efwFolder = mock(EfwFolder.class);
		File file = new File(ApplicationProperties.INSTANCE.getSolutionDirectory() +File.separator+"System" +File.separator+ "index.efwfolder");
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(EfwFolder.class, file)).thenReturn(efwFolder);
			Object invoke = method.invoke(handler, listOfMaps, arr.toString());
			boolean asBoolean = JsonParser.parseString(invoke.toString()).getAsBoolean();
			assertEquals(true,asBoolean);
		}
	}
	@Test
	public void ut_b1_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		Efwsr efwsr = mock(Efwsr.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(Efwsr.class, fileToBeRenamed)).thenReturn(efwsr);
			method.invoke(handler, fileToBeRenamed, "newName", "efwsr");
			
		}
		
	}
	
	@Test
	public void ut_b2_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		EfwResult efwResult = mock(EfwResult.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(EfwResult.class, fileToBeRenamed)).thenReturn(efwResult);
			method.invoke(handler, fileToBeRenamed, "newName", "result");
			
		}
		
	}
	
	@Test
	public void ut_b3_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		AdhocReport adhocReport = mock(AdhocReport.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(AdhocReport.class, fileToBeRenamed)).thenReturn(adhocReport);
			method.invoke(handler, fileToBeRenamed, "newName", "report");
			
		}
		
	}
	
	@Test
	public void ut_b4_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		EfwDashboardDesigner efwDashboardDesigner = mock(EfwDashboardDesigner.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(EfwDashboardDesigner.class, fileToBeRenamed)).thenReturn(efwDashboardDesigner);
			method.invoke(handler, fileToBeRenamed, "newName", "efwdd");
			
		}
		
	}
	
	@Test
	public void ut_b5_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		Efw efw = mock(Efw.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(Efw.class, fileToBeRenamed)).thenReturn(efw);
			method.invoke(handler, fileToBeRenamed, "newName", "efw");
			
		}
		
	}
	@Test
	public void ut_b6_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		EfwDashboardDesigner efwDashboardDesigner = mock(EfwDashboardDesigner.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(EfwDashboardDesigner.class, fileToBeRenamed)).thenReturn(efwDashboardDesigner);
			method.invoke(handler, fileToBeRenamed, "newName", "efwdx");
			
		}
		
	}
	@Test
	public void ut_b7_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		EFWCE eFWCE = mock(EFWCE.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(EFWCE.class, fileToBeRenamed)).thenReturn(eFWCE);
			method.invoke(handler, fileToBeRenamed, "newName", "efwce");
			
		}
		
	}
	@Test
	public void ut_b8_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		HCReport hCReport = mock(HCReport.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(HCReport.class, fileToBeRenamed)).thenReturn(hCReport);
			method.invoke(handler, fileToBeRenamed, "newName", "hcr");
			
		}
		
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_b9_test_modifyXml() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RenameOperationHandler handler = new RenameOperationHandler();
		File fileToBeRenamed = mock(File.class);
		
		Method method = RenameOperationHandler.class.getDeclaredMethod("modifyXml", File.class, String.class, String.class);
		method.setAccessible(true);
			
		Efw efw = mock(Efw.class);
		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
			mockedStatic.when(()->  JaxbUtils.unMarshal(Efw.class, fileToBeRenamed)).thenReturn(efw);
			method.invoke(handler, fileToBeRenamed, "newName", "dummy");
			
		}
		
	}
	
	
}

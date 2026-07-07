package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.EfwdMetadataHandler;
import com.helicalinsight.adhoc.EnhancedMetadataWorkflowComponent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnhancedMetadataWorkflowComponentTest {

	@Test(expected = InvocationTargetException.class)
	public void ut_a1_test_createMetadata() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EnhancedMetadataWorkflowComponent component = new EnhancedMetadataWorkflowComponent();
		
		Method method = EnhancedMetadataWorkflowComponent.class.getDeclaredMethod("createMetadata", JsonObject.class, String.class
				, Connection.class, JsonObject.class);
		method.setAccessible(true);
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extension";
		Connection connection = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		
		try(MockedConstruction<EfwdMetadataHandler> construction = mockConstruction(EfwdMetadataHandler.class,(mock,context)->{
			
		})){
			method.invoke(component, parameters,efwdFileNameWithExtension,connection,connectionJson);
			
		}
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_a2_test_getValidatedParameters() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EnhancedMetadataWorkflowComponent component = new EnhancedMetadataWorkflowComponent();
		
		Method method = EnhancedMetadataWorkflowComponent.class.getDeclaredMethod("getValidatedParameters", JsonObject.class);
		method.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
	
		method.invoke(component, requestParameterJson);
			
		
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_a3_test_getValidatedParameters() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EnhancedMetadataWorkflowComponent component = new EnhancedMetadataWorkflowComponent();
		
		Method method = EnhancedMetadataWorkflowComponent.class.getDeclaredMethod("getValidatedParameters", JsonObject.class);
		method.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.add("parameters", new JsonObject());
		
		method.invoke(component, requestParameterJson);
			
		
	}
	
	@Test
	public void ut_a4_test_getValidatedParameters() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EnhancedMetadataWorkflowComponent component = new EnhancedMetadataWorkflowComponent();
		
		Method method = EnhancedMetadataWorkflowComponent.class.getDeclaredMethod("getValidatedParameters", JsonObject.class);
		method.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("key", "value");
		requestParameterJson.add("parameters", jsonObject);
		
		Object invoke = method.invoke(component, requestParameterJson);
		assertEquals(jsonObject, invoke);
			
		
	}
}

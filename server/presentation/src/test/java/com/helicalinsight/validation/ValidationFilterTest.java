package com.helicalinsight.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.FilterUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.validation.filter.ValidationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ValidationFilterTest {

	@Test
	public void testinit() throws ServletException {
		ValidationFilter filter = new ValidationFilter();
		FilterConfig filterConfig = null;
		filter.init(filterConfig);
	}
	@Test
	public void testDestroy() {
		ValidationFilter filter = new ValidationFilter();
		filter.destroy();
	}
	
		
	@Test
	public void testGetValidationMessage_JsonObject() {
		
		
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
		List<String> keys = Arrays.asList("key1","key2");
		JsonObject jsonObject = new JsonObject();
		JsonObject jObject = new JsonObject();
		jsonObject.add("key1", jObject);
		mockedStatic.when(()  ->  JsonUtils.getKeys(jsonObject)).thenReturn(keys);
		
		ValidationFilter filter = new ValidationFilter();
		filter.getValidationMessage(jsonObject);
		}
	}
	
	@Test
	public void testGetValidationMessage_JsonArray() {
		
		
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
		List<String> keys = Arrays.asList("key1","key2");
		JsonObject jsonObject = new JsonObject();
		JsonArray jArray = new JsonArray();
		jArray.add("jsonArray");
		jsonObject.add("key1", jArray);
		mockedStatic.when(()  ->  JsonUtils.getKeys(jsonObject)).thenReturn(keys);
		
		ValidationFilter filter = new ValidationFilter();
		filter.getValidationMessage(jsonObject);
		}
	}
	
	@Test
	public void testGetValidationMessage_String() {
		
		
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
		List<String> keys = Arrays.asList("key1","key2");
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("key1", "sample");
		mockedStatic.when(()  ->  JsonUtils.getKeys(jsonObject)).thenReturn(keys);
		
		ValidationFilter filter = new ValidationFilter();
		filter.getValidationMessage(jsonObject);
		}
	}
	
	@Test
	public void testDoFilter_a1() throws IOException, ServletException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		try(MockedStatic<FilterUtils> mockedStatic = mockStatic(FilterUtils.class)){
			
			JsonArray urlExcludePatterns = new JsonArray();
            mockedStatic.when(() -> FilterUtils.newIsExcludePattern(any(JsonArray.class), anyString())).thenReturn(true);
            ValidationFilter obj =  new ValidationFilter();
            HttpServletRequest request = mock (HttpServletRequest.class);
    		HttpServletResponse response = mock(HttpServletResponse.class);
    		FilterChain chain = mock(FilterChain.class);
    		Field field = obj.getClass().getDeclaredField("allowValidation");
    		field.setAccessible(true);
    		field.set(obj, true);
    		Field field2 = obj.getClass().getDeclaredField("urlExcludePatterns");
            field2.setAccessible(true);
            field2.set(obj, urlExcludePatterns);
    		when(request.getServletPath()).thenReturn("url");
			ValidationFilter filter = new ValidationFilter();
			filter.doFilter(request, response, chain);
			verify(chain, times(1)).doFilter(request, response);
		}
		
	}
	
	@Test
	public void testDoFilter_a2() {
		 HttpServletRequest request = mock (HttpServletRequest.class);
 		HttpServletResponse response = mock(HttpServletResponse.class);
 		FilterChain chain = mock(FilterChain.class);
 		Field field;
 		ValidationFilter obj =  new ValidationFilter();
		try {
			field = obj.getClass().getDeclaredField("allowValidation");
			field.setAccessible(true);
	 		field.set(obj, true);
	 		when(request.getServletPath()).thenReturn("url");
	 		
            JsonObject validationXmlJson = new JsonObject();
            JsonObject mapping = new JsonObject();
            mapping.addProperty("class", "ValidationCalss");
            JsonArray url = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("pattern", "url.html");
            jsonObject.addProperty("class", "");
            url.add(jsonObject);
            mapping.add("url", url);
            validationXmlJson.add("mapping", mapping);
            
            Field field2 = obj.getClass().getDeclaredField("validationXmlJson");
            field2.setAccessible(true);
            field2.set(obj, validationXmlJson);
            Field field3 = obj.getClass().getDeclaredField("configurationsDirectory");
            field3.setAccessible(true);
            field3.set(obj, "configurationsDirectory");
            try (MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)) {
            	mockedStatic.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            	IValidation iValidation = mock(IValidation.class);
            	try(MockedStatic<FactoryMethodWrapper> mockedStaticFactory = mockStatic(FactoryMethodWrapper.class)){
            		mockedStaticFactory.when(() ->FactoryMethodWrapper.getTypedInstance("ValidationClass", IValidation.class) ).thenReturn(iValidation);
            		
            		try (MockedStatic<JsonUtils> mockedStaticJson = mockStatic(JsonUtils.class)) {
            			JsonObject httpRequestJson = new JsonObject();
            			mockedStaticJson.when(() -> JsonUtils.newHttpRequestToFormData(request)).thenReturn(httpRequestJson);
            		
            			ValidationFilter filter = new ValidationFilter();
            			filter.doFilter(request, response, chain);
            		}
            	}
            	
            }
            
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
	
	@Test
	public void testDoFilter_a3() {
		 HttpServletRequest request = mock (HttpServletRequest.class);
 		HttpServletResponse response = mock(HttpServletResponse.class);
 		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
 		ValidationFilter obj =  new ValidationFilter();
 		FilterChain chain = mock(FilterChain.class);
 		Field field;
		try {
			field = obj.getClass().getDeclaredField("allowValidation");
			field.setAccessible(true);
	 		field.set(obj, true);
	 		when(request.getServletPath()).thenReturn("url");
	 		
            JsonObject validationXmlJson = new JsonObject();
            JsonObject mapping = new JsonObject();
            mapping.addProperty("class", "ValidationCalss");
            JsonArray url = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("pattern", "url.html");
            jsonObject.addProperty("class", "");
            url.add(jsonObject);
            mapping.add("url", url);
            validationXmlJson.add("mapping", mapping);
            
            Field field2 = obj.getClass().getDeclaredField("validationXmlJson");
            field2.setAccessible(true);
            field2.set(obj, validationXmlJson);
            Field field3 = obj.getClass().getDeclaredField("configurationsDirectory");
            field3.setAccessible(true);
            field3.set(obj, "configurationsDirectory");
            try (MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)) {
            	mockedStatic.when(() -> ControllerUtils.isAjax(request)).thenReturn(false);
            	IValidation iValidation = mock(IValidation.class);
            	try(MockedStatic<FactoryMethodWrapper> mockedStaticFactory = mockStatic(FactoryMethodWrapper.class)){
            		mockedStaticFactory.when(() ->FactoryMethodWrapper.getTypedInstance("ValidationClass", IValidation.class) ).thenReturn(iValidation);
            		
            		try (MockedStatic<JsonUtils> mockedStaticJson = mockStatic(JsonUtils.class)) {
            			JsonObject httpRequestJson = new JsonObject();
            			mockedStaticJson.when(() -> JsonUtils.newHttpRequestToFormData(request)).thenReturn(httpRequestJson);
            		
            			 when(request.getRequestDispatcher("WEB-INF/jsp/errorPages/errorPage.jsp")).thenReturn(dispatcher);
            			obj.doFilter(request, response, chain);
            		}
            	}
            	
            }
            
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
	
	@Test
	public void testDoFilter_a4() {
		 HttpServletRequest request = mock (HttpServletRequest.class);
 		HttpServletResponse response = mock(HttpServletResponse.class);
 		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
 		ValidationFilter obj =  new ValidationFilter();
 		FilterChain chain = mock(FilterChain.class);
 		Field field;
		try {
			field = obj.getClass().getDeclaredField("allowValidation");
			field.setAccessible(true);
	 		field.set(obj, true);
	 		when(request.getServletPath()).thenReturn("url");
	 		
            JsonObject validationXmlJson = new JsonObject();
            JsonObject mapping = new JsonObject();
            mapping.addProperty("class", "ValidationCalss");
            JsonArray url = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("pattern", "url.html");
            //jsonObject.addProperty("pattern", "/services.html");
            jsonObject.add("class", null);
            url.add(jsonObject);
            mapping.add("url", url);
            validationXmlJson.add("mapping", mapping);
            
            Field field2 = obj.getClass().getDeclaredField("validationXmlJson");
            field2.setAccessible(true);
            field2.set(obj, validationXmlJson);
            Field field3 = obj.getClass().getDeclaredField("configurationsDirectory");
            field3.setAccessible(true);
            field3.set(obj, "configurationsDirectory");
            
            	try(MockedStatic<FactoryMethodWrapper> mockedStaticFactory = mockStatic(FactoryMethodWrapper.class)){
            		IValidation iValidation = mock(IValidation.class);
            		mockedStaticFactory.when(() ->FactoryMethodWrapper.getTypedInstance(any(String.class), eq(IValidation.class) )).thenReturn(iValidation);
            		
            		try (MockedStatic<JsonUtils> mockedStaticJson = mockStatic(JsonUtils.class)) {
            			JsonObject httpRequestJson = new JsonObject();
            			mockedStaticJson.when(() -> JsonUtils.newHttpRequestToFormData(request)).thenReturn(httpRequestJson);
            		
            			when(iValidation.isValid(any(JsonObject.class), any(JsonObject.class))).thenReturn(true);
            			
            			obj.doFilter(request, response, chain);
            			
            		}
            	}
            	
            
            
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
	@Test
	public void testDoFilter_a5() {
		 HttpServletRequest request = mock (HttpServletRequest.class);
 		HttpServletResponse response = mock(HttpServletResponse.class);
 		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
 		ValidationFilter obj =  new ValidationFilter();
 		FilterChain chain = mock(FilterChain.class);
 		Field field;
		try {
			field = obj.getClass().getDeclaredField("allowValidation");
			field.setAccessible(true);
	 		field.set(obj, true);
	 		when(request.getServletPath()).thenReturn("/services");
	 		
            JsonObject validationXmlJson = new JsonObject();
            JsonObject mapping = new JsonObject();
            mapping.addProperty("class", "ValidationCalss");
            JsonArray url = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            
            jsonObject.addProperty("pattern", "/services.html");
            jsonObject.add("class", null);
            url.add(jsonObject);
            mapping.add("url", url);
            validationXmlJson.add("mapping", mapping);
            
            Field field2 = obj.getClass().getDeclaredField("validationXmlJson");
            field2.setAccessible(true);
            field2.set(obj, validationXmlJson);
            Field field3 = obj.getClass().getDeclaredField("configurationsDirectory");
            field3.setAccessible(true);
            field3.set(obj, "configurationsDirectory");
            
            	try(MockedStatic<FactoryMethodWrapper> mockedStaticFactory = mockStatic(FactoryMethodWrapper.class)){
            		IValidation iValidation = mock(IValidation.class);
            		mockedStaticFactory.when(() ->FactoryMethodWrapper.getTypedInstance(any(String.class), eq(IValidation.class) )).thenReturn(iValidation);
            		
            		try (MockedStatic<JsonUtils> mockedStaticJson = mockStatic(JsonUtils.class)) {
            			JsonObject httpRequestJson = new JsonObject();
            			mockedStaticJson.when(() -> JsonUtils.newHttpRequestToFormData(request)).thenReturn(httpRequestJson);
            		
            			when(iValidation.isValid(any(JsonObject.class), any(JsonObject.class))).thenReturn(true);
            			
            			ValidationFilter filter = new ValidationFilter();
            			filter.doFilter(request, response, chain);
            			
            		}
            	}
            	
            
            
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
	@Test(expected = RuntimeException.class)
	public void testDoFilter_a6_Exception() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		ValidationFilter obj =  new ValidationFilter();
		Field field = obj.getClass().getDeclaredField("allowValidation");
		field.setAccessible(true);
 		field.set(obj, true);
 		doThrow(new RuntimeException("exception")).when(chain).doFilter(request, response);
		obj.doFilter(request, response, chain);
		
	}
	@Test
	public void testDoFilter_a7_Exception() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		ValidationFilter obj =  new ValidationFilter();
		Field field = obj.getClass().getDeclaredField("allowValidation");
		field.setAccessible(true);
 		field.set(obj, true);
 		when(request.getServletPath()).thenThrow(new JsonSyntaxException("Invalid JSON"));
		obj.doFilter(request, response, chain);
		
	}
	@Test(expected =  EfwServiceException.class)
	public void testDoFilter_a8_Exception() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		ValidationFilter obj = new ValidationFilter();
		Field field = obj.getClass().getDeclaredField("allowValidation");
		field.setAccessible(true);
 		field.set(obj, true);
 		when(request.getServletPath()).thenThrow(new EfwServiceException("EfwServiceException"));
		obj.doFilter(request, response, chain);
		
	}
	@Test
	public void  testDoFilter_a9() throws IOException, ServletException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		ValidationFilter obj =  new ValidationFilter();
		Field field = obj.getClass().getDeclaredField("allowValidation");
		field.setAccessible(true);
 		field.set(obj, false);
		obj.doFilter(request, response, chain);
		
	}
	@Test
	public void testDoFilter_b1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		ValidationFilter obj =  new ValidationFilter();
		Field field = obj.getClass().getDeclaredField("allowValidation");
		field.setAccessible(true);
 		field.set(obj, true);
 		when(request.getServletPath()).thenReturn("url.html");
		obj.doFilter(request, response, chain);
		
	}


}

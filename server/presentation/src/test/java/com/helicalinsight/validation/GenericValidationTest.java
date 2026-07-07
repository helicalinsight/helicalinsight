package com.helicalinsight.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.validation.form.GenericValidation;
import com.helicalinsight.validation.form.GroovyCodeExecutionManager;

@RunWith(MockitoJUnitRunner.class)
public class GenericValidationTest {

	@Test
	public void testJsonNavigator() {
		// Arrange
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", "John");
		jsonObject.addProperty("age", 30);

		JsonArray jsonArray = new JsonArray();
		jsonArray.add("item1");
		jsonArray.add("item2");
		jsonObject.add("items", jsonArray);

		GenericValidation genericValidation = new GenericValidation();

		// Act
		String result1 = GenericValidation.jsonNavigator(jsonObject, "name");
		String result2 = GenericValidation.jsonNavigator(jsonObject, "items[0]");
		String result3 = GenericValidation.jsonNavigator(jsonObject, "items[1]");
		String result4 = GenericValidation.jsonNavigator(jsonObject, "age");

		// Assert
		assertNotNull(result1);
		assertNotNull(result2);
		assertNotNull(result3);
		assertNotNull(result4);
	}

	@Test
	public void testJsonNavigatorWithJsonObject() {
		// Arrange
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("item1");
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("key", "value");

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", "John");
		jsonObject.addProperty("age", 30);
		jsonObject.add("inner", innerObject);

		// Act
		String result1 = GenericValidation.jsonNavigator(jsonObject, "inner.key");
		GenericValidation.jsonNavigator(jsonObject, "items[0]");
		// Assert
		assertNotNull(result1);

	}

	@Test
	public void testIsThreadSafeToCache() {
		GenericValidation genericValidation = new GenericValidation();
		boolean threadSafeToCache = genericValidation.isThreadSafeToCache();
		assertEquals(false, threadSafeToCache);
	}

	@Test
	public void testIsRequiredWithNonNullValue() {
		GenericValidation genericValidation = new GenericValidation();
		String value = "SomeValue";

		boolean result = genericValidation.isRequired(value);

		assertTrue(result);
	}

	@Test
	public void testIsRequiredEmptyValue() {
		GenericValidation genericValidation = new GenericValidation();
		String value = "[]";
		boolean result = genericValidation.isRequired(value);
		assertFalse(result);

		String value2 = null;
		boolean result2 = genericValidation.isRequired(value2);
		assertFalse(result2);

		String value3 = "   ";
		boolean result3 = genericValidation.isRequired(value3);
		assertFalse(result3);
	}

	@Test
	public void testMinLength() {
		GenericValidation genericValidation = new GenericValidation();
		String value = null;
		int length = 5;

		boolean result = genericValidation.minLength(value, length);
		assertFalse(result);

		String value1 = "short";
		int length1 = 10;
		boolean result1 = genericValidation.minLength(value1, length1);
		assertFalse(result1);

		String value2 = "exactlength";
		int length2 = 11;
		boolean result2 = genericValidation.minLength(value2, length2);
		assertTrue(result2);

		String value3 = "longvalue";
		int length3 = 5;
		boolean result3 = genericValidation.minLength(value3, length3);
		assertTrue(result3);
	}

	@Test
	public void testMaxLength() {
		GenericValidation genericValidation = new GenericValidation();
		String value = null;
		int length = 5;

		boolean result = genericValidation.maxLength(value, length);
		assertFalse(result);

		String value1 = "short";
		int length1 = 10;
		boolean result1 = genericValidation.maxLength(value1, length1);
		assertTrue(result1);

		String value2 = "exactlength";
		int length2 = 11;
		boolean result2 = genericValidation.maxLength(value2, length2);
		assertTrue(result2);

		String value3 = "longvalue";
		int length3 = 5;
		boolean result3 = genericValidation.maxLength(value3, length3);
		assertFalse(result3);
	}

	@Test
	public void testIsOfTypeWithNullType() {

		String value = "123";
		String type = null;
		boolean result = new GenericValidation().isOfType(value, type);
		assertTrue(result);
	}

	@Test
	public void testIsOfTypeWithNonMatchingType() {

		String value = "abc";
		String type = "^[0-9]+$"; // Match digits
		boolean result = new GenericValidation().isOfType(value, type);
		assertTrue(result);
	}

	@Test
	public void testInit() {
		Map<String, String> regexMap = new HashMap<>();
		GenericValidation genericValidation = new GenericValidation();
		PropertiesFileReader mockReader = mock(PropertiesFileReader.class);

		GenericValidation.init();
	}

	
	@Test
	public void testValidateJsonWithGroovyLanguage() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		JsonObject jsFunction = new JsonObject();
		jsFunction.addProperty("", "groovy code here");
		jsFunction.addProperty("name", "functionName");
		jsFunction.addProperty("language", "groovy");

		JsonObject requestJsonObject = new JsonObject();
		JsonObject errorMessage = new JsonObject();
		try (MockedConstruction<GroovyCodeExecutionManager> mocked = mockConstruction(GroovyCodeExecutionManager.class)) {
            GroovyCodeExecutionManager codeExecutionManager = mock(GroovyCodeExecutionManager.class);
		    getValidateJsonMethod().invoke(new GenericValidation(), jsFunction, requestJsonObject, errorMessage);
		}
	}
	@Test
	public void testValidateJson_withOutLanguage() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		JsonObject jsFunction = new JsonObject();
		jsFunction.addProperty("", "groovy code here");
		jsFunction.addProperty("name", "functionName");
		jsFunction.addProperty("language", "123");

		JsonObject requestJsonObject = new JsonObject();
		JsonObject errorMessage = new JsonObject();

		getValidateJsonMethod().invoke(new GenericValidation(), jsFunction, requestJsonObject, errorMessage);

	}

	@Test
	public void testValidateJsonWithGroovyLanguage_exception() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		JsonObject jsFunction = new JsonObject();
		jsFunction.addProperty("", "groovy code here");
		jsFunction.addProperty("name", "functionName");
		// jsFunction.addProperty("@language", "groovy");

		JsonObject requestJsonObject = new JsonObject();
		JsonObject errorMessage = new JsonObject();

		getValidateJsonMethod().invoke(new GenericValidation(), jsFunction, requestJsonObject, errorMessage);

	}

	@Test
	public void testIsValid_a1() {
		JsonObject formData = new JsonObject();
		JsonObject xmlRuleJson = new JsonObject();
		xmlRuleJson.addProperty("definitionFolder", "folder");
		xmlRuleJson.addProperty("definition-file", "file");
		
		
		try(MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			IProcessor processor = mock(IProcessor.class);
			mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
			JsonObject object = null;
			when(processor.getJsonObject(anyString(),anyBoolean())).thenReturn(object);
			GenericValidation genericValidation = new GenericValidation();

			boolean valid = genericValidation.isValid(formData, xmlRuleJson);
			assertFalse(valid);

		}
	}
	@Test(expected = NullPointerException.class)
	public void testIsValid_a2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		JsonObject formData = new JsonObject();
		JsonObject xmlRuleJson = new JsonObject();
		xmlRuleJson.addProperty("definitionFolder", "folder");
		xmlRuleJson.addProperty("definition-file", "file");
		
		
		try(MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			IProcessor processor = mock(IProcessor.class);
			mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
			JsonObject validationRulesJson = new JsonObject();
			JsonObject jsFunction = new JsonObject();
			validationRulesJson.add("jsFunction", jsFunction);
			
			when(processor.getJsonObject(anyString(),anyBoolean())).thenReturn(validationRulesJson);
			
			GenericValidation genericValidation = new GenericValidation();
			genericValidation.isValid(formData, xmlRuleJson);

		}
	}
	@Test
	public void testIsValid_a3() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		JsonObject formData = new JsonObject();
		JsonObject xmlRuleJson = new JsonObject();
		xmlRuleJson.addProperty("definitionFolder", "folder");
		xmlRuleJson.addProperty("definition-file", "file");
		
		
		try(MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			IProcessor processor = mock(IProcessor.class);
			mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
			JsonObject validationRulesJson = new JsonObject();
			JsonObject jsFunction = new JsonObject();
			//validationRulesJson.add("jsFunction", jsFunction);
			
			when(processor.getJsonObject(anyString(),anyBoolean())).thenReturn(validationRulesJson);
			
			GenericValidation genericValidation = new GenericValidation();
			genericValidation.isValid(formData, xmlRuleJson);

		}
	}


	
	private Method getValidateJsonMethod() throws NoSuchMethodException, SecurityException {
		Class<?> genericValidationClass = GenericValidation.class;
		Method method = genericValidationClass.getDeclaredMethod("validateJson", JsonObject.class, JsonObject.class,
				JsonObject.class);
		method.setAccessible(true);
		return method;
	}

	@Test
	public void testRecursiveValidation_a1() {
		JsonObject jsonCopy = new JsonObject();
		JsonObject sampleObject = new JsonObject();
		sampleObject.addProperty("key", "value");
		jsonCopy.add("key1", sampleObject);
		jsonCopy.addProperty("key2", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "requiredIf");
		record.addProperty("condition", "conditon");
		record.addProperty("value", "value");

		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			try (MockedStatic<GenericValidation> mockedStaticValidation = mockStatic(GenericValidation.class)) {
				mockedStaticValidation.when(() -> GenericValidation.jsonNavigator(any(), anyString()))
						.thenReturn("123");

				// Call the method under test
				boolean result = genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages,
						requestJsonObject);

				assertTrue(result);
				assertTrue(errorMessages.has("key1"));
				

				// Verify the static method was called
				mockedStatic.verify(() -> JsonUtils.getKeys(formValidation));
			}
		}
	}

	@Test
	public void testRecursiveValidation_a2() {
		JsonObject jsonCopy = new JsonObject();
		JsonObject sampleObject = new JsonObject();
		sampleObject.addProperty("key", "value");
		jsonCopy.add("key1", sampleObject);
		jsonCopy.addProperty("key2", "");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "requiredIf");
		record.addProperty("condition", "<");
		record.addProperty("value", "123");
		record.addProperty("type", "custom");	
		record.addProperty("expression", "");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			try (MockedStatic<GenericValidation> mockedStaticValidation = mockStatic(GenericValidation.class)) {
				mockedStaticValidation.when(() -> GenericValidation.jsonNavigator(any(), anyString()))
						.thenReturn("1234");

				genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			}
		}
	}
	
	@Test
	public void testRecursiveValidation_a3() {
		JsonObject jsonCopy = new JsonObject();
		JsonObject sampleObject = new JsonObject();
		sampleObject.addProperty("key", "value");
		jsonCopy.add("key1", sampleObject);
		jsonCopy.addProperty("key2", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("condition", "<");
		record.addProperty("value", "123");
		record.addProperty("type", "custom");	
		record.addProperty("expression", "");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			try (MockedStatic<GenericValidation> mockedStaticValidation = mockStatic(GenericValidation.class)) {
				mockedStaticValidation.when(() -> GenericValidation.jsonNavigator(any(), anyString()))
						.thenReturn("1234");

				genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			}
		}
	}
	@Test
	public void testRecursiveValidation_a3_if_condition1() {
		JsonObject jsonCopy = new JsonObject();
		JsonObject sampleObject = new JsonObject();
		sampleObject.addProperty("key", "value");
		jsonCopy.add("key1", sampleObject);
		jsonCopy.addProperty("key2", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("condition", "<");
		record.addProperty("value", "123");
		record.addProperty("type", "custom");	
		record.addProperty("expression", "value");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			try (MockedStatic<GenericValidation> mockedStaticValidation = mockStatic(GenericValidation.class)) {
				mockedStaticValidation.when(() -> GenericValidation.jsonNavigator(any(), anyString()))
						.thenReturn("1234");

				genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			}
		}
	}
	@Test
	public void testRecursiveValidation_a3_if_condition2() {
		JsonObject jsonCopy = new JsonObject();
		JsonObject sampleObject = new JsonObject();
		sampleObject.addProperty("key", "value");
		jsonCopy.add("key1", sampleObject);
		jsonCopy.addProperty("key2", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "");
		record.addProperty("requiredIf", "");
		record.addProperty("condition", "<");
		record.addProperty("value", "123");
		record.addProperty("type", "");	
		record.addProperty("expression", "value");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			try (MockedStatic<GenericValidation> mockedStaticValidation = mockStatic(GenericValidation.class)) {
				mockedStaticValidation.when(() -> GenericValidation.jsonNavigator(any(), anyString()))
						.thenReturn("1234");

				genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			}
		}
	}


	
	@Test
	public void testRecursiveValidation_a4() {
		JsonObject jsonCopy = new JsonObject();
		JsonObject sampleObject = new JsonObject();
		sampleObject.addProperty("key", "value");
		jsonCopy.add("key1", sampleObject);
		jsonCopy.addProperty("key2", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("condition", "<");
		record.addProperty("value", "123");
		record.addProperty("type", "value,null");	
		record.addProperty("expression", "");
		record.addProperty("maxLength", 3);
		record.addProperty("minLength", 10);
		record.addProperty("length", 10);
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			try (MockedStatic<GenericValidation> mockedStaticValidation = mockStatic(GenericValidation.class)) {
				mockedStaticValidation.when(() -> GenericValidation.jsonNavigator(any(), anyString()))
						.thenReturn("1234");

				genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			}
		}
	}


	@Test(expected = NullPointerException.class)
	public void testRecursiveValidation_a5() {
		JsonObject jsonCopy = new JsonObject();
		
		jsonCopy.addProperty("key1", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("type", "");	
		record.addProperty("maxLength", 3);
		record.addProperty("minLength", 10);
		record.addProperty("length", 10);
		formValidation.add("key1", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			
		}
	}

	@Test(expected = NullPointerException.class)
	public void testRecursiveValidation_a6() {
		JsonObject jsonCopy = new JsonObject();
		
		jsonCopy.addProperty("key1", "value");
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("type", "");	
		record.addProperty("maxLength", 5);
		record.addProperty("minLength", 5);
		record.addProperty("length", 5);
		formValidation.add("key1", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			List<String> keys = Arrays.asList("key1", "key2");
			mockedStatic.when(() -> JsonUtils.getKeys(formValidation)).thenReturn(keys);
			GenericValidation genericValidation = new GenericValidation();

			genericValidation.recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);

			
		}
	}



}

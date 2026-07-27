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
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.validation.form.GenericValidation;
import com.helicalinsight.validation.form.GroovyCodeExecutionManager;

@RunWith(MockitoJUnitRunner.class)
public class GenericValidationTest {

	@Test
	public void testJsonNavigator() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", "John");
		jsonObject.addProperty("age", 30);

		JsonArray jsonArray = new JsonArray();
		jsonArray.add("item1");
		jsonArray.add("item2");
		jsonObject.add("items", jsonArray);

		assertEquals("John", GenericValidation.jsonNavigator(jsonObject, "name"));
		assertEquals("item1", GenericValidation.jsonNavigator(jsonObject, "items[0]"));
		assertEquals("item2", GenericValidation.jsonNavigator(jsonObject, "$.items[1]"));
		assertEquals("30", GenericValidation.jsonNavigator(jsonObject, "age"));
		assertEquals(null, GenericValidation.jsonNavigator(jsonObject, "missing"));
	}

	@Test
	public void testJsonNavigatorWithJsonObject() {
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("key", "value");

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", "John");
		jsonObject.addProperty("age", 30);
		jsonObject.add("inner", innerObject);

		assertEquals("value", GenericValidation.jsonNavigator(jsonObject, "inner.key"));
		assertEquals("value", GenericValidation.jsonNavigator(jsonObject, "$.inner.key"));
		assertEquals(null, GenericValidation.jsonNavigator(jsonObject, "items[0]"));
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
	public void testCreateUserAddBlankPasswordProducesMessage() {
		// Mirrors createUsers.xml password rule for action=add
		JsonObject passwordRule = new JsonObject();
		passwordRule.addProperty("type", "userPassword");
		passwordRule.addProperty("condition", "=");
		passwordRule.addProperty("maxLength", 60);
		passwordRule.addProperty("minLength", 6);
		passwordRule.addProperty("requiredIf", "action");
		passwordRule.addProperty("value", "add");
		passwordRule.addProperty("errorMessage", "Invalid password");

		JsonObject emailRule = new JsonObject();
		emailRule.addProperty("condition", "=");
		emailRule.addProperty("maxLength", 60);
		emailRule.addProperty("requiredIf", "action");
		emailRule.addProperty("type", "email");
		emailRule.addProperty("value", "add");

		JsonObject nameRule = new JsonObject();
		nameRule.addProperty("condition", "=");
		nameRule.addProperty("requiredIf", "action");
		nameRule.addProperty("type", "userName");
		nameRule.addProperty("value", "add");

		JsonObject organisationRule = new JsonObject();
		organisationRule.addProperty("type", "organisation");

		JsonObject formValidation = new JsonObject();
		formValidation.add("password", passwordRule);
		formValidation.add("email", emailRule);
		formValidation.add("name", nameRule);
		formValidation.add("organisation", organisationRule);

		// Same shape as /admin/users after newHttpRequestToFormData merges action + formData
		JsonObject request = new JsonObject();
		request.addProperty("action", "add");
		request.addProperty("id", "");
		request.addProperty("email", "test@gmail.com");
		request.addProperty("name", "Test");
		request.addProperty("enabled", true);
		request.addProperty("password", "");
		request.addProperty("organisation", 1);

		JsonObject errorMessages = new JsonObject();
		GenericValidation validation = new GenericValidation();
		boolean valid = validation.validateWithJsonPath(formValidation, errorMessages, request);

		assertFalse("Blank password on action=add must fail validation", valid);
		assertTrue("Expected password error key", errorMessages.has("password"));
		assertEquals("Please enter the mandatory field password",
				errorMessages.get("password").getAsString());
	}

	@Test
	public void testCreateUserAddValidPasswordPassesRequired() {
		JsonObject passwordRule = new JsonObject();
		passwordRule.addProperty("condition", "=");
		passwordRule.addProperty("requiredIf", "action");
		passwordRule.addProperty("value", "add");
		passwordRule.addProperty("minLength", 6);

		JsonObject formValidation = new JsonObject();
		formValidation.add("password", passwordRule);

		JsonObject request = new JsonObject();
		request.addProperty("action", "add");
		request.addProperty("password", "test123");

		JsonObject errorMessages = new JsonObject();
		assertTrue(new GenericValidation().validateWithJsonPath(formValidation, errorMessages, request));
		assertTrue(errorMessages.entrySet().isEmpty());
	}


	@Test
	public void testValidateWithJsonPathNested() {
		JsonObject requestJsonObject = new JsonObject();
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Subject", "Hello");
		requestJsonObject.add("EmailSettings", emailSettings);

		JsonObject subjectRule = new JsonObject();
		subjectRule.addProperty("required", "true");
		JsonObject emailRules = new JsonObject();
		emailRules.add("Subject", subjectRule);
		JsonObject formValidation = new JsonObject();
		formValidation.add("EmailSettings", emailRules);

		JsonObject errorMessages = new JsonObject();
		GenericValidation genericValidation = new GenericValidation();

		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);

		assertTrue(result);
		assertTrue(errorMessages.entrySet().isEmpty());
	}

	@Test
	public void testValidateWithJsonPath_a2() {
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "threshold");
		record.addProperty("condition", "<");
		record.addProperty("value", "123");
		record.addProperty("type", "custom");
		record.addProperty("expression", "");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();
		requestJsonObject.addProperty("key2", "");
		requestJsonObject.addProperty("threshold", "1234");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertFalse(result);
	}

	@Test
	public void testValidateWithJsonPath_a3() {
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("type", "custom");
		record.addProperty("expression", "");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();
		requestJsonObject.addProperty("key2", "value");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertFalse(result);
	}

	@Test
	public void testValidateWithJsonPath_a3_if_condition1() {
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("type", "custom");
		record.addProperty("expression", "value");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();
		requestJsonObject.addProperty("key2", "value");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertTrue(result);
	}

	@Test
	public void testValidateWithJsonPath_a3_if_condition2() {
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "");
		record.addProperty("requiredIf", "");
		record.addProperty("type", "");
		record.addProperty("expression", "value");
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();
		requestJsonObject.addProperty("key2", "value");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertTrue(result);
	}

	@Test
	public void testValidateWithJsonPath_a4() {
		JsonObject formValidation = new JsonObject();
		JsonObject record = new JsonObject();
		record.addProperty("required", "required");
		record.addProperty("requiredIf", "");
		record.addProperty("type", "value,null");
		record.addProperty("expression", "");
		record.addProperty("maxLength", 3);
		record.addProperty("minLength", 10);
		record.addProperty("length", 10);
		formValidation.add("key2", record);
		JsonObject errorMessages = new JsonObject();
		JsonObject requestJsonObject = new JsonObject();
		requestJsonObject.addProperty("key2", "value");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertFalse(result);
	}

	@Test
	public void testValidateWithJsonPath_a5() {
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
		requestJsonObject.addProperty("key1", "value");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertFalse(result);
		assertTrue(errorMessages.has("key1"));
	}

	@Test
	public void testValidateWithJsonPath_a6() {
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
		requestJsonObject.addProperty("key1", "value");

		GenericValidation genericValidation = new GenericValidation();
		boolean result = genericValidation.validateWithJsonPath(formValidation, errorMessages, requestJsonObject);
		assertTrue(result);
	}



}

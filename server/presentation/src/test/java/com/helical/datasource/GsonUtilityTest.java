package com.helical.datasource;

import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

public class GsonUtilityTest {

	@Test
	public void ut_a1_optJsonObject() {
		
		/**
		 * Get an optional JSONObject associated with a key. 
		 * It returns null if there is no such key, or if its value is not a JSONObject.
		 */
		
		JsonObject optJsonObject = GsonUtility.optJsonObject(null, null);
		Assert.assertNull(optJsonObject);
		JsonObject object1 = new JsonObject();
		object1.addProperty("id", "1");
		JsonObject object2 = new JsonObject();
		object2.add("obj1", object1);
		JsonArray array = new JsonArray();
		array.add("123");
		object2.add("array", array );
		JsonObject optJsonObject2 = GsonUtility.optJsonObject(object2, "obj1");  //optional JSONObject associated with a key. 
		Assert.assertEquals(object1, optJsonObject2);
		JsonObject optJsonObject3 = GsonUtility.optJsonObject(object2,"array"); //array is not a jsonobject so returns null
		Assert.assertNull(optJsonObject3);
		JsonObject optJsonObject4 = GsonUtility.optJsonObject(object2,"service");//returns null if there is no such key
		Assert.assertNull(optJsonObject4);
		
	}
	@Test
	public void ut_a2_optJsonArray() {
		/**
		 * Get an optional JSONArray associated with a key. 
		 * It returns null if there is no such key, or if its value is not a JSONArray.
		 */
		
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		array.add("123");
		object.add("array", array );
		object.addProperty("id", "id");
		JsonArray optJsonArray = GsonUtility.optJsonArray(object, "array");// optional JSONArray associated with a key.
		Assert.assertEquals(array, optJsonArray);
		JsonArray optJsonArray2 = GsonUtility.optJsonArray(object, "obj"); //returns null if there is no such key
		Assert.assertNull(optJsonArray2);
		JsonArray optJsonArray3 = GsonUtility.optJsonArray(object, "id");
		Assert.assertNull(optJsonArray3);
		
	}
	
	@Test
	public void ut_a3_optString() {
		/**
		 * Get an optional string associated with a key. It returns an empty string if there is no such key.
		 *  If the value is not a string and is not null, then it is coverted to a string. 
		 */
		JsonObject object = new JsonObject();
		object.addProperty("id", "12");
		object.add("name", null);
		JsonObject json = new JsonObject();
		json.addProperty("key", "value");
		object.add("json", json);
		String optString = GsonUtility.optString(object, "id"); //optional string associated with a key
		Assert.assertEquals("12", optString);
		String optString2 = GsonUtility.optString(object, "name"); //if value is null it returns empty string
		Assert.assertEquals("", optString2);
		String optString3 = GsonUtility.optString(object, "school");//returns an empty string if there is no such key
		Assert.assertEquals("", optString3);
		String optString4 = GsonUtility.optString(object, "json");//value is not a string and is not null, then it is coverted to a string
		Assert.assertEquals(json.toString(), optString4);
	}
	
	@Test
	public void ut_a4_optStringValue() {
		/**
		 * Get an optional string associated with a key. It returns the defaultValue if there is no such key.
		 */
		JsonObject object = new JsonObject();
		object.addProperty("empty", "");
		object.add("string", null);
		object.addProperty("id", "12");
		JsonObject obj = new JsonObject();
		obj.addProperty("key","value");
		object.add("obj", obj);
		String optStringValue = GsonUtility.optStringValue(object, "id", "1");//optional string associated with a key
		Assert.assertEquals("12", optStringValue);
		String optStringValue2 = GsonUtility.optStringValue(object, "name", "helical");//returns the defaultValue if there is no such key
		Assert.assertEquals("helical", optStringValue2);
		String optStringValue4 = GsonUtility.optStringValue(object, "string", "helical");//if value is null returns default value
		Assert.assertEquals("helical", optStringValue4);
		String optStringValue3 = GsonUtility.optStringValue(object, "obj", "value"); //if value is not a string and not a null then it convert value into string format
		Assert.assertEquals(obj.toString(), optStringValue3);
		String optStringValue5 = GsonUtility.optStringValue(object, "empty", "emptyString");//if value is empty String then returns same
		Assert.assertEquals("", optStringValue5);
		
	}
	@Test
	public void ut_a5_optBoolean() {
		/**
		 * Get an optional boolean associated with a key. 
		 * It returns false if there is no such key, or if the value is not Boolean.TRUE or the String "true".
		 */
		JsonObject obj = new JsonObject();
		obj.addProperty("right", false);
		obj.add("search", null);
		obj.add("id", new JsonObject());
		obj.addProperty("create", "false");
		boolean optBoolean = GsonUtility.optBoolean(obj, "right");//optional boolean associated with a key. 
		Assert.assertFalse(optBoolean);
		boolean optBoolean2 = GsonUtility.optBoolean(obj, "search");//returns false when value is null
		Assert.assertFalse(optBoolean2);
		boolean optBoolean3 = GsonUtility.optBoolean(obj, "id");//returns false value is not boolean
		Assert.assertFalse(optBoolean3);
		boolean optBoolean4 = GsonUtility.optBoolean(obj, "create");//value is not Boolean.TRUE or the String "true"
		Assert.assertEquals(false, optBoolean4);
		
	}
	
	@Test
	public void ut_a6_optBooleanValue() {
		/**
		 * Get an optional boolean associated with a key.
		 *  It returns the defaultValue if there is no such key, 
		 *  or if it is not a Boolean or the String "true" or "false" (case insensitive).
		 */
		
		JsonObject object = new JsonObject();
		object.addProperty("search", "true");
		object.addProperty("correct", "false");
		boolean optBooleanValue = GsonUtility.optBooleanValue(object, "search", false);//optional boolean associated with a key
		Assert.assertTrue(optBooleanValue);
		boolean optBooleanValue2 = GsonUtility.optBooleanValue(object, "correct", true);
		Assert.assertFalse(optBooleanValue2);
		boolean optBooleanValue3 = GsonUtility.optBooleanValue(object, "id", false);//if key is not present returns default value
		Assert.assertFalse(optBooleanValue3);
		
	}
	@Test
	public void ut_a7_optInt() {
		/**
		 * Get an optional int value associated with a key, or zero if there is no such key or if the value is not a number. 
		 * If the value is a string, an attempt will be made to evaluate it as a number.
		 */
		JsonObject object = new JsonObject();
		Integer num = 1212;
		object.addProperty("id", 1212);
		object.addProperty("number", "number");
		object.addProperty("sum", "30");
		Integer optInt = GsonUtility.optInt(object, "id");  //optional int value associated with a key
		Assert.assertEquals(num, optInt); 
		Integer optInt2 = GsonUtility.optInt(object, "digit"); // returns zero key is not present in object
		Integer digit = 0;
		Assert.assertEquals(digit, optInt2);
		Integer optInt3 = GsonUtility.optInt(object, "number");//returns zero if the value is not a number
		Assert.assertEquals(digit, optInt3);
		Integer optInt4 = GsonUtility.optInt(object, "sum");//value is a string, an attempt will be made to evaluate it as a number
		Integer sum = 30;
		Assert.assertEquals(sum, optInt4);
		
	}
	@Test
	public void ut_a8_optIntValue() {
		/**
		 * Get an optional int value associated with a key, or the default if there is no such key or if the value is not a number.
		 * If the value is a string, an attempt will be made to evaluate it as a number.
		 */
		JsonObject object = new JsonObject();
		int value = 111;
		object.addProperty("key",value);
		object.addProperty("num", "1");
		int optIntValue = GsonUtility.optIntValue(object, "key", 555);//optional int value associated with a key
		Assert.assertEquals(value, optIntValue);
		int optIntValue2 = GsonUtility.optIntValue(object, "id", 555);//default if there is no such key
		Assert.assertEquals(555, optIntValue2);	
		int optIntValue3 = GsonUtility.optIntValue(object, "num", 555);//value is a string, attempt will be made to evaluate it as a number.
		Assert.assertEquals(1, optIntValue3);	
	}
	@Test
	public void ut_a9_optStringFromJsonArray() {
		/**
		 * Get an optional value associated with a key.
		 * Returns:An object which is the value, or null if there is no value.
		 */
		JsonArray a1 = new JsonArray();
		JsonObject object = new JsonObject();
		int value = 111;
		object.addProperty("key",value);
		a1.add(object);
		a1.add("ram");
		String optStringFromJsonArray = GsonUtility.optStringFromJsonArray(a1, 0);
		Assert.assertEquals(object.toString(), optStringFromJsonArray);
		String optStringFromJsonArray2 = GsonUtility.optStringFromJsonArray(a1, 1);
		Assert.assertEquals("ram", optStringFromJsonArray2);
		JsonArray a2 = new JsonArray();
		String optStringFromJsonArray3 = GsonUtility.optStringFromJsonArray(a2, 0);
		Assert.assertEquals(null, optStringFromJsonArray3);
	}
	
	@Test
	public void ut_b1_testGetByPath() {
		String json = "{\"status\":\"0\",\"response\":{\"data\":{\"property\":\"value\"}}}";
		JsonObject object = GsonUtility.parseString(json, JsonObject.class);
		String propertyValue = GsonUtility.getByPath(object, "response.data.property").getAsString();
		Assert.assertEquals("value", propertyValue);
	}
}

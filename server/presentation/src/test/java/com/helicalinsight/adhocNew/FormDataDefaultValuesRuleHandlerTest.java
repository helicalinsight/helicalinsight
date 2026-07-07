package com.helicalinsight.adhocNew;

import static org.mockito.Mockito.mockStatic;

import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FormDataDefaultValuesRuleHandler;
import com.helicalinsight.efw.utility.ApplicationUtilities;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FormDataDefaultValuesRuleHandlerTest {

	@Test
	public void ut_a1_test_setDefaultValuesInFormData() {
		FormDataDefaultValuesRuleHandler ruleHandler = new FormDataDefaultValuesRuleHandler();
		
		JsonObject formData = new JsonObject();
		Map<String, String> map = new HashMap<>();
		map.put("adhoc.query.useDefaults", "false");
		try(MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)){
			mockedStatic.when(()-> ApplicationUtilities.getDefaultsMap()).thenReturn(map);
			FormDataDefaultValuesRuleHandler.setDefaultValuesInFormData(formData);
		}
	}
	
	@Test
	public void ut_a2_test_setDefaultValuesInFormData() {
		
		JsonObject formData = new JsonObject();
		JsonArray filters = new JsonArray();
		JsonObject item = new JsonObject();
		item.add("values", new JsonArray());
		filters.add(item);
		formData.add("filters", filters);
		formData.addProperty("customFilterExpression", "customFilterExpression");
		JsonArray having = new JsonArray();
		JsonObject item2 = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add(")");
		item2.add("values", arr);
		having.add(item2);
		formData.add("having", having);
		FormDataDefaultValuesRuleHandler.setDefaultValuesInFormData(formData);
	}
	
	@Test
	public void ut_a3_test_ignoreFilter() {
		
		JsonObject formData = new JsonObject();
		JsonArray filters = new JsonArray();
		JsonObject item = new JsonObject();
		item.addProperty("ignore", true);
		filters.add(item);
		formData.add("filters", filters);
		formData.addProperty("customFilterExpression", "customFilterExpression");
		
		FormDataDefaultValuesRuleHandler.ignoreFilter(formData);
	}
	
	@Test
	public void ut_a4_test_ignoreFilter() {
		
		JsonObject formData = new JsonObject();
		JsonArray filters = new JsonArray();
		JsonObject item = new JsonObject();
		
		filters.add(item);
		formData.add("filters", filters);
		formData.addProperty("customFilterExpression", "customFilterExpression");
		
		FormDataDefaultValuesRuleHandler.ignoreFilter(formData);
	}
}

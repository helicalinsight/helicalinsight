package com.helicalinsight.validation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.validation.form.GenericValidation;
import com.helicalinsight.validation.form.ServiceValidation;

// TODO: Convert it to mockito unit test
public class ServiceValidationTest {
	
	@Test(expected = NullPointerException.class)
	public void testIsValid_ValidDefinitionFile() {
		ServiceValidation serviceValidation = new ServiceValidation();

		JsonObject formData = new JsonObject();
		JsonObject serviceJson = new JsonObject();
		serviceJson.addProperty("service", "your-service");
		serviceJson.addProperty("serviceType", "your-service-type");
		serviceJson.addProperty("type", "your-type");
		formData.add("serviceJson", serviceJson);


		
		JsonObject xmlRuleJson = new JsonObject();
		JsonArray serviceParameters = new JsonArray();
		JsonObject record = new JsonObject();
		record.addProperty("type", "your-type");
		record.addProperty("service", "your-service");
		record.addProperty("serviceType", "your-service-type");
		record.addProperty("definition-file", "valid-definition-file");
		serviceParameters.add(record);
		xmlRuleJson.add("serviceParameters", serviceParameters);
	

        serviceValidation.isValid(formData, xmlRuleJson);

		
	}
	
	
	//@Test
    public void testIsValid_InvalidDefinitionFile() {
        // Arrange
        ServiceValidation serviceValidation = new ServiceValidation();

        JsonObject formData = new JsonObject();
        JsonObject serviceJson = new JsonObject();
        serviceJson.addProperty("service", "your-service");
        serviceJson.addProperty("serviceType", "your-service-type");
        serviceJson.addProperty("type", "your-type");
        formData.add("serviceJson", serviceJson);

        JsonObject xmlRuleJson = new JsonObject();
        JsonArray serviceParameters = new JsonArray();
        JsonObject record = new JsonObject();
        record.addProperty("type", "your-type");
        record.addProperty("service", "your-service");
        record.addProperty("serviceType", "your-service-type");
        record.addProperty("definition-file", ""); // This should be different
        serviceParameters.add(record);
        xmlRuleJson.add("serviceParameters", serviceParameters);

        
        serviceValidation.isValid(formData, xmlRuleJson);

        
    }
	//@Test
    public void testIsValid_InvalidDefinitionFile2() {
        // Arrange
        ServiceValidation serviceValidation = new ServiceValidation();

        JsonObject formData = new JsonObject();
        JsonObject serviceJson = new JsonObject();
        serviceJson.addProperty("service", "your-service");
        serviceJson.addProperty("serviceType", "your-service-type");
        serviceJson.addProperty("type", "your-type");
        formData.add("serviceJson", serviceJson);

        JsonObject xmlRuleJson = new JsonObject();
        JsonArray serviceParameters = new JsonArray();
        JsonObject record = new JsonObject();
        record.addProperty("type", "your-type");
        record.addProperty("service", "your-service-type");
        record.addProperty("serviceType", "your-service");
        record.addProperty("definition-file", ""); // This should be different
        serviceParameters.add(record);
        xmlRuleJson.add("serviceParameters", serviceParameters);

        
        serviceValidation.isValid(formData, xmlRuleJson);

        
    }

	
}

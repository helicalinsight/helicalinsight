package com.helicalinsight.validation.form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Author on 30/07/2015
 * This class validates formData and xml data, which extends <p>GenericValidation</p>
 * @author Somen
 */

@Component
public class ServiceValidation extends GenericValidation {

    private static final Logger logger = LoggerFactory.getLogger(ServiceValidation.class);
    /**
     * isValid(JsonObject formData, JsonObject xmlRuleJson)
     * @param formData           formData
	 * @param xmlRuleJson        xml data in JsonObject
	 * {@return True if validation is successful}{@code false} if data is not correct.
	 */
    public boolean isValid(JsonObject formData, JsonObject xmlRuleJson) {

        //pickup the definition file and pass it to generic validation
        // from the formData get the service, serviceType and type..(the type can be an array)
        //loop the xmlRuleJson serviceParameters jsonArray
        //accumulate the definition-file in the xmlRuleJson
        //discard the service parameters
        //return generic validation
        JsonObject serviceJson = formData.getAsJsonObject("serviceJson");
        String service = serviceJson.get("service").getAsString();
        String serviceType = serviceJson.get("serviceType").getAsString();
        String type = serviceJson.get("type").getAsString();
        formData.remove("serviceJson");

        String definitionFile = "";
        JsonArray serviceParameter = xmlRuleJson.getAsJsonArray("serviceParameters");
        for (int index = 0; index < serviceParameter.size(); index++) {
            JsonObject record = serviceParameter.get(index).getAsJsonObject();
            if (type.equals(record.get("type").getAsString()) && service.equals(record.get("service").getAsString()) && serviceType
                    .equals(record.get("serviceType").getAsString())) {
                definitionFile = record.get("definition-file").getAsString();
                break;
            }
        }

        if ("".equals(definitionFile)) {
            return true;
        }

        xmlRuleJson.remove("serviceParameters");
        xmlRuleJson.addProperty("definition-file", definitionFile);

        return super.isValid(formData.getAsJsonObject("formData"), xmlRuleJson);
    }
}
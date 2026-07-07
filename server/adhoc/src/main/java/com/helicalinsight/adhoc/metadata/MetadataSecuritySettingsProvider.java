package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * Class MetadataSecuritySettingsProvider implements {@link IComponent} interface.
 * This component provides metadata security settings.
 * @author Somen
 * Created  on 8/2/2016.
 */
public class MetadataSecuritySettingsProvider implements IComponent {
	/**
     * it provides metadata security settings.
     *
     * @param jsonFormData 				 string containing form data used to validate.
     * @return A string representing 	 metadata security settings.
     * @throws EfwServiceException 	If the request has no action or the action value is unknown.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject request = JsonParser.parseString(jsonFormData).getAsJsonObject();
        this.validateComponent(request);
        JsonObject settingsJson = MetadataSecurityObjectFactory.getSettingJson();
        JsonObject securityObject = settingsJson.getAsJsonObject("metadataSecurity");

        JsonObject response = new JsonObject();
        this.addExpressionAndTypeFromSettingJson(response, securityObject);
        JsonElement unknownObject = securityObject.get("securityType");
        this.whenJsonObject(response, unknownObject);
        this.whenJsonArray(response, unknownObject);
        return response.toString();
    }
    /**
     * Adds expression type and access from the setting JSON to the response JSON.
     *
     * @param response				array used to store the expression type and access.	
     * @param securityObject		object provides expression type and access information.
     */
    private void addExpressionAndTypeFromSettingJson(JsonObject response, JsonObject securityObject) {
        String expressionType = securityObject.get("expressionType").getAsString();
        String access = securityObject.get("access").getAsString();
        String[] split = expressionType.split(",");
        JsonArray jsonArray = new JsonArray();
        for (String value : split) {
            jsonArray.add(value);
        }
        response.add("expressionType", jsonArray);
        String[] split2 = access.split(",");
        JsonArray jsonArray2 = new JsonArray();
        for (String value : split2) {
            jsonArray2.add(value);
        }
        response.add("access", jsonArray2);
    }
    /**
     * Processes the JSON array of security settings.
     *
     * @param response      		 JSON object to which security settings are added.
     * @param unknownObject 		 security settings JSON element.
     */
    private void whenJsonArray(JsonObject response, JsonElement unknownObject) {
        if (unknownObject.isJsonArray()) {
            JsonArray metadataSecurity = unknownObject.getAsJsonArray();

            for (JsonElement securityType : metadataSecurity) {
                JsonObject handler = securityType.getAsJsonObject();
                String type = handler.get("type").getAsString();
                GsonUtility.accumulate(response,"type", type);
                handler.remove("type");
                handler.remove("class");
                response.add(type, handler);
            }
        }
    }

    private void whenJsonObject(JsonObject response, JsonElement unknownObject) {
        if (unknownObject.isJsonObject()) {
            JsonObject security = unknownObject.getAsJsonObject();
            String type = security.get("type").getAsString();
            security.remove("type");
            security.remove("class");
            JsonArray array = new JsonArray();
            array.add(type);
            GsonUtility.accumulate(response,"type", array);
            response.add(type, security);
        }
    }
    /**
     * Processes the JSON object of security settings.
     *
     * @param response      		 JSON object to which security settings are added.
     * @param unknownObject 		 security settings JSON element.
     */
    private void validateComponent(JsonObject request) {
        if (!request.has("action")) {
            throw new EfwServiceException("The request has no 'action' in the formData");
        }
        String action = request.get("action").getAsString();
        if (!"getSettings".equalsIgnoreCase(action)) {
            throw new EfwServiceException("The parameter 'action' value is unknown");
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

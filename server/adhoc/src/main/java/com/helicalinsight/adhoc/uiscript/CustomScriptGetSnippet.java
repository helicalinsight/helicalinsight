package com.helicalinsight.adhoc.uiscript;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * CustomScriptGetSnippet
 * 
 * This class retrieves snippets of custom scripts based on script IDs and parameters.
 * 
 * Author: Somen
 * Created on: 10/20/2016
 */
public class CustomScriptGetSnippet implements IComponent {
	/**
     * Executes the component to get snippets of custom scripts.
     * 
     * @param jsonFormData 		 JSON data containing script IDs and parameters
     * @return A JSON string containing the snippets of custom scripts
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonArray result = new JsonArray();
        JsonObject response = new JsonObject();

        if (formJson.has("scriptIds")) {
            processScriptIds(formJson, result);

        }
        if (formJson.has("parameters")) {
            processScriptIdAndParameters(formJson, result);
        }


        response.add("result", result);
        return response.toString();
    }

    private void processScriptIdAndParameters(JsonObject formJson, JsonArray result) {
        JsonArray parametersArray = formJson.getAsJsonArray("parameters");
        for (JsonElement jsonObject : parametersArray) {
            JsonObject parameter = jsonObject.getAsJsonObject();
            String scriptId = parameter.get("scriptId").getAsString();
            CustomScript customScript = CustomScriptUtils.getCustomScriptFromScriptId(scriptId);
            JsonObject parameterValues = GsonUtility.optJsonObject(parameter,"parameterValues");

            if (parameterValues == null || parameterValues.entrySet().isEmpty()) {
                parameterValues = getDefaultParameters(customScript);
            }

            JsonObject replacedSnippet = replaceSnippet(scriptId, customScript, parameterValues);
            replacedSnippet.add("parameters", parameterValues);
            CustomScriptUtils.addExtraInformation(customScript, replacedSnippet);
            result.add(replacedSnippet);

        }
    }

    private void processScriptIds(JsonObject formJson, JsonArray result) {
        JsonArray scriptIds = formJson.getAsJsonArray("scriptIds");
        for (JsonElement scriptId : scriptIds) {
            CustomScript customScript = CustomScriptUtils.getCustomScriptFromScriptId(scriptId.getAsString());
            JsonObject parameters = getDefaultParameters(customScript);
            JsonObject replacedSnippet = replaceSnippet(scriptId.getAsString(), customScript, parameters);
            CustomScriptUtils.addFunctionNameAndParameters(customScript, replacedSnippet);
            CustomScriptUtils.addExtraInformation(customScript, replacedSnippet);
            result.add(replacedSnippet);

        }
    }

    private JsonObject getDefaultParameters(CustomScript customScript) {
        JsonObject parameters = new JsonObject();
        CustomScriptUtils.addFunctionNameAndParameters(customScript, parameters);
        parameters = parameters.getAsJsonObject("parameters");
        return parameters;
    }

    private JsonObject replaceSnippet(String scriptId, CustomScript customScript, JsonObject parameterValues) {
        JsonObject response = new JsonObject();
        if (parameterValues.entrySet().isEmpty()) {
            response.addProperty("snippet", customScript.getSnippet());
            return response;

        }
        String snippet = customScript.getSnippet();
        Iterator keys = parameterValues.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = parameterValues.get(key).getAsString();
            snippet = snippet.replaceAll("\\$\\{" + key + "\\}", value);
        }
        response.addProperty("scriptId", scriptId);
        response.addProperty("snippet", snippet);
        return response;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

package com.helicalinsight.adhoc.uiscript;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * CustomScriptFetchHandler
 *
 * This class fetches a custom script based on its script ID and returns its details.
 *
 * Author: Somen
 * Created on: 15/05/2015
 */
@SuppressWarnings("unused")
public class CustomScriptFetchHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to fetch a custom script based on its script ID and return its details.
     *
     * @param jsonFormData 		 JSON data containing the script ID
     * @return A JSON string containing the details of the fetched custom script
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String scriptId = formJson.get("scriptId").getAsString();
        CustomScript customScript = CustomScriptUtils.getCustomScriptFromScriptId(scriptId);
        JsonObject response = new JsonObject();
        response.addProperty("scriptId", customScript.getScriptId());
        response.addProperty("uiSnippet", customScript.getUiSnippet());
        response.addProperty("snippet", customScript.getSnippet());
        if (!formJson.has("parameters")) {
            CustomScriptUtils.addFunctionNameAndParameters(customScript, response);
        } else {
            response.add("parameters", formJson.get("parameters"));
        }

        CustomScriptUtils.addExtraInformation(customScript, response);
        return response.toString();
    }
}
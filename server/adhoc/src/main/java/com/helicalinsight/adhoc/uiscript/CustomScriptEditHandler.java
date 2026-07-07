package com.helicalinsight.adhoc.uiscript;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;

import java.io.File;

/**
 * CustomScriptEditHandler
 * 
 * This class handles the editing of custom scripts.
 * 
 * Created: 22-09-2016
 * Author: Somen
 */
@SuppressWarnings("unused")
public class CustomScriptEditHandler implements IComponent {
	/**
     * Executes the component to edit custom scripts.
     * 
     * @param jsonFormData 		 JSON data containing script details to edit
     * @return A JSON string indicating the edit status
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String location = CustomScriptSaveHandler.getCustomScriptPath();
        JsonObject responseJson = new JsonObject();
        String dir = formDataJson.get("dir").getAsString();
        String fileName = formDataJson.get("file").getAsString();
        location += File.separator + dir + File.separator + fileName + "." + JsonUtils.getScriptExtension();


        File resource = new File(location);
        if (!resource.exists()) {
            throw new ResourceNotFoundException("The file doesn't exist in the given location");
        }

        CustomScript customScript = JaxbUtils.unMarshal(CustomScript.class, resource);
        if (formDataJson.has("name")) {
            customScript.setName(formDataJson.get("name").getAsString());
        }

        if (formDataJson.has("description")) {
            customScript.setDescription(formDataJson.get("description").getAsString());
        }
        if (formDataJson.has("icon")) {
            customScript.setIcon(formDataJson.get("icon").getAsString());
        }
        if (formDataJson.has("snippet")) {
            customScript.setSnippet(formDataJson.get("snippet").getAsString());
        }
        if (formDataJson.has("uiSnippet")) {
            customScript.setUiSnippet(formDataJson.get("uiSnippet").getAsString());
        }


        synchronized (this) {
            JaxbUtils.marshal(customScript, resource);
        }

        GsonUtility.accumulate(responseJson,"message", "Script updated successfully");

        return responseJson.toString();
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

package com.helicalinsight.adhoc.uiscript;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;

import java.io.File;

/**
 * CustomScriptSaveHandler
 *
 * This class handles the saving of custom scripts. It receives JSON-formatted form data,
 * creates a custom script object, assigns properties to it, and saves it to the appropriate location.
 *
 * Author: Somen
 * Created on: 14-05-2015
 */
@SuppressWarnings("unused")
public class CustomScriptSaveHandler implements IComponent {
	/**
     * Retrieves the path for saving custom scripts.
     * @return The path for saving custom scripts
     */
    public static String getCustomScriptPath() {
        return ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator +
                "CustomScripts";
    }
    /**
     * the saving of a custom script based on the provided JSON form data.
     *
     * @param jsonFormData 	 JSON-formatted form data containing information about the custom script like type, name, group etc
     * @return The JSON response indicating the status of the saving operation
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();

        JsonObject responseJson = new JsonObject();
        String location = getCustomScriptPath();

        CustomScript customScript = ApplicationContextAccessor.getBean(CustomScript.class);
        customScript.setVisible("true");
        String scriptType = formDataJson.get("scriptType").getAsString();
        customScript.setScriptType(scriptType);
        customScript.setName(formDataJson.get("name").getAsString());
        String group = formDataJson.get("group").getAsString();
        customScript.setGroup(group);
        String underscore = "_";
        String scriptId = group + underscore;
        location += File.separator + group.toLowerCase() + File.separator;
        if (formDataJson.has("subGroup")) {
            String subGroup = formDataJson.get("subGroup").getAsString();
            scriptId += subGroup + underscore;
            location += subGroup.toLowerCase() + File.separator;
            customScript.setSubGroup(subGroup);
        }
        if (formDataJson.has("description")) {
            customScript.setDescription(formDataJson.get("description").getAsString());
        }
        scriptId += scriptType + underscore;
        location += scriptType.toLowerCase() + File.separator;
        if (formDataJson.has("icon")) {
            customScript.setIcon(formDataJson.get("icon").getAsString());
        }
        File fileLocation = new File(location);
        if (!fileLocation.exists()) {
            fileLocation.mkdirs();
        }
        String uuid = AdhocUtils.getUuid();
        scriptId += uuid;
        location += uuid + "." + JsonUtils.getScriptExtension();
        customScript.setScriptId(scriptId);
        customScript.setSnippet(formDataJson.get("snippet").getAsString());
        customScript.setUiSnippet(formDataJson.get("uiSnippet").getAsString());

        synchronized (this) {
            JaxbUtils.marshal(customScript, new File(location));
        }
        GsonUtility.accumulate(responseJson,"scriptId", scriptId);
        GsonUtility.accumulate(responseJson,"message", "Script saved successfully");

        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

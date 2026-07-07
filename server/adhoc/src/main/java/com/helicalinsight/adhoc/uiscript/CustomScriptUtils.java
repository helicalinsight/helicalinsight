package com.helicalinsight.adhoc.uiscript;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import java.io.File;
import java.util.List;

/**
 * CustomScriptUtils
 *
 * This utility class provides methods for retrieving custom script details based on script IDs,
 * as well as adding extra information to the custom script response.
 *
 * Author: Somen
 * Created on: 10/20/2016
 */
public class CustomScriptUtils {

	/**
     * Retrieves the JSON representation of a custom script based on its script ID.
     *
     * @param scriptId 		 script ID
     * @return The JSON representation of the custom script
     */
    public static JsonObject getCustomScriptJsonFromScriptId(String scriptId) {
        String filePath = buildCustomScriptFilePath(scriptId);
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(filePath, false);
    }
    /**
     * Retrieves the CustomScript object based on its script ID.
     *
     * @param scriptId 		 script ID
     * @return The CustomScript object
     */
    public static CustomScript getCustomScriptFromScriptId(String scriptId) {
        String filePath = buildCustomScriptFilePath(scriptId);
        File file = new File(filePath);
        return JaxbUtils.unMarshal(CustomScript.class, file);
    }

    /**
     * Retrieves the JSON representation of a custom script using the JSON processor and its script ID.
     *
     * @param scriptId 		 script ID
     * @return JSON representation of the custom script
     */
    public static JsonObject getCustomScriptUsingJsonProcessorAndScriptId(String scriptId) {
        String filePath = buildCustomScriptFilePath(scriptId);
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(filePath, true);
    }

    private static String buildCustomScriptFilePath(String scriptId) {
        String fileLocation = scriptId.replaceAll("_", "\\" + File.separator);
        return CustomScriptSaveHandler.getCustomScriptPath() + File.separator + fileLocation + "." + JsonUtils
                .getScriptExtension();
    }

    /**
     * Adds function name and parameters to the custom script response.
     *
     * @param script   		 CustomScript object
     * @param response 		 JSON response
     */
    public static void addFunctionNameAndParameters(CustomScript script, JsonObject response) {
        CustomScript.Parameters parameters = script.getParameters();
        if (parameters != null && parameters.getParameterList() != null) {
            List<CustomScript.Parameter> parameterList = parameters.getParameterList();
            JsonObject param = new JsonObject();
            for (CustomScript.Parameter parameter : parameterList) {
                param.addProperty(parameter.getName(), parameter.getValue());
            }
            response.add("parameters", param);
        }

        response.addProperty("renderOn", script.getRenderOn());
    }

    /**
     * Adds extra information to the custom script response such as name , script type, group, description.
     *
     * @param customScript 		 CustomScript object
     * @param response     		 JSON response to store extra information.
     */
    public static void addExtraInformation(CustomScript customScript, JsonObject response) {
        response.addProperty("name", customScript.getName());
        response.addProperty("scriptType", customScript.getScriptType());
        response.addProperty("group", customScript.getGroup());
        if (customScript.getDescription() != null) {
            response.addProperty("description", customScript.getDescription());
        }
        if (customScript.getSubGroup() != null) {
            response.addProperty("subGroup", customScript.getSubGroup());
        }
        if (customScript.getIcon() != null) {
            response.addProperty("icon", customScript.getIcon());
        }
        response.addProperty("renderOn", customScript.getRenderOn());
    }
}

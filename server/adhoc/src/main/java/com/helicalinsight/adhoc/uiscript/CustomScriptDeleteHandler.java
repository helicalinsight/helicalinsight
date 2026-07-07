package com.helicalinsight.adhoc.uiscript;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;



/**
 * CustomScriptDeleteHandler
 * 
 * This class handles the deletion of custom scripts.
 * 
 * Author: Somen
 * Created: 01/04/2015
 */
@SuppressWarnings("unused")
public class CustomScriptDeleteHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * This component to delete custom scripts.
     * 
     * @param jsonFormData 		 JSON data containing script IDs to delete
     * @return A JSON string indicating the deletion status
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonArray scriptIds = formJson.getAsJsonArray("scriptIds");

        JsonObject jsonObject = new JsonObject();
        int deleteCount = deleteReportFromDirectory(scriptIds);
        if (deleteCount > 0) {
            jsonObject.addProperty("message", "Script(s) deleted successfully.");
        } else {
            jsonObject.addProperty("message", "Script(s) could not be deleted");
        }
        return jsonObject.toString();
    }


    private int deleteReportFromDirectory(@NotNull JsonArray reportList) {
        String directoryLocation = CustomScriptSaveHandler.getCustomScriptPath() + File.separator;
        File fileObj;
        int count = 0;
        for (JsonElement aReportList : reportList) {
            String underScore = "_";
            String filePath = directoryLocation + File.separator + aReportList.getAsString().replaceAll(underScore,
                    "\\" + File.separator);
            fileObj = new File(filePath + "." + JsonUtils.getScriptExtension());
            if (fileObj.exists()) {
                //noinspection ResultOfMethodCallIgnored
                fileObj.delete();
                count++;
            }
        }
        return count;
    }
}
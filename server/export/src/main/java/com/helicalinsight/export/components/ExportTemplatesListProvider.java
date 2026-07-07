package com.helicalinsight.export.components;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;



/**
 * An ExportTemplatesListProvider class implements the IComponent interface that provides a list of export templates from the templates directory.
 * It reads JSON files from the directory, processes their content, and returns the templates in JSON format.
 *
 * Created by Author on 08/12/2016
 * @author Somen
 */
@SuppressWarnings("unused")
public class ExportTemplatesListProvider implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Executes the ExportTemplatesListProvider component to provide a list of export templates.
     *
     * @param jsonFormData      JSON input data (not used in this implementation).
     * @return JSON response containing the list of export templates.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        String templatesDirectory = ExportUtils.getTemplatesDirectory();
        File folder = new File(templatesDirectory);
        String extension[] = {ExportUtils.JSON_EXTENSION.substring(1)};
        Collection<File> files = FileUtils.listFiles(folder, extension, false);
        JsonObject response = new JsonObject();
        JsonArray templates = new JsonArray();
        for (File file : files) {
            String filePath = file.getPath();


            String jsonContent = ExportUtils.getFileAsString(filePath);
            JsonObject fileAsJson = JsonParser.parseString(jsonContent).getAsJsonObject();
            fileAsJson.remove("execute");
            String jsFileString = filePath.replace(ExportUtils.JSON_EXTENSION, ExportUtils.JS_EXTENSION);
            File jsFile = new File(jsFileString);
            if (jsFile.exists() && fileAsJson.has("body")) {

                String jsContent = ExportUtils.getFileAsString(jsFileString);
                int beginIndex = jsContent.indexOf(ExportUtils.SCRIPT_BEIGN_INDEX);
                int endIndex = jsContent.indexOf(ExportUtils.SCRIPT_END_INDEX);
                if (beginIndex > -1 && endIndex > -1) {
                    String substring = jsContent.substring(beginIndex + 11, endIndex);
                    JsonObject body = fileAsJson.getAsJsonObject("body");
                    body.addProperty("script", substring);
                }

            }
            fileAsJson.addProperty("templateId", FilenameUtils.removeExtension(file.getName()));
            templates.add(fileAsJson);
        }
        GsonUtility.accumulate(response,"templates", templates);
        return response.toString();
    }


}
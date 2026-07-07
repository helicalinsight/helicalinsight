package com.helicalinsight.export.components;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.export.ExportUtils;



/**
 * An implementation of the IComponent interface for saving export templates.
 *
 * Created by Author on 08/12/2016
 * @author Somen
 */
@SuppressWarnings("unused")
public class SaveTemplate implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the SaveTemplate component to save export templates.
     *
     * @param jsonFormData JSON input data containing the template information.
     * @return JSON response containing the templateId.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonObject response = new JsonObject();
        String uuid = UUID.randomUUID().toString();
        String templatesDirectory = ExportUtils.getTemplatesDirectory();
        String jsonFileName = templatesDirectory + File.separator + uuid + ExportUtils.JSON_EXTENSION;
        try {
            File file = new File(jsonFileName);
            String encoding = ApplicationUtilities.getEncoding();
            JsonObject template = formData.getAsJsonObject("template");
            if (template.has("body")) {
                JsonObject body = template.getAsJsonObject("body");
                if (body.has("script")) {
                    String script = body.get("script").getAsString();
                    String scriptText = ExportUtils.SCRIPT_HEADER  + ExportUtils.SCRIPT_BEIGN_INDEX
                            +script +
                            ExportUtils.SCRIPT_END_INDEX + ExportUtils.SCRIPT_FOOTER;
                    File jsFile = new File(templatesDirectory + File.separator + uuid + ExportUtils.JS_EXTENSION);

                    FileUtils.writeStringToFile(jsFile, scriptText, ApplicationUtilities.getEncoding());
                    body.remove("script");
                    body.addProperty("hasScript", "true");
                }
            }
            Gson gsonPreety = new GsonBuilder().setPrettyPrinting().create();
            FileUtils.writeStringToFile(file, gsonPreety.toJson(template), encoding);
            GsonUtility.accumulate(response,"templateId", uuid);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getCause() + " " + ioe.getMessage());
        }

        return response.toString();
    }
}
package com.helicalinsight.export.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;


import java.io.File;


/**
 * An implementation of the IComponent interface that retrieves the content of a specific export template.
 *
 * Created by Author on 08/12/2016
 * @author Somen
 */
@SuppressWarnings("unused")
public class GetTemplate implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Executes the GetTemplate component to retrieve the content of a specific export template.
     *
     * @param jsonFormData 			JSON input data containing the templateId.
     * @return JSON response containing the template content.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonObject response = new JsonObject();
        String templateId = formData.get("templateId").getAsString();
        String jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + templateId + ExportUtils
                .JSON_EXTENSION;
        String fileAsString = ExportUtils.getFileAsString(jsonFileName);
        JsonObject body = JsonParser.parseString(fileAsString).getAsJsonObject().getAsJsonObject("body");
        GsonUtility.accumulate(response,"template", body);

        return response.toString();
    }


}
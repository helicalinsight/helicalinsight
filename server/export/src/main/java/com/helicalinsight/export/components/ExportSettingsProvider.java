package com.helicalinsight.export.components;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;


import java.io.File;

/**
 * An implementation of the IComponent interface that provides export settings from a JSON file.
 * It reads the contents of "TemplateSettings.json" and returns the JSON response.
 *
 * Created by Author on 08/12/2016
 * @author Somen
 */
@SuppressWarnings("unused")
public class ExportSettingsProvider implements IComponent {
	 /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, otherwise {@code false}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     *This method provide export settings from "TemplateSettings.json".
     *
     * @param jsonFormData 		JSON input data (not used in this implementation).
     * @return JSON response containing the contents of "TemplateSettings.json".
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject response = new JsonObject();
        String settingFilePath = ExportUtils.getReportDirectory() + File.separator + "TemplateSettings.json";
        JsonObject jsonContent = ExportUtils.getFileAsJsonObject(settingFilePath);
        response.add("templateSettings",jsonContent);
        return response.toString();
    }


}
package com.helicalinsight.efw.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 *         Created by helical021 on 4/26/2018.
 */
public class UploadClassFactory {


    private static final Logger logger = LoggerFactory.getLogger(UploadClassFactory.class);

    public static IUpload getIUploadClass(String type) {
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        Object uploadImplementations = settingsJson.getAsJsonObject("upload").getAsJsonArray("handler");
        if (type == null || type.isEmpty() || uploadImplementations == null) {
            logger.info("No upload implementation found returning the default implementation");
            return ApplicationContextAccessor.getBean(ImportOperationHandler.class);
        }
        if (uploadImplementations instanceof JsonObject) {
            JsonObject implementer = (JsonObject) uploadImplementations;
            return (IUpload) ApplicationContextAccessor.getBean(implementer.get("bean").getAsString());
        }


        JsonArray implementations =  (JsonArray) uploadImplementations;
        //jsonFxml.getJSONArray("HwfSources");

        for (int count = 0; count < implementations.size(); count++) {
            JsonObject item = implementations.get(count).getAsJsonObject();
            if (type.equalsIgnoreCase(item.get("type").getAsString())) {
                String requiredBean = item.get("bean").getAsString();
                return (IUpload) ApplicationContextAccessor.getBean(requiredBean);
            }
        }
        return null;
    }
}

package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
public class DsTypeHandlerFactory {

    public static EfwdDataSourceHandler handler(String type) {
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        if (!settingsJson.has("efwdDataSourceHandlers")) {
            throw new XmlConfigurationException("Setting XML is not configured with the efwd datasource handlers");
        }
        JsonArray efwdDataSourceHandlers = settingsJson.getAsJsonObject("efwdDataSourceHandlers").getAsJsonArray("handler");
        String clazz = null;
        for (Object typeHandler : efwdDataSourceHandlers) {
            JsonObject handler = JsonParser.parseString(typeHandler.toString()).getAsJsonObject();
            String classifier = handler.get("classifier").getAsString();
            if (classifier.equalsIgnoreCase(type)) {
                clazz = handler.get("class").getAsString();
                break;
            }
        }

        if (clazz == null) {
            throw new ClassNotConfiguredException("Efwd datasource handler class not configured");
        }

        EfwdDataSourceHandler handler = FactoryMethodWrapper.getTypedInstance(clazz, EfwdDataSourceHandler.class);

        if (handler == null) {
            throw new EfwServiceException("Couldn't produce instance of type EfwdDataSourceHandler");
        } else {
            return handler;
        }
    }
}

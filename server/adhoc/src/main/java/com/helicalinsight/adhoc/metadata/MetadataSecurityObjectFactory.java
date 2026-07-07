package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * MetadataSecurityObjectFactor.
 * This factory class is responsible for creating instances of security handlers based on the specified condition type.
 * Created on 7/13/2016.
 * @author Somen
 */
public class MetadataSecurityObjectFactory {
	/**
     * Gets the appropriate security class based on the specified condition type.
     *
     * @param conditionType 					 type of condition.
     * @return The security handler instance.
     * @throws ClassNotConfiguredException 		 the security class is not configured.
     * @throws EfwServiceException         		 an instance of type ISecureMetadata cannot be produced.
     */
    public static ISecureMetadata getSecurityClass(String conditionType) {
        String clazz = getClass(conditionType);
        if (clazz == null) {
            throw new ClassNotConfiguredException("Security implementation for " + conditionType + " class not " +
                    "configured");
        }

        ISecureMetadata securityHandler = FactoryMethodWrapper.getTypedInstance(clazz, ISecureMetadata.class);

        if (securityHandler == null) {
            throw new EfwServiceException("Couldn't produce instance of type ISecureMetadata");
        } else {
            return securityHandler;
        }
    }
    /**
     * Gets the class name corresponding to the specified condition type from the settings JSON.
     *
     * @param conditionType 		 type of condition.
     * @return The class name.
     */
    public static String getClass(String conditionType) {
        String clazz = null;
        JsonObject settingsJson = getSettingJson();
        JsonObject securityObject = settingsJson.getAsJsonObject("metadataSecurity");
        JsonElement unknownObject = securityObject.get("securityType");
        if (unknownObject.isJsonObject()) {
            JsonObject security =  unknownObject.getAsJsonObject();
            String type = security.get("type").getAsString();
            if (type.equalsIgnoreCase(conditionType)) {
                clazz = security.get("class").getAsString();
            }
        }
        if (unknownObject.isJsonArray()) {
            JsonArray metadataSecurity =  unknownObject.getAsJsonArray();

            for (JsonElement securityType : metadataSecurity) {
                JsonObject handler = securityType.getAsJsonObject();
                String type = handler.get("type").getAsString();
                if (type.equalsIgnoreCase(conditionType)) {
                    clazz = handler.get("class").getAsString();
                    break;
                }
            }
        }
        return clazz;
    }
    /**
     * It returns setting.xml file into the json object the settings JSON.
     *
     * @return settings JSON object.
     * @throws XmlConfigurationException If the setting XML is not configured with the metadataSecurity.
     */
    public static JsonObject getSettingJson() {
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        if (!settingsJson.has("metadataSecurity")) {
            throw new XmlConfigurationException("Setting XML is not configured with the metadataSecurity");
        }
        return settingsJson;
    }
}
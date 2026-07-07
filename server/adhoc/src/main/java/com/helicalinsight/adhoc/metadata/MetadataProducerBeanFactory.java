package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Created by user on 5/13/2016.
 * <p/>
 * Uses the factory pattern with a message. The message is type and the factory uses setting.xml configuration.
 * <p/>
 * Configured objects must be of type IMetadataProducer which needs to Spring Beans.
 *
 * @author Rajasekhar
 */
public class MetadataProducerBeanFactory {
	/**
     * Returns a metadata instance based on the specified type.
     * 
     * @param type 				 type of metadata producer to retrieve.
     * @return An instance of the metadata producer.
     * @throws ConfigurationException If there is an issue with the configuration.
     */
    public static IMetadataProducer getMetadataProducer(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type parameter can't be null");
        }

        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        if (!settingsJson.has("metadataXmlProducers")) {
            throw new XmlConfigurationException("Provide metadataXmlProducers tag with mandatory attribute in " +
                    "the setting.xml");
        }

        JsonObject metadataXmlProducers;
        try {
            metadataXmlProducers = settingsJson.getAsJsonObject("metadataXmlProducers");
        } catch (Exception ex) {
            throw new XmlConfigurationException("Provide metadataXmlProducers tag with mandatory attribute in " +
                    "the setting.xml", ex);
        }

        JsonElement producers = metadataXmlProducers.get("producer");
        try {
            if (producers.isJsonObject()) {
                JsonObject jsonObject = producers.getAsJsonObject();
                String string = jsonObject.get("type").getAsString();
                if (type.equalsIgnoreCase(string)) {
                    return (IMetadataProducer) ApplicationContextAccessor.getBean(jsonObject.get("bean").getAsString());
                }
            } else if (producers.isJsonArray()) {
                //Many implementations are there
                JsonArray array = producers.getAsJsonArray();
                for (int index = 0; index < array.size(); index++) {
                    JsonObject jsonObject = array.get(index).getAsJsonObject();
                    if (type.equalsIgnoreCase(jsonObject.get("type").getAsString())) {
                        return (IMetadataProducer) ApplicationContextAccessor.getBean(jsonObject.get("bean").getAsString());
                    }
                }
            } else {
                throw new ConfigurationException("Unknown configuration of metadataXmlProducers");
            }
        } catch (ConfigurationException ex) {
            throw new ConfigurationException(ex);
        }

        throw new ConfigurationException("No relevant type is configured for the Metadata Producers");
    }
}

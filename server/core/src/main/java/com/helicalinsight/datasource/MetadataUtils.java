package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MetadataUtils
 * Utility class for working with metadata configurations.
 * This class provides methods for retrieving metadata utility based on data source type.
 *
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public class MetadataUtils {

	
    /**
     * getMetadataUtility(String dataSourceType, JsonArray metadataImplementations)
     * @param dataSourceType                        type of data source like "sql.jdbc","global.jdbc"
     * @param  metadataImplementations              consists of metadata information
     * @return MetadataUtility object if a match is found, or {@code null} if no matching configuration is found.
     * @throws ConfigurationException If there's an issue with the configuration of the "Setting.xml" metadata implementations node.					
     */
    @Nullable
    public static MetadataUtility getMetadataUtility(String dataSourceType, JsonArray metadataImplementations) {
        try {
            for (Object metadata : metadataImplementations) {
                //JSONObject aMetadata = JSONObject.fromObject(metadata);
            	JsonObject aMetadata = new Gson().fromJson((JsonObject)metadata, JsonObject.class);
                String useDefault = aMetadata.get("useDefault").getAsString();
                if ("true".equalsIgnoreCase(useDefault)) {
                    try {
                        JsonArray refs = aMetadata.getAsJsonArray("ref");
                        for (Object object : refs) {
                           // String type = JSONObject.fromObject(object).getString("@type");
                        	JsonObject fromJson = new Gson().fromJson((JsonObject)object, JsonObject.class);
                        	String type = fromJson.get("type").getAsString();
                            if (dataSourceType.equalsIgnoreCase(type)) {
                                return metadataUtility(aMetadata);
                            }
                        }
                    } catch (Exception ex) {
                        String type = aMetadata.getAsJsonObject("ref").get("type").getAsString();
                        if (dataSourceType.equalsIgnoreCase(type)) {
                            return metadataUtility(aMetadata);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new ConfigurationException("Setting.xml metadata implementations node is configured " +
                    "incorrectly", ex);
        }
        return null;
    }
    
	 /**
	  * metadataUtility(@NotNull JsonObject metadata) 
	  * @param metadata					provides metadata details, including class, connectionProvider, and type
	  * @return MetadataUtility object
	  * @throws ConfigurationException If there's an issue with the configuration of the metadata implementations node.
	  */
	 @NotNull
	 private static MetadataUtility metadataUtility(@NotNull JsonObject metadata) {
	        String metadataImplementation;
	        String connectionProvider;
	        String metadataRetrievalType;
	        try {
	            metadataImplementation = metadata.get("class").getAsString();
	            connectionProvider = metadata.get("connectionProvider").getAsString();
	            metadataRetrievalType = metadata.get("type").getAsString();
	        } catch (Exception ex) {
	            throw new ConfigurationException("Setting.xml metadata implementations node is " + "configured " +
	                    "incorrectly", ex);
	        }
	        return new MetadataUtility(connectionProvider, metadataImplementation, metadataRetrievalType);
	    }
}

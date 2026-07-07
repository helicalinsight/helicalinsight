package com.helicalinsight.adhoc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadataDeleteRule;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.ImportOperationHandler;
import com.helicalinsight.efw.utility.JsonUtils;
/**
 * A factory class for obtaining instances of data source delete and metadata delete classes based on the specified type.
 */
public class DatasourceDeleteClassFactory {
	 private static final Logger logger = LoggerFactory.getLogger(DatasourceDeleteClassFactory.class);
	 /**
	     * Retrieves an instance of the data source delete class based on the specified type.
	     *
	     * @param type 			 type of data source delete class.
	     * @return An instance of the data source delete class.
	     */
	    public static IDataSourceDeleteRule getIDataSourceDeleteClass(String type) {
	        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
	        JsonElement dataSourceDeleteImplementations = settingsJson.getAsJsonObject("datasourceDeleteHandlers").get("dataSourceDelete");
	        if (type == null || type.isEmpty() || dataSourceDeleteImplementations.isJsonNull()) {
	            logger.info("No upload implementation found returning the default implementation");
	            return (IDataSourceDeleteRule) ApplicationContextAccessor.getBean(ImportOperationHandler.class);
	        }
	        if (dataSourceDeleteImplementations.isJsonObject()) {
	            JsonObject implementer = dataSourceDeleteImplementations.getAsJsonObject();
	            return (IDataSourceDeleteRule) ApplicationContextAccessor.getBean(implementer.get("bean").getAsString());
	        }


	        JsonArray implementations = dataSourceDeleteImplementations.getAsJsonArray();
	        //jsonFxml.getJSONArray("HwfSources");

	        for (int count = 0; count < implementations.size(); count++) {
	            JsonObject item = implementations.get(count).getAsJsonObject();
	            if (type.equalsIgnoreCase(item.get("type").getAsString())) {
	                String requiredBean = item.get("bean").getAsString();
	                return (IDataSourceDeleteRule) ApplicationContextAccessor.getBean(requiredBean);
	            }
	        }
	        return null;
	    }
	    /**
	     * Retrieves an instance of the metadata delete class based on the specified type.
	     *
	     * @param type 			 type of metadata delete class.
	     * @return An instance of the metadata delete class.
	     */
	    public static IMetadataDeleteRule getIMetadataDeleteClass(String type) {
	        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
	        JsonElement dataSourceDeleteImplementations = settingsJson.getAsJsonObject("metadataDeleteHandlers").get("metadataDelete");
	        if (type == null || type.isEmpty() || dataSourceDeleteImplementations.isJsonNull()) {
	            logger.info("No upload implementation found returning the default implementation");
	            return (IMetadataDeleteRule) ApplicationContextAccessor.getBean(ImportOperationHandler.class);
	        }
	        if (dataSourceDeleteImplementations.isJsonObject()) {
	            JsonObject implementer = dataSourceDeleteImplementations.getAsJsonObject();
	            return (IMetadataDeleteRule) ApplicationContextAccessor.getBean(implementer.get("bean").getAsString());
	        }


	        JsonArray implementations = dataSourceDeleteImplementations.getAsJsonArray();
	        //jsonFxml.getJSONArray("HwfSources");

	        for (int count = 0; count < implementations.size(); count++) {
	            JsonObject item = implementations.get(count).getAsJsonObject();
	            if (type.equalsIgnoreCase(item.get("type").getAsString())) {
	                String requiredBean = item.get("bean").getAsString();
	                return (IMetadataDeleteRule) ApplicationContextAccessor.getBean(requiredBean);
	            }
	        }
	        return null;
	    }
	}
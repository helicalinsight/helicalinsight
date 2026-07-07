package com.helicalinsight.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * FileOperationOverNetworkFactory
 * Factory class for obtaining an implementation of the AbstractFileOperationsOverNetwork interface
 * based on the specified type.
 */
public class FileOperationOverNetworkFactory {

	private static final Logger logger = LoggerFactory.getLogger(FileOperationOverNetworkFactory.class);
    /**
	 * getFileOperationHandlerClass(String type)
     * returns an implementation of AbstractFileOperationsOverNetwork based on the specified type.
     *
     * @param type					type of file operation handler to retrieve.
     * @return An instance of AbstractFileOperationsOverNetwork that corresponds to the specified type.
     * @throws EfwServiceException If no implementation is found for the specified type.
     */
	public static AbstractFileOperationsOverNetwork getFileOperationHandlerClass(String type) {
		JsonObject settingsJson = JsonUtils.newGetSettingsJson();
//		Object fileOperationOverNetworkHandlerImpl = settingsJson.opt("fileOperationOverNetworkHandler");
		Object fileOperationOverNetworkHandlerImpl = settingsJson.getAsJsonObject("fileOperationOverNetworkHandler").getAsJsonArray("fileOperationHandler");
		if (type == null || type.isEmpty() || fileOperationOverNetworkHandlerImpl == null) {
			logger.info("No upload implementation found returning the default implementation");
			return  ApplicationContextAccessor.getBean(SFTPTransferHandler.class);
		}
		if (fileOperationOverNetworkHandlerImpl instanceof JsonObject) {
			JsonObject implementer = (JsonObject) fileOperationOverNetworkHandlerImpl;
			return (AbstractFileOperationsOverNetwork) ApplicationContextAccessor
					.getBean(implementer.get("bean").getAsString());
		}

		JsonArray implementations = (JsonArray) fileOperationOverNetworkHandlerImpl;

		for (int count = 0; count < implementations.size(); count++) {
			JsonObject item = implementations.get(count).getAsJsonObject();
			if (type.equalsIgnoreCase(item.get("type").getAsString())) {
				String requiredBean = item.get("bean").getAsString();
				return (AbstractFileOperationsOverNetwork) ApplicationContextAccessor.getBean(requiredBean);
			}
		}
		throw new EfwServiceException("No Implementation found for the type. Please configure the setting.xml "+type);
	}

}

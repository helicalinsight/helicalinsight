package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * The FileProcessStatus class implements the {@code IComponent} interface and is responsible for handling file processing status.
 */
public class FileProcessStatus implements IComponent{
	/**
     * Executes the component by retrieving file processing status for the given JSON form data.
     *
     * @param jsonFormData 			 form data provides requestId.
     * @return JSON string representing the file processing status.
     */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);
		JsonObject responseJson = new JsonObject();
		ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
		if (formJson.has("requestId")) {
			JsonObject fileProcessedPercentage = registry.getFileProcessedPercentage(formJson.get("requestId").getAsString());
			for (String key : fileProcessedPercentage.keySet()) {
				responseJson.add(key, fileProcessedPercentage.get(key));
			}
		}

		return responseJson.toString();
	}
	/**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
	@Override
	public boolean isThreadSafeToCache() {
		return false;
	}
}

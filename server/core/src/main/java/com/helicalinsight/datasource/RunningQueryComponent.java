package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * RunningQueryComponent implements the {@link IComponent} interface
 * Class is responsible for checking active query which are running in background or not.
 * @author Somen
 * Created  on 2/10/2017.
 */
public class RunningQueryComponent implements IComponent {

	/**
	 * executeComponent(String jsonFormData)
	 * @param jsonFormData           formData in String format
	 * @return jsonObject in String format with add query details if query are present , otherwise
	 *  message with no query running running in background.
	 */
    @Override
    public String executeComponent(String jsonFormData) {
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        JsonObject responseJson = new JsonObject();
        JsonObject data = registry.listDetails();
        if (data.size() > 0) {
            responseJson.add("data", data);
        } else {
            responseJson.addProperty("message", "No query running in the background found");
        }
        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
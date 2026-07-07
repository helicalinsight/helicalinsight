package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * CancelQuery implements {@link IComponent}
 * Class is responsible for canceling query.
 * @author Somen
 * Created  on 2/9/2017.
 */
public class CancelQuery implements IComponent {

	/**
	 * executeComponent(String jsonFormData)
	 * This method is responsible for canceling query by using query Id or thread name
	 * @param jsonFormData         formData in String Format
	 * @return jsonObject in String format.
	 */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);
        JsonObject responseJson = new JsonObject();
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        if (formJson.has("queryId")) {
            String queryId = formJson.get("queryId").getAsString();
            
            JsonObject cancelQuery = registry.cancelQuery(queryId);
            for(String key : cancelQuery.keySet()) {
            	responseJson.add(key, cancelQuery.get(key));
            }
            
        }
        if (formJson.has("threadName")) {
            String threadName = formJson.get("threadName").getAsString();
            JsonObject cancelQueryByThreadName = registry.cancelQueryByThreadName(threadName);
            for(String key : cancelQueryByThreadName.keySet()) {
            	responseJson.add(key, cancelQueryByThreadName.get(key));
            }
        }
        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

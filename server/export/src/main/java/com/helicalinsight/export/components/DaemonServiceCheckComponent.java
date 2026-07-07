//DaemonServiceCheckComponent.java
package com.helicalinsight.export.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.DaemonPhantomService;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.export.PhantomThreadDemonManager;

/**
 * The `DaemonServiceCheckComponent` class implements the `IComponent` interface to manage daemon thread states.
 * This component class is can able to call utility methods present in
 * PhantomThreadManager for managing all daemon thread states based on request
 * parameter values
 *
 * @author Rajasekhar M
 */
@Deprecated
public class DaemonServiceCheckComponent implements IComponent {
	/**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, otherwise {@code false}
     */
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
	/**
     * Executes the component based on the provided JSON form data.
     *
     * @param jsonFormData JSON form data containing the action to perform (start, stop, check).
     * @return A JSON string containing the result of the requested action.
     */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
		String action = GsonUtility.optString(formJson,"action");
		JsonObject responseJson = new JsonObject();
		PhantomThreadDemonManager phantomThreadDemonManager = new PhantomThreadDemonManager();
		switch (action) {
		case "start":
			phantomThreadDemonManager.start();
			
			
			while(DaemonPhantomService.getProcess()==null ||!ExportUtils.isProcessActive(DaemonPhantomService.getProcess())){
				continue;
			}
		
			responseJson.addProperty("message", PhantomThreadDemonManager.getMessage());
			break;
		case "stop":
			phantomThreadDemonManager.stop();
			responseJson.addProperty("message", PhantomThreadDemonManager.getMessage());
			break;
		case "check":
			responseJson.addProperty("running", PhantomThreadDemonManager.isServiceActive());
			break;
		}
		return responseJson.toString();
	}

}

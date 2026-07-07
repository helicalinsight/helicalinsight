package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * Handles the ownership change service.
 */
public class OwnershipChangeService  implements IService {
	/**
     * Executes the ownership change service.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The specific service.
     * @param formData    The form data as JSON string.
     * @return The result of the service execution.
     */
	@Override
	public String doService(String type, String serviceType, String service, String formData) {
		JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();
        String componentJson = ServiceUtils.componentJson(type, serviceType, service);
        String componentClass = ServiceUtils.componentClass(componentJson);
        return ServiceUtils.executeService(formDataJson, componentJson, componentClass);
	}

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
}

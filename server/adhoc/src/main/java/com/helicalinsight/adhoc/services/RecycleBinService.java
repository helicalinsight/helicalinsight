package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * Service for handling operations related to the recycle bin.
 */
public class RecycleBinService implements IService {

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

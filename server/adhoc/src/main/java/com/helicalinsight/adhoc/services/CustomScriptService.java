package com.helicalinsight.adhoc.services;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * A service class responsible for executing custom script-related services.
 * Created by author on 14-05-2015.
 * @author Somen
 */
public class CustomScriptService implements IService {
	/**
     * Executes the specified custom script service.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The service to be executed.
     * @param formData    The form data containing parameters for the service.
     * @return The result of executing the custom script service.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.execute(type, serviceType, service, formData);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

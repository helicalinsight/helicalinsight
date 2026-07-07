package com.helicalinsight.adhoc.services;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * A service handler responsible for writing data sources.
 * Created by author on 24-02-2015.
 * @author Rajasekhar
 */
public class DataSourceWriter implements IService {
	/**
     * Executes the service to write data sources.
     * 
     * @param type        The type of service.
     * @param serviceType The type of service.
     * @param service     The service to execute.
     * @param formData    The form data containing parameters for writing data sources.
     * @return A string representing the result of the service execution.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.executeService(type, serviceType, service, formData);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

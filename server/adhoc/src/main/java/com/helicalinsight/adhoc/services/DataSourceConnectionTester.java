package com.helicalinsight.adhoc.services;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * A service class for testing data source connections.
 * Created by author on 24-02-2015.
 * @author Rajasekhar
 */
public class DataSourceConnectionTester implements IService {
	/**
     * Executes the data source connection testing service.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The service to be executed.
     * @param formData    The form data containing parameters for the service.
     * @return The result of executing the data source connection testing service.
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

package com.helicalinsight.adhoc.services;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * A service class for handling dashboard designer-related services.
 */
public class DashboardDesignerService implements IService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the specified dashboard designer service.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The service to be executed.
     * @param formData    The form data containing parameters for the service.
     * @return The result of executing the dashboard designer service.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.execute(type, serviceType, service, formData);
    }
    /**
     * Executes the specified dashboard designer service component.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The service component to be executed.
     * @param formData    form data containing parameters for the service component.
     * @return The result of executing the dashboard designer service component.
     */
    public Object executeService(String type, String serviceType, String service, String formData){
        return ServiceUtils.executeComponent(type, serviceType, service, formData);
    }

}

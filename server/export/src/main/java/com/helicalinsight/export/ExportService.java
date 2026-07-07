package com.helicalinsight.export;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * The ExportService class implements the IService interface and serves as a service for handling export-related operations.
 * It provides a way to execute export services using the ServiceUtils class.
 * Created by author on 07-Dec-16.
 * @author Somen
 */

public class ExportService implements IService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * It executes the export service based on the provided parameters.
     *
     * @param type      	type of service.(Ex: core,adhoc)
     * @param serviceType 	type of the export service.(Ex: Generate Report, save report)
     * @param service   	The specific export service to execute.(Ex: datasource, report, metadata)
     * @param formData  	form data or parameters required for the export service.
     * @return A string representing the result of the export service execution.
     * @implNote This method delegates the execution of export services to the ServiceUtils class.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        return ServiceUtils.execute(type, serviceType, service, formData);
    }
}

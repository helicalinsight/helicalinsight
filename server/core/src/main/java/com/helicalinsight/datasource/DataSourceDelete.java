package com.helicalinsight.datasource;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
/**
 * DataSourceDelete implements the {@link IService} interface.
 * This class allows you to delete data sources using specific services.
 */
public class DataSourceDelete implements IService {

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

	/**
	 * doService(String type, String serviceType, String service, String formData)
	 * Executes a service operation for deleting data sources.
	 *
	 * @param type        feature type
	 * @param serviceType type of the service to be executed.
	 * @param service     actual service operation to be performed.
	 * @param formData    Additional data or parameters required for the service
	 *                    operation, provided in string format.
	 * @return A result or response from executing the specified service operation.
	 */
	@Override
	public String doService(String type, String serviceType, String service, String formData) {
		return ServiceUtils.executeService(type, serviceType, service, formData);
	}

}

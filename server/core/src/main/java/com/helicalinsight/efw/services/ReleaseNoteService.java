package com.helicalinsight.efw.services;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

public class ReleaseNoteService implements IService {

	

	@Override
	public String doService(String type, String serviceType, String service, String formData) {
		return ServiceUtils.execute(type, serviceType, service, formData);
	}
	
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
}

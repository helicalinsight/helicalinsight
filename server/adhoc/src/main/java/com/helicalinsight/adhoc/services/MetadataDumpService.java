package com.helicalinsight.adhoc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

public class MetadataDumpService implements IService{
	
	private static final Logger logger = LoggerFactory.getLogger(MetadataDumpService.class);

	@Override
	public String doService(String type, String serviceType, String service, String formData) {
		return  ServiceUtils.execute(type, serviceType, service, formData);		
	}
	
	
	
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

}

package com.helicalinsight.core;

import org.springframework.stereotype.Component;

import com.helicalinsight.admin.management.URLContextManager;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.resourcedb.processor.ResourceAuthenticatorDB;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import com.helicalinsight.resourcesecurity.jaxb.SubContext;


@Component
public class ResourceLevelPermissionEvaluator {
	
	private final URLContextManager urlContextManager;
	private final ResourceAuthenticatorDB resourceAuthenticatorDB;
	
	public ResourceLevelPermissionEvaluator(URLContextManager urlContextManager, ResourceAuthenticatorDB resourceAuthenticatorDB) {
		this.urlContextManager = urlContextManager;
		this.resourceAuthenticatorDB = resourceAuthenticatorDB;
	}
	
	
	public Boolean evaluate(String url,  String type, String serviceType, String service, String formJson) {
		Context context = urlContextManager.findUrlContext(url);
		SubContext subContext = urlContextManager.findSubContext(context.getSubContexts(), type, serviceType, service);
    	boolean result =  resourceAuthenticatorDB.checkPermission(formJson, context, subContext);
    	if (!result) {
    		String err = "Access Denied. You don't have sufficient privileges to access  the requested resource";
    		throw new AccessDeniedException(err);
    	}
    	return result;
	}

}

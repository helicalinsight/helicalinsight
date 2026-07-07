package com.helicalinsight.efw;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.context.ServiceRequestContext;
import com.helicalinsight.efw.controller.UserSession;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.ServiceManager;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ServiceContextBuilder {
	
    private final StatusValidator statusValidator;
    private final ServiceManager serviceManager;
    
    
    public ServiceContextBuilder(ServiceManager serviceManager, StatusValidator statusValidator) {
    	this.statusValidator = statusValidator;
    	this.serviceManager = serviceManager;
    }
    
	
	public ServiceRequestContext buildContext(String type, String serviceType, String service, HttpServletRequest request,  Map<String, JsonObject>  gsonStore) {

	    if (statusValidator.isStatusNotOkay()) {
	        throw new EfwServiceException("Unexpected error occured!");
	    }

	    Map<String, String> parameters = new HashMap<>();
	    parameters.put("type", type);
	    parameters.put("serviceType", serviceType);
	    parameters.put("service", service);

	    String formData = request.getParameter("formData");
	    if (formData == null) {
	        formData = new JsonObject().toString();
	    }

	    ControllerUtils.checkForNullsAndEmptyParameters(parameters);

	    JsonObject formJson = GsonUtility.parseString(formData, JsonObject.class);

	    String uniqueKey = null;
	    if (formJson.has("htmlString")) {
	        uniqueKey = Math.random() + "" + System.currentTimeMillis();
	        gsonStore.put(uniqueKey, formJson);

	        JsonObject formJsonNew = new JsonObject();
	        formJsonNew.addProperty("formDataId", uniqueKey);
	        formJson = formJsonNew;
	    }

	    String workerClass = serviceManager.pickTheWorkerClass(type, serviceType, service);

	    String requestId = request.getParameter("requestId");
	    if (StringUtils.isBlank(requestId)) {
	        requestId = (String) request.getAttribute("requestId");
	    }

	    RequestContext.set(requestId);
	    formJson.addProperty("requestId", requestId);

	    UserSession userSession = new UserSession();
	    userSession.addSessionDetails(service, request, formJson);

	    return new ServiceRequestContext(formJson, workerClass, uniqueKey, requestId);
	}
}

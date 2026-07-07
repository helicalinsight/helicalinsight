package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;


/**
 * A service class for visualization operations.
 */
public class VisualizationService implements IService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Performs the visualization service based on the specified parameters.
     *
     * @param type       The type of service.
     * @param serviceType The service type.
     * @param service    The specific service being executed.
     * @param formData   The form data for the service.
     * @return A string representing the result of the service operation.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        
    	if("getReportForEdit".equalsIgnoreCase(service)) {
        	formData = JacksonUtility.fromObject(formData)
        			.put("isEdit", true)
        			.toString();
        }
    	String result = ServiceUtils.execute(type, serviceType, service, formData);
        JsonObject jsonResult = JsonParser.parseString(result).getAsJsonObject();
        JsonObject response = GsonUtility.optJsonObject(jsonResult,"response");

        if (response != null && !response.entrySet().isEmpty()) {
        	JsonObject data = GsonUtility.optJsonObject(response,"data");
	        	if(data != null ) {
	        		JsonObject metadataJson = GsonUtility.optJsonObject(data,"metadata");
		            if (metadataJson != null) {
		                String location = GsonUtility.optString(metadataJson,"location");
		                String fileName = GsonUtility.optString(metadataJson,"metadataFileName");
		                MetadataProvider metadataProvider = new MetadataProvider();
		                JsonObject jsonFormData;
		                jsonFormData = new JsonObject();
		                GsonUtility.accumulate(jsonFormData,"location", location);
		                GsonUtility.accumulate(jsonFormData,"metadataFileName", fileName);
		                String metadataResult = metadataProvider.doService("adhoc", "metadata", "get", jsonFormData.toString());
		                JsonObject serviceJson = JsonParser.parseString(metadataResult).getAsJsonObject();
		                JsonObject metadataJsonFile = ControllerUtils.getDataFromResponse(serviceJson);
		                GsonUtility.accumulate(metadataJson,"data", metadataJsonFile);
		                GsonUtility.accumulate(metadataJson,"databaseName", metadataJsonFile.get("databaseName").getAsString());
		            }
	        	}
            return jsonResult.toString();
        } else {
            return result;
        }
    }
}

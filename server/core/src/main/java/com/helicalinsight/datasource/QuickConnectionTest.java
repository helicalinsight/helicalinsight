package com.helicalinsight.datasource;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.GroovyUtils;
import com.helicalinsight.efw.utility.JsonUtils;
 

import java.util.List;

/**
 * QuickConnectionTest is a service that performs quick connection testing for data sources.
 * It implements the {@link IService} interface.
 * Created by author on 25-08-2015.
 * @author Somen
 */
@SuppressWarnings("unused")
public class QuickConnectionTest implements IService {
	/**
	 * doService(String type, String serviceType, String service, String formData)
     * Performs the quick connection testing service.
     *
     * @param type       		feature type.
     * @param serviceType 		specific service type.
     * @param service    		actual service operation to be performed.
     * @param formData   		form data as a string.
     * @return The result of the connection testing service.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject requestFormData = new Gson().fromJson(formData,JsonObject.class);

        DataSourceReader dataSourceReader = new DataSourceReader();

        String result = dataSourceReader.doService(type, serviceType, "read", formData);
        JsonObject readServiceResult = new Gson().fromJson(result,JsonObject.class);

        if (readServiceResult.get("status").getAsString().equals("0")) {
            return result;
        }


        JsonObject dataJson = ControllerUtils.getDataFromResponse(readServiceResult);
        String usernameString = GsonUtility.optString(dataJson, "userName");
        dataJson.addProperty("userName", usernameString.equals("[]") ? "" : usernameString);
        String passwordString = GsonUtility.optString(dataJson, "password");
        dataJson.addProperty("password", passwordString.equals("[]") ? "" : passwordString);
        JsonObject freshRequest = removeAtFromJsonKey(dataJson);
        String classifier;
        if(GlobalJdbcTypeUtils.isManagedGroovyDataSource(freshRequest.get("type").getAsString())){
        	String dataString = freshRequest.get("data").getAsString();
        	JsonObject data = new Gson().fromJson(dataString, JsonObject.class);
            JsonObject condition = GroovyUtils.executeGroovy(data.get("condition").getAsString(), "evalCondition", JsonObject.class);
            String globalId= GsonUtility.optString(condition, "globalId");
            if (globalId.isBlank()) {
            	throw new EfwServiceException("You do not have privileges to access the global connections.");
            }
            JsonObject freshReq = new JsonObject();
            freshReq.addProperty("globalId",globalId);
            freshReq.addProperty("id",globalId);
            freshReq.addProperty("classifier","global");
            freshReq.addProperty("type","dynamicDataSource");
            String resultG = dataSourceReader.doService(type, serviceType, "read", freshReq.toString());
            JsonObject res = new Gson().fromJson(resultG,JsonObject.class);
            if (res.get("status").getAsString().equals("0")) {
                resultG = resultG.replace("Error: ", "");
                return resultG;
            }


             dataJson = ControllerUtils.getDataFromResponse(res);

            freshRequest=removeAtFromJsonKey(dataJson);
            freshRequest.addProperty("classifier","global");



        }
        if (!freshRequest.has("classifier")) {
            classifier = GsonUtility.optStringValue(requestFormData, "classifier","");
            freshRequest.addProperty("classifier", classifier);
        }
        if(!freshRequest.has("globalId")) {
            String dir = GsonUtility.optStringValue(requestFormData, "dir","");
            freshRequest.addProperty("dir", dir);
        }
        return ServiceUtils.executeService(type, serviceType, "test", freshRequest.toString());
    }
    /**
     * removeAtFromJsonKey(JsonObject dataJson)
     * This method is responsible for removing "@" symbol from JSON keys.
     * @param dataJson           formData 
     * @return jsonObject after removing "@" key.
     */
    private JsonObject removeAtFromJsonKey(JsonObject dataJson) {
        List<String> jsonKeys = JsonUtils.getKeys(dataJson);
        JsonObject duplicateJson = new JsonObject();
        for (String key : jsonKeys) {
            String tempKey;
            String tempValue;
            if (key.startsWith("@")) {
                tempKey = key.replace("@", "");
            } else {
                tempKey = key;
            }
            JsonElement jsonElement = dataJson.get(key);
            if(!jsonElement.isJsonNull()) {
            if(jsonElement instanceof JsonArray) {
            	tempValue = jsonElement.toString();
            }else {
            	if(jsonElement.isJsonPrimitive()) {
            	tempValue = jsonElement.getAsString();
            	}
            	else {
            		tempValue = jsonElement.toString();
            	}
            	
            }
//            String asString = dataJson.get(key).getAsString();
//            tempValue = dataJson.get(key).toString();
            duplicateJson.addProperty(tempKey, tempValue);
            }
        }
        return duplicateJson;
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

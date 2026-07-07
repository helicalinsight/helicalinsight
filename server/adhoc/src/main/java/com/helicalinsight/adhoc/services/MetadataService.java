package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * Handles metadata-related services.
 * Created by Rajasekhar on 22-04-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class MetadataService implements IService {

	/**
     * Executes the metadata service.
     * 
     * @param type 					 type of service.
     * @param serviceType 			 service type.
     * @param service 				 specific service.
     * @param formData 				 form data provides type.
     * @return The result of the service execution.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject formJson = new Gson().fromJson(formData,JsonObject.class);
        String sourceType;
        try {
            sourceType = formJson.get("type").getAsString();
        } catch (Exception ex) {
            throw new EfwdServiceException("Without data source type information schemas can not be retrieved.");
        }
        DataSourceUtils.validate(sourceType);
        AdhocServiceUtils.addExtraDataForNormalProcess(formJson, sourceType);
        return ServiceUtils.executeService(type, serviceType, service, formJson.toString());
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

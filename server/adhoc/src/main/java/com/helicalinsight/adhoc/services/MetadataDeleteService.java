package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import java.io.File;

/**
 * A service class responsible for handling metadata deletion operations.
 * created by Author on 03-04-15
 * @author Somen
 */
public class MetadataDeleteService implements IService {
	/**
     * Executes the metadata deletion service.
     *
     * @param type      		 type of the service.
     * @param serviceType 		 service type.
     * @param service   		 service to be executed.
     * @param formData  		 JSON string containing the form data for the service provides location and metadata file name.
     * @return The result of the service execution.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject formJson = new Gson().fromJson(formData,JsonObject.class);
        String dataSourceType;
        if (!formJson.has("classifier")) {
            File metadataFile = new File(ApplicationProperties.getInstance().getSolutionDirectory() + File.separator
                    + formJson.get("location").getAsString() + File.separator +
                    formJson.get("metadataFileName").getAsString());

            if (metadataFile.exists()) {
                JsonObject metadataFileAsJson = JsonUtils.newGetAsJson(metadataFile);
                dataSourceType = metadataFileAsJson.getAsJsonObject("connection").get("connectionType").getAsString();
                AdhocServiceUtils.addExtraDataForNormalProcess(formJson, dataSourceType);
            } else {
                throw new RuntimeException("The given metadata file  not found");
            }
        }

        return ServiceUtils.executeService(type, serviceType, service, formJson.toString());
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

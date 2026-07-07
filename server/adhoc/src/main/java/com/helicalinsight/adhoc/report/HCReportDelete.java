package com.helicalinsight.adhoc.report;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import java.util.HashMap;
import java.util.Map;
/**
 * The HCReportDelete class implements the {@code IComponent} interface and provides functionality
 * to delete an HCReport from the system.
 */
public class HCReportDelete implements IComponent {

    private final HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
    /**
     * The executeComponent method is called to execute the deletion operation for an HCReport.
     *
     * @param jsonFormData 		 JSON string containing form data. formData provides dir and filename
     * @return A JSON string indicating the status of the deletion operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {

        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formData.get("dir").getAsString();
        String fileName = formData.get("file").getAsString();

        //Validate parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", directory);
        parameters.put("file", fileName);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        HIResource hiResource=serviceDB.getResourceByUrl(directory+"/"+fileName);
        try {
            serviceDB.deleteHIResourceById(hiResource.getResourceId());
        }catch(Exception e){
            throw new HCRException(e.getMessage());
        }
        JsonObject response = new JsonObject();
        response.addProperty("message","HCR file "+fileName+" deleted successfully");
        return response.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
}

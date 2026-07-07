package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;

/**
 * A service class responsible for executing ad-hoc report-related services.
 * Created by author on 20-03-2015.
 * @author Rajasekhar
 */
public class AdhocReportService implements IService {
	/**
     * Executes the specified service related to ad-hoc reports.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The service to be executed.
     * @param formData    The form data containing parameters for the service.
     * @return The result of executing the service.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        String result = ServiceUtils.executeService(type, serviceType, service, formData);
        JsonObject formDataJs = JsonParser.parseString(formData).getAsJsonObject();
        GsonUtility.accumulate(formDataJs,"result", result);

        //ServiceUtils.executeService("adhoc", "report", "generateThumbnail", formDataJs.toString());
        return result;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

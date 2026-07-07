package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.JaxbUtils;

import java.io.File;

/**
 * Service for generating and executing queries.
 * Created by author on 09-04-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class QueryGeneratorAndExecutorService implements IService {

	/**
     * Executes the query generation and execution service.
     *
     * @param type        The type of the service.
     * @param serviceType The service type.
     * @param service     The specific service to execute.
     * @param formData    The form data provides location, metadataFileName  etc.
     * @return The result of the service execution.
     * @throws IllegalStateException if required configurations to execute the component are not found.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        QueryGeneratorService queryGeneratorService = new QueryGeneratorService();
        JsonObject configurationSettings = queryGeneratorService.newFindConfigurationSettings(formData);
        JsonObject formJson = JsonParser.parseString(formData).getAsJsonObject();
        String location = formJson.get("location").getAsString();
        String metadataFileName = formJson.get("metadataFileName").getAsString();
        String fullPath = ApplicationProperties.getInstance().getSolutionDirectory() + "/" + location + "/" + metadataFileName;
        Metadata metadata =  MetadataDBUtility.getMetadata(location, metadataFileName);
        Boolean cached = metadata.getCached();
        if (configurationSettings != null) {
            if(cached!=null && cached && AppStatistics.isSPARK_STARTED() && AppStatistics.isMASTER_STARTED()){
                service="fetchDataSpark";
            }
            return ServiceUtils.executeService(type, serviceType, service, configurationSettings.toString());
        } else {
            throw new IllegalStateException("Couldn't find required configurations to execute the component.");
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

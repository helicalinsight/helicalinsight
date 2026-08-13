package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;


/**
 * Visualization / Instant BI open services.
 * <p>
 * When {@code provideMetadata} is true (default if omitted), nests full
 * metadata under {@code response.data.metadata.data} — same for getReport
 * and getAgent; independent of edit mode.
 */
public class AgentReadService implements IService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Performs the visualization / agent open service.
     *
     * @param type        The type of service.
     * @param serviceType The service type.
     * @param service     The specific service being executed.
     * @param formData    The form data for the service.
     * @return A string representing the result of the service operation.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject formJson = JsonParser.parseString(formData).getAsJsonObject();

        boolean provideMetadata = GsonUtility.optBooleanValue(formJson, "provideMetadata", false);

        String result = ServiceUtils.execute(type, serviceType, service, formJson.toString());
       
        if (!provideMetadata) {
            return result;
        }
		JsonObject jsonResult = JsonParser.parseString(result).getAsJsonObject();
        JsonObject response = GsonUtility.optJsonObject(jsonResult, "response");


        attachMetadata(response);
        return jsonResult.toString();
    }

    /**
     * Call metadata/get and nest under {@code response.data.metadata.data} (+ databaseName).
     */
    private void attachMetadata(JsonObject response) {
        JsonObject data = GsonUtility.optJsonObject(response, "data");
        if (data == null) {
            return;
        }
        JsonObject metadataJson = GsonUtility.optJsonObject(data, "metadata");
        
        String location = GsonUtility.optString(metadataJson, "location");
        String fileName = GsonUtility.optString(metadataJson, "metadataFileName");
        if (location == null || location.isEmpty() || fileName == null || fileName.isEmpty()) {
            return;
        }

        MetadataProvider metadataProvider = new MetadataProvider();
        JsonObject jsonFormData = new JsonObject();
        GsonUtility.accumulate(jsonFormData, "location", location);
        GsonUtility.accumulate(jsonFormData, "metadataFileName", fileName);
        GsonUtility.accumulate(jsonFormData, "provideJoins", true);
        GsonUtility.accumulate(jsonFormData, "uniqueId", true);

        String metadataResult = metadataProvider.doService(
                "adhoc", "metadata", "get", jsonFormData.toString());
        JsonObject serviceJson = JsonParser.parseString(metadataResult).getAsJsonObject();
        JsonObject metadataJsonFile = ControllerUtils.getDataFromResponse(serviceJson);
        if (metadataJsonFile == null) {
            return;
        }

        GsonUtility.accumulate(metadataJson, "data", metadataJsonFile);
        if (metadataJsonFile.has("databaseName")
                && !metadataJsonFile.get("databaseName").isJsonNull()) {
            GsonUtility.accumulate(
                    metadataJson,
                    "databaseName",
                    metadataJsonFile.get("databaseName").getAsString());
        }
    }
}

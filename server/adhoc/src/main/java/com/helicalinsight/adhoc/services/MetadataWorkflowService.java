package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;


/**
 * Handles the metadata workflow service.
 * Created by user on 5/13/2016.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class MetadataWorkflowService implements IService {
	
	/**
	 * This method essentially process the workflow for handling metadata services, 
	 * including data source validation, service execution, and modification of JSON responses based on certain conditions.
	 * 
	 * @param type       The type of service.
     * @param serviceType The service type.
     * @param service    The specific service.
     * @param formData   The form data provides type.
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
        DataSourceUtils.addExtraDataForWorkflowProcess(formJson, sourceType);
        String componentResult = ServiceUtils.executeService(type, serviceType, service, formJson.toString());
        if (formData.indexOf("fetchTables") > -1) {
            JsonObject resultJson = new Gson().fromJson(componentResult,JsonObject.class);
            //json.response.metadata.catalogs[0].schemas[0].tables
            if (resultJson.get("status").getAsInt() == 1) {
                JsonObject response = resultJson.getAsJsonObject("response");
                JsonObject metadata = response.getAsJsonObject("metadata");
                JsonArray catalogs = metadata.getAsJsonArray("catalogs");
                JsonObject catalogObject = catalogs.get(0).getAsJsonObject();
                JsonArray schemaArray = catalogObject.getAsJsonArray("schemas");
                JsonObject schemaItem = schemaArray.get(0).getAsJsonObject();
                JsonArray tableArray = schemaItem.getAsJsonArray("tables");

                JsonObject dataSource = metadata.getAsJsonObject("dataSource");
                String catalog = dataSource.get("catalog").getAsString();
                String schema = dataSource.get("schema").getAsString();

                if (!tableArray.isEmpty()) {

                    JsonArray newTableArray = new JsonArray();
                    for (int index = 0; index < tableArray.size(); index++) {

                        JsonObject tableItem = new JsonObject();
                        String tableName = tableArray.get(index).getAsString();
                        tableItem.addProperty("id", MetadataUtils.getId(catalog, schema, tableName));
                        tableItem.addProperty("name", tableName);
                        newTableArray.add(tableItem);
                    }
                    schemaItem.add("tables", newTableArray);

                }

            }
            return resultJson.toString();

        }
        return componentResult;
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

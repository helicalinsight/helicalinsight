package com.helicalinsight.adhoc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.serviceframework.IService;


/**
 * EnhancedMetadataJoinsRetriver class implements the {@link IComponent} interface to retrieve enhanced metadata joins.
 * It prepares a JSON form data and processes the response to retrieve enhanced metadata joins.
 *
 * @author Rajesh
 * created by author on 2/21/2019
 */
public class EnhancedMetadataJoinsRetriver implements IComponent {
	
	/**
     * Executes the component by preparing form data, sending a request to MetadataWorkflowService,
     * and processing the response to retrieve enhanced metadata joins.
     *
     * @param formData 			form data in JSON format.
     * @return A JSON string representing the enhanced metadata joins.
     */
    @Override
    public String executeComponent(String formData) {

        IService metadataWorkflowSaveHandler = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.adhoc.services.MetadataWorkflowService", IService.class);
        JsonObject preparedJsonFormData = prepareFormData(formData);
        preparedJsonFormData.addProperty("joinsOnly", "true");
        String response = metadataWorkflowSaveHandler.doService("adhoc", "metadata", "saveWorkflow", preparedJsonFormData.toString());
        JsonObject formatedResponse = JsonParser.parseString(response).getAsJsonObject();
        JsonObject metadataJson = formatedResponse.getAsJsonObject("response").getAsJsonObject("metadata");
        metadataJson.remove("sets");
        metadataJson.remove("tables");
        return metadataJson.toString();

    }
    /**
     * Prepares the JSON form data by extracting and formatting relevant information from the input.
     *
     * @param formData 			 form data in JSON format.
     * @return A JsonObject representing the prepared form data.
     */
    private JsonObject prepareFormData(String formData) {
        JsonObject jsonFormData = JsonParser.parseString(formData).getAsJsonObject();
        JsonObject preparedJsonFormData = new JsonObject();
        preparedJsonFormData.addProperty("id", jsonFormData.get("id").getAsString());
        if(jsonFormData.has("dir")){
            preparedJsonFormData.addProperty("dir",jsonFormData.get("dir").getAsString());
        }
        preparedJsonFormData.addProperty("type", jsonFormData.get("type").getAsString());
        String catalogName = null;
        String schemaName = null;
        JsonArray jsonTableArray = new JsonArray();
        if (jsonFormData.has("parameters")) {
            JsonObject parameters = jsonFormData.getAsJsonObject("parameters");
            JsonObject fetchData = parameters.getAsJsonArray("fetchData").get(0).getAsJsonObject();
            if (fetchData.has("catalog")) {
                catalogName = fetchData.get("catalog").getAsString();
            }
            JsonObject schema = fetchData.getAsJsonArray("schemas").get(0).getAsJsonObject();
            if (schema.has("name")) {
                schemaName = schema.get("name").getAsString();
            }
            JsonArray tablesArray = schema.getAsJsonArray("tables");
            tablesArray.forEach(tableName -> {
                JsonObject tableJson = new JsonObject();
                tableJson.add("name", tableName);
                jsonTableArray.add(tableJson);
            });
        }
        JsonObject metadata = new JsonObject();

        JsonArray catalogArray = new JsonArray();
        JsonObject catalog = new JsonObject();

        JsonArray schemasArray = new JsonArray();
        JsonObject schema = new JsonObject();
        schema.add("tables", jsonTableArray);
        if (schemaName != null) {
            schema.addProperty("name", schemaName);
        }
        schemasArray.add(schema);
        catalog.add("schemas", schemasArray);
        if (catalogName != null) {
            catalog.addProperty("catalog", catalogName);
        }
        catalogArray.add(catalog);
        metadata.add("catalogs", catalogArray);

        preparedJsonFormData.add("metadata", metadata);
        return preparedJsonFormData;
    }
    /**
     * Indicates whether the component is thread-safe to cache.
     *
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

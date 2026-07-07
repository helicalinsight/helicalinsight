package com.helicalinsight.efw.utility;

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.FormDataTemplateUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.ServiceManager;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

import java.util.Iterator;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Rajesh
 * Created by helical019 on 3/8/2019.
 */
@Component
@Scope("prototype")
public class AutoTriggerUtils {
    private static Logger logger = LoggerFactory.getLogger(AutoTriggerUtils.class);

    @Autowired
    private ServiceManager serviceManager;
    
    @Autowired
    private TaskExecutorService executorService;

    public void setData(JsonObject data) {
        this.data = data;
    }

    private JsonObject data;

    private String requestId;

    private boolean checkStatus(JsonObject formData) {
        return formData.get("status").getAsInt() == 1;
    }


    public void cacheTablesAndColumns(JsonObject resultJson) {
        if (checkStatus(resultJson)) {
            validStatus(resultJson);
        } else {
            logger.error("The status obtained is 0. Not caching the tables for all schemas/catalog");
        }
    }

    private void validStatus(JsonObject resultJson) {
        JsonObject response = resultJson.getAsJsonObject("response");
        JsonObject metadata = response.getAsJsonObject("metadata");
        JsonArray catalogArray = metadata.getAsJsonArray("catalogs");
        JsonArray reducedCatalog = FormDataTemplateUtils.removeUnwantedCatalog(catalogArray);
        JsonObject catalogJson = reducedCatalog.get(0).getAsJsonObject();
        String catalogName = catalogJson.get("name").getAsString();

        if (catalogArray.size() == 1 && catalogName.equalsIgnoreCase(ApplicationProperties.getInstance().getNullValue())) {
            handleSchema(catalogJson);
        } else if (catalogArray.size() >= 1/*catalog is there, schema null mysql*/) {
            JsonArray schemas = catalogJson.getAsJsonArray("schemas");
            if (schemas.size() == 0) {
                handleCatalogOnly();
            } else {
                handleCatalogAndSchema(schemas, catalogName);
            }
        }
    }

   
    private void handleSchema(JsonObject catalogJson) {
        JsonArray schemas = catalogJson.getAsJsonArray("schemas");
         int size=schemas.size();
        if (size==0)
        	handleEmptyCatSchema();
        else
        {	
        	Iterator<JsonElement> it=schemas.iterator();
        	it.forEachRemaining(t->{
        		handleEachSchema((JsonObject)t, size);
        	});
        }    
    }

    private void handleEmptyCatSchema() {
        JsonObject formData = new JsonObject();
        formData.addProperty("id", this.data.get("id").getAsString());
        formData.addProperty("type", this.data.get("type").getAsString());
        addDir(formData);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchTables", true);
        JsonObject fetchDataJson = new JsonObject();
        fetchDataJson.add("schemas", new JsonArray());
        JsonArray fetchDataJsonArray = new JsonArray();
        fetchDataJsonArray.add(fetchDataJson);
        parameters.add("fetchData", fetchDataJsonArray);
        formData.add("parameters", parameters);
        fetchColumns(0, formData, parameters, null);
    }

    private void addDir(JsonObject formData) {
        if (data.has("dir")) {
            formData.addProperty("dir", data.get("dir").getAsString());
        }
    }

    private void handleEachSchema(JsonObject iThSchema, int size) {
        JsonObject formData = new JsonObject();
        formData.addProperty("id", this.data.get("id").getAsString());
        formData.addProperty("type", this.data.get("type").getAsString());
        addDir(formData);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchTables", true);

        JsonArray fetchDataArray = new JsonArray();
        JsonObject fetchSchema = new JsonObject();

        JsonArray schemaArray = new JsonArray();
        if (size > 0) {
            JsonObject item = new JsonObject();
            item.addProperty("name", iThSchema.get("name").getAsString());
            schemaArray.add(item);
        }
        fetchSchema.add("schemas", schemaArray);
        fetchDataArray.add(fetchSchema);
        parameters.add("fetchData", fetchDataArray);
        formData.add("parameters", parameters);
        fetchColumns(size, formData, parameters, iThSchema);
    }

    private void fetchColumns(int size, JsonObject formData, JsonObject parameters, JsonObject iterateItem) {
        String workerClass = serviceManager.pickTheWorkerClass("adhoc", "metadata", "metadataWorkflow");
        formData.addProperty("requestId", requestId);
        JsonObject resultTableJson = (JsonObject) serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formData, workerClass);

        if (!checkStatus(resultTableJson)) {
            return;
        }

        String status = resultTableJson.get("status").getAsString();
        JsonArray tableArray = resultTableJson.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("catalogs").get(0).getAsJsonObject().getAsJsonArray("schemas").get(0).getAsJsonObject().getAsJsonArray("tables");
        if (tableArray != null && tableArray.isEmpty()) {
            return;
        }
        formData.remove("serviceDetails");

        JsonObject formDataColumn = new JsonObject();
        formDataColumn.addProperty("id", this.data.get("id").getAsString());
        formDataColumn.addProperty("type", this.data.get("type").getAsString());
        addDir(formDataColumn);

        JsonObject parametersColumn = new JsonObject();
        parametersColumn.addProperty("fetchColumns", true);

        JsonArray fetchDataColumnArray = new JsonArray();
        JsonObject fetchSchemaColumn = new JsonObject();

        JsonArray schemaArrayColumn = new JsonArray();
        JsonObject itemColumn = new JsonObject();
        if (size > 0) {
            itemColumn.addProperty("name", iterateItem.get("name").getAsString());
        }
        itemColumn.add("tables", stripId(tableArray));
        schemaArrayColumn.add(itemColumn);
        fetchSchemaColumn.add("schemas", schemaArrayColumn);
        fetchDataColumnArray.add(fetchSchemaColumn);
        parametersColumn.add("fetchData", fetchDataColumnArray);
        formDataColumn.add("parameters", parametersColumn);
        formDataColumn.addProperty("requestId", requestId);
        //String columnResults = serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formDataColumn, workerClass);
        //cacheJoins(parameters, workerClass, formDataColumn);
    }

    private void handleCatalogOnly() {
        JsonObject formData = new JsonObject();
        formData.addProperty("id", this.data.get("id").getAsString());
        formData.addProperty("type", this.data.get("type").getAsString());
        addDir(formData);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchTables", true);

        JsonArray fetchDataArray = new JsonArray();

        JsonObject catalogJSON = new JsonObject();

        catalogJSON.addProperty("catalog", this.data.get("database").getAsString());
        catalogJSON.add("schemas", new JsonArray());
        fetchDataArray.add(catalogJSON);
        parameters.add("fetchData", fetchDataArray);
        formData.add("parameters", parameters);
        formData.addProperty("requestId", requestId);
        String workerClass = serviceManager.pickTheWorkerClass("adhoc", "metadata", "metadataWorkflow");
        JsonObject resultTableJson = (JsonObject) serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formData, workerClass);

        if (!checkStatus(resultTableJson)) {
            return;
        }

        JsonArray tableArray = resultTableJson.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("catalogs").get(0).getAsJsonObject().getAsJsonArray("schemas").get(0).getAsJsonObject().getAsJsonArray("tables");
        if (tableArray != null && tableArray.isEmpty()) {
            return;
        }
        JsonObject formDataColumn = new JsonObject();
        formDataColumn.addProperty("id", this.data.get("id").getAsString());
        formDataColumn.addProperty("type", this.data.get("type").getAsString());
        addDir(formDataColumn);

        JsonObject parametersColumn = new JsonObject();
        parametersColumn.addProperty("fetchColumns", true);

        JsonArray fetchDataArrayColumn = new JsonArray();

        JsonObject catalogJSONColumn = new JsonObject();

        catalogJSONColumn.addProperty("catalog", this.data.get("database").getAsString());
        JsonArray schemaArray = new JsonArray();
        JsonObject table = new JsonObject();
        table.add("tables", stripId(tableArray));
        schemaArray.add(table);
        catalogJSONColumn.add("schemas", schemaArray);
        fetchDataArrayColumn.add(catalogJSONColumn);
        parametersColumn.add("fetchData", fetchDataArrayColumn);
        formDataColumn.add("parameters", parametersColumn);
        formDataColumn.addProperty("requestId", requestId);
        //String columnResults = serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formDataColumn, workerClass);
        //cacheJoins(parameters, workerClass, formDataColumn);
    }

    private void handleCatalogAndSchema(JsonArray schemas, String catName) {
        String catalogName = null;
        for (int i = 0; i < schemas.size(); i++) {


            JsonObject formData = new JsonObject();
            formData.addProperty("id", this.data.get("id").getAsString());
            formData.addProperty("type", this.data.get("type").getAsString());
            addDir(formData);

            JsonObject parameters = new JsonObject();
            parameters.addProperty("fetchTables", true);

            JsonArray fetchDataArray = new JsonArray();

            JsonObject catalogJSON = new JsonObject();
            String databaseName;
            databaseName = GsonUtility.optString(this.data,"database");
            if (catName == null && databaseName != null && !databaseName.isEmpty()) {
                catalogName = databaseName;
                //catalogJSON.put("catalog", databaseName);
            } else if (catName != null && !catName.isEmpty()) {
                catalogName = catName;
            }

            catalogJSON.addProperty("catalog", catalogName);
            JsonArray schemArray = new JsonArray();
            JsonObject item = schemas.get(i).getAsJsonObject();
            schemArray.add(item);
            catalogJSON.add("schemas", schemArray);
            fetchDataArray.add(catalogJSON);
            parameters.add("fetchData", fetchDataArray);
            formData.add("parameters", parameters);
            String workerClass = serviceManager.pickTheWorkerClass("adhoc", "metadata", "metadataWorkflow");
            formData.addProperty("requestId", requestId);
            JsonObject resultTableJson = (JsonObject) serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formData, workerClass);

            if (!checkStatus(resultTableJson)) {
                return;
            }

            JsonArray tableArray = resultTableJson.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("catalogs").get(0).getAsJsonObject().getAsJsonArray("schemas").get(0).getAsJsonObject().getAsJsonArray("tables");
            if (tableArray != null && tableArray.isEmpty()) {
                continue;
            }
            JsonObject formDataColumn = new JsonObject();
            formDataColumn.addProperty("id", this.data.get("id").getAsString());
            formDataColumn.addProperty("type", this.data.get("type").getAsString());
            addDir(formDataColumn);

            JsonObject parametersColumn = new JsonObject();
            parametersColumn.addProperty("fetchColumns", true);

            JsonArray fetchDataArrayColumn = new JsonArray();

            JsonObject catalogJSONColumn = new JsonObject();

            catalogJSONColumn.addProperty("catalog", catalogName);
            JsonArray schemaArray = new JsonArray();
            JsonObject table = new JsonObject();
            table.add("tables", stripId(tableArray));
            table.addProperty("name", item.get("name").getAsString());
            schemaArray.add(table);
            catalogJSONColumn.add("schemas", schemaArray);
            fetchDataArrayColumn.add(catalogJSONColumn);
            parametersColumn.add("fetchData", fetchDataArrayColumn);
            formDataColumn.add("parameters", parametersColumn);
            formDataColumn.addProperty("requestId", requestId);
            //String columnResults = serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formDataColumn, workerClass);
            //cacheJoins(parameters, workerClass, formDataColumn);
        }
    }

    private JsonArray stripId(JsonArray tables) {
        JsonArray array = new JsonArray();
        for (int index = 0; index < tables.size(); index++) {
            array.add(tables.get(index).getAsJsonObject().get("name").getAsString());
        }
        return array;

    }

    private void cacheJoins(JsonObject parameters, String workerClass, JsonObject formDataColumn) {
        JsonObject parametere = formDataColumn.getAsJsonObject("parameters");
        parametere.addProperty("fetchJoins", true);
        parameters.remove("fetchColumns");
        formDataColumn.addProperty("requestId", requestId);
        JsonObject joinsResult = (JsonObject) serviceManager.getResult("adhoc", "metadata", "retrieveJoins", formDataColumn, workerClass);
    }

    public JsonObject cacheCatalogAndSchema() {
        this.requestId = this.data.get("requestId").getAsString();
        this.data.remove("name");
        this.data.remove("driver");
        String database = null;
        if (this.data.has("database")) {
            database = this.data.get("database").getAsString();
            this.data.remove("database");
        }
        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchCatalogs", true);
        parameters.addProperty("fetchSchemas", true);
        parameters.addProperty("view", "tree");
        this.data.add("parameters", parameters);
        String workerClass = serviceManager.pickTheWorkerClass("adhoc", "metadata", "metadataWorkflow");
        
        Runnable serviceTask = () -> serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", this.data, workerClass);
        
        Future<?> future =  executorService.submit(serviceTask, requestId);
        try {
        	JsonObject result = (JsonObject) future.get();
        	this.data.addProperty("database", database);
        	return result;
        }
        catch (Exception e) {
        	throw new EfwServiceException(e);
		}

    }

}

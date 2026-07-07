package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.parallelprocessor.api.GenericWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Handles metadata retrieval operations in a parallel processing environment.
 * 
 * @author Somen
 * Created  on 1/24/2019.
 */
@Component
@Scope("prototype")
public class MetadataRetrievalWorker extends GenericWorker {
    private static Logger logger = LoggerFactory.getLogger(MetadataRetrievalWorker.class);
    private String name;
    private Integer priority;
    private JsonObject resultData;

    /**
     * using gson
     */
    @Override
    public void setFormData(JsonObject formData) {
        this.formData = formData;
    }
    @Override
    public void setResultData(JsonObject resultData) {
        this.resultData = resultData;
    }

    @Override
    public void runProcess() {
        super.genericRun();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String getWorkerName() {
        if (this.name == null) return "MetadataRetrievalWorker";
        return this.name;
    }

    @Override
    public void setWorkerName(String name) {
        this.name = name;
    }

    @Override
    public void map() {

    }
    /**
     * Reduces the result data and performs additional processing if necessary.
     * @return The reduced result data.
     */
    @Override
    public JsonObject reduce() {
        JsonObject parameters = formData.getAsJsonObject("parameters");
        if (parameters.has("fetchColumns") && !parameters.has("fetchJoins")) {
            JsonObject newFormData = prepareFormDataForFetchColumn();
            JsonObject serviceDetails = new JsonObject();
            serviceDetails.addProperty("type", "adhoc");
            serviceDetails.addProperty("serviceClass", "com.helicalinsight.adhoc.services.MetadataWorkflowService");
            serviceDetails.addProperty("serviceType", "metadata");
            serviceDetails.addProperty("service", "saveWorkflow");
            newFormData.add("serviceDetails", serviceDetails);
            newFormData.addProperty("position",formData.get("position").getAsString());
            newFormData.addProperty("maxSize",formData.get("maxSize").getAsString());
            newFormData.addProperty("resultSize",this.formData.get("maxSize").getAsString());
            newFormData.addProperty("pageNumber",this.formData.get("position").getAsString());
             if(this.formData.has("dir")){
                 newFormData.addProperty("dir",this.formData.get("dir").getAsString());
             }
            this.setFormData(newFormData);
            super.genericRun();
        }
        resultData.addProperty("cacheType","datasource");

        return resultData;
    }

    private JsonObject prepareFormDataForFetchColumn() {
        JsonObject preparedJsonFormData = new JsonObject();
        preparedJsonFormData.addProperty("id", formData.get("id").getAsString());
        if(formData.has("dir")){
            preparedJsonFormData.addProperty("dir", formData.get("dir").getAsString());
        }
        preparedJsonFormData.addProperty("type", formData.get("type").getAsString());
        preparedJsonFormData.addProperty("fetchColumnsOnly", true);
        JsonObject response = resultData.getAsJsonObject("response");
        if (response.has("metadata")) {
            JsonObject metadata = response.getAsJsonObject("metadata");
            JsonObject catalog = metadata.getAsJsonArray("catalogs").get(0).getAsJsonObject();

            String catalogName = catalog.get("name").getAsString();
            catalog.remove("name");
            if (catalogName.equalsIgnoreCase(ApplicationProperties.getInstance().getNullValue())) {
                catalogName = null;
            }
            JsonObject schema = catalog.getAsJsonArray("schemas").get(0).getAsJsonObject();
            String schemaName = schema.get("name").getAsString();
            if (schemaName.equalsIgnoreCase(ApplicationProperties.getInstance().getNullValue())) {
                schema.remove("name");
            }
            catalog.addProperty("catalog", catalogName);
            preparedJsonFormData.add("metadata", metadata);
        }
        return preparedJsonFormData;
    }
    /**
     * Aborts the execution of the worker.
     */
    @Override
    public void abort() {
        Thread currentThread = Thread.currentThread();
        if (currentThread.isAlive()) {
            logger.error("Interrupting the worker " + currentThread.getName());
            currentThread.interrupt();
        }
    }
	
}

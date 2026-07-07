package com.helicalinsight.efw.context;

import java.util.Map;

import com.google.gson.JsonObject;

public class ServiceRequestContext {

    final JsonObject formJson;
    final String workerClass;
    final String uniqueKey;
    final String requestId;

    public ServiceRequestContext(JsonObject formJson, String workerClass, String uniqueKey, String requestId) {
        this.formJson = formJson;
        this.workerClass = workerClass;
        this.uniqueKey = uniqueKey;
        this.requestId = requestId;
    }

    public void cleanup(Map<String, JsonObject> gsonStore) {
        if (uniqueKey != null) {
            gsonStore.remove(uniqueKey);
        }
    }
    
    public final String  getRequestId() {
    	return requestId;
    }
    
    public final String getWorkerClass() {
    	return workerClass;
    }
    
    public final String getUniqueKey() {
    	return uniqueKey;
    }
    
    public final JsonObject getFormJson() {
    	return formJson;
    }
}
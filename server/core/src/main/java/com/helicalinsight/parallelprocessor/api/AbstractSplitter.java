package com.helicalinsight.parallelprocessor.api;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.SplitterUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Somen
 *         Created on 1/24/2019.
 */
public abstract class AbstractSplitter implements ISplitter {
    protected JsonObject newActualFormData;
    private Map<String, String> uuidCacheMap = new HashMap<>();
    protected  String connectionName;

   
    /**
     * using gson
     * setFormData(JsonObject actualFormData)
     */
    public void setFormData(JsonObject newActualFormData) {
        this.newActualFormData = newActualFormData;
    }
       /**
     * using gson
     * newPrepareUniqueId(String type, String serviceType, String service)
     */
    public String newPrepareUniqueId(String type, String serviceType, String service) {
        Boolean hasSkip = this.newActualFormData.has("skipNext");
        this.newActualFormData.remove("skipNext");
        this.newActualFormData.remove("htmlId");
        Boolean hasMaxSize = this.newActualFormData.has("maxSize");
        Boolean hasPosition = this.newActualFormData.has("position");
        Boolean hasServiceDetails = this.newActualFormData.has("serviceDetails");
        Boolean hasRequestId = this.newActualFormData.has("requestId");
        String serviceDetails = null;
        String requestId=null;
        Integer maxSize = null;
        Integer position = null;
        if (hasPosition) position = this.newActualFormData.get("position").getAsInt();
        if (hasMaxSize) maxSize = this.newActualFormData.get("maxSize").getAsInt();
        if (hasServiceDetails) serviceDetails = GsonUtility.optString(this.newActualFormData, "serviceDetails");
        if (hasRequestId) requestId = GsonUtility.optString(this.newActualFormData,"requestId");
        this.newActualFormData.remove("maxSize");
        this.newActualFormData.remove("position");
        this.newActualFormData.remove("serviceDetails");
        this.newActualFormData.remove("requestId");

        String formData = type + serviceType + service + this.newActualFormData.toString();
        String actualId = SplitterUtils.prepareServiceId(formData);

        if (hasSkip) this.newActualFormData.addProperty("skipNext", true);
        this.newActualFormData.addProperty("maxSize", maxSize);
        this.newActualFormData.addProperty("position", position);
        this.newActualFormData.addProperty("serviceDetails", serviceDetails);
        this.newActualFormData.addProperty("requestId", requestId);
        return actualId;
    }

}
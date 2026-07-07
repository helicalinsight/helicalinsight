package com.helicalinsight.parallelprocessor.api;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * @author Somen
 * Created on 1/24/2019.
 */
public interface ISplitter {
	
	/**
	 * using gson
	 * setFormData(JsonObject actualFormData)
	 * @param actualFormData
	 */
    public void setFormData(JsonObject actualFormData);
    
    /**
     * using gson
     * newPrepareFormDataList()
     * @return
     */
    public List<JsonObject> newPrepareFormDataList();
    
    /**
     * using gson
     * @param type
     * @param serviceType
     * @param service
     * @return
     */
    public String newPrepareUniqueId(String type,String serviceType,String service);
    public Object getCacheObject();
}

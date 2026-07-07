package com.helicalinsight.efw.resourcecache;

import net.sf.json.JSONObject;

import java.util.Map;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.resourceprocessor.IProcessor;

@SuppressWarnings("unused")
public interface IResourceManager {

    int CACHE_MAX_SIZE = 1000;

    //Properties is in place if it may be used to increase or decrease the CACHE_MAX_SIZE
    Properties properties = new Properties();

    /**
     * newGetResource
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link IResourceManager#newGetResource(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    @Deprecated
    JSONObject getResource(String resource, boolean flag);
    /**
     * newGetResource using gson
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    JsonObject newGetResource(String resource, boolean flag);
    void updateResource(String resource, boolean flag, JSONObject obj);

    boolean deleteResource(String key);

    boolean deleteAll();

    @SuppressWarnings("rawtypes")
    Map dump();

    void compressResource();

    Object deCompressResource();

    Integer getSize();
}

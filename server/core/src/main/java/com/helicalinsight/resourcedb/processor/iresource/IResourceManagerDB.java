package com.helicalinsight.resourcedb.processor.iresource;

import net.sf.json.JSONObject;

import java.util.Map;
import java.util.Properties;

@SuppressWarnings("unused")
public interface IResourceManagerDB {

    int CACHE_MAX_SIZE = 1000;

    //Properties is in place if it may be used to increase or decrease the CACHE_MAX_SIZE
    Properties properties = new Properties();

    Map<String,Object> getContent(String resource);

    void updateResource(String resource, boolean flag, JSONObject json);

    boolean deleteResource(String key);

    boolean deleteAll();

    @SuppressWarnings("rawtypes")
    Map dump();

    void compressResource();

    Object deCompressResource();

    Integer getSize();
}

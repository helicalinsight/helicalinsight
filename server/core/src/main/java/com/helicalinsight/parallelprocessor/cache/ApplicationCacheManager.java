package com.helicalinsight.parallelprocessor.cache;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.cache.impl.ApplicationDatabaseCache;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Somen
 *         Created by helical021 on 1/21/2019.
 */
@Component
public class ApplicationCacheManager {
    @Autowired
    @Qualifier(value = "applicationDatabaseCache")
    private ICache implementedCache;

    /**
     * readFromCache
     * @deprecated
     * This method is no longer acceptable 
	 * <p> Use {@link ApplicationCacheManager#newReadFromCache(String key)} instead.</p>
     * @param key
     * @return
     */
    public List<JSONObject> readFromCache(String key) {

        Object cache = implementedCache.getCache(key);
        return (List<JSONObject>) cache;
    }
    
    public List<JsonObject> newReadFromCache(String key) {

        Object cache = implementedCache.getCache(key);
        List<JsonObject> object = (List<JsonObject>) cache;
        return object;
    }

    public boolean deleteFromCache(String key) {

        return implementedCache.deleteCache(key);
    }


    public boolean deleteAllCache() {
        return implementedCache.deleteAllCache();
    }

    public Map<Object, List<JsonObject>> getAllCache() {
        return implementedCache.getAllCache();
    }

    public Object getStatus() {
        return implementedCache.getStatus();
    }


    public void addToCache(JsonObject cachingItem, String key) {
        JsonObject sendJSON = new JsonObject();
        sendJSON.add("cachingItem", cachingItem);
        sendJSON.addProperty("key", key);
        implementedCache.putACache(sendJSON);
    }


    public void putAllToCache(Map<String, List<JsonObject>> listOfMap) {
        List sendJSONObjList = new ArrayList();
        listOfMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            List<JsonObject> values = entry.getValue();
            values.forEach(eachJson -> {
                JsonObject sendJson = new JsonObject();
                sendJson.add("cachingItem", eachJson);
                sendJson.addProperty("key", key);
                sendJSONObjList.add(sendJson);
            });
        });

        implementedCache.putAllCache(sendJSONObjList);
    }


    public ICache getImplementedCache() {
        return implementedCache;
    }

    public void searchInCache(String id) {

        implementedCache.searchInCache(id);
    }

    public Object getRawCache(Object key) {
        //   List<JSONObject> rawCache = (List<JSONObject>) implementedMemoryCache.getRawCache(key);
        List<JsonObject> rawCache = (List<JsonObject>) implementedCache.getRawCache(key);
        return rawCache;
    }

    public void putRawCache(Object key, Object object) {
        implementedCache.putARawCache(key, object);
    }

    public String getServiceId(Object key) {
        if (key instanceof DataSourceMapping) {
            DataSourceMapping dataSourceKey = (DataSourceMapping) key;
            DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
            return databaseCacheService.findKeyDataSourceMapping(dataSourceKey);
        }
        return "";
    }
}

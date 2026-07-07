package com.helicalinsight.parallelprocessor.cache.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.service.ApplicationCacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.SplitterUtils;
import com.helicalinsight.parallelprocessor.cache.ICache;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.*;

/**
 * @author Somen
 *         Created by helical021 on 1/25/2019.
 */

@Component("applicationDatabaseCache")
public class ApplicationDatabaseCache implements ICache {

    @Autowired
    private ApplicationCacheService applicationCacheService;

    @Override
    public Object getCache(String id) {
        List<ApplicationCache> applicationCache = applicationCacheService.findApplicationCache(id);
        if (applicationCache == null || applicationCache.isEmpty()) {
            return null;
        }
        List<JsonObject> jsonArray = new ArrayList<>();
        applicationCache.forEach(item -> {
            Object deSerialize = ApplicationUtilities.unCompressObject(item);
            jsonArray.add(new Gson().fromJson(deSerialize.toString(),JsonObject.class));
        });
        return jsonArray;
    }

    @Override
    public Map<Object, List<JsonObject>> getAllCache() {
        List<ApplicationCache> applicationCache = applicationCacheService.readApplicationCache();
        if (applicationCache == null || applicationCache.isEmpty()) {
            return null;
        }
        Map<Object, List<JsonObject>> resultMap = new HashMap<>();
        applicationCache.forEach(item -> {

            String key = item.getKey();
            List<JsonObject> jsonObjects = resultMap.get(key);
            if (jsonObjects == null) {
                jsonObjects = new ArrayList<>();
            }

            Object deSerialize = ApplicationUtilities.unCompressObject(item);
            if (deSerialize instanceof List) {
                jsonObjects.addAll((List<JsonObject>) deSerialize);
            } else {
                jsonObjects.add(new Gson().fromJson(deSerialize.toString(),JsonObject.class));
            }
            resultMap.put(key, jsonObjects);
        });
        return resultMap;
    }

    @Override
    public void putAllCache(List<Object> objects) {

    }

    @Override
    public synchronized void putACache(Object object) {
        JsonObject objectJson = (JsonObject) object;
        JsonObject cachingItem = objectJson.getAsJsonObject("cachingItem");
        String key = objectJson.get("key").getAsString();
        Integer position = GsonUtility.optInt(cachingItem,"position");
        String cacheType = GsonUtility.optString(cachingItem,"cacheType");
        cachingItem.remove("cacheType");

        Boolean isPresent = applicationCacheService.findByKeyAndPage(key, position);
        if (!isPresent) {
            ApplicationCache applicationCache = new ApplicationCache();
            applicationCache.setKey(key);
            applicationCache.setPage(position);

            String status = cachingItem.get("status").getAsString();
            applicationCache.setResultStatus(status);
            ApplicationUtilities.compressObject(cachingItem, applicationCache);
            applicationCache.setCreateDateTime(new Date());
            applicationCache.setType(cacheType);
            applicationCacheService.addApplicationCache(applicationCache);
        }
        /*else{
            logger.error("The cache is already present in the database");
        }*/

    }

    @Override
    public boolean updateCache(String id, List<JsonObject> listOfJson) {
        return false;
    }

    @Override
    public boolean searchInCache(Object requestedElement) {
        return false;
    }

    @Override
    public boolean deleteCache(String id) {
        return applicationCacheService.deleteByKey(id);
    }

    @Override
    public boolean deleteAllCache() {
        return applicationCacheService.deleteAllCache();
    }

    @Override
    public Integer getPriority() {
        return null;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public JsonObject getStatus() {
        return null;
    }

    @Override
    public Object getRawCache(Object key) {
        List<ApplicationCache> cacheByKey = applicationCacheService.findCacheByKey(key.toString());
        List<JsonObject> resultArray = new ArrayList<>();
        if (cacheByKey != null) {
            cacheByKey.forEach(i -> {
                Object unknownObject = ApplicationUtilities.unCompressObject(i);
                if (unknownObject instanceof List) {
                    resultArray.addAll((List<JsonObject>) unknownObject);
                } else {
                    resultArray.add(new Gson().fromJson(unknownObject.toString(), JsonObject.class) );
                }
            });
        }
        return resultArray;
    }

    @Override
    public void putARawCache(Object key, Object object) {
        ApplicationCache cache = new ApplicationCache();
        cache.setKey(SplitterUtils.prepareServiceId(key.toString()));
        ApplicationUtilities.compressObject(object, cache);
        cache.setCreateDateTime(new Date());
        cache.setPage(0);
        cache.setResultStatus("1");
        applicationCacheService.addApplicationCache(cache);
    }


}

package com.helicalinsight.parallelprocessor.cache.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.cache.ICache;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Somen
 *         Created by helical021 on 1/25/2019.
 */
@Component("memory")
public class ApplicationMemoryCache implements ICache {


    private CacheManager cacheManager = CacheManager.getInstance();


    private Cache implementedCache;

    @PostConstruct
    public void init() {
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        String applicationCacheName = settingsJson.get("applicationCacheName").getAsString();
        implementedCache = cacheManager.getCache(applicationCacheName);
    }

    @PreDestroy
    public void destroy() {
        cacheManager.shutdown();
    }


    @Override
    public JsonObject getStatus() {
    	JsonObject status = new JsonObject();
        status.addProperty("cacheName", implementedCache.getName());
        status.addProperty("cacheSize", implementedCache.getSize());
        status.addProperty("cacheAlgorithm", implementedCache.getMemoryStoreEvictionPolicy().getName());
        status.addProperty("diskStoreSize", implementedCache.getDiskStoreSize());
        JsonArray fromJson = new Gson().fromJson(new Gson().toJson(cacheManager.getCacheNames()),JsonArray.class);
        status.add("cacheName", fromJson);
        JsonObject statisticsJSON = new JsonObject();
        //String[] cacheNames = cacheManager.getCacheNames();
        //JSONObject settingsJson = JsonUtils.getSettingsJson();
        Cache cache = implementedCache;
        StatisticsGateway statistics = cache.getStatistics();
        long cacheEvictedCount = statistics.cacheEvictedCount();
        long cacheExpiredCount = statistics.cacheExpiredCount();
        long cacheHitCount = statistics.cacheHitCount();
        //double cacheHitRatio = statistics.cacheHitRatio();
        long cacheMissCount = statistics.cacheMissCount();
        long cacheMissExpiredCount = statistics.cacheMissExpiredCount();
        long cacheMissNotFoundCount = statistics.cacheMissNotFoundCount();
        long cachePutAddedCount = statistics.cachePutAddedCount();
        long cachePutCount = statistics.cachePutCount();
        long cachePutUpdatedCount = statistics.cachePutUpdatedCount();
        long cacheRemoveCount = statistics.cacheRemoveCount();
        long getLocalDiskSize = statistics.getLocalDiskSize();
        long getLocalDiskSizeInBytes = statistics.getLocalDiskSizeInBytes();
        long getLocalHeapSize = statistics.getLocalHeapSize();
        long getLocalHeapSizeInBytes = statistics.getLocalHeapSizeInBytes();
        long getLocalOffHeapSize = statistics.getLocalOffHeapSize();
        long getLocalOffHeapSizeInBytes = statistics.getLocalOffHeapSizeInBytes();
        long getRemoteSize = statistics.getRemoteSize();
        long getSize = statistics.getSize();
        long getWriterQueueLength = statistics.getWriterQueueLength();
        long localDiskHitCount = statistics.localDiskHitCount();
        long localDiskMissCount = statistics.localDiskMissCount();
        long localDiskPutAddedCount = statistics.localDiskPutAddedCount();
        long localDiskPutCount = statistics.localDiskPutCount();
        long localDiskPutUpdatedCount = statistics.localDiskPutUpdatedCount();
        long localDiskRemoveCount = statistics.localDiskRemoveCount();
        long localHeapHitCount = statistics.localHeapHitCount();
        long localHeapMissCount = statistics.localHeapMissCount();
        long localHeapPutAddedCount = statistics.localHeapPutAddedCount();
        long localHeapPutCount = statistics.localHeapPutCount();
        long localHeapPutUpdatedCount = statistics.localHeapPutUpdatedCount();
        long localHeapRemoveCount = statistics.localHeapRemoveCount();
        long localOffHeapHitCount = statistics.localOffHeapHitCount();
        long localOffHeapMissCount = statistics.localOffHeapMissCount();
        long localOffHeapPutAddedCount = statistics.localOffHeapPutAddedCount();
        long localOffHeapPutCount = statistics.localOffHeapPutCount();
        long localOffHeapPutUpdatedCount = statistics.localOffHeapPutUpdatedCount();
        long localOffHeapRemoveCount = statistics.localOffHeapRemoveCount();
        long xaCommitCommittedCount = statistics.xaCommitCommittedCount();
        long xaCommitCount = statistics.xaCommitCount();
        long xaCommitExceptionCount = statistics.xaCommitExceptionCount();
        long xaCommitReadOnlyCount = statistics.xaCommitReadOnlyCount();
        long xaRecoveryCount = statistics.xaRecoveryCount();
        long xaRecoveryNothingCount = statistics.xaRecoveryNothingCount();
        long xaRecoveryRecoveredCount = statistics.xaRecoveryRecoveredCount();
        long xaRollbackCount = statistics.xaRollbackCount();
        long xaRollbackExceptionCount = statistics.xaRollbackExceptionCount();
        long xaRollbackSuccessCount = statistics.xaRollbackSuccessCount();
        Status cacheStatus = cache.getStatus();
        String statusCache = cacheStatus.toString();

        statisticsJSON.add("cacheNames", fromJson);
        statisticsJSON.addProperty("cacheEvictedCount", cacheEvictedCount);
        statisticsJSON.addProperty("cacheExpiredCount", cacheExpiredCount);
        statisticsJSON.addProperty("cacheHitCount", cacheHitCount);
        //statisticsJSON.put("cacheHitRatio", cacheHitRatio);
        statisticsJSON.addProperty("cacheMissCount", cacheMissCount);
        statisticsJSON.addProperty("cacheMissExpiredCount", cacheMissExpiredCount);
        statisticsJSON.addProperty("cacheMissNotFoundCount", cacheMissNotFoundCount);
        statisticsJSON.addProperty("cachePutAddedCount", cachePutAddedCount);
        statisticsJSON.addProperty("cachePutCount", cachePutCount);

        statisticsJSON.addProperty("cachePutUpdatedCount", cachePutUpdatedCount);
        statisticsJSON.addProperty("cacheRemoveCount", cacheRemoveCount);
        statisticsJSON.addProperty("getLocalDiskSize", getLocalDiskSize);
        statisticsJSON.addProperty("getLocalDiskSizeInBytes", getLocalDiskSizeInBytes);
        statisticsJSON.addProperty("getLocalHeapSize", getLocalHeapSize);
        statisticsJSON.addProperty("getLocalHeapSizeInBytes", getLocalHeapSizeInBytes);
        statisticsJSON.addProperty("getLocalOffHeapSize", getLocalOffHeapSize);
        statisticsJSON.addProperty("getLocalOffHeapSizeInBytes", getLocalOffHeapSizeInBytes);
        statisticsJSON.addProperty("getRemoteSize", getRemoteSize);
        statisticsJSON.addProperty("getSize", getSize);
        statisticsJSON.addProperty("getWriterQueueLength", getWriterQueueLength);

        statisticsJSON.addProperty("localDiskHitCount", localDiskHitCount);
        statisticsJSON.addProperty("localDiskMissCount", localDiskMissCount);
        statisticsJSON.addProperty("localDiskPutAddedCount", localDiskPutAddedCount);
        statisticsJSON.addProperty("localDiskPutCount", localDiskPutCount);
        statisticsJSON.addProperty("localDiskRemoveCount", localDiskRemoveCount);
        statisticsJSON.addProperty("localHeapHitCount", localHeapHitCount);
        statisticsJSON.addProperty("localHeapMissCount", localHeapMissCount);
        statisticsJSON.addProperty("localHeapPutAddedCount", localHeapPutAddedCount);
        statisticsJSON.addProperty("localDiskPutUpdatedCount", localDiskPutUpdatedCount);
        statisticsJSON.addProperty("localHeapPutCount", localHeapPutCount);
        statisticsJSON.addProperty("localHeapPutUpdatedCount", localHeapPutUpdatedCount);

        statisticsJSON.addProperty("localHeapRemoveCount", localHeapRemoveCount);
        statisticsJSON.addProperty("localOffHeapHitCount", localOffHeapHitCount);
        statisticsJSON.addProperty("localOffHeapMissCount", localOffHeapMissCount);
        statisticsJSON.addProperty("localOffHeapPutAddedCount", localOffHeapPutAddedCount);
        statisticsJSON.addProperty("localOffHeapPutCount", localOffHeapPutCount);
        statisticsJSON.addProperty("localOffHeapPutUpdatedCount", localOffHeapPutUpdatedCount);
        statisticsJSON.addProperty("localOffHeapRemoveCount", localOffHeapRemoveCount);
        statisticsJSON.addProperty("xaCommitCommittedCount", xaCommitCommittedCount);
        statisticsJSON.addProperty("xaCommitCount", xaCommitCount);
        statisticsJSON.addProperty("xaCommitExceptionCount", xaCommitExceptionCount);
        statisticsJSON.addProperty("xaCommitReadOnlyCount", xaCommitReadOnlyCount);

        statisticsJSON.addProperty("xaRecoveryCount", xaRecoveryCount);
        statisticsJSON.addProperty("xaRecoveryCount", xaRecoveryCount);
        statisticsJSON.addProperty("xaRecoveryNothingCount", xaRecoveryNothingCount);
        statisticsJSON.addProperty("xaRecoveryRecoveredCount", xaRecoveryRecoveredCount);
        statisticsJSON.addProperty("xaRollbackCount", xaRollbackCount);
        statisticsJSON.addProperty("xaRollbackExceptionCount", xaRollbackExceptionCount);

        statisticsJSON.addProperty("xaRollbackSuccessCount", xaRollbackSuccessCount);
        statisticsJSON.addProperty("statusCache", statusCache);
        
        //status.putAll(statisticsJSON);
        
        for (String key : statisticsJSON.keySet()) {
        	status.add(key, statisticsJSON.get(key));
		}
        
        return status;
    }


    @Override
    public Object getCache(String id) {
        Element element = implementedCache.get(id);
        if (element != null) {
            return (List<JsonObject>) element.getObjectValue();
        }
        return null;
    }

    public Object getRawCache(Object key) {
        Element element = implementedCache.get(key);
        if (element != null) {
            Object objectValue = element.getObjectValue();
            if (objectValue instanceof List)
                return (List<JsonObject>) objectValue;
            else
                return (String) objectValue;
        }
        return null;
    }

    @Override
    public Map<Object, List<JsonObject>> getAllCache() {
        Map<Object, Element> cacheAll = implementedCache.getAll(implementedCache.getKeys());
        Map<Object, List<JsonObject>> allResultCache = new HashMap<>();
       /* Iterator<Map.Entry<Object, Element>> iterator = cacheAll.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Element> entry = iterator.next();
            Object eachKey = entry.getKey();
            Element eachValue = entry.getValue();
            List<JSONObject> objectValue = (List<JSONObject>) eachValue.getObjectValue();
            allResultCache.put(eachKey, objectValue);
        }*/
        cacheAll.entrySet().forEach(entry -> {
            Object eachKey = entry.getKey();
            Element eachValue = entry.getValue();
            List<JsonObject> objectValue = (List<JsonObject>) eachValue.getObjectValue();
            allResultCache.put(eachKey, objectValue);
        });
        return allResultCache;
    }

    @Override
    public void putAllCache(List<Object> objects) {
        objects.forEach(eachObject -> putACache(eachObject));
    }

    @Override
    public void putACache(Object object) {
        JsonObject objectJson = (JsonObject) object;
        JsonObject cachingItem = objectJson.getAsJsonObject("cachingItem");
        String key = objectJson.get("key").getAsString();
        Element element = implementedCache.get(key);
        List<JsonObject> jsonObjects;
        //int position = cachingItem.getInt("position");
        if (element == null) {
            int maxSize = cachingItem.get("maxSize").getAsInt();
            jsonObjects =  new ArrayList<>();
            jsonObjects.add( cachingItem);
          /*  for (int i = 1; i < maxSize; i++) {
                JSONObject nullJson =  new JSONObject();
                nullJson.put("i",i);
                jsonObjects.add(i, nullJson);
            }
*/            cachingItem.remove("maxSize");
            implementedCache.put(new Element(key, jsonObjects));

        } else {
            jsonObjects = (List<JsonObject>) element.getObjectValue();
            jsonObjects.add(cachingItem);
            implementedCache.put(new Element(key, jsonObjects));
        }
        cachingItem.remove("position");
        cachingItem.remove("maxSize");

    }

    @Override
    public void putARawCache(Object key, Object object) {
        Element element = implementedCache.get(key);
        List<JsonObject> objectJson = null;
        String stringResult = null;
        if (object instanceof java.util.List) {
            objectJson = (List<JsonObject>) object;
        } else {
            stringResult = (String) object;
        }

        if (element == null) {
            implementedCache.put(new Element(key, objectJson != null ? objectJson : stringResult));
        } else {
            List<JsonObject> jsonObjects = null;
            String result = null;
            Object objectValue = element.getObjectValue();
            if (objectValue instanceof java.util.List) {
                jsonObjects = (List<JsonObject>) objectValue;
            } else {
                result = (String) objectValue;
            }
            implementedCache.put(new Element(key, jsonObjects != null ? jsonObjects : result));
        }

    }


    @Override
    public boolean updateCache(String id, List<JsonObject> listOfJson) {
        implementedCache.put(new Element(id, listOfJson));
        Element element = implementedCache.get(id);
        if (element != null) {
            List<JsonObject> jsonObjects = (List<JsonObject>) element.getObjectValue();
            return jsonObjects.equals(listOfJson);
        }
        return false;
    }

    @Override
    public boolean deleteCache(String id) {
       /* Element element = implementedCache.get(id);
        if (element != null) {
            return implementedCache.removeElement(element);
        }*/
        return implementedCache.remove(id);
    }

    @Override
    public boolean deleteAllCache() {
        implementedCache.removeAll();
        return (implementedCache.getSize() == 0);
    }

    public boolean searchInCache(Object requestedElement) {
        //for (Object key : implementedCache.getKeys()) {
        Element element = implementedCache.get(requestedElement.toString());
        if (element != null) {
            List<JsonObject> objectValue = (List<JsonObject>) element.getObjectValue();
            Stream<JsonObject> filter = objectValue.stream().filter(jsonObj -> jsonObj.has("status")).filter(w -> w.get("status").getAsString().equals("1"));
           return true;
        }
        //}
       /* Attribute<Object> status = implementedCache.getSearchAttribute("status");
        Attribute<Object> message = implementedCache.getSearchAttribute("message");
        Attribute<List<JSONObject>> data = implementedCache.getSearchAttribute("data");
        Query query = implementedCache.createQuery().includeValues();

        query.end();
        Results results = implementedCache.createQuery().includeValues()
                .addCriteria(status.eq("0")).execute();
        List<Result> all = results.all();
*/
        return false;
    }


    @Override
    public Integer getPriority() {

        return null;
    }

    @Override
    public void setPriority(int priority) {

    }

    public Long getMaxEntriesLocalHeap() {
        CacheConfiguration config = implementedCache.getCacheConfiguration();
        return config.getMaxEntriesLocalHeap();
    }
}

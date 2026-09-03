package com.helicalinsight.efw.components;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.parallelprocessor.cache.ApplicationCacheManager;
import com.helicalinsight.parallelprocessor.cache.ICache;
import com.helicalinsight.parallelprocessor.cache.impl.HibernateCache;

/**
 * @author Rajesh kumar
 *         Created by helical019 on 2/5/2019.
 */
public class ApplicationCacheStatisticsProvider implements IComponent {


    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String action = formData.get("action").getAsString();
        
        ApplicationCacheManager applicationCacheManager = ApplicationContextAccessor.getBean(ApplicationCacheManager.class);
        
        //TODO : Remove these 2 line once HibernateCache is deprecated.
        ICache implementedCache = ApplicationContextAccessor.getBean(HibernateCache.class);
        applicationCacheManager.setImplementedCache(implementedCache);
        
        JsonObject response = new JsonObject();
        if (action.equalsIgnoreCase("stats")) {
        	
        	String region = GsonUtility.optStringValue(formData, "region", "");
        	if(StringUtils.isNotBlank(region)) {
        		response.add("stats", new Gson().toJsonTree(applicationCacheManager.newReadFromCache(region)).getAsJsonArray() );
        	}
        	else {
        		response.add("stats", new Gson().toJsonTree(applicationCacheManager.getStatus()).getAsJsonObject() );
        	}
        } else if (action.equalsIgnoreCase("cacheResult")) {
            Map<Object, List<JsonObject>> allCache = applicationCacheManager.getAllCache();
          //here HashMap is converted to JsonObject 
            response.add("cacheResults", new Gson().toJsonTree(allCache).getAsJsonObject());  
        } else if (action.equalsIgnoreCase("clearCache")) {
            applicationCacheManager.deleteAllCache();
            response.addProperty("response", "Successfully cleared all the cache.");
        } else if (action.equalsIgnoreCase("deleteCacheById")) {
            String cacheId = formData.get("cacheId").getAsString();
            boolean deleteStatus = applicationCacheManager.deleteFromCache(cacheId);
            response.addProperty("response", "Delete status for given cacheId :" + deleteStatus);
        } else if (action.equalsIgnoreCase("readCacheById")) {
            String cacheId = formData.get("cacheId").getAsString();
            List<JsonObject> jsonObjects = applicationCacheManager.newReadFromCache(cacheId);
            if (GsonUtility.optBoolean(formData,"size")) {
                response.addProperty("cacheResultsSize", jsonObjects.size());
            } else {
                response.add("cacheResults", new Gson().toJsonTree(jsonObjects).getAsJsonArray());//here list is converted jsonArray
            }
        } 
        
        /**
        else if (action.equalsIgnoreCase("fileBrowserCache")) {
            response.addProperty("allFileBrowserCache", getAllFileBrowserCache().toString());
        } else if (action.equalsIgnoreCase("rawCache")) {
            Cache cache = CacheManager.getInstance().getCache("com.helicalinsight.admin.model.FileBrowserCache");
            List keys = cache.getKeys();
            Map<Object, Element> cacheAll = cache.getAll(keys);
            Map<String, String> allResultCache = new HashMap<>();

            cacheAll.entrySet().forEach(entry -> {
                Object key = entry.getKey();
                Element eachValue = entry.getValue();
                String objectValue = eachValue.getObjectValue().toString();
                allResultCache.put(key.toString(), objectValue);
            });

            JsonObject result = JsonParser.parseString(allResultCache.toString()).getAsJsonObject();
            response.addProperty("hibernateCache", result.toString());
        } else {
            response.addProperty("stats", applicationCacheManager.getStatus().toString());
        }
        **/
        return response.toString();
    }

    /**
    private Map<Object, Element> getAllFileBrowserCache() {
        Cache fileBrowserCache = CacheManager.getInstance().getCache("com.helicalinsight.admin.model.FileBrowserCache");
        Map<Object, Element> cacheAll = fileBrowserCache.getAll(fileBrowserCache.getKeys());
        Map<Object, Object> allResultCache = new HashMap<>();
        cacheAll.entrySet().forEach(entry -> {
            Object eachKey = entry.getKey();
            Element eachValue = entry.getValue();
            Object objectValue = eachValue.getObjectValue();
            allResultCache.put(eachKey, objectValue);
        });
        return cacheAll;
    }
    **/

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

package com.helicalinsight.parallelprocessor.cache.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.parallelprocessor.cache.ICache;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Somen
 *         Created by helical021 on 1/25/2019.
 */
@Component
public class ApplicationDistributedCache implements ICache {
    @Override
    public Object getCache(String id) {
        return null;
    }

    @Override
    public Map<Object, List<JsonObject>> getAllCache() {
        return null;
    }

    @Override
    public void putAllCache(List<Object> objects) {
    }

    @Override
    public void putACache(Object object) {
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
        return false;
    }

    @Override
    public boolean deleteAllCache() {
        return false;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public Integer getPriority() {
        return null;
    }

    @Override
    public JsonObject getStatus() {
        return null;
    }

    @Override
    public Object getRawCache(Object key) {
        return null;
    }

    @Override
    public void putARawCache(Object key, Object object) {

    }
}

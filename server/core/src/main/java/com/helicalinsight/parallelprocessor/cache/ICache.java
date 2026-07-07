package com.helicalinsight.parallelprocessor.cache;

import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * @author Somen
 *         Created by helical021 on 1/25/2019.
 */
public interface ICache {

    public Object getCache(String id);

    public Map<Object, List<JsonObject>> getAllCache();

    public void putAllCache(List<Object> objects);

    public void putACache(Object object);

    public boolean updateCache(String id, List<JsonObject> listOfJson);

    public boolean searchInCache(Object requestedElement);

    public boolean deleteCache(String id);

    public boolean deleteAllCache();

    public void setPriority(int priority);

    public Integer getPriority();

    public JsonObject getStatus();

    public Object getRawCache(Object key);

    public void putARawCache(Object key, Object object);

}

package com.helicalinsight.efw.resourcecache;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.resourceprocessor.JSONProcessor;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("unused")
public enum ResourceManager implements IResourceManager {
    RESOURCE_MANAGER;
	/**
     * newJsonStore
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link ResourceManager#newJsonStore = Map<String, JsonObject> newJsonStore} instead.</p>
     *
     * @param String 
     * @param JsonObject  
     */
	
	// FIXME : There must be single source of truth for cache
    @Deprecated
    private final Map<String, JSONObject> jsonStore = new ConcurrentHashMap<String, JSONObject>(CACHE_MAX_SIZE, 0.9f,
            1) {
        private static final long serialVersionUID = 1L;

        public boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > CACHE_MAX_SIZE;
        }
    };
    /**
     * newJsonStore using gson
     */
    private final Map<String, JsonObject> newJsonStore = new ConcurrentHashMap<String, JsonObject>(CACHE_MAX_SIZE, 0.9f,
            1) {
        private static final long serialVersionUID = 1L;

        public boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > CACHE_MAX_SIZE;
        }
    };
    
    private final Map<String, Long> lastChanged = new ConcurrentHashMap<String, Long>(CACHE_MAX_SIZE, 0.9f, 1) {
        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > CACHE_MAX_SIZE;
        }
    };
    
    private final Map<String, Long> newLastChanged = new ConcurrentHashMap<String, Long>(CACHE_MAX_SIZE, 0.9f, 1) {
        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > CACHE_MAX_SIZE;
        }
    };

    public static IResourceManager getInstance() {
        return RESOURCE_MANAGER;
    }
    /**
     * newGetResource
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link ResourceManager#newGetResource(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    @Deprecated
    @Override
    public JSONObject getResource(@NotNull String resource, boolean flag) {
        //retrieve jsonobj from cache(map) if found then return.
        File resourceFile = new File(resource);
        String key = resource + ", " + flag;
        if (resourceFile.exists()) {
            long lastModifiedDate = resourceFile.lastModified();
            Long objectLastChanged = lastChanged.get(key);
            long storedLastModified = objectLastChanged == null ? 0 : objectLastChanged;
            boolean isNotModified = (storedLastModified == lastModifiedDate);
            if (isNotModified && jsonStore.containsKey(key)) {
                return JSONObject.fromObject(jsonStore.get(key));
            } else {
                JSONObject json = JSONProcessor.prepareJsonFromXML(resource, flag);
                this.updateResource(resource, flag, json);
                //Do not return the original reference. Make a copy
                return JSONObject.fromObject(json);
            }
        } else {
            this.deleteResource(key);
        }
        return null;
    }
    /**
     * newGetResource using gson
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    @Override
	public JsonObject newGetResource(String resource, boolean flag) {
    	// To maintain the updated data in both the stores.
    	File resourceFile = new File(resource);
        String key = resource + ", " + flag;
        if (resourceFile.exists()) {
            long lastModifiedDate = resourceFile.lastModified();
            Long objectLastChanged = newLastChanged.get(key);
            long storedLastModified = objectLastChanged == null ? 0 : objectLastChanged;
            boolean isNotModified = (storedLastModified == lastModifiedDate);
            if (isNotModified && newJsonStore.containsKey(key)) {
                return new Gson().fromJson(new Gson().toJson(newJsonStore.get(key)),JsonObject.class);
            } else {
                JsonObject json = JSONProcessor.newPrepareJsonFromXML(resource, flag);
                this.updateResource(resource, flag, json);
                //Do not return the original reference. Make a copy
                return json;
            }
        } else {
            this.deleteResource(key);
        }
        return null;
	}
    /**
     * updateResource
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link ResourceManager#updateResource(String resource, boolean flag, JsonObject json)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @param JsonObject json 
     */
    @Deprecated
    @Override
    public void updateResource(String resource, boolean flag, JSONObject json) {
        File resourceFile = new File(resource);
        String key = resource + ", " + flag;
        if (resourceFile.exists()) {
            final long value = resourceFile.lastModified();
            synchronized (this) {
                //Causing NullPointerException. Had put the if condition to avoid
                if (json != null) {
                    lastChanged.put(key, value);
                    jsonStore.put(key, json);
                }
            }
        }
    }
    /**
     * updateResource using gson
     * @param String resource
     * @param boolean flag
     * @param JsonObject json 
     */
    public void updateResource(String resource, boolean flag, JsonObject json) {
        File resourceFile = new File(resource);
        String key = resource + ", " + flag;
        if (resourceFile.exists()) {
            final long value = resourceFile.lastModified();
            synchronized (this) {
                //Causing NullPointerException. Had put the if condition to avoid
                if (json != null) {
                	newLastChanged.put(key, value);
                    newJsonStore.put(key, json);
                }
            }
        }
    }
    @Override
    public boolean deleteResource(String key) {
        if (jsonStore.containsKey(key)) {
            synchronized (this) {
                lastChanged.remove(key);
                newLastChanged.remove(key);
                jsonStore.remove(key);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        synchronized (this) {
            lastChanged.clear();
            newLastChanged.clear();
            jsonStore.clear();
        }
        return true;
    }

    public void compressResource() {
        //NOOP
    }

    @Override
    public Object deCompressResource() {
        return null;
    }

    @Override
    public Integer getSize() {
        return jsonStore.size();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map dump() {
        return jsonStore;
    }

	
}
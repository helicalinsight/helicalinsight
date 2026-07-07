package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.resourcedb.processor.iresource.IResourceManagerDB;
import net.sf.json.JSONObject;


import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("unused")
public enum ResourceManagerDB implements IResourceManagerDB {
    RESOURCE_MANAGER;

    private final Map<String, JSONObject> jsonStore = new ConcurrentHashMap<String, JSONObject>(CACHE_MAX_SIZE, 0.9f,
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

    public static IResourceManagerDB getInstance() {
        return RESOURCE_MANAGER;
    }

    private DBProcessor dbProcessor = new DBProcessor();


    @Override
    public Map<String, Object> getContent(String resource) {
        Map<String, Object> content = dbProcessor.getContent(resource);
        HIResource hiResource= (HIResource) content.get("efwFolder");
       // this.updateResource(resource,flag,content);
        return null;

    }

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
        this.updateResource(resource,flag,json);
    }


    @Override
    public boolean deleteResource(String key) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public Map dump() {
        return null;
    }

    @Override
    public void compressResource() {

    }

    @Override
    public Object deCompressResource() {
        return null;
    }

    @Override
    public Integer getSize() {
        return null;
    }
}

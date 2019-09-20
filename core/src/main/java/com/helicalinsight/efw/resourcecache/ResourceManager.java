/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.resourcecache;

import com.helicalinsight.efw.resourceprocessor.JSONProcessor;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("unused")
public enum ResourceManager implements IResourceManager {
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

    public static IResourceManager getInstance() {
        return RESOURCE_MANAGER;
    }

    @Override
    public JSONObject getResource(String resource, boolean flag) {
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

    @Override
    public boolean deleteResource(String key) {
        if (jsonStore.containsKey(key)) {
            synchronized (this) {
                lastChanged.remove(key);
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
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

package com.helicalinsight.efw.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by user on 1/25/2017.
 *
 * @author Rajasekhar
 */
public enum AvailablePluginClassesRepository {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(AvailablePluginClassesRepository.class);

    public static AvailablePluginClassesRepository getInstance() {
        return INSTANCE;
    }

    private final ConcurrentHashMap<String, Plugin> classesMap = new ConcurrentHashMap<>(16, 0.9f, 1);

    public Map<String, Plugin> getRepository() {
        return Collections.unmodifiableMap(this.classesMap);
    }

    public void update(String fileOrFolder, Plugin plugin) {
        if (fileOrFolder == null || plugin == null) {
            throw new IllegalArgumentException("Null values can't be updated in Repository");
        }

        synchronized (this.classesMap) {
            this.classesMap.put(fileOrFolder, plugin);
        }
        logger.debug("Successfully updated the plugin " + fileOrFolder);
    }

    public Plugin getPlugin(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path can't be null or empty");
        }
        Plugin plugin = this.classesMap.get(path);
        logger.debug("Retrieving result for look up of " + path);
        return plugin;
    }

    public Plugin deletePlugin(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path can't be null or empty");
        }
        Plugin plugin;
        synchronized (this.classesMap) {
            plugin = this.classesMap.remove(path);
        }
        logger.debug("Removing plugin " + path);
        return plugin;
    }
}
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
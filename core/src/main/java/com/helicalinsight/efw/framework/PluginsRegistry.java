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

import com.helicalinsight.datasource.managed.ConnectionProviderUtility;
import com.helicalinsight.datasource.managed.DataSourcePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by user on 12/23/2016.
 * <p/>
 * Consists of two ConcurrentHashMaps one representing all available classes of the plugins; and the other consisting
 * of the class loaders for those classes(entry points) which are used.
 *
 * @author Rajasekhar
 */
public enum PluginsRegistry {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(PluginsRegistry.class);

    private final ConcurrentHashMap<String, PluginEntry> enabledPlugins = new ConcurrentHashMap<>(16, 0.9f, 1);

    public static PluginsRegistry getInstance() {
        return INSTANCE;
    }

    public Map<String, PluginEntry> getEnabledPlugins() {
        return Collections.unmodifiableMap(this.enabledPlugins);
    }

    public ParentLastClassLoader getPluginClassLoader(String clazz) {
        PluginEntry pluginEntry = this.enabledPlugins.get(clazz);
        if (pluginEntry != null) {
            return pluginEntry.getParentLastClassLoader();
        } else {
            return null;
        }
    }

    public PluginEntry registerPlugin(String entryPointClass, PluginEntry pluginEntry) {
        if (entryPointClass == null || pluginEntry == null) {
            throw new IllegalArgumentException("Entry point class name, plugin entry should not be null");
        }
        //Step 1. Enable plugin
        //Step 2. Add the entry point class(s) to the plugin
        Plugin plugin = pluginEntry.getPlugin();
        plugin.setEnabled(true);
        plugin.getEntryPointClasses().add(entryPointClass);

        if (PluginUtils.isDriver(entryPointClass, pluginEntry.getParentLastClassLoader())) {
            plugin.setDriver(true);
        }
        return this.enabledPlugins.putIfAbsent(entryPointClass, pluginEntry);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ParentLastClassLoader deRegisterPlugin(String entryPointClass) {
        if (entryPointClass == null) {
            throw new IllegalArgumentException("Entry point class name should not be null");
        }
        PluginEntry pluginEntry = this.enabledPlugins.remove(entryPointClass);
        pluginEntry.getPlugin().setEnabled(false);
        return pluginEntry.getParentLastClassLoader();
    }

    public void deRegisterAndClosePlugin(String entryPointClass) {
        if (entryPointClass == null) {
            throw new IllegalArgumentException("Entry point class name should not be null");
        }

        PluginEntry pluginEntry = this.enabledPlugins.remove(entryPointClass);
        if (pluginEntry == null) {
            return;
        }
        Plugin plugin = pluginEntry.getPlugin();
        plugin.setEnabled(false);
        try (ParentLastClassLoader loader = pluginEntry.getParentLastClassLoader()) {
            //Close explicitly
            if (loader != null) {
                loader.close();
            }
        } catch (IOException ignore) {
            //Hope All is well :)
        } finally {
            //Clean references of the class if it is a driver
            if (plugin.isDriver()) {
                try {
                    boolean isDeRegistered = ConnectionProviderUtility.deRegisterDriver(entryPointClass);
                    if (isDeRegistered) {
                        logger.info("Plugin " + entryPointClass + " is successfully de-registered from DriverManager");
                    }
                    DataSourcePool.getInstance().removeEntries(entryPointClass);
                } catch (SQLException ex) {
                    logger.info("Exception when cleaning the class references:", ex);
                }
            }
        }
    }

    public PluginEntry getPluginEntry(String pluginName) {
        for (Map.Entry<String, PluginEntry> entry : this.enabledPlugins.entrySet()) {
            PluginEntry pluginEntry = entry.getValue();
            String name = pluginEntry.getPlugin().getName();
            if (name.equals(pluginName)) {
                return pluginEntry;
            }
        }
        return null;
    }
}
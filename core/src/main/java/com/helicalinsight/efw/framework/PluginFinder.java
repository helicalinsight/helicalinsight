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

import com.helicalinsight.efw.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by user on 1/12/2017.
 *
 * @author Rajasekhar
 */
public class PluginFinder {

    static final AvailablePluginClassesRepository REPOSITORY = AvailablePluginClassesRepository.getInstance();

    public ParentLastClassLoader getPluginClassLoader(String clazz) {
        Plugin plugin = getPlugin(clazz);
        List<URL> list;
        try {
            list = getUrls(plugin.getName());
            if (list.isEmpty()) {
                throw new IllegalStateException("Plugin does not consist of any resources");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

        //Registers this parentLastClassLoader as the class loader for all the specified URLs
        ParentLastClassLoader parentLastClassLoader = new ParentLastClassLoader(list);
        final PluginsRegistry registry = PluginsRegistry.getInstance();
        PluginEntry pluginEntry = new PluginEntry(plugin, parentLastClassLoader);
        PluginEntry oldEntry = registry.registerPlugin(clazz, pluginEntry);

        //Remove the traces of this old entry
        if (oldEntry != null) {
            ParentLastClassLoader oldClassLoader = oldEntry.getParentLastClassLoader();
            //Ideally no class loader should have been registered and oldClassLoader should always be null in the above
            if (oldClassLoader != null) {
                try {
                    oldClassLoader.close();
                } catch (IOException ignore) {
                }
            }
            Plugin oldEntryPlugin = oldEntry.getPlugin();
            oldEntryPlugin.setEnabled(false);
        }
        return parentLastClassLoader;
    }

    private Plugin getPlugin(String name) {
        Plugin plugin = null;
        Map<String, Plugin> repository = REPOSITORY.getRepository();

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        //If repository is not loaded at boot time
        //Fixed the issue in case if the boot time reading is disabled. Then the plugin will always be null.
        if (!applicationProperties.isReadPluginsBootTime()) {
            //If not loaded even for once
            if (repository.size() != 0) {
                RepositoryLoader loader = new RepositoryLoader();
                loader.load();
            }
        }

        for (Map.Entry<String, Plugin> entry : repository.entrySet()) {
            plugin = entry.getValue();
            Set<String> setOfClasses = plugin.getClasses();
            if (setOfClasses.contains(name)) {
                break;
            }
        }
        if (plugin == null) {
            throw new RuntimeException("Plugins have no such class " + name);
        }
        return plugin;
    }

    private List<URL> getUrls(String pluginName) throws MalformedURLException {
        List<URL> urlList = new ArrayList<>();
        File file = new File(pluginName);
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            if (files != null) {
                for (File resource : files) {
                    if (!resource.isDirectory()) {
                        urlList.add(resource.toURI().toURL());
                    }
                }
            }
        } else {
            urlList.add(file.toURI().toURL());
        }
        return urlList;
    }
}
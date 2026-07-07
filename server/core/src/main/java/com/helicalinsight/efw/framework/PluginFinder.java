package com.helicalinsight.efw.framework;
import com.helicalinsight.datasource.managed.PlainJdbcConnectionProvider;
import com.helicalinsight.efw.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by user on 1/12/2017.
 *
 * @author Rajasekhar
 */
public class PluginFinder {

    static final AvailablePluginClassesRepository REPOSITORY = AvailablePluginClassesRepository.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(PluginFinder.class);
    private static int flag=0;
    
    public ParentLastClassLoader getPluginClassLoader(String clazz) {
        Plugin plugin = getPlugin(clazz);
    	logger.debug("Loaded Plugin: Reference Data "+plugin);
        List<URL> list;
        try {
            list = getUrls(plugin.getName());
            if (list.isEmpty()) {
                throw new IllegalStateException("Plugin does not consist of any resources");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

        // Registers this parentLastClassLoader as the class loader for all the
        // specified URLs
        ParentLastClassLoader parentLastClassLoader = new ParentLastClassLoader(list);
        final PluginsRegistry registry = PluginsRegistry.getInstance();
        PluginEntry pluginEntry = new PluginEntry(plugin, parentLastClassLoader);
    	logger.debug("Calling Register Plugin: Reference Data "+pluginEntry);        
        PluginEntry oldEntry=null;
        if(flag==1)
        	oldEntry= registry.registerPlugin(clazz, pluginEntry);
        // registry.
        // Remove the traces of this old entry
        if (oldEntry != null) {
            ParentLastClassLoader oldClassLoader = oldEntry.getParentLastClassLoader();
            // Ideally no class loader should have been registered and
            // oldClassLoader should always be null in the above
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
        // If repository is not loaded at boot time
        // Fixed the issue in case if the boot time reading is disabled. Then
        // the plugin will always be null.
        if (!applicationProperties.isReadPluginsBootTime()) {
            loadRepository(repository);
        }

        for (Map.Entry<String, Plugin> entry : repository.entrySet()) {
            plugin = entry.getValue();
            Set<String> setOfClasses = plugin.getClasses();
            if (setOfClasses.contains(name)) {
            	flag=1;
                break;
            }
        }
        if (plugin == null) {
            throw new RuntimeException("Plugins have no such class / No Suitable Driver Found " + name);
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

    public void loadRepository() {
        Map<String, Plugin> repository = REPOSITORY.getRepository();
        loadRepository(repository);
    }

    public void loadRepository(Map<String, Plugin> repository) {
        if (repository.size() != 0) {
            RepositoryLoader loader = new RepositoryLoader();
            loader.load();
        }
    }
}
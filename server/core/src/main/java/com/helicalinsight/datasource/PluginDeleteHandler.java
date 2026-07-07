package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.AvailablePluginClassesRepository;
import com.helicalinsight.efw.framework.Plugin;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * PluginDeleteHandler implements {@link IComponent}
 * This component is used to delete the jar file or folder with jar
 * dependencies also from driver/plugins.
 * @author Rajesh        
 */
public class PluginDeleteHandler implements IComponent {
    private final Logger logger = LoggerFactory.getLogger(PluginDeleteHandler.class);
    static final AvailablePluginClassesRepository REPOSITORY = AvailablePluginClassesRepository.getInstance();
    /**
     * executeComponent(String jsonFormData)
     * this method is used to delete the plugin file
     * @param jsonFormData       provides  path to delete the plugin
     * @return a message successfully deleted in jsonObject form.
     * @throws EfwServiceException  if jar files are not present in path.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        boolean flag = true;
        JsonObject result;
        result = new JsonObject();
        JsonObject formJson = new Gson().fromJson(jsonFormData,JsonObject.class);
        String pluginJar = null;
        if (formJson.has("pluginJar") && !("".equals(GsonUtility.optString(formJson, "pluginJar")))) {
            pluginJar = GsonUtility.optString(formJson, "pluginJar");
        } else {
            throw new EfwServiceException("Please select a valid Jar");
        }
        String actualPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + pluginJar;
        logger.info("The actual path to delete plugin is " + actualPath);
        if (actualPath == null) {
            throw new EfwServiceException("Selected jar is not present in driver/plugins");
        }

        if ((CustomWatcherUtils.deleteFiles(actualPath))) {
            File file = new File(actualPath);
            if (file.isDirectory()) {
                File[] folder = file.listFiles();
                for (File aFile : folder) {
                    CustomWatcherUtils.closeEntryPointClass(aFile);
                }
            } else {
                CustomWatcherUtils.closeEntryPointClass(file);
            }
            flag = CustomWatcherUtils.deleteFiles(actualPath);
        } else {
            flag=false;
            logger.debug("deleted the jar successfully");
        }
        logger.debug(flag == true ? "Cannot delete the plugin" : "Successfully deleted the plugin");
        if(flag){
            throw new EfwServiceException("Cannot delete the plugin");
        }
        result.addProperty("message", "Successfully deleted the plugin");
        return result.toString();

    }
    /**
     * getPlugin(String name)
     * @param name        name of the plugin class
     * @return {@code true} if class name found , otherwise {@code false}.
     */
    public boolean getPlugin(String name) {
        Plugin plugin = null;
        boolean contains = false;
        Map<String, Plugin> repository = REPOSITORY.getRepository();

        for (Map.Entry<String, Plugin> entry : repository.entrySet()) {
            plugin = entry.getValue();

            Set<String> setOfClasses = plugin.getClasses();
            if (setOfClasses.contains(name)) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

}

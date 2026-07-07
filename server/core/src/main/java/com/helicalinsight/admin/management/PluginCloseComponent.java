package com.helicalinsight.admin.management;

import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.PluginsRegistry;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

/**
 * Created by user on 1/20/2017.
 *
 * @author Rajasekhar
 */
public class PluginCloseComponent implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String pluginClass = null;
        if (formJson.has("pluginClass")) {
            pluginClass = formJson.getString("pluginClass");
        }

        if (pluginClass != null) {
            PluginsRegistry registry = PluginsRegistry.getInstance();
            registry.deRegisterAndClosePlugin(pluginClass);
        } else {
            throw new RequiredParameterIsNullException("Parameter pluginClass is null");
        }

        JSONObject result;
        result = new JSONObject();
        result.accumulate("message", "Successfully closed the plugin");
        return result.toString();
    }
}
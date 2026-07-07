package com.helicalinsight.admin.management;

import java.util.Map;

import com.helicalinsight.efw.framework.Plugin;
import com.helicalinsight.efw.framework.PluginEntry;
import com.helicalinsight.efw.framework.PluginsRegistry;
import com.helicalinsight.efw.serviceframework.IComponent;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by user on 2/6/2017.
 *
 * @author Rajasekhar
 */
public class LoadedClassesJsonProvider implements IComponent {

	@Override
	public String executeComponent(String jsonFormData) {
		JSONObject formData = JSONObject.fromObject(jsonFormData);

		String provideCore = "false";
		String providePlugins = "false";

		if (formData.has("provideCore")) {
			provideCore = formData.getString("provideCore");
		}

		if (formData.has("providePlugins")) {
			providePlugins = formData.getString("providePlugins");
		}
		JSONObject result;
		result = new JSONObject();

		if ("true".equalsIgnoreCase(provideCore)) {

			result.accumulate("coreClasses", DriverListLoaderUtility.getCoreClasses());
		}

		if ("true".equalsIgnoreCase(providePlugins)) {
			PluginsRegistry registry = PluginsRegistry.getInstance();
			Map<String, PluginEntry> enabledPlugins = registry.getEnabledPlugins();

			JSONArray pluginClasses;
			pluginClasses = new JSONArray();

			for (Map.Entry<String, PluginEntry> entry : enabledPlugins.entrySet()) {
				JSONObject json = new JSONObject();
				PluginEntry pluginEntry = entry.getValue();

				Plugin plugin = pluginEntry.getPlugin();
				json.accumulate("id", plugin.getUuid());
				json.accumulate("name", plugin.getName());
				json.accumulate("classes", DriverListLoaderUtility.getPluginClassesForEntry(entry));
				json.accumulate("status", "Enabled");
				pluginClasses.add(json);
			}

			result.accumulate("pluginClasses", pluginClasses);
		}
		return result.toString();
	}

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
}
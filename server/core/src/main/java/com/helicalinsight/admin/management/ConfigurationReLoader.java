package com.helicalinsight.admin.management;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.serviceframework.ServiceManager;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.utility.SendPoolMail;
import com.helicalinsight.parallelprocessor.WorkerManager;
import net.sf.json.JSONObject;

/**
 * Created by author on 01-09-2015.
 *
 * @author Rajasekhar
 */
public class ConfigurationReLoader implements IComponent {

    private final ApplicationProperties properties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        JSONObject model;
        model = new JSONObject();

        if (this.properties != null && formData.has("refresh") && "true".equalsIgnoreCase(formData.getString
                ("refresh"))) {
            synchronized (this.properties) {
            	
            	ApplicationSettings settings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
            	settings.init();
            	
                SettingsLoader settingsLoader = new SettingsLoader(this.properties);
                settingsLoader.loadApplicationSettings();
                URLContextManager urlContextManager = ApplicationContextAccessor.getBean(URLContextManager.class);
                ServiceManager serviceManager= ApplicationContextAccessor.getBean(ServiceManager.class);
                serviceManager.clearAll();
                WorkerManager workerManager = ApplicationContextAccessor.getBean(WorkerManager.class);
                workerManager.clearAll();
                urlContextManager.refreshUrlContext();
                SendPoolMail sendPoolMail =ApplicationContextAccessor.getBean(SendPoolMail.class);
                sendPoolMail.getDurationStat().clear();
            }
            model.put("message", "Application settings are reloaded");
        } else {
            model.put("message", "Couldn't reload application settings.");
        }
        return model.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

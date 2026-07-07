package com.helicalinsight.admin.management;

import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerContext;

import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

public class LogLevelModifier implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String logLevel = formJson.optString("setLevel");
        String info = formJson.optString("getLevel");
        JSONObject response = new JSONObject();

        if (info != null && !info.isEmpty()) {
            if ("options".equalsIgnoreCase(info)) {
                response.put("data", Arrays.asList("ALL,DEBUG,INFO,WARN,ERROR,FATAL,OFF,TRACE".split(",")));
            } else if ("currentLevel".equalsIgnoreCase(info)) {
                response.put("currentLevel", LogManager.getRootLogger().getLevel().toString());
            } else {
                throw new FormValidationException("The formData level is invalid");
            }
        } else if (logLevel == null || logLevel.isEmpty()) {
            throw new FormValidationException("The formData does not have logLevel information");
        } else {
            logLevel = logLevel.toLowerCase();
            Level level = Level.toLevel(logLevel);
            response.put("message", "Log level is set to " + level + ". Restart the server to see changes");
            response.put("currentLevel", logLevel);

            updateFile(logLevel);
        }
        return response.toString();
    }

    private void updateFile(String level) {
        try {
            URL resource = this.getClass().getClassLoader().getResource("log4j2.properties");
            PropertiesConfiguration properties = new PropertiesConfiguration(resource);
            properties.setProperty("rootLogger.level", level);

            properties.save();
        } catch (ConfigurationException ignore) {
        }
    }
}

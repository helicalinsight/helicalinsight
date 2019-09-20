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

package com.helicalinsight.admin.management;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * This class is responsible for loading all the settings of the application and
 * set the singleton class ApplicationProperties.
 *
 * @author Rajasekhar
 */
public class SettingsLoader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SettingsLoader.class);
    /**
     * Member of type ApplicationProperties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Sets the ApplicationProperties instance to the member variable
     */
    public SettingsLoader(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void run() {
        loadApplicationSettings();
    }

    /**
     * Loads the application settings by reading the project.properties file and
     * sets the singleton class members
     */
    public void loadApplicationSettings() {
        String settingPath;
        String type;
        String nullValue;

        if (logger.isInfoEnabled()) {
            logger.info("Initializing the application. Loading project.properties from classpath to set application"
                    + " properties");
        }
        Map<String, String> properties = ConfigurationFileReader.mapFromClasspathPropertiesFile("project.properties");

        // Load the project.properties file and set the variables
        settingPath = properties.get("settingPath");
        type = properties.get("type");
        nullValue = properties.get("nullValues");
        // set the setting path of the application.

        File file = new File(settingPath);
        String systemFolder = StringUtils.removeEndIgnoreCase(file.getAbsolutePath(),
                File.separator + "Admin" + File.separator + "setting.xml");

        String actualSolutionResourcesDirectory = null;
        JSONObject settingsJson = settingsJson(settingPath);
        if (settingsJson.has("efwSolution")) {
            actualSolutionResourcesDirectory = new File(settingsJson.getString("efwSolution")).getAbsolutePath();
        }

        String solutionDirectory;

        if (actualSolutionResourcesDirectory != null) {
            solutionDirectory = actualSolutionResourcesDirectory;
        } else {
            solutionDirectory = StringUtils.removeEndIgnoreCase(systemFolder, File.separator + "System");
        }

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Found settingPath %s. Found solutionDirectory %s", settingPath,
                    solutionDirectory));
        }

        // Call the setters of the singleton class ApplicationProperties
        applicationProperties.setSolutionDirectory(solutionDirectory);
        // Set the plugin path. The path is fully qualified
        String pluginPath = properties.get("pluginPath");
        if (pluginPath == null) {
            logger.error("Failed to set the plugin path. Application requires this path to be set to load plugins " +
                    "during the boot time.");
        } else {
            applicationProperties.setPluginPath(pluginPath);
        }

        applicationProperties.setReadPluginsBootTime(Boolean.parseBoolean(settingsJson.getString
                ("readPluginsBootTime")));

        applicationProperties.setDomain(settingsJson.getString("BaseUrl"));
        applicationProperties.setRecursiveResourceLoad(settingsJson.getBoolean("recursiveLoading"));
        if (settingsJson.has("enableSavedResult")) {
            applicationProperties.setEnableSavedResult(settingsJson.getString("enableSavedResult"));
        } else {
            applicationProperties.setEnableSavedResult("false");
        }

        applicationProperties.setSettingPath(settingPath);
        applicationProperties.setSystemDirectory(systemFolder);
        applicationProperties.setDriverPath(getDriverPath(systemFolder));
        applicationProperties.setXsd(getXSDPath(systemFolder));
        applicationProperties.setType(type);
        applicationProperties.setNullValue(nullValue);
        applicationProperties.setCacheEnabled(properties.get("enableCache"));
        applicationProperties.setDefaultEmailResourceType(settingsJson.getString("defaultEmailResourceType"));
        int jdbcQueryCancellationTime;
        try {
            jdbcQueryCancellationTime = settingsJson.getInt("jdbcQueryCancellationTime");
        } catch (Exception ex) {
            throw new EfwException("JdbcQueryCancellationTime is not a number", ex);
        }
        applicationProperties.setJdbcQueryCancellationTime(jdbcQueryCancellationTime);
        applicationProperties.setProvideExportViaHtml("true".equalsIgnoreCase(settingsJson.getString
                ("provideExportViaHtml")));
    }

    /**
     * Returns the actual 'SolutionResourcesDirectory' where all the framework related files and
     * folder, html, css, js, image files are kept.
     *
     * @param settingPath The location from project.properties
     * @return A string representing a directory of resources
     */
    private JSONObject settingsJson(String settingPath) {
        return ResourceProcessorFactory.getIProcessor().getJSONObject(settingPath, false);
    }

    /**
     * The method returns the EFW/System/Drivers location
     *
     * @param systemFolder The EFW/System folder
     * @return Returns the Drivers directory location in the EFW directory
     */
    protected String getDriverPath(String systemFolder) {
        return systemFolder + File.separator + "Drivers";
    }

    /**
     * The method returns the EFW/System/XSDfile location
     *
     * @param systemFolder The EFW/System folder
     * @return Returns the XSDfile directory in the EFW directory
     */
    protected String getXSDPath(String systemFolder) {
        return systemFolder + File.separator + "XSDfile";
    }
}

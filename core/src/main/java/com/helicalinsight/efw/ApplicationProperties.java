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

package com.helicalinsight.efw;

import com.helicalinsight.admin.management.SettingsLoader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The singleton instance which holds the application settings. Only one
 * instance per application
 *
 * @author Rajasekhar
 * @author Muqthar Ahmed
 */
public enum ApplicationProperties {

    INSTANCE;

    /**
     * The setting.xml location
     */
    private String settingPath;
    /**
     * The xsd files location
     */
    private String xsd;
    /**
     * The EFW/System/Drivers directory location
     */
    private String driverPath;
    /**
     * The value of the type from project.properties
     */
    private String type;
    /**
     * The value of nullValues from project.properties
     */
    private String nullValue;
    /**
     * The System directory of EFW-Project
     */
    private String systemDirectory;

    private String pluginPath;

    private String cacheEnabled;

    private String domain;

    private String enableSavedResult;

    private String solutionDirectory;

    private boolean readPluginsBootTime;

    private boolean isRecursiveResourceLoad;

    private boolean provideExportViaHtml;

    private String defaultEmailResourceType;

    private int jdbcQueryCancellationTime;

    private ApplicationProperties() {
        Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);
        if (logger.isInfoEnabled()) {
            logger.info("Invoking settings loader in a separate thread and halting the current thread execution");
        }
        SettingsLoader settingsLoader = new SettingsLoader(this);
        Thread loader = new Thread(settingsLoader);
        loader.setName("Settings-Loader");
        loader.setPriority(Thread.MAX_PRIORITY);
        try {
            loader.start();
            loader.join();
            if (logger.isInfoEnabled()) {
                logger.info("Settings loader has completed its task. Resuming the thread.");
            }
        } catch (InterruptedException ex) {
            logger.error("Application settings loader is interrupted. Exception is", ex);
        }
        if (this.settingPath == null || !new File(this.settingPath).exists()) {
            throw new EfwServiceException("Couldn't start the application. Settings are not loaded. Please check " +
                    "whether file permissions are provided correctly.");
        }
    }

    /**
     * Creates a singleton instance. Only one instance per application
     *
     * @return Returns the properties object. Creates one if it is null.
     */
    public static ApplicationProperties getInstance() {
        return INSTANCE;
    }


    public boolean isRecursiveResourceLoad() {
        return isRecursiveResourceLoad;
    }

    public void setRecursiveResourceLoad(boolean isRecursiveResourceLoad) {
        this.isRecursiveResourceLoad = isRecursiveResourceLoad;
    }

    /**
     * Getter for systemDirectory
     *
     * @return Returns the System directory of EFW-Project
     */
    public String getSystemDirectory() {
        return systemDirectory;
    }

    /**
     * Setter for the systemDirectory
     *
     * @param systemDirectory The System directory of EFW-Project
     */
    public void setSystemDirectory(String systemDirectory) {
        this.systemDirectory = systemDirectory;
    }

    /**
     * Returns the setting.xml location
     *
     * @return Returns the setting.xml location
     */
    public String getSettingPath() {
        return settingPath;
    }

    /**
     * Sets the setting.xml location
     *
     * @param settingPath The setting.xml location
     */
    public void setSettingPath(String settingPath) {
        this.settingPath = settingPath;
    }

    /**
     * Returns the xsd files location
     *
     * @return Returns the xsd files location
     */
    public String getXsd() {
        return xsd;
    }

    /**
     * Sets the xsd property with the value of xsd files location
     *
     * @param xsd The xsd property
     */
    public void setXsd(String xsd) {
        this.xsd = xsd;
    }

    /**
     * Returns the EFW/System/Drivers directory location
     *
     * @return Returns the EFW/System/Drivers directory location
     */
    public String getDriverPath() {
        return driverPath;
    }

    /**
     * Sets the EFW/System/Drivers directory location
     *
     * @param driverPath EFW/System/Drivers directory location
     */
    public void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    /**
     * Returns the type value in project.properties
     *
     * @return The type value in project.properties
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type value from project.properties
     *
     * @param type The type value from project.properties
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the nullValues value from project.properties
     *
     * @return Returns the nullValues value from project.properties
     */
    public String getNullValue() {
        return nullValue;
    }

    /**
     * Sets the nullValues value from project.properties
     *
     * @param nullValue The nullValues value from project.properties
     */
    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    /**
     * Returns the EFW solution directory, where all the solutions are kept.
     * Path separator should be used with this value.
     * <p/>
     *
     * @return Returns the EFW solution directory
     */
    public String getSolutionDirectory() {
        return solutionDirectory;
    }

    public void setSolutionDirectory(String solutionDirectory) {
        this.solutionDirectory = solutionDirectory;
    }

    public String isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(String cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEnableSavedResult() {
        return enableSavedResult;
    }

    public void setEnableSavedResult(String enableSavedResult) {
        this.enableSavedResult = enableSavedResult;
    }

    public boolean isProvideExportViaHtml() {
        return provideExportViaHtml;
    }

    public void setProvideExportViaHtml(boolean provideExportViaHtml) {
        this.provideExportViaHtml = provideExportViaHtml;
    }

    public String getDefaultEmailResourceType() {
        return defaultEmailResourceType;
    }

    public void setDefaultEmailResourceType(String defaultEmailResourceType) {
        this.defaultEmailResourceType = defaultEmailResourceType;
    }

    public int getJdbcQueryCancellationTime() {
        return jdbcQueryCancellationTime;
    }

    public void setJdbcQueryCancellationTime(int jdbcQueryCancellationTime) {
        this.jdbcQueryCancellationTime = jdbcQueryCancellationTime;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public boolean isReadPluginsBootTime() {
        return readPluginsBootTime;
    }

    public void setReadPluginsBootTime(boolean readPluginsBootTime) {
        this.readPluginsBootTime = readPluginsBootTime;
    }
}
package com.helicalinsight.admin.management;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.ResultSetConfigDTO;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ConfigurationFileReader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for loading all the settings of the application and
 * set the singleton class ApplicationProperties.
 *
 * @author Rajasekhar
 * @since 1.1
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
        String allValues;

        if (logger.isInfoEnabled()) {
            logger.info("Initializing the application. Loading project.properties from classpath to set application"
                    + " properties");
        }
        Map<String, String> properties = ConfigurationFileReader.getProjectPropertiesFile();

        // Load the project.properties file and set the variables
        settingPath = properties.get("settingPath");
        type = properties.get("type");
        nullValue = properties.get("nullValues");
        allValues = properties.get("allValues");
        // set the setting path of the application.

        File file = new File(settingPath);
        String systemFolder = StringUtils.removeEndIgnoreCase(file.getAbsolutePath(),
                File.separator + "Admin" + File.separator + "setting.xml");

        String actualSolutionResourcesDirectory = null;
        JsonObject settingsJson = settingsJson(settingPath);
        if (settingsJson.has("efwSolution")) {
            actualSolutionResourcesDirectory = new File(GsonUtility.optString(settingsJson,"efwSolution")).getAbsolutePath();
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

        applicationProperties.setReadPluginsBootTime(Boolean.parseBoolean(GsonUtility.optString(settingsJson,"readPluginsBootTime")));

        applicationProperties.setDomain(GsonUtility.optString(settingsJson, "BaseUrl"));
        applicationProperties.setAdhocReportUrl(GsonUtility.optString(settingsJson, "adhocReportSchedulingPage"));
        applicationProperties.setRecursiveResourceLoad(GsonUtility.optBoolean(settingsJson,"recursiveLoading"));
        if (settingsJson.has("enableSavedResult")) {
            applicationProperties.setEnableSavedResult(GsonUtility.optString(settingsJson,"enableSavedResult"));
        } else {
            applicationProperties.setEnableSavedResult("false");
        }
        
        applicationProperties.setShowExperimentalFeatures(GsonUtility.optStringValue(settingsJson,"showExperimentalFeatures","false"));

        applicationProperties.setSettingPath(settingPath);
        applicationProperties.setSystemDirectory(systemFolder);
        applicationProperties.setDriverPath(getDriverPath(systemFolder));
        applicationProperties.setXsd(getXSDPath(systemFolder));
        applicationProperties.setType(type);
        applicationProperties.setEncryptionAlgorithm(properties.get("encryptionAlgorithm"));
        applicationProperties.setEncryptionSecret(properties.get("encryptionSecret"));
        applicationProperties.setNullValue(nullValue);
        applicationProperties.setAllValues(allValues);
        applicationProperties.setCacheEnabled(properties.get("enableCache"));
        applicationProperties.setDefaultEmailResourceType(GsonUtility.optString(settingsJson, "defaultEmailResourceType"));
        applicationProperties.setManifestVersion(properties.get("manifest_version"));
        applicationProperties.setResultSetConfig(configureResultSetConfig(settingsJson));
        applicationProperties.setProductWebSite(properties.get("product_website"));
        int jdbcQueryCancellationTime;
        try {
            jdbcQueryCancellationTime = GsonUtility.optInt(settingsJson, "jdbcQueryCancellationTime");
        } catch (Exception ex) {
            throw new EfwException("JdbcQueryCancellationTime is not a number", ex);
        }
        applicationProperties.setJdbcQueryCancellationTime(jdbcQueryCancellationTime);
        applicationProperties.setProvideExportViaHtml("true".equalsIgnoreCase(GsonUtility.optString(settingsJson,"provideExportViaHtml")));

        applicationProperties.setNlpEngineLanguage(GsonUtility.optString(settingsJson,"nlpEngineLanguage"));
        applicationProperties.setNlpProcessor(GsonUtility.optString(settingsJson,"nlpProcessor"));

    }

    /**
     * Returns the actual 'SolutionResourcesDirectory' where all the framework related files and
     * folder, html, css, js, image files are kept.
     *
     * @param settingPath The location from project.properties
     * @return A string representing a directory of resources
     */
    private JsonObject settingsJson(String settingPath) {
        return ResourceProcessorFactory.getIProcessor().getJsonObject(settingPath, false);
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
    
    private ResultSetConfigDTO configureResultSetConfig(JsonObject settingJson) {
    	
    	if  ( settingJson.has("resultSetConfig")) {
    		JsonObject resultSetConfig = GsonUtility.optJsonObject(settingJson, "resultSetConfig");
    		int batchSize =  GsonUtility.optInt(resultSetConfig, "batchSize");
    		int fetchSize = GsonUtility.optInt(resultSetConfig,"fetchSize");
    		boolean autoCommit = GsonUtility.optBoolean(resultSetConfig,"autoCommit");
    		
    		JsonObject dbConfigOverrides = GsonUtility.optJsonObject(resultSetConfig,"dbConfigOverrides");
    		
    		JsonArray dbs = GsonUtility.optJsonArray(dbConfigOverrides, "db");
    		
    		Map<String,ResultSetConfigDTO.ConfigOverrides> dbSpecificOverrides = new HashMap<>();
    		
    		for(JsonElement db : dbs ) {
    			JsonObject eachDb = (JsonObject) db;
    			int  dbSpecificFetchSize =  GsonUtility.optInt(eachDb, "fetchSize");
    			boolean dbSpecificAutoCommit = GsonUtility.optBoolean(eachDb, "autoCommit");
    			String key = GsonUtility.optString(eachDb, "name");
    			ResultSetConfigDTO.ConfigOverrides value = new ResultSetConfigDTO.ConfigOverrides(dbSpecificFetchSize, dbSpecificAutoCommit);
    			dbSpecificOverrides.put(key, value);
    		}
    		return new ResultSetConfigDTO(batchSize, fetchSize, dbSpecificOverrides, autoCommit);
    	}
    	return null;
    }
}

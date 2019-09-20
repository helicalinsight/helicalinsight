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

package com.helicalinsight.cache.manager;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.validator.ResourceValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Somen
 *         Created by Somen on 5/30/2015.
 */
@Component
@Scope("prototype")
public class EfwdCacheManager extends CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(EfwdCacheManager.class);
    private static ApplicationProperties applicationProperties;
    private static Map<String, String> settingsDataSourcesMap = new HashMap<>();
    private static JSONObject settingFileAsJson;
    private static String solutionDirectory;
    private JSONObject dataMapTagContent;
    private JSONObject requestParameterJson;
    private JSONObject connectionDetails;
    private JSONObject efwdFileAsJson;
    private Integer mapId;
    private IDriver driverObject;
    private String efwdFile;

    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      JsonObject fileContent) {
        response.setContentType(ControllerUtils.defaultContentType());
        response.setCharacterEncoding(ApplicationUtilities.getEncoding());
        PrintWriter out = null;
        logger.info("Serving cache file for executeDatasource......");
        try {
            out = response.getWriter();
            out.print(fileContent);
        } catch (IOException ioe) {
            logger.error("Exception occurred ", ioe);
        } finally {
            ApplicationUtilities.closeResource(out);
        }
        return true;
    }

    @PostConstruct
    public void init() {
        logger.debug("initializing content");
        applicationProperties = ApplicationProperties.getInstance();
        String settingPath = applicationProperties.getSettingPath();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        settingFileAsJson = processor.getJSONObject(settingPath, false);
        solutionDirectory = applicationProperties.getSolutionDirectory();
        this.setSettingsDataSourcesMap();
    }

    private void setSettingsDataSourcesMap() {
        JSONArray settingsDataSources = settingFileAsJson.getJSONArray("DataSources");
        for (int dataSource = 0; dataSource < settingsDataSources.size(); dataSource++) {
            JSONObject settingsDataSource = settingsDataSources.getJSONObject(dataSource);
            String dataSourceType = settingsDataSource.getString("@type");
            String clazz = settingsDataSource.getString("@class");
            settingsDataSourcesMap.put(dataSourceType, clazz);
        }

    }


    public void setRequestData(String data) {
        this.requestParameterJson = (JSONObject) JSONSerializer.toJSON(data);
    }

    public String getConnectionFilePath() {
        QueryExecutor dataSource = new QueryExecutor(this.requestParameterJson.toString(), applicationProperties);
        JSONObject efwdJson = this.requestParameterJson.optJSONObject("efwd");
        String outerDir = this.requestParameterJson.getString("dir");
        String file = null;
        if (efwdJson != null) {
            file = efwdJson.optString("file");
            String dir = efwdJson.optString("dir");
            if (dir != null && !dir.isEmpty()) {
                outerDir = dir;
            }
        }
        efwdFileAsJson = dataSource.readEFWD(outerDir, file, solutionDirectory);

        efwdFile = efwdFileAsJson.getString("_efwdFileName_");
        ResourceValidator resourceValidator = new ResourceValidator(efwdFileAsJson);
        boolean isValidEFWD = resourceValidator.validateEfwd();
        if (isValidEFWD) {
            int length = (solutionDirectory + File.separator).length();
            return efwdFile.substring(length);
        } else {
            return null;
        }
    }

    public String getDirectory() {
        return this.requestParameterJson.getString("dir");
    }

    public Long getConnectionId() {
        logger.info("RequestParameterJson is " + requestParameterJson);
        int mapId = requestParameterJson.getInt("map_id");
        int connectionId = 0;
        if (efwdFile != null) {
            JSONArray dataMapsArray = this.efwdFileAsJson.getJSONArray("DataMaps");
            for (int counter = 0; counter < dataMapsArray.size(); counter++) {
                JSONObject dataMapTag = dataMapsArray.getJSONObject(counter);
                if (mapId == dataMapTag.getInt("@id")) {
                    this.mapId = mapId;
                    dataMapTagContent = (JSONObject) dataMapsArray.get(counter);
                    connectionId = dataMapTag.getInt("@connection");
                    break;
                }
            }

        }

        JSONObject formData = new JSONObject();
        formData.put("id", connectionId);
        formData.put("dir", getDirectory());
        formData.put("access",DataSourceSecurityUtility.EXECUTE);
        DataSourceSecurityUtility.isDataSourceAuthenticated(formData);
        return (long) connectionId;
    }

    public Integer getMapId() {
        return this.mapId;
    }

    public String getConnectionType(Long connectionId) {
        String type = null;
        if (connectionId != 0) {
            JSONArray dataSources = efwdFileAsJson.getJSONArray("DataSources");
            for (int counter = 0; counter < dataSources.size(); counter++) {
                JSONObject connection = dataSources.getJSONObject(counter);
                if (connectionId == connection.getInt("@id")) {
                    connectionDetails = connection;
                    type = connection.getString("@type");
                    break;
                }
            }
        }
        return type;
    }

    public String getQuery(String connectionType) {
        driverObject = this.getInstance(connectionType);
        if (driverObject != null) {
            return driverObject.getQuery(dataMapTagContent, requestParameterJson);
        }
        return null;
    }

    private IDriver getInstance(String connectionType) {
        IDriver driverObject = null;
        String clazz = settingsDataSourcesMap.get(connectionType);
        if (clazz != null) {
            driverObject = (IDriver) FactoryMethodWrapper.getUntypedInstance(clazz);
        }
        return driverObject;

    }

    public JsonObject getDataFromDatabase(String query) {
        requestParameterJson.accumulate("query", query);
        return driverObject.getJSONData(requestParameterJson, connectionDetails, dataMapTagContent,
                applicationProperties);
    }
}

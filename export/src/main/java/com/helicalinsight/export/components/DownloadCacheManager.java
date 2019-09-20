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

package com.helicalinsight.export.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.EfwvfCacheManager;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.IDownload;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


@Component
@Scope("prototype")
public class DownloadCacheManager extends com.helicalinsight.cache.manager.CacheManager {
    private final static Map<String, String> propertyMap = ControllerUtils.getPropertyMap();

    private static final Logger logger = LoggerFactory.getLogger(DownloadCacheManager.class);
    private static Map<String, String> settingsDownloadMap = new HashMap<>();

    private JSONObject requestParameterJson;
    private String contentType;
    private IDownload iDownload;
    private String fileExtension;

    @Autowired
    private EfwvfCacheManager efwvfManager;

    private CacheManager CacheManager;

    @PostConstruct
    public void init() {
        logger.debug("initializing content of Download Manager");

        JSONArray downloadMapArray = CacheUtils.getCacheXmlJson().getJSONArray("downloadManager");
        for (int mapIndex = 0; mapIndex < downloadMapArray.size(); mapIndex++) {
            JSONObject settingsDataSource = downloadMapArray.getJSONObject(mapIndex);
            String downloadFormatType = settingsDataSource.getString("@type");
            String clazz = settingsDataSource.getString("@bean");
            settingsDownloadMap.put(downloadFormatType, clazz);
        }
    }

    @Override
    public void setRequestData(String data) {
        requestParameterJson = (JSONObject) JSONSerializer.toJSON(data);
        String downloadType = requestParameterJson.optString("type");
        // Set the content type for the response from the properties file
        this.contentType = (propertyMap.get(downloadType));

        if (downloadType == null || downloadType.isEmpty()) {
            //Default type being csv format
            this.fileExtension = ".csv";
            downloadType = "csv";
        } else {
            this.fileExtension = "." + downloadType;
        }

        String beanName = settingsDownloadMap.get(downloadType);
        iDownload = (IDownload) ApplicationContextAccessor.getBean(beanName);
        CacheManager = this.getCacheManager();
        CacheManager.setRequestData(data);
    }

    private CacheManager getCacheManager() {
        return efwvfManager;
    }

    @Override
    public String getConnectionFilePath() {
        return CacheManager.getConnectionFilePath();
    }

    @Override
    public Long getConnectionId() {
        return CacheManager.getConnectionId();
    }

    @Override
    public Integer getMapId() {
        return CacheManager.getMapId();
    }

    @Override
    public String getConnectionType(Long connectionId) {
        return CacheManager.getConnectionType(connectionId);
    }

    @Override
    public String getQuery(String connectionType) {
        return CacheManager.getQuery(connectionType);
    }

    @Override
    public JsonObject getDataFromDatabase(String query) {
        return CacheManager.getDataFromDatabase(query);
    }

    @Override
    public String getDirectory() {
        return CacheManager.getDirectory();
    }

    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      JsonObject fileContent) {
        try {
            ControllerUtils.addCookie(request, response, "hi_report_downloadStatus", "1");
            response.setContentType(getContentTypeString());
            String attachmentName = ReportsUtility.getReportName(request.getParameter("reportName"));
            logger.info("Downloading the content......");
            JsonArray jsonDataArray;

            jsonDataArray = fileContent.getAsJsonArray("data");

            Object object = getBinaryObject(jsonDataArray);
            OutputStream outputStream = response.getOutputStream();
            String dispositionType = request.getParameter("print") != null ? "inline; " : "attachment; ";

            response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"",
                    attachmentName + getFileExtension()));
            String resultNameTag = attachmentName == null ? ReportsUtility.getReportName(null) : attachmentName;
            String tempDir = TempDirectoryCleaner.getTempDirectory() + File.separator +
                    attachmentName + getFileExtension();
            File fileToDownload = new File(tempDir);

            if (object instanceof StringBuilder) {
                StringBuilder responseObject = (StringBuilder) object;
                ApplicationUtilities.createAFile(fileToDownload, responseObject.toString());
                outputStream.write(responseObject.toString().getBytes());
            } else if (object instanceof HSSFWorkbook) {
                try {
                    HSSFWorkbook workbook = (HSSFWorkbook) object;
                    FileOutputStream fileOutputStream = new FileOutputStream(tempDir);
                    workbook.write(outputStream);
                    workbook.write(fileOutputStream);
                    ApplicationUtilities.closeResource(fileOutputStream);
                } catch (ClassCastException exception) {
                    logger.error("Casting exception occurred {}", exception);
                    return false;
                }
            } else if (object != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(tempDir);
                outputStream.write(object.toString().getBytes());
                fileOutputStream.write(object.toString().getBytes());
            }
            outputStream.close();
            outputStream.flush();
            ControllerUtils.saveFile(request, resultNameTag, fileToDownload);
        } catch (Exception ignore) {
            return false;
        }
        return true;
    }

    public String getContentTypeString() {
        return this.contentType;
    }

    public Object getBinaryObject(JsonArray dataArray) {
        JSONArray array = JSONArray.fromObject(dataArray.toString());
        if (iDownload != null) {
            return iDownload.downloadFormat(array, requestParameterJson);
        } else {
            logger.info("Cannot instantiate the class for IDownload");
            return null;
        }
    }

    public String getFileExtension() {
        return this.fileExtension;
    }
}

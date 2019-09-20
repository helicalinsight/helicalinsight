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

package com.helicalinsight.cache.management;

import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheReportService;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Author on 13/05/2015
 *
 * @author Somen
 */
public class ResourceEmptyHandler implements IComponent {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceEmptyHandler.class);
    CacheReportService cacheReportService;

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject responseJson = new JSONObject();
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        cacheReportService = ApplicationContextAccessor.getBean(CacheReportService.class);
        JSONArray directoryArray = formData.optJSONArray("dir");
        List<CacheReport> reportList;
        if (directoryArray == null || directoryArray.size() == 0) {
            IResourceManager resourceManager = ResourceManager.getInstance();
            if (resourceManager.deleteAll()) {
                responseJson.put("message", "Resource cleaned successfully.");

            } else {
                responseJson.put("message", "Resource  could not be cleaned");
            }
        } else {

            boolean isRootDirectory = directoryArray.getString(0).equalsIgnoreCase("/");
            if (!isRootDirectory) {
                for (int index = 0; index < directoryArray.size(); index++) {
                    reportList = cacheReportService.getReports(directoryArray.getString(index));
                    processDelete(reportList);
                }
                responseJson.put("message", "Cache files cleaned successfully");
            } else {
                deleteAllCaches();
                responseJson.put("message", "All cache files deleted  successfully.");
            }
        }
        return responseJson.toString();
    }

    public void processDelete(List<CacheReport> reportList) {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        for (CacheReport record : reportList) {
            Long cacheId = record.getCacheId();
            Cache cache = cacheService.findCache(cacheId);
            if (cache != null) {
                String filePath = cache.getCacheFilePath();
                String absoluteFile = CacheUtils.getCacheDirectory() + File.separator + filePath;
                File file = new File(absoluteFile);
                if (file.exists()) {
                    file.delete();
                }
                cacheService.deleteCache(cacheId);
            }
        }
    }

    public void deleteAllCaches() {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        cacheReportService.deleteAllCacheReport();
        cacheService.deleteAllCache();
        try {
            FileUtils.cleanDirectory(new File(CacheUtils.getCacheDirectory()));
        } catch (IOException ioe) {
            logger.error("IO exception occurred while deleting all cache files ", ioe);
        }
    }
}
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

import java.util.List;

/**
 * Created by Author on 13/05/2015
 *
 * @author Somen
 */
public class ResourceDumpHandler implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject responseJson = new JSONObject();
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        CacheReportService cacheReportService = ApplicationContextAccessor.getBean(CacheReportService.class);
        String directory = formData.optString("dir");
        String action = formData.optString("action");
        List<String> reportList;

        if ("list".equalsIgnoreCase(action)) {
            return getReportCacheInformation(directory, cacheReportService).toString();
        }

        if (directory != null && !directory.equals("/") && directory.length() > 0) {
            reportList = cacheReportService.getUniqueReports(directory);
            processReport(responseJson, reportList);
        } else if ("/".equals(directory)) {
            reportList = cacheReportService.getAllUniqueReports();
            processReport(responseJson, reportList);

        } else {
            IResourceManager resourceManager = ResourceManager.getInstance();
            responseJson.put("dump", resourceManager.dump().entrySet().toString());
        }

        return responseJson.toString();
    }

    public JSONObject getReportCacheInformation(String reportName, CacheReportService cacheReportService) {
        List<CacheReport> reportList = cacheReportService.getReports(reportName);
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        JSONArray jsonArray = new JSONArray();
        for (CacheReport record : reportList) {
            Cache cache = cacheService.findCache(record.getCacheId());
            JSONObject cacheJson = new JSONObject();
            if (cache != null) {
                cacheJson.put("cacheId", cache.getCacheId());
                cacheJson.put("cacheExpiyTime", cache.getCacheExpiryTime().getTime());
                cacheJson.put("cachedTime", cache.getCacheFileTimeStamp().getTime());
                cacheJson.put("cacheFileSize", cache.getCacheFileSize());
                jsonArray.add(cacheJson);
            }
        }
        JSONObject response = new JSONObject();
        response.put("listInfo", jsonArray);
        return response;
    }

    public void processReport(JSONObject responseJson, List<String> reportList) {
        JSONArray jsonArray = new JSONArray();
        for (String record : reportList) {
            JSONObject jsonRecord = new JSONObject();
            jsonRecord.put("path", record);
            jsonArray.add(jsonRecord);
        }
        responseJson.accumulate("reportList", jsonArray);
    }

}
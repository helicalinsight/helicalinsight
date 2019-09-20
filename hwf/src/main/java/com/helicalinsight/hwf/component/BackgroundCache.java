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

package com.helicalinsight.hwf.component;


import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * @author Somen
 */
public class BackgroundCache {

    private static final Logger logger = LoggerFactory.getLogger(BackgroundCache.class);

    private CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);

    public void prepareCache(JSONObject inputs) {

        /*IF CACHE IS ENABLED THEN THE CACHE FILTER WILL START CACHING THE REQUESTS*/
        if (!CacheUtils.isCacheEnabled()) {
            logger.info("Cache layer is disabled. Forwarding request.");
            return;
        }


        String url = inputs.getString("url");
        logger.info("The current request is {} in cache", url);

        CacheManager cacheManager;

        Boolean isCacheFound;
        try {
            cacheManager = CacheUtils.getCacheManager(url);
            if (cacheManager == null) {
                return;
            }

            String data;

            data = inputs.getString("data");
            if (data == null) {
                throw new EfwServiceException("Data for the given report is null");
            }
            cacheManager.setRequestData(data);

            String fileName = inputs.getString("file");
            String dir = inputs.getString("dir");
            String reportName = dir + File.separator + fileName;

            Cache requestCache = this.prepareCacheFromRequest(cacheManager);
            isCacheFound = processCache(reportName, requestCache, cacheManager);
        } catch (Exception exception) {
            logger.error("Exception occurred ", exception);
            return;
        }

        if (!isCacheFound) {
            logger.info("Process of fetching was not successful the request takes the  normal " + "flow");
        }
    }


    private synchronized Cache prepareCacheFromRequest(CacheManager CacheManager) {
        logger.info("Preparing cache object for Hibernate");
        Cache cache = new Cache();
        cache.setConnectionFilePath(CacheManager.getConnectionFilePath());
        Long connectionId = CacheManager.getConnectionId();
        cache.setConnectionId(connectionId);
        String connectionType = CacheManager.getConnectionType(connectionId);
        cache.setMapId(CacheManager.getMapId());
        cache.setConnectionType(connectionType);
        String query = CacheManager.getQuery(connectionType);
        final byte[] bytes;
        if (query != null) {
            bytes = query.getBytes();
            String encoded = new String(Base64.encodeBase64(bytes));
            cache.setQuery(encoded);
        }
        return cache;
    }

    private Boolean processCache(String reportName, Cache requestCache, CacheManager CacheManager) {
        String cacheDirectory = CacheUtils.getCacheDirectory();
        String query = requestCache.getQuery();
        String directory = CacheManager.getDirectory();
        if (query != null && !("".equals(query.trim()))) {//Don't worry. There is something wrong. Query is null.
            //Let filter.doChain be called.
            Cache cacheModel = cacheService.findUniqueCache(requestCache);
            if (cacheModel != null && cacheModel.getCacheFilePath() != null) {
                String physicalCacheFile = cacheDirectory + File.separator +
                        cacheModel.getCacheFilePath();
                CacheUtils.deleteOldCache(cacheModel, physicalCacheFile, cacheService);
                return process(directory, requestCache, reportName, CacheManager);
            } else {
                return process(directory, requestCache, reportName, CacheManager);
            }
        }
        return false;
    }


    private Boolean process(final String directory, final Cache requestCache, final String reportName,
                            final CacheManager CacheManager) {
        try {
            //Future Idea:
            //Save partially in the database
            //may recheck before inserting in database

            Long cacheId = cacheService.addCache(requestCache);
            CacheReport cacheReport = new CacheReport();
            cacheReport.setReportPath(reportName);
            cacheReport.setCacheId(cacheId);
            cacheService.addReport(cacheReport);
            requestCache.setCacheId(cacheId);

            String query = requestCache.getQuery();
            String decodedQuery = new String(Base64.decodeBase64(query.getBytes()));
            final JsonObject jsonData = CacheManager.getDataFromDatabase(decodedQuery);
            if (jsonData == null) {
                cacheService.deleteCache(cacheId);
                return false;
            }

            if (logger.isInfoEnabled()) {
                logger.info("Creating a new cache file and serving data synchronously");
            }
            saveToDisk(requestCache, directory, jsonData);
            return true;
        } catch (Exception ex) {
            logger.error("Exception in refresh Cache " + ex.getMessage(), ex);
            return false;
        }
    }

    private void saveToDisk(Cache requestCache, String directory, JsonObject jsonData) {
        String cacheUUID = UUID.randomUUID().toString();
        String cacheFilePath = directory + File.separator + cacheUUID + "." + CacheUtils.getCacheExtension();
        String cacheFileSavePath = CacheUtils.getCacheDirectory() + File.separator + cacheFilePath;
        requestCache.setCacheFilePath(cacheFilePath);
        if (logger.isInfoEnabled()) {
            logger.info("File created with the file name " + cacheFilePath);
        }
        boolean saved = true;
        try {
            CacheUtils.saveFileToDisk(jsonData, cacheFileSavePath);

            //Save the details in the database

            File file = new File(cacheFileSavePath);
            requestCache.setCacheFileTimeStamp(new Date(file.lastModified()));

            requestCache.setCacheExpiryTime(new Date(CacheUtils.getActualCacheExpireDuration()));

            requestCache.setCacheFileSize(file.length());

            cacheService.editCache(requestCache);
        } catch (Exception ex) {
            saved = false;
            logger.error("Exception occurred ", ex.getMessage());
        } finally {
            if (!saved) {
                cacheService.deleteCache(requestCache.getCacheId());
            }
        }
    }
}
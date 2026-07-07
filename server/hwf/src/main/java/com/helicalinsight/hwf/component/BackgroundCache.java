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
 * Used by HWF
 *
 * @author Somen
 *         Created on 5/17/2016.
 */
@SuppressWarnings("unused")
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
            String data;

            if (url.contains("/services") || url.contains("/visualizeAdhoc")) {
                if (!setServiceFormData(inputs, cacheManager, url)) {
                    return;
                }
            } else {
                data = inputs.getString("data");
                if (data == null) {
                    throw new EfwServiceException("Data for the given report is null");
                }
                cacheManager.setRequestData(data);
            }

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


    private boolean setServiceFormData(JSONObject request, CacheManager cacheManager, String url) {
        String formData;
        String type = request.optString("type");
        String serviceType = request.optString("serviceType");
        String service = request.optString("service");
        String adhoc = "adhoc";
        String visualizeAdhoc = "visualizeAdhoc";
        String report = "report";
        if ((adhoc.equals(type) && report.equals(serviceType)) || (url.contains(visualizeAdhoc))) {
            formData = request.getString("formData");
            if (formData != null && Base64.isBase64(formData)) {
                formData = new String(Base64.decodeBase64(formData));
            }
            if (formData != null) {
                JSONObject formDataJson = JSONObject.fromObject(formData);
                String reportName = formDataJson.optString("metadataFileName");
                String dir = formDataJson.optString("location");
                request.put("dir", dir);

                if (reportName.isEmpty()) {
                    reportName = formDataJson.optString("uniqueId");
                }
                request.put("file", reportName);

                String fetchData = "fetchData";
                if ((fetchData.equalsIgnoreCase(service) && adhoc.equalsIgnoreCase(type) && report.equalsIgnoreCase
                        (serviceType)) || (url.contains(visualizeAdhoc))) {
                    formDataJson.accumulate("requestType", adhoc);
                    formDataJson.accumulate("serviceType", report);
                    formDataJson.accumulate("service", fetchData);
                    cacheManager.setRequestData(formDataJson.toString());
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized Cache prepareCacheFromRequest(CacheManager cacheManager) {
        logger.info("Preparing cache object for Hibernate");
        Cache cache = new Cache();
        cache.setConnectionFilePath(cacheManager.getConnectionFilePath());
        Long connectionId = cacheManager.getConnectionId();
        cache.setConnectionId(connectionId);
        String connectionType = cacheManager.getConnectionType(connectionId);
        cache.setMapId(cacheManager.getMapId());
        cache.setConnectionType(connectionType);
        String query = cacheManager.getQuery(connectionType);
        final byte[] bytes;
        if (query != null) {
            bytes = query.getBytes();
            String encoded = new String(Base64.encodeBase64(bytes));
            cache.setQuery(encoded);
        }
        return cache;
    }

    private Boolean processCache(String reportName, Cache requestCache, CacheManager cacheManager) {
        String cacheDirectory = CacheUtils.getCacheDirectory();
        String query = requestCache.getQuery();
        String directory = cacheManager.getDirectory();
        if (query != null && !("".equals(query.trim()))) {//Don't worry. There is something wrong. Query is null.
            //Let filter.doChain be called.
            Cache cacheModel = cacheService.findUniqueCache(requestCache);
            if (cacheModel != null && cacheModel.getCacheFilePath() != null) {
                String physicalCacheFile = cacheDirectory + File.separator +
                        cacheModel.getCacheFilePath();
                CacheUtils.deleteOldCache(cacheModel, physicalCacheFile, cacheService);
                return process(directory, requestCache, reportName, cacheManager);
            } else {
                return process(directory, requestCache, reportName, cacheManager);
            }
        }
        return false;
    }


    private Boolean process(final String directory, final Cache requestCache, final String reportName,
                            final CacheManager cacheManager) {
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
            final Object jsonData = cacheManager.getDataFromDatabase(decodedQuery);
            if (jsonData == null) {
                cacheService.deleteCache(cacheId);
                return false;
            }

            if (logger.isInfoEnabled()) {
                logger.info("Creating a new cache file and serving data synchronously");
            }
            saveToDisk(requestCache, directory, (JsonObject) jsonData);
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
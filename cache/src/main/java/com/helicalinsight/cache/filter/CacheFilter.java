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

package com.helicalinsight.cache.filter;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Component("cacheFilter")
public class CacheFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CacheFilter.class);

    @Autowired
    private CacheService cacheService;

    public CacheFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest httpRequest = ((HttpServletRequest) request);

        /*IF CACHE IS ENABLED THEN THE CACHE FILTER WILL START CACHING THE REQUESTS*/
        if (!CacheUtils.isCacheEnabled()) {
            logger.info("Cache layer is disabled. Forwarding request.");
            chain.doFilter(request, response);
            return;
        }


        String url = httpRequest.getServletPath();
        logger.info("The current request is {} in cache", url);

        CacheManager cacheManager;

        Boolean cacheRequired = false;
        try {
            HttpSession session = setThreadName(httpRequest);
            if (session == null) return;

            logger.info("This current Thread is " + Thread.currentThread().getName());
            logger.info("This current Thread id " + Thread.currentThread().getId());

            if (CacheUtils.getRefreshUrl().contains(url)) {
                setUnsetRefresh(request, session);
            } else {
                cacheManager = CacheUtils.getCacheManager(url);
                if (cacheManager == null) {
                    chain.doFilter(request, response);
                    return;
                }
                String data = request.getParameter("data");
                if (data == null) {
                    chain.doFilter(request, response);
                    return;
                }
                cacheManager.setRequestData(data);


                Boolean refresh = false;
                String refreshParam = (String) session.getAttribute("refresh");
                String reportName = (String) session.getAttribute("requestedReport");

                if (refreshParam != null) {
                    refresh = "true".equals(refreshParam);
                }
                Cache requestCache = this.prepareCacheFromRequest(cacheManager);
                cacheRequired = processCache(request, response, reportName, refresh, requestCache, cacheManager);
            }
        } catch (Exception exception) {
            logger.error("Exception occurred at cache filter ", exception);
            if (exception instanceof AccessDeniedException) {
                ControllerUtils.accessDenied((HttpServletRequest) request, (HttpServletResponse) response);
            } else {
                throw exception;
            }
            return;
        }

        if (!cacheRequired) {

            logger.info("Cache filter set/unset flag set to true ");
            chain.doFilter(request, response);
        } else {
            logger.info("Returning from cache without any result");
            return;
        }
    }

    private HttpSession setThreadName(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);

        String queryId = httpRequest.getParameter("queryId");
        Boolean cancelQuery = Boolean.valueOf(httpRequest.getParameter("cancelQuery"));

        if (queryId != null) {
            ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
            if (!cancelQuery) {
                String uuid = UUID.randomUUID().toString();
                Long queryCount = (Long) session.getAttribute("queryCount");

                queryCount = queryCount == null ? 0l : ++queryCount;
                session.setAttribute("queryCount", queryCount);
                String threadName = uuid + "##" + queryCount + "##" + queryId;

                Thread.currentThread().setName(threadName);
                registry.registerQueryIdThreadName(queryId, threadName);
                logger.info("Thread Name is registered for cache" + threadName);
            }
        }

        return session;
    }

    private void setUnsetRefresh(ServletRequest request, HttpSession session) {
        String reportName;
        String dir;
        String refreshParam = request.getParameter("refresh");
        reportName = request.getParameter("file");
        dir = request.getParameter("dir");
        if (reportName != null && dir != null) {
            session.setAttribute("requestedReport", dir + File.separator + reportName);
        } else {
            session.removeAttribute("requestedReport");
        }
        if (refreshParam != null) {
            session.setAttribute("refresh", "true");
        } else {
            session.removeAttribute("refresh");
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

    private Boolean processCache(ServletRequest request, ServletResponse response, String reportName,
                                 Boolean refresh, Cache requestCache, CacheManager CacheManager) {
        String cacheDirectory = CacheUtils.getCacheDirectory();
        String query = requestCache.getQuery();
        String directory = CacheManager.getDirectory();
        if (query != null && !("".equals(query.trim()))) {//Don't worry. There is something wrong. Query is null.
            //Let filter.doChain be called.
            Cache cacheModel = cacheService.findUniqueCache(requestCache);
            if (cacheModel != null && cacheModel.getCacheFilePath() != null) {
                Long currentTime = System.currentTimeMillis();
                String physicalCacheFile = cacheDirectory + File.separator +
                        cacheModel.getCacheFilePath();
                long expiryTime = cacheModel.getCacheExpiryTime().getTime();
                if (!refresh && (currentTime < expiryTime)) {
                    logger.info("Cache file time is less so it can be served ");
                    Date lastModified = cacheModel.getCacheFileTimeStamp();
                    return serveCacheFile(response, physicalCacheFile, request, lastModified, CacheManager);
                } else {
                    CacheUtils.deleteOldCache(cacheModel, physicalCacheFile, cacheService);
                    return process(request, response, directory, cacheDirectory, requestCache, reportName,
                            CacheManager);
                }
            } else {
                return process(request, response, directory, cacheDirectory, requestCache, reportName, CacheManager);
            }
        }
        return false;
    }

    private Boolean serveCacheFile(ServletResponse response, String physicalCacheFile, ServletRequest request,
                                   Date lastUpdatedDate, CacheManager CacheManager) {
        try {
            JsonObject fileContent = CacheUtils.readFileContent(physicalCacheFile);
            fileContent.addProperty("lastModified", lastUpdatedDate.getTime());
            CacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, fileContent);
        } catch (IOException exception) {
            logger.error("IO Exception occurred during io read", exception);
            return false;
        }
        return true;
    }

    private Boolean process(ServletRequest request, ServletResponse response, final String directory,
                            String solutionDirectory, final Cache requestCache, final String reportName,
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

            boolean threadingEnabled = CacheUtils.isThreadingEnabled();
            if (threadingEnabled) {
                if (logger.isInfoEnabled()) {
                    logger.info("Creating a new cache file in a separate thread and serving the data asynchronously");
                }
                Thread cacheWorker = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToDisk(requestCache, directory, jsonData);
                    }
                });
                cacheWorker.setName("Cache-Worker-" + cacheWorker.getId());
                cacheWorker.start();

                return CacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response,
                        jsonData);
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Creating a new cache file and serving data synchronously");
                }
                saveToDisk(requestCache, directory, jsonData);

                String newCacheFile = solutionDirectory + File.separator + requestCache.getCacheFilePath();
                Date lastModified = requestCache.getCacheFileTimeStamp();
                return serveCacheFile(response, newCacheFile, request, lastModified, CacheManager);
            }
        } catch (Exception ex) {
            logger.error("Exception in refresh Cache " + ex.getMessage(), ex);
            throw ex;
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

    public void destroy() {
    }
}

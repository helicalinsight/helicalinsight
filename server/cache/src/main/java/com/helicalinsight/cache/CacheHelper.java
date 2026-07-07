package com.helicalinsight.cache;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.google.gson.Gson;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.concurrent.StreamedResultset;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.utility.CloningUtils;
import com.helicalinsight.efw.utility.SplitterUtils;
import com.helicalinsight.cache.store.ApplicationCacheStore;

import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

/**
 * Created by author on 12/10/2019.
 * this class used to create different cache object like for database,service. 
 * The {@code @Component} annotation is used to mark a class as a Spring bean/component.
 * {@code @Scope("prototype")} new instance of the component will be created each time.
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class CacheHelper {
    private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);
    @Autowired
    private CacheService cacheService;
    
    private Integer noOfRecords;

    public synchronized Cache prepareCacheFromRequest(CacheManager cacheManager) {
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

    public String designCacheKeyFor(Cache requestCache) {
        Cache cacheModel = cacheService.findUniqueCache(requestCache);
        if (cacheModel != null && cacheModel.getCacheFilePath() != null) {
            return SplitterUtils.prepareServiceId(cacheModel.toString());
        }
        return null;
    }


    public Boolean processCache(ServletRequest request, ServletResponse response, String reportName,
                                Boolean refresh, Cache requestCache, CacheManager cacheManager) {
        String cacheDirectory = CacheUtils.getCacheDirectory();
        String query = requestCache.getQuery();
        String directory = cacheManager.getDirectory();
        
        if (query != null && !("".equals(query.trim()))) {//Don't worry. There is something wrong. Query is null.
            //Let filter.doChain be called.
        	String decodedQuery = new String(Base64.decodeBase64(query.getBytes()));
        	Cache cacheModel = cacheService.findUniqueCache(requestCache);

            if (CacheUtils.isCacheEnabled() && ( cacheModel != null && cacheModel.getCacheFilePath() != null)) {
            	logger.debug("Cache hit for Query : {}", decodedQuery);
                noOfRecords = cacheModel.getNoOfRecords();
                Long currentTime = System.currentTimeMillis();
                String physicalCacheFile = cacheDirectory + File.separator + cacheModel.getCacheFilePath();
                long expiryTime = cacheModel.getCacheExpiryTime().getTime();
                if (!refresh && (currentTime < expiryTime)) {
                    logger.info("Cache file time is less so it can be served ");
                    Date lastModified = cacheModel.getCacheFileTimeStamp();
                    return serveCacheFile(response, physicalCacheFile, request, lastModified, cacheManager);
                } else {
                	logger.debug("Cache expired for Query : {}, reinstating the cache.", query);
                	CacheUtils.deleteOldCache(cacheModel, physicalCacheFile, cacheService);
                    return process(request, response, directory, cacheDirectory, requestCache, reportName, cacheManager);
                }
            } else {
            	logger.debug("Cache is not present (or) Cache might have been disabled.");
                return process(request, response, directory, cacheDirectory, requestCache, reportName, cacheManager);
            }
        }
        return false;
    }

    private Boolean serveCacheFile(ServletResponse response, String physicalCacheFile, ServletRequest request,
                                   Date lastUpdatedDate, CacheManager cacheManager) {
        try {
            if ("com.helicalinsight.adhoc.cachemanager.HCRPrintCacheManager".equals(cacheManager.getClass().getName())) {
            	JsonObject formData = new Gson().fromJson(cacheManager.getRequestData(), JsonObject.class);
                formData.addProperty("noOfRecords", noOfRecords);
                cacheManager.setRequestData(formData.toString());
                JasperPrint cacheJasperPrint = (JasperPrint) cacheManager.readFileContent(physicalCacheFile);
                cacheJasperPrint.setProperty("lastModifiedCache", ""+lastUpdatedDate.getTime());
                boolean b = cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, cacheJasperPrint);
            } else if ("com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet".equals(cacheManager.getClass().getName())) {
            	cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, new StreamedResultset(physicalCacheFile));
                
            } else if ("com.helicalinsight.export.components.DownloadCacheManager".equals(cacheManager.getClass().getName())) {
                Object o = cacheManager.readFileContent(physicalCacheFile);
                if (o instanceof ResultSet) {
                	if(request!=null) {
                        request.setAttribute("lastModifiedCache", lastUpdatedDate.getTime());
                    }
                	cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, new StreamedResultset(physicalCacheFile));
                } else {
                    JsonObject fileContent = (JsonObject) o;
                    fileContent.addProperty("lastModified", lastUpdatedDate.getTime());
                    cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, fileContent);
                }
            }
            else if ("com.helicalinsight.cache.manager.HCRQueryProcessCacheManager".equals(cacheManager.getClass().getName())) {
            	  Object o = cacheManager.readFileContent(physicalCacheFile);
            	  JsonObject fileContent = (JsonObject) o;
                  fileContent.addProperty("lastModified", lastUpdatedDate.getTime());
                  cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, fileContent);
            }
            else {
                if(request!=null) {
                    request.setAttribute("lastModifiedCache", lastUpdatedDate.getTime());
                }
                cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, new StreamedResultset(physicalCacheFile));
            }
        } catch (IOException exception) {
            logger.error("IO Exception occurred during io read", exception);
            return false;
        }
        return true;
    }

    private Boolean process(ServletRequest request, ServletResponse response, final String directory,
                            String solutionDirectory, final Cache requestCache,  String reportName,
                            final CacheManager cacheManager) {
        Long cacheId = null;
    	try {
            //Future Idea:
            //Save partially in the database
            //may recheck before inserting in database
        	
        	boolean isCacheEnabled  = CacheUtils.isCacheEnabled();
        	String query = requestCache.getQuery();
            String decodedQuery = new String(Base64.decodeBase64(query.getBytes()));
       	
            if ( isCacheEnabled ) {
            	logger.debug("Initiating cache for Query : {}", decodedQuery);
            	cacheId = cacheService.addCache(requestCache);
            }
            
            saveCacheReport(cacheId, reportName, isCacheEnabled);
            
            requestCache.setCacheId(cacheId);

            if (isCacheEnabled && CacheUtils.isStreamingCache() ) {
            	 String newCacheDir = "";
            	try {
            	 logger.debug("Caching the streaming resultset");
            	 long start = System.currentTimeMillis();
            	 String cacheDir =  new StreamingCacheProvider().provide(decodedQuery, directory, requestCache, cacheManager);
            	 
            	 newCacheDir = solutionDirectory + File.separator + cacheDir;
            	 
            	 File cacheDirFile = new File(newCacheDir);
            	 
            	 requestCache.setCacheFileTimeStamp(new Date(cacheDirFile.lastModified()));
            	 
            	 requestCache.setCacheExpiryTime(new Date(CacheUtils.getActualCacheExpireDuration()));
            	 
            	 requestCache.setCacheFilePath(cacheDir);
            	 cacheService.editCache(requestCache);
            	 double totalTime = (System.currentTimeMillis() - start) / 1000.0;
            	 logger.debug("Time taken to create the streaming cache : {} s", totalTime);
            	 
            	 ResultSet resultSet = new StreamedResultset(newCacheDir);
            	 return cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse)response, resultSet);
            	}
            	catch (Exception e) {
            		logger.error("Exception occurred rolling back the cache.",e);
            		rollBack(cacheId);
            		return false;
            	}
            }
            
            logger.warn("Caching the normal resultset, please switch to `stream` cache strategy to improve the performance and reduce the risk of OOM");
            Object jsonData = cacheManager.getDataFromDatabase(decodedQuery);

            if (jsonData == null) {
                cacheService.deleteCache(cacheId);
                return false;
            }

            boolean threadingEnabled = CacheUtils.isThreadingEnabled();
            if (threadingEnabled) {
                /** TODO JsonData state is being changed by read and write process in the same thread.Hence it is not thread safe.Cloning of this data is now sent one for writing and one for reading, hence making it as a thread safe**/

                CloningUtils cloningUtils = new CloningUtils();
                Object jsonDataClone = cloningUtils.cloneObject(jsonData);
                
                if(isCacheEnabled) {
                	
                	if (logger.isInfoEnabled()) {
                        logger.info("Creating a new cache file in a separate thread and serving the data asynchronously");
                    }
                	
                    HIManagedThread cacheWorker = new HIManagedThread(() ->
                            cacheManager.saveToDisk(requestCache, directory, jsonDataClone)
                    );
                    cacheWorker.setName("Cache-Worker-" + cacheWorker.threadId());
                    cacheWorker.start();
                    cacheWorker.join();
                }
                ResultSet resultSet = new StreamedResultset(solutionDirectory + File.separator + requestCache.getCacheFilePath());
           	 	return cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse)response, resultSet);
            } else {
                if(isCacheEnabled) {
                	if (logger.isInfoEnabled()) {
                        logger.info("Creating a new cache file and serving data synchronously");
                    }
                    cacheManager.saveToDisk(requestCache, directory, jsonData);
                    String newCacheFile = solutionDirectory + "/" + requestCache.getCacheFilePath();
                    Date lastModified = requestCache.getCacheFileTimeStamp();
                    return  serveCacheFile(response, newCacheFile, request, lastModified, cacheManager);
                }
                
                logger.debug("Caching is disabled. Fetching data directly from the data source.");
                return cacheManager.serveCachedContent((HttpServletRequest) request, (HttpServletResponse) response, jsonData);
            }
        } catch (Exception ex) {
            logger.error("Exception in refresh Cache " + ex.getMessage(), ex);
            rollBack(cacheId);
            return false;
        }
    }
    
    private void rollBack(Long cacheId) {
    	if ( cacheId != null) {
    		cacheService.deleteCache(cacheId);
    	}
    }
    
    public final void saveCacheReport(Long cacheId, String reportName , Boolean isCacheEnabled) {
    	CacheReport cacheReport = new CacheReport();
        if(reportName!=null&& reportName.contains("hi_hcr_db")){
           String connectionId = reportName.replace(".efwd", "").replace("hi_hcr_db", "");

            EFWDConnectionService efwdConnectionService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
            HIHcrConnections item = efwdConnectionService.fetchHIHcrConnectionsById(Integer.valueOf(connectionId));
            if (item != null) {
                 reportName = item.getHiResourceHcr().getResourceURL();
            }
        }
        cacheReport.setReportPath(reportName);
        cacheReport.setCacheId(cacheId);
        if(isCacheEnabled) {
            cacheService.addReport(cacheReport);
        }
    }
    
}

package com.helicalinsight.cache.management;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheReportService;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.cache.store.ApplicationCacheStore;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.jasperintegration.JReportExecutionContext;
import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.serviceframework.IComponent;

import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * Created by Author on 13/05/2015
 * this class  delete all cache from Resources which extends <p>@link IComponent</p>
 * @author Somen
 */
public class ResourceEmptyHandler implements IComponent {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceEmptyHandler.class);
    CacheReportService cacheReportService;

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
	 * executeComponent(String jsonFormData)
	 * uses the formData to clean cache from resource.
	 * @param jsonFormData formData in String format.
	 * @returns cache cleaned response
	 */
    @Override
    public String executeComponent(String jsonFormData) {
    	JsonObject responseJson = new JsonObject();
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        cacheReportService = ApplicationContextAccessor.getBean(CacheReportService.class);
        JsonArray directoryArray = GsonUtility.optJsonArray(formData,"dir");
        List<CacheReport> reportList;
        if (directoryArray == null || directoryArray.size() == 0) {
            IResourceManager resourceManager = ResourceManager.getInstance();
            if (resourceManager.deleteAll()) {
                responseJson.addProperty("message", "Resource cleaned successfully.");

            } else {
                responseJson.addProperty("message", "Resource  could not be cleaned");
            }
        } else {

            boolean isRootDirectory = directoryArray.get(0).getAsString().equalsIgnoreCase("/");
            if (!isRootDirectory) {
                for (int index = 0; index < directoryArray.size(); index++) {
                    reportList = cacheReportService.getReports(directoryArray.get(index).getAsString());
                    processDelete(reportList);
                }
                responseJson.addProperty("message", "Cache files cleaned successfully");
            } else {
                deleteAllCaches();
                responseJson.addProperty("message", "All cache files deleted  successfully.");
            }
        }
        return responseJson.toString();
    }
    /**
	 * processDelete(@NotNull List<CacheReport> reportList)
	 * Deletes the caches that are specified in the `reportList` parameter. If a
	 * cache has multiple records, the method will delete all of the associated
	 * files.
	 *
	 * @param reportList 		A list of `CacheReport` objects that represent the caches
	 *                   		to be deleted.
	 */
    public void processDelete(@NotNull List<CacheReport> reportList) {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        for (CacheReport record : reportList) {
            Long cacheId = record.getCacheId();
            Cache cache = cacheService.findCache(cacheId);
            String filePath = removeDesignCache(cache.getCacheFilePath());
            String cacheDirectory = CacheUtils.getCacheDirectory();
            String cacheExtension = CacheUtils.getCacheExtension();
            if (cache != null) {
                filePath = StringUtils.isBlank(filePath) ?  cache.getCacheFilePath() : filePath;
                Integer noOfRecords = cache.getNoOfRecords();
                if (noOfRecords != null && noOfRecords > 1) {
                    List<String> fileStringsToBeDeleted = new ArrayList<>();
                    String designerCacheFilePath = cacheDirectory + File.separator + filePath.replace("." + cacheExtension, "designer." + cacheExtension);
                    fileStringsToBeDeleted.add(designerCacheFilePath);
                    for (int index = 0; index < noOfRecords; index++) {
                        String eachPageIndexPath = cacheDirectory + File.separator + filePath.replace("." + cacheExtension, "page_" + index + "." + cacheExtension);
                        fileStringsToBeDeleted.add(eachPageIndexPath);
                    }
                    deleteAll(fileStringsToBeDeleted);
                } else {
                    String absoluteFile = cacheDirectory + File.separator + filePath;
                    File file = new File(absoluteFile);
                    if (file.exists()) {
                    	if ( file.isDirectory() ) {
                    		FileUtils.deleteQuietly(file);
                    	}
                    	else {
                    		file.delete();
                    	}
                    }
                }
                cacheService.deleteCache(cacheId);
            }
        }
    }
    
    private final String removeDesignCache(String key) {
    	ApplicationCacheStore store =  CacheUtils.getCacheStore();
    	Object obj =  store.get(key);
    	if ( obj != null && obj instanceof JReportExecutionContext ctx) {
    		store.remove(key);
    		return  ctx.getSwapDir();
    	}
    	return "";
    }
    /**
	 * deleteAll(List<String> fileStringsToBeDeleted)
	 * Deletes all of the files in the `fileStringsToBeDeleted` list.
	 *
	 * @param fileStringsToBeDeleted 	A list of files to be deleted.
	 */
    private void deleteAll(List<String> fileStringsToBeDeleted) {
        fileStringsToBeDeleted.forEach(item -> {
            File file = new File(item);
            boolean exists = file.exists() && file.delete();
        });
    }
    /**
	 * deleteAllCaches()
	 * Deletes all of the caches in the resource system.
	 * This method uses the CacheService and CacheReportService beans to delete the caches in the system.
	 */
    public void deleteAllCaches() {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        cacheReportService.deleteAllCacheReport();
        cacheService.deleteAllCache();
        CacheUtils.getCacheStore().clear();
        try {
            File directory=new File(CacheUtils.getCacheDirectory());
            if(!directory.exists())
                throw new ResourceNotFoundException("No cached files to delete");
            FileUtils.cleanDirectory(directory);
        } catch (IOException ioe) {
            logger.error("IO exception occurred while deleting all cache files ", ioe);
        }
    }
}
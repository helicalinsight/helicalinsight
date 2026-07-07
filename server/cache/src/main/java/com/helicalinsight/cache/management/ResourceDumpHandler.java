package com.helicalinsight.cache.management;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheReportService;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Author on 13/05/2015
 * This class will show which cache data is present in the resource file and it extends <p>@link IComponent</p>
 * @author Somen
 */
public class ResourceDumpHandler implements IComponent {

	
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

	/**
	 * executeComponent(String jsonFormData)
	 * @param jsonFormData         formData in String format.
	 * @return cache data present in resource.
	 */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject responseJson = new JsonObject();
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        CacheReportService cacheReportService = ApplicationContextAccessor.getBean(CacheReportService.class);
        String directory = GsonUtility.optString(formData,"dir");
        String action = GsonUtility.optString(formData,"action");
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
            responseJson.addProperty("dump", resourceManager.dump().entrySet().toString());
        }

        return responseJson.toString();
    }
    
    /**
     * getReportCacheInformation( String reportName, CacheReportService cacheReportService)
   
     * @param reportName 				cache report name 
     * @param cacheReportService 		service layer object provides report list
     * @return all cache information from cache report in JsonObject format. 								
     */
	public JsonObject getReportCacheInformation(@NotNull String reportName, CacheReportService cacheReportService) {
        List<CacheReport> reportList = cacheReportService.getReports(reportName);
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        JsonArray jsonArray = new JsonArray();
        for (CacheReport record : reportList) {
            Cache cache = cacheService.findCache(record.getCacheId());
            JsonObject cacheJson = new JsonObject();
            if (cache != null) {
                cacheJson.addProperty("cacheId", cache.getCacheId());
                cacheJson.addProperty("cacheExpiyTime", cache.getCacheExpiryTime().getTime());
                cacheJson.addProperty("cachedTime", cache.getCacheFileTimeStamp().getTime());
                cacheJson.addProperty("cacheFileSize", cache.getCacheFileSize());
                jsonArray.add(cacheJson);
            }
        }
        JsonObject response = new JsonObject();
        response.add("listInfo", jsonArray);
        return response;
    }
	/**
	 * processReport(@NotNull JsonObject responseJson, @NotNull List<String> reportList)
	 * this method adds report list to resources
	 * @param responseJson 		json data of cache resources
	 * @param reportList 		cache report list
	 */
    public void processReport(@NotNull JsonObject responseJson, @NotNull List<String> reportList) {
        JsonArray jsonArray = new JsonArray();
        for (String record : reportList) {
            JsonObject jsonRecord = new JsonObject();
            jsonRecord.addProperty("path", record);
            jsonArray.add(jsonRecord);
        }
        responseJson.add("reportList", jsonArray);
    }

}
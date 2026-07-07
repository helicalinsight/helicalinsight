package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.components.DownloadCacheManager;
import com.helicalinsight.validation.filter.ValidationFilter;
import com.helicalinsight.validation.form.GenericValidation;


/**
 * This class implements the IComponent interface and handles the refreshing of resources based on the provided key.
 * 
 * Created by Author on 8/07/2015
 * @author Somen
 */
public class ResourceRefreshHandler implements IComponent {
	/**
	 * Checks class is designed to be thread-safe for caching.
	 * @return {@code true} if the class is thread-safe to cache, {@code false} otherwise.
	 */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * It Executes the component to refresh resources based on the provided key.
     * It supports refreshing validation settings and cache settings. If no specific key is provided, it refreshes both.
     * @param jsonFormData         JSON-formatted data containing the refresh key.
     * @return JSON-formatted result indicating the success of the refresh operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String refreshKey = GsonUtility.optString(formJson,"refresh");
        JsonObject jsonObject = new JsonObject();
        switch (refreshKey) {
            case "validation":
                new ValidationFilter().initialize();
                GenericValidation.init();
                break;
            case "cache":
                CacheUtils.init();
                new DownloadCacheManager().init();
                break;
            default:
                new ValidationFilter().initialize();
                GenericValidation.init();
                CacheUtils.init();
                new DownloadCacheManager().init();
                break;
        }
        jsonObject.addProperty("message", "Successfully refreshed " + refreshKey + " settings");
        return jsonObject.toString();
    }
}
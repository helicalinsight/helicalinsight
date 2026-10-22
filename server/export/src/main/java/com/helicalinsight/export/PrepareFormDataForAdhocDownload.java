package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.export.components.DownloadCacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The `PrepareFormDataForAdhocDownload` class is responsible for preparing form data
 * for adhoc download requests. 
 *
 * Created by helical019 on 7/6/2020.
 */
@Component
@Scope("prototype")
public class PrepareFormDataForAdhocDownload {

    /**
     * method prepares form data for adhoc download.
     * It is used to set the necessary parameters in the
     * adhoc form data such as format, report name, etc. and then process the cache to
     * obtain the file to be downloaded.
     *
     * @param adhocFormData 		adhoc form data as a JsonObject.
     * @param format        		format in which the report should be downloaded.
     * @param reportName    		name of the report.
     * @return The file path of the downloaded file.
     */
	public String prepareFormData(JsonObject adhocFormData, String format, String reportName) {
        if (format != null)
            adhocFormData.addProperty("type", format);
        adhocFormData.addProperty("reportName", reportName);
        DownloadCacheManager cacheManager = (DownloadCacheManager) CacheUtils.getCacheManager("/downloadReport");
        cacheManager.setRequestData(adhocFormData.toString());
        try {
            return cacheManager.materializeToFile(reportName, false).getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare adhoc download file", e);
        }
    }
}

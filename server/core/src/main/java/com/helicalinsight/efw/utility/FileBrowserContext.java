package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.FileBrowserCacheRepository;
import com.helicalinsight.efw.HIManagedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Rajesh
 *         Created by helical019 on 6/26/2019.
 */
@Component
public class FileBrowserContext {
    public static final Logger logger = LoggerFactory.getLogger(FileBrowserContext.class);
    private ThreadPoolExecutor executor;
    @Autowired
    private FileBrowserCacheRepository cacheRepository;

    public void triggerFileBrowserCache() {
        logger.error("trigger fileBrowser cache");
        new HIManagedThread(() -> cacheRepository.processFilesAndFolder(null, 0, null, false)).start();
        logger.error("trigger fileBrowser cache return");
    }

    public void triggerFileBrowserCache(String path, int parentId, String parentLogicalPath, boolean isFromWatcher) {
        prepareThreadPool();
        executor.execute(new HIManagedThread(() -> cacheRepository.processFilesAndFolder(path, parentId, parentLogicalPath, isFromWatcher)));
    }

    private void prepareThreadPool() {
        if (executor == null) {
            Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.getProjectPropertiesFile();
            String threshold = mapFromClasspathPropertiesFile.get("cache.file_browser_cache_threshold");
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.valueOf(threshold));
        }
    }

    public void shutdownExecutor() {
        if (executor != null)
            executor.shutdownNow();
    }

    public void dropAllFileBrowserCacheTables() {
        cacheRepository.dropFileBrowserCache();
    }

    public boolean isFileBrowserCacheEmpty() {
        return cacheRepository.isFileBrowserCacheEmpty();
    }

}

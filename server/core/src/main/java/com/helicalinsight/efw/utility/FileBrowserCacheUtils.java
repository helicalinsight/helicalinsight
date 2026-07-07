package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.FileBrowserCacheRepository;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * Created by author on 5/27/2019.
 *
 * @author Rajesh
 */
public class FileBrowserCacheUtils {


    public static void madeChangesDb(String path) {
        if (JsonUtils.isFileBrowserCacheEnabled()) {
            FileBrowserCacheRepository cacheRepository = ApplicationContextAccessor.getBean(FileBrowserCacheRepository.class);
            cacheRepository.processFilesAndFolder(path, 0, null, true);
            //updateInCache();
        }
    }

    private static void updateInCache() {
        try {
            ControllerUtils.checkCache(null, null, true);
        } catch (UnSupportedRuleImplementationException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromDb(String path) {
        if (JsonUtils.isFileBrowserCacheEnabled()) {
            String relativeSolutionPath = ApplicationUtilities.getRelativeSolutionPath(path);
            String relativePath = FileBrowserCacheRepository.replaceBackWardSlashToForward(relativeSolutionPath);

            FileBrowserCacheRepository cacheRepository = ApplicationContextAccessor.getBean(FileBrowserCacheRepository.class);
            cacheRepository.deleteFromDb(relativePath);
            // updateInCache();
        }
    }
}

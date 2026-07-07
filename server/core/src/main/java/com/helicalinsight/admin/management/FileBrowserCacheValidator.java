package com.helicalinsight.admin.management;

import com.helicalinsight.admin.FileBrowserCacheRepository;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileBrowserContext;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Rajesh on 4/8/2019.
 */
public class FileBrowserCacheValidator implements IComponent {
    private static final Logger logger = LoggerFactory.getLogger(FileBrowserCacheRepository.class);


    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject result = new JSONObject();
        if (!JsonUtils.isFileBrowserCacheEnabled()) {
            result.put("message", "FileBrowserCache is not enabled.");
            return result.toString();
        }
        FileBrowserContext bean = ApplicationContextAccessor.getBean(FileBrowserContext.class);
        logger.debug("Deleting all the data of FileBrowserCacheTable in Database.");
        bean.dropAllFileBrowserCacheTables();

        //Add all the data in FileBrowserCache Table
        logger.debug("Adding all the data of FileBrowserCacheTable in Database.");
        bean.triggerFileBrowserCache();
        logger.debug("after triggering file browser cache");
        result.put("message", "Successfully started the FileBrowser cache validation process.");
        return result.toString();
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

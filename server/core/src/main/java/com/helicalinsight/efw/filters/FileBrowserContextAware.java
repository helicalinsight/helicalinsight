package com.helicalinsight.efw.filters;

import com.helicalinsight.admin.FileBrowserCacheRepository;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.efw.utility.FileBrowserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Rajesh
 *         Created by helical019 on 6/14/2019.
 */
//@Component
public class FileBrowserContextAware /*implements ApplicationContextAware */{
   /* private static final Logger logger = LoggerFactory.getLogger(FileBrowserContextAware.class);
    @Autowired
    private FileBrowserContext fileBrowserContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (fileBrowserContext.isFileBrowserCacheEmpty()) {
            logger.error("Triggering file browser cache.");
            fileBrowserContext.triggerFileBrowserCache();
            logger.error("Triggering file browser cache return.");
        }

    }*/
}

package com.helicalinsight.efw.framework;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.FileBrowserContext;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Rajesh
 *         Created by helical019 on 2/19/2019.
 */
@WebListener
public class ApplicationCacheStartStopListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationCacheStartStopListener.class);
    private static final ApplicationProperties INSTANCE = ApplicationProperties.getInstance();
    private ApplicationWatcherUtils watcherUtils = new ApplicationWatcherUtils(Paths.get(INSTANCE.getSolutionDirectory()));

    @Autowired
    private FileBrowserContext fileBrowserContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (JsonUtils.isFileBrowserCacheEnabled()) {
            AutowireCapableBeanFactory autowireCapableBeanFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext()).getAutowireCapableBeanFactory();
            autowireCapableBeanFactory.autowireBean(this);

            if (fileBrowserContext.isFileBrowserCacheEmpty()) {
                logger.debug("Triggering file browser cache.");
                fileBrowserContext.triggerFileBrowserCache();
                logger.debug("Triggering file browser cache return.");
            }

            //Watcher api for FileBrowser in async way(watching folder tree)
            watcherUtils.watch();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (JsonUtils.isFileBrowserCacheEnabled()) {
            logger.debug("Cache Stop Listener invoked.");
            FileBrowserContext bean = ApplicationContextAccessor.getBean(FileBrowserContext.class);
            try {
                bean.shutdownExecutor();
                watcherUtils.stopWatching();

            } catch (IOException ignore) {
                //Don't worry
            }
        }

    }
}

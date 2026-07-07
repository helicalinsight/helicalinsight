package com.helicalinsight.efw.framework;

/**
 * Created by user on 12/23/2016.
 *
 * @author Rajasekhar
 */

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.HIManagedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.io.IOException;

public class BootTimeListener implements ServletContextListener {

    private DirectoryWatcher directoryWatcher = new DirectoryWatcher();

    // Public constructor is required by servlet spec
    public BootTimeListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        //No need of this logger for the life time of this web app :)
        Logger logger = LoggerFactory.getLogger(BootTimeListener.class);

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        if (applicationProperties.isReadPluginsBootTime()) {
            //First time
            RepositoryLoader repositoryLoader = new RepositoryLoader();
            repositoryLoader.load();
            //Subsequent dynamic changes
            HIManagedThread watcher = new HIManagedThread(this.directoryWatcher);
            watcher.start();
        } else {
            logger.info("Boot time loading of plugins is disabled.");
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is un deployed or
         Application Server shuts down.
      */
        try {
            this.directoryWatcher.watcher.close();
        } catch (IOException ignore) {
            //Don't worry
        }
    }
}
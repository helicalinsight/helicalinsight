/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.framework;

/**
 * Created by user on 12/23/2016.
 *
 * @author Rajasekhar
 */

import com.helicalinsight.efw.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
            Thread watcher = new Thread(this.directoryWatcher);
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
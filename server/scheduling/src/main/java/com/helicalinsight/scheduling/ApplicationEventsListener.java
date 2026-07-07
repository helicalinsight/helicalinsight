package com.helicalinsight.scheduling;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;

/**
 * Created by author on 29-Nov-14.
 * @author Rajasekhar
 * ApplicationEventsListener implements {@link ServletContextListener}
 * receives notification events about ServletContext lifecycle changes.
 * 
 */
@SuppressWarnings("UnusedDeclaration")
public class ApplicationEventsListener implements ServletContextListener {

    //Sets the jboss logging choose the logging backend as log4j; setting slf4j requires logback
    //as the logging backend
    static {
        System.setProperty("org.jboss.logging.provider", "log4j");
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventsListener.class);

    // Public constructor is required by servlet spec
    public ApplicationEventsListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed).
         You can initialize servlet context related data here.
      */
        //runs when the main class is loaded
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context
         (the Web application) is undeployed or
         Application Server shuts down.
      */
        try {
            //Do Not Wait for Executing Jobs to Finish shutdown immediately
            SchedulerUtility.getInstance().shutdown(false);
        } catch (SchedulerException ex) {
            logger.error("Error while stopping Quartz", ex);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Calling Garbage Collector to prevent memory leak");
        }
        System.gc();
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }
}

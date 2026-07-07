package com.helicalinsight.externalauth.filter;

import com.helicalinsight.efw.utility.ConfigurationFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;


/**
 * DestroyRequest class implements {@link ServletRequestListener}
 * responsible to log out the user.
 */
public class DestroyRequest implements ServletRequestListener {
    private static final Logger logger = LoggerFactory.getLogger(DestroyRequest.class);
    Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");

    /**
     * requestDestroyed(ServletRequestEvent e)
     * this method is responsible for logging out .
     * @param ServletRequestEvent         used to get client request object
     */
    public void requestDestroyed(ServletRequestEvent e) {
        HttpServletRequest request = (HttpServletRequest) e.getServletRequest();
        String HEADER_STRING = mapFromClasspathPropertiesFile.get("header_string");
        String token = request.getParameter(HEADER_STRING);
        String tokenHeader = request.getHeader(HEADER_STRING);
        if (token != null || tokenHeader != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                logger.info("Request Logged out: ");
            }
        }
    }


    public void requestInitialized(ServletRequestEvent e) {
    }
}

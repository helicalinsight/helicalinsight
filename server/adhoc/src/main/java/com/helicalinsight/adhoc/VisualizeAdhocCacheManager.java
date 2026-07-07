package com.helicalinsight.adhoc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Cache manager for serving cached content in the context of Visualize Adhoc.
 * Extends the {@link AdhocCacheManager} class.
 * 
 * This class is responsible for serving cached content by forwarding the request to the Visualize page.
 * @author Somen (created on 12/01/2016)
 */
@Component
@Scope("prototype")
public class VisualizeAdhocCacheManager extends AdhocCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(VisualizeAdhocCacheManager.class);

    /**
     * Serves the cached content by forwarding the request to the Visualize page.
     *
     * @param request       	 HttpServletRequest object.
     * @param response      	 HttpServletResponse object.
     * @param rawObject     	 raw object containing cached content.
     * @return boolean  {@code true} if the content is served successfully.
     */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object rawObject) {
        try {
            JsonObject fileContent = (JsonObject) rawObject;
            request.setAttribute("cachedContent", fileContent.toString());
            request.getRequestDispatcher("/visualizeAdhoc.html").forward(request, response);
        } catch (IOException ioe) {
            logger.error("Error ", ioe);
        } catch (ServletException se) {
            logger.error("ServletException ", se);
        }
        return true;
    }
}

package com.helicalinsight.resourcesecurity.filter;

import com.google.gson.JsonArray;
import com.helicalinsight.admin.management.URLContextManager;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.filters.FilterUtils;
import com.helicalinsight.resourcedb.processor.iresource.IResourceAuthenticatorDB;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by author on 30-07-2015.
 *
 * @author Rajasekhar
 */
@Component("resourceAuthenticationFilter")
public class ResourceAuthenticationAndAuthorizationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAuthenticationAndAuthorizationFilter.class);

    private JsonArray urlExcludePatterns;

    private List<Context> configuredContexts;

    @Autowired
    private IResourceAuthenticatorDB authenticator;
    
    @Autowired
    private URLContextManager urlContextManagr;

    /**
     * Initializes the URL exclude patterns and the urlContexts
     *
     * @param filterConfig see jakarta.servlet.FilterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	this.urlExcludePatterns = FilterUtils.newGetExcludeUlrPattern();
        this.configuredContexts = urlContextManagr.refreshUrlContext();
    }




   

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestedUrl = request.getServletPath();


        if (logger.isInfoEnabled()) {
            logger.info("The url requested is " + requestedUrl);
        }

        /********EXCLUDE THE PATTERN FOR .js, .css,.jpeg,.png etc********************/
        if (FilterUtils.newIsExcludePattern(urlExcludePatterns, requestedUrl)) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("AuthorizationFilter message: The url %s is " + "excluded.", requestedUrl));
            }
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        /*****************************************************************************/

        if (this.configuredContexts == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Context context =  urlContextManagr.findUrlContext(requestedUrl);
        if (context == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }


        if (this.authenticator.authenticate(request, context)) {
            chain.doFilter(request, response);
        } else {
            //Deny request.
            ControllerUtils.accessDenied(request, response, "Access Denied. You don't have sufficient privileges to access  the requested resource.");
        }
    }

    @Override
    public void destroy() {
    }
}

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

package com.helicalinsight.resourcesecurity.filter;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.filters.FilterUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.IResourceAuthenticator;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import com.helicalinsight.resourcesecurity.jaxb.UrlContexts;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

    private JSONArray urlExcludePatterns;

    private List<Context> configuredContexts;

    @Autowired
    private IResourceAuthenticator authenticator;

    /**
     * Initializes the URL exclude patterns and the urlContexts
     *
     * @param filterConfig see javax.servlet.FilterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();

        this.urlExcludePatterns = FilterUtils.getExcludeUlrPattern();

        String pathname = systemDirectory + File.separator + "Admin" + File.separator +
                "urlContexts.xml";
        File contextsXml = new File(pathname);

        if (contextsXml.exists()) {
            UrlContexts urlContexts = JaxbUtils.unMarshal(UrlContexts.class, contextsXml);
            if (urlContexts != null) {
                this.configuredContexts = urlContexts.getContexts();
            }
        }
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
        if (FilterUtils.isExcludePattern(urlExcludePatterns, requestedUrl)) {
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

        Context context = findUrlContext(requestedUrl);
        if (context == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }


        if (this.authenticator.authenticate(request, context)) {
            chain.doFilter(request, response);
        } else {
            //Deny request.
            ControllerUtils.accessDenied(request, response);
        }
    }

    private Context findUrlContext(String requestedUrl) {
        Context presentContext = null;
        for (Context context : this.configuredContexts) {
            if (requestedUrl.contains(context.getName())) {
                presentContext = context;
                break;
            }
        }
        return presentContext;
    }

    @Override
    public void destroy() {
    }
}

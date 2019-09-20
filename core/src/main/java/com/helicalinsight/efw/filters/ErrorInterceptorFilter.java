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

package com.helicalinsight.efw.filters;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * The filter class is useful to log the exceptions to log files for debugging
 * purposes.
 * <p/>
 * Any uncaught exception or runtime exception or error will be logged to the
 * log files for debugging purposes preserving the original exception
 * stacktrace.
 * <p/>
 * The original request and response will be preserved and forwarded to the
 * custom error page
 * <p/>
 * Created by author on 31-10-2014.
 *
 * @author Rajasekhar
 */

public class ErrorInterceptorFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ErrorInterceptorFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Logs the stack trace to the log files with original exception that has
     * not been handled and also forwards the request to the errorPage.
     *
     * @param request  The request object
     * @param response The response object
     * @param chain    The filterChain object
     * @throws IOException      The java.io.IOException
     * @throws ServletException The javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            logger.info(String.format("The current request thread is %s.", Thread.currentThread()));
            chain.doFilter(request, response);
        } catch (Throwable exception) {
            logger.error("There was an exception ", exception);
            String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
            request.setAttribute("errorMessage", rootCauseMessage);
            if (exception instanceof RuntimeException) {
                logger.error("A Runtime Exception has occurred. The cause is " + rootCauseMessage, exception);
                throw new RuntimeException();
            }
            if (exception instanceof Exception) {
                logger.error("An Exception has occurred. The cause is " + rootCauseMessage, exception);
            } else {
                logger.error("An Error has occurred. The cause is " + rootCauseMessage, exception);
            }
            throw new RuntimeException();
        }
    }

    @Override
    public void destroy() {
    }
}
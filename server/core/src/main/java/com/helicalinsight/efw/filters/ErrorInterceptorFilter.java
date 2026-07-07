package com.helicalinsight.efw.filters;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * @since 1.2
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
     * @throws ServletException The jakarta.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            logger.info(String.format("The current request thread is %s.", Thread.currentThread()));
            chain.doFilter(request, response);
        } catch (Throwable exception) {
            boolean ajax = ControllerUtils.isAjax((HttpServletRequest) request);

            logger.error("There was an exception ", exception);
            String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
            request.setAttribute("errorMessage", rootCauseMessage);
            if (exception instanceof RuntimeException) {
                logger.error("A Runtime Exception has occurred. The cause is " + rootCauseMessage, exception);
            }
            if (exception instanceof Exception) {
                logger.error("An Exception has occurred. The cause is " + rootCauseMessage, exception);
                if(ajax){
                    ControllerUtils.handleFailure((HttpServletResponse)response,ajax,(Exception)exception);
                    return;
                }
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
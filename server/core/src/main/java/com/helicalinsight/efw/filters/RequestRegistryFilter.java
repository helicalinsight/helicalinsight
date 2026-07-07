package com.helicalinsight.efw.filters;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.core.request.RequestContext;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 16-06-2015
 *
 * @author Somen
 */
public class RequestRegistryFilter implements Filter {

    public final static Logger logger = LoggerFactory.getLogger(RequestRegistryFilter.class);

    public static Map<String,Thread> requestRegister  = new ConcurrentHashMap<>();
    public static Set<String> cancelledRequests = ConcurrentHashMap.newKeySet();
    public RequestRegistryFilter() {
    }

    public void destroy() {
    	requestRegister.clear();
    	cancelledRequests.clear();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestId = httpRequest.getParameter("requestId");
        
        if (StringUtils.isBlank(requestId)) {
        	logger.warn("Request ID was not provided. A temporary request ID has been generated.");
        	requestId = UUID.randomUUID().toString();
        	request.setAttribute("requestId", requestId);
        }
        
        
        logger.debug("HTTP REQUEST ARRIVED: {} thread={}", requestId, Thread.currentThread().getName());
        
        String servletPath = httpRequest.getServletPath();
        boolean isCancelRequest = servletPath != null && servletPath.contains("/cancelRequest");
        if(!isCancelRequest){
            Thread thread = Thread.currentThread();
            RequestContext.set(requestId);
            requestRegister.put(requestId,thread);
        }
        if(isCancelRequest) {
        	logger.debug("Cancellation requested for RequestID : {}",requestId);
        	cancelledRequests.add(requestId);
        }
        
		try {
			chain.doFilter(request, response);
		} finally {
			if (StringUtils.isNotBlank(requestId)) {
				if (!isCancelRequest) {
					logger.debug("Clearing registry filter");
					RequestContext.clear();
					requestRegister.remove(requestId);
					if (cancelledRequests.contains(requestId)) {
						cancelledRequests.remove(requestId);
					}
				}
			}
		}
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }
}

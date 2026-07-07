package com.helicalinsight.cache.filter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * filter layer is an intermediate layer for cache object which extends Filter
 * The {@code @Component("cacheFilter")} annotation is used to mark a class as a Spring bean/component and bean name is cacheFilter.
 */
@Component("cacheFilter")
public class CacheFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CacheFilter.class);


    @Autowired
    private CacheHelper cacheHelper;

    public CacheFilter() {
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    /**
     * doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     * 
     * it checks the request is belongs to the cache object or not
     * @param request 			provides cache object
     * @param response 			response sets the content type
     * @param chain 			The filter chain takes the request and response objects as parameters
     *                           because its purpose is to process these objects as they flow through the chain of filters
     * @throws IOException 		If an I/O error occurs.
     * @throws ServletException If a servlet exception occurs.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest httpRequest = ((HttpServletRequest) request);

        /*IF CACHE IS ENABLED THEN THE CACHE FILTER WILL START CACHING THE REQUESTS*/
        if (!CacheUtils.isCacheEnabled()) {
            logger.info("Cache layer is disabled. Forwarding request.");
            chain.doFilter(request, response);
            return;
        }


        String url = httpRequest.getServletPath();
        logger.info("The current request is {} in cache", url);

        CacheManager cacheManager;

        Boolean setUnsetUrl = false;
        Boolean cacheRequired = false;
        try {
            HttpSession session = setThreadName(httpRequest);
            if (session == null) {
                chain.doFilter(request, response);
                return;
            }

            logger.info("This current Thread is " + Thread.currentThread().getName());
            logger.info("This current Thread id " + Thread.currentThread().getId());

            JsonArray refreshUrl = CacheUtils.getRefreshUrl();
            boolean isPresent = false;
            for (JsonElement element : refreshUrl) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();
                    if (primitive.isString() && primitive.getAsString().equals(url)) {
                    	isPresent = true;
                    }
                }
            }
            if (isPresent) {
                setUnsetRefresh(request, session);
                setUnsetUrl = true;
            } else {
                cacheManager = CacheUtils.getCacheManager(url);
                String data = null;

                if (url.contains("/services") || url.contains("/visualizeAdhoc")) {
                    if (!setServiceFormData(httpRequest, cacheManager, url)) {
                        chain.doFilter(request, response);
                        return;
                    }
                } else {
                    data = request.getParameter("data");
                    if (data == null) {
                        chain.doFilter(request, response);
                        return;
                    }
                    cacheManager.setRequestData(data);
                }

                Boolean refresh = false;
                String refreshParam = (String) session.getAttribute("refresh");
                String reportName = (String) session.getAttribute("requestedReport");

                if (refreshParam != null) {

                    refresh = "true".equals(refreshParam);
                }
                if(data!=null){
                    JsonObject dataJson  = new Gson().fromJson(data,JsonObject.class);
                    if(dataJson.has("hi_refresh")){
                        refresh = GsonUtility.optBoolean(dataJson,"hi_refresh");
                    }
                    if(dataJson.has("hi_requestedReport")){
                        reportName = GsonUtility.optString(dataJson,"hi_requestedReport");
                    }
                }
                Cache requestCache = cacheHelper.prepareCacheFromRequest(cacheManager);
                cacheRequired = cacheHelper.processCache(request, response, reportName, refresh, requestCache, cacheManager);
            }
        } catch (Exception exception) {
            logger.error("Exception occurred at cache filter ", exception);
            if (exception instanceof AccessDeniedException) {
                ControllerUtils.accessDenied((HttpServletRequest) request, (HttpServletResponse) response, exception.getMessage());
            } else {
                throw exception;
            }
            return;
        }

        if (!cacheRequired) {

            logger.info("Cache filter set/unset flag set to true ");
            chain.doFilter(request, response);
        } else {
            logger.info("Returning from cache without any result");
            return;
        }
    }
   	/**
	 * setThreadName(HttpServletRequest httpRequest)
	 * This method sets the thread name and returns the session object.
	 * method gets the session object from the httpRequest object.
	 * 
	 * 
	 * @param httpRequest 		provides query details
	 * @return HttpSession		returns session object from htttpRequest Object
	 */
	private HttpSession setThreadName(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        String uuid = UUID.randomUUID().toString();
        String queryId = httpRequest.getParameter("queryId");
        Boolean cancelQuery = Boolean.valueOf(httpRequest.getParameter("cancelQuery"));
        if (queryId != null) {
            if (!cancelQuery) {
                Long queryCount = (Long) session.getAttribute("queryCount");

                queryCount = queryCount == null ? 0l : ++queryCount;
                session.setAttribute("queryCount", queryCount);
                String threadName = uuid + "##" + queryCount + "##" + queryId;

                Thread.currentThread().setName(threadName);
                registry.registerQueryIdThreadName(queryId, threadName);
                logger.info("Thread Name is registered for cache" + threadName);
            }
        }
        return session;
    }
	/**
	 * setUnsetRefresh(ServletRequest request, HttpSession session)
	 * it checks request is refreshed or not
	 * @param request 			provides cache object
	 * @param session 			sets the content dir,file from cache object
	 */
    private void setUnsetRefresh(ServletRequest request, HttpSession session) {
        String reportName;
        String dir;
        String refreshParam = request.getParameter("refresh");
        reportName = request.getParameter("file");
        dir = request.getParameter("dir");
        if (reportName != null && dir != null) {
            session.setAttribute("requestedReport", dir +"/" + reportName);
            request.setAttribute("requestedReport", dir + "/" + reportName);
        } else {
            session.removeAttribute("requestedReport");
        }
        if (refreshParam != null) {
            session.setAttribute("refresh", "true");
            request.setAttribute("refresh", "true");
        } else {
            session.removeAttribute("refresh");
            request.setAttribute("removeRefresh","true");
        }
    }
    /**
     * setServiceFormData(HttpServletRequest request, CacheManager cacheManager, String url)
     * 
     * it generates FormData for service calls
     * @param request 			formData
     * @param cacheManager 		to set formData
     * @param url 				visualizeUrl of adhoc
     * {@return true if xml data matches with adhoc data}{@code false} if not match		
     */
    private boolean setServiceFormData(HttpServletRequest request, CacheManager cacheManager, String url) {
        String formData;
        String type = request.getParameter("type");
        String serviceType = request.getParameter("serviceType");
        String service = request.getParameter("service");
        String adhoc = "adhoc";
        String visualizeAdhoc = "visualizeAdhoc";
        String report = "report";

         JsonObject cacheXmlJsonObject = CacheUtils.getCacheXmlJson();
         JsonObject adhocManager = GsonUtility.optJsonObject(cacheXmlJsonObject,"adhocManager");
        if (adhocManager != null) {
            adhoc = adhocManager.get("type").getAsString();
            visualizeAdhoc = adhocManager.get("visualizeUrl").getAsString();
            report = adhocManager.get("serviceType").getAsString();
        }
        if ((adhoc.equalsIgnoreCase(type) && report.equalsIgnoreCase(serviceType)) || (url.toLowerCase().contains
                (visualizeAdhoc.toLowerCase()))) {
            formData = request.getParameter("formData");
            if (formData != null && Base64.isBase64(formData)) {
                formData = new String(Base64.decodeBase64(formData));
            }
            if (formData != null) {
                JsonObject formDataJson = new Gson().fromJson(formData.substring(0, formData.lastIndexOf("}") + 1),JsonObject.class);
                String refresh = GsonUtility.optString(formDataJson,"refresh");
                HttpSession session = request.getSession(false);
                String reportName = GsonUtility.optString(formDataJson,"metadataFileName");
                
                String dir = GsonUtility.optString(formDataJson,"location");
                if (reportName.isEmpty()) {
                    reportName = GsonUtility.optString(formDataJson,"uniqueId");
                }
                if (!reportName.isEmpty()) {
                    session.setAttribute("requestedReport", dir + "/" + reportName);
                } else {

                    session.removeAttribute("requestedReport");
                }
                if ("true".equalsIgnoreCase(refresh)) {
                    session.setAttribute("refresh", "true");
                } else {
                    session.removeAttribute("refresh");
                }
                String fetchData = "fetchData";
                if ((fetchData.equalsIgnoreCase(service) && adhoc.equalsIgnoreCase(type) && report.equalsIgnoreCase
                        (serviceType)) || (url.contains(visualizeAdhoc))) {
                    formDataJson.addProperty("requestType", adhoc);
                    formDataJson.addProperty("serviceType", report);
                    formDataJson.addProperty("service", fetchData);
                    cacheManager.setRequestData(formDataJson.toString());
                    return true;
                }
            }
        }
        return false;
    }

   
	public void destroy() {
    }
}

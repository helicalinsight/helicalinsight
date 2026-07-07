package com.helicalinsight.efw.filters;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ResponseMetadataEnricher;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;

/**
 * Injects product metadata into JSON API responses after the request is handled.
 */
@Component("responseMetadataFilter")
public class ResponseMetadataFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseMetadataFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();
        if (!ControllerUtils.isAjax(httpRequest) || (path != null && isStaticAsset(path))) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        chain.doFilter(request, wrappedResponse);

        if (shouldEnrich(wrappedResponse, httpRequest)) {
            try {
                byte[] body = wrappedResponse.getContentAsByteArray();
                byte[] enriched = ResponseMetadataEnricher.enrichJsonBytes(body);
                if (!Arrays.equals(body, enriched)) {
                    wrappedResponse.resetBuffer();
                    wrappedResponse.getOutputStream().write(enriched);
                }
            } catch (Throwable throwable) {
                logger.warn("Skipping response metadata enrichment due to an error", throwable);
            }
        }

        wrappedResponse.copyBodyToResponse();
    }

    private boolean shouldEnrich(ContentCachingResponseWrapper response, HttpServletRequest request) {
        if (!ControllerUtils.isAjax(request)) {
            return false;
        }

        if (response.isCommitted()) {
            return false;
        }

        int status = response.getStatus();
        if (status < 200 || status >= 300) {
            return false;
        }

        String contentEncoding = response.getHeader("Content-Encoding");
        if (contentEncoding != null && contentEncoding.toLowerCase().contains("gzip")) {
            return false;
        }

        String contentType = response.getContentType();
        if (contentType == null || !isEnrichableContentType(contentType)) {
            return false;
        }

        String path = request.getServletPath();
        if (path != null && isStaticAsset(path)) {
            return false;
        }

        return true;
    }

    private boolean isEnrichableContentType(String contentType) {
        String lowerContentType = contentType.toLowerCase();
        return lowerContentType.startsWith("application/json")
                || lowerContentType.startsWith("text/plain");
    }

    private boolean isStaticAsset(String path) {
        String lowerPath = path.toLowerCase();
        return lowerPath.startsWith("/js/")
                || lowerPath.startsWith("/css/")
                || lowerPath.startsWith("/images/")
                || lowerPath.endsWith(".js")
                || lowerPath.endsWith(".css")
                || lowerPath.endsWith(".html")
                || lowerPath.endsWith(".woff")
                || lowerPath.endsWith(".woff2");
    }
}

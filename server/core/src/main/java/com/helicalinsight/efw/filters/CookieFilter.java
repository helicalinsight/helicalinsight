package com.helicalinsight.efw.filters;

import com.helicalinsight.efw.utility.PropertiesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.*;

/**
 * Created on 22/09/2020
 *
 * @author Somen
 */
public class CookieFilter implements Filter {

    public final static Logger logger = LoggerFactory.getLogger(CookieFilter.class);
    private Map<String, String> properties = null;

    public CookieFilter() {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {


        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (!httpRequest.isSecure()) {
            chain.doFilter(request, response);
            return;
        }
        String excludeHeaders = properties.get("excludeHeaders");
        List<String> excludeList = new ArrayList<>();
        if (excludeHeaders != null) {
            String[] split = excludeHeaders.split(",");
            excludeList = Arrays.asList(split);
        }

        Collection<String> headers = httpResponse.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        String newCookies = properties.get("addCookie");
        if (newCookies != null) {
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, newCookies);
            firstHeader = false;
        }
        for (String header : headers) {
            String appendInAll = properties.get("appendInAll");
            String appendInAllFormat = properties.get("appendInAllFormat");
            if (excludeList.contains(header)) {
                continue;
            }
            if (firstHeader) {
                httpResponse.setHeader(HttpHeaders.SET_COOKIE, String.format(appendInAllFormat, header, appendInAll));
                firstHeader = false;
                continue;
            }
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, String.format(appendInAllFormat, header, appendInAll));
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        properties = propertiesFileReader.read("cookies.properties");

    }
}

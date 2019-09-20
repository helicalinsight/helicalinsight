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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**

 *
 * @author Somen
 */
public class SessionTimeOutFilter implements Filter {

    public final static Logger logger = LoggerFactory.getLogger(SessionTimeOutFilter.class);

    public SessionTimeOutFilter() {
    }

    public void destroy() {
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        String clientTime = httpRequest.getHeader("currentTime");
        String cookiePath = httpRequest.getContextPath();

        if (session != null) {
            long currentTime = System.currentTimeMillis();
            int maxInactiveInterval = session.getMaxInactiveInterval();
            long expiryTime = currentTime + maxInactiveInterval * 1000;
            Cookie cookie = new Cookie("serverTime", "" + currentTime);
            cookie.setPath(cookiePath);
            httpResponse.addCookie(cookie);
            if (httpRequest.getRemoteUser() != null) {
                cookie = new Cookie("sessionExpiry", "" + expiryTime);
            } else {
                cookie = new Cookie("sessionExpiry", "" + currentTime);
            }
            cookie.setPath(cookiePath);
            httpResponse.addCookie(cookie);
            Cookie currentTimeCookie = new Cookie("currentTime", clientTime);
            currentTimeCookie.setPath(cookiePath);
            httpResponse.addCookie(currentTimeCookie);
            //httpResponse.setHeader("access-control-allow-origin", "*");
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }
}

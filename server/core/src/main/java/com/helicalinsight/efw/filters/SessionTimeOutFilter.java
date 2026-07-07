package com.helicalinsight.efw.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created on 16-06-2015
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
     * @see jakarta.servlet.Filter#doFilter(jakarta.servlet.ServletRequest,
     * jakarta.servlet.ServletResponse,
     * jakarta.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        String clientTime = httpRequest.getHeader("currentTime");
        String cookiePath = httpRequest.getContextPath();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (session != null) {
            long currentTime = System.currentTimeMillis();
            int maxInactiveInterval = session.getMaxInactiveInterval();
            long expiryTime = currentTime + maxInactiveInterval * 1000;
            Cookie cookie = new Cookie("serverTime", "" + currentTime);
            cookie.setPath(cookiePath);
            httpResponse.addCookie(cookie);
            Object jwt = request.getAttribute("jwt");
            if (authentication!=null && !(authentication.getPrincipal() instanceof AnonymousAuthenticationToken) && httpRequest.getRemoteUser() != null && !"true".equals(jwt)) {
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

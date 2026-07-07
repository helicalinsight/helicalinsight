package com.helicalinsight.admin.filter;

import com.helicalinsight.admin.RestRedirectStrategy;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This is filter class which implements the servlet filter, Processes Request
 * Parameter from URL for authentication, putting the result into the
 * SecurityContextHolder If authentication is successful, the resulting
 * Authentication object will be placed into the SecurityContextHolder.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */
@SuppressWarnings("unused")
public class RequestParameterAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestParameterAuthenticationFilter.class);

    /**
     * Spring security AuthenticationManager object
     */

    private AuthenticationManager authenticationManager;

    /**
     * instance String variable for authentication failure url
     */

    private String authenticationFailureUrl;

    /**
     * String array for exclude Urls
     */
    private String[] excludeUrls;

    /**
     * servlet filter init method
     */

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * servlet filter destroy method
     */

    public void destroy() {
    }

    /**
     * servlet filter doFilter method which takes ServletRequest,
     * ServletResponse, FilterChain augments if HttpServletRequest is valid
     * request then take the user name and password parameters from request
     * object and set it in UsernamePasswordAuthenticationToken, create the
     * instance of PreUsernamePasswordAuthenticationFilter set the
     * authentication manager, http get request and call the obtainUsername
     * method for this request, which filter the parameter and pass this request
     * to user detail service which check for authentication.
     */

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (requiresAuthentication(httpRequest)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            PreAuthenticationFilter userAuthNOrganization = new PreAuthenticationFilter();
            userAuthNOrganization.setAuthenticationManager(authenticationManager);
            userAuthNOrganization.setPostOnly(false);

            Authentication authResult;

            try {
                authResult = userAuthNOrganization.attemptAuthentication(httpRequest, httpResponse);
            } catch (AuthenticationException e) {

                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to authenticate user " + username + ". The reason is " +
                            e.toString());
                }
                SecurityContextHolder.getContext().setAuthentication(null);
                HttpSession session = httpRequest.getSession(false);
                if (session == null) {
                    session = httpRequest.getSession();
                }
                session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, e);
                httpResponse.sendRedirect(httpResponse.encodeRedirectURL(getFullFailureUrl(httpRequest)));
                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("User " + username + " authentication result is " + authResult);
            }

            SecurityContextHolder.getContext().setAuthentication(authResult);
            if (ControllerUtils.isAjax((HttpServletRequest) request)) {
                RestRedirectStrategy.sendRedirect((HttpServletRequest) request, (HttpServletResponse) response);
                return;
            }
        }


        chain.doFilter(request, response);
    }

    /**
     * this method takes the http servlet request and return the boolean value
     * depend upon user authentication
     *
     * @return boolean true/false
     */

    protected boolean requiresAuthentication(HttpServletRequest request) {
        boolean authenticate;
        String username = request.getParameter("username");
        if (username == null) {
            authenticate = false;
        } else {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            //noinspection SimplifiableIfStatement
            if (existingAuth != null && existingAuth.isAuthenticated() && existingAuth.getName().equals(username)) {
                authenticate = false;
            } else {
                authenticate = !isUrlExcluded(request);
            }
        }
        return authenticate;
    }

    /**
     * getter for full failure URL and return the URL
     *
     * @return URL
     */

    protected String getFullFailureUrl(HttpServletRequest request) {
        return request.getContextPath() + authenticationFailureUrl;
    }

    /**
     * this method check for valid URL
     *
     * @return boolean value true/false
     */
    protected boolean isUrlExcluded(HttpServletRequest request) {
        if (excludeUrls == null || excludeUrls.length == 0) {
            return false;
        }

        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');
        if (pathParamIndex > 0) {
            uri = uri.substring(0, pathParamIndex);
        }

        for (String excludedUrl : excludeUrls) {
            if (uri.endsWith(request.getContextPath() + excludedUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * getter for getAuthenticationManager object
     *
     * @return getAuthenticationManager object
     */

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    /**
     * setter for getAuthenticationManager object
     */

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * getter for authentication failure URL
     *
     * @return authenticationFailureUrl
     */

    public String getAuthenticationFailureUrl() {
        return authenticationFailureUrl;
    }

    /**
     * setter for authentication failure URL
     *
     * @param authenticationFailureUrl Url
     */

    public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
        this.authenticationFailureUrl = authenticationFailureUrl;
    }

    /**
     * getter for ExcludeUrls array
     *
     * @return ExcludeUrls array
     */

    public String[] getExcludeUrls() {
        return excludeUrls;
    }

    /**
     * setter for ExcludeUrls array
     */

    public void setExcludeUrls(String[] excludeUrls) {
        this.excludeUrls = excludeUrls;
    }
}
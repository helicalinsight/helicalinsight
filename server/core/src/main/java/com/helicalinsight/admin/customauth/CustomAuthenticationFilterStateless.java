package com.helicalinsight.admin.customauth;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CustomAuthenticationFilterStateless extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilterStateless.class);
    Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");
    private String authenticationFailureUrl;
    /**
     * String array for exclude Urls
     */
    private String[] excludeUrls;
    private List<String> ignoreUrls;

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (ignoreUrls == null || ignoreUrls.isEmpty()) {
            return false;
        }
        String path = request.getServletPath();
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : ignoreUrls) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Request URI = " + request.getRequestURI());
        }

        String token = isAuthentication(request);
        boolean authenticatedByToken = false;

        if (token != null) {
            try {
                CustomUserDetailService cu = ApplicationContextAccessor.getBean(CustomUserDetailService.class);
                cu.processToken(request, token);
                String userName = (String) request.getAttribute("userName");
                UserDetails userDetails = cu.getUserService().loadUserByUsername(userName);
                UsernamePasswordAuthenticationToken authRequest =
                        new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authRequest);
                authenticatedByToken = true;
                request.setAttribute("jwt", Boolean.TRUE);
                if (logger.isDebugEnabled()) {
                    logger.debug("Stateless token authentication successful for user: " + userName);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                writeAuthFailure(request, response, e);
                return;
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            if (authenticatedByToken) {
                SecurityContextHolder.clearContext();
            }
        }
    }

    /**
     * Stateless auth failure response. For AJAX/REST callers we return a 401
     * with a JSON body; for browser flows we redirect to the configured
     * failure URL. We never create a new {@link HttpSession} here -- the
     * filter is meant to be stateless.
     */
    private void writeAuthFailure(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws IOException {
        String message = e.getMessage() == null ? "" : e.getMessage();
        if (ControllerUtils.isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", ControllerUtils.defaultContentType());
            PrintWriter out = response.getWriter();
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "0");
            responseJson.put("message", "Login Failed." + message);
            out.print(responseJson);
            out.flush();
            return;
        }
        HttpSession session = request.getSession(false);
        if (session != null && e instanceof AuthenticationException) {
            session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, e);
        }
        response.sendRedirect(response.encodeRedirectURL(getFullFailureUrl(request)));
    }

    private String getToken(HttpServletRequest req) {
        String jwtCookie = mapFromClasspathPropertiesFile.get("custom_sso_cookie");
        String headerType = mapFromClasspathPropertiesFile.get("header_string_type");
        String expectedType = mapFromClasspathPropertiesFile.get("header_string_type_sso");
        String authToken = mapFromClasspathPropertiesFile.get("header_string_authToken");
        String header = req.getHeader(authToken);
        String headerTypeValue = req.getHeader(headerType);
        if(!expectedType.equals(headerTypeValue)){
            return null;
        }

        boolean cookieTrue = "true".equalsIgnoreCase(jwtCookie);
        if (cookieTrue) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(authToken)) {
                        header = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (header == null) {
            header = req.getParameter(authToken);
        }
        return header;

    }

    protected String isAuthentication(HttpServletRequest request) {
        String token = getToken(request);
        //logger.error("Token obtained "+token);
        if (token == null) {
            return null;
        } else {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if ((existingAuth != null) && (existingAuth.isAuthenticated())) {
                return null;
            } else {
                if (isUrlExcluded(request)) return null;
            }
        }
        return token;
    }

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

    protected String getFullFailureUrl(HttpServletRequest request) {
        return request.getContextPath() + authenticationFailureUrl;
    }

    public String[] getExcludeUrls() {
        return excludeUrls;
    }

    /**
     * setter for ExcludeUrls array
     */

    public void setExcludeUrls(String[] excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public String getAuthenticationFailureUrl() {
        return authenticationFailureUrl;
    }

    public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
        this.authenticationFailureUrl = authenticationFailureUrl;
    }
}

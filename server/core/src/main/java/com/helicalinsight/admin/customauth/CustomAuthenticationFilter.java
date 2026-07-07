package com.helicalinsight.admin.customauth;

import com.helicalinsight.admin.RestRedirectStrategy;
import com.helicalinsight.admin.filter.PreAuthenticationFilter;
import com.helicalinsight.admin.filter.RequestParameterAuthenticationFilter;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomAuthenticationFilter
        extends PreAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
    private String authenticationFailureUrl;

    /**
     * String array for exclude Urls
     */
    private String[] excludeUrls;

    /**
     * Local reference to the {@link SecurityContextRepository} so that the
     * authenticated context can be persisted from
     * {@link #successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}
     * without going through the configured success handler (which would
     * short-circuit the request with a "Login success" JSON response).
     */
    private SecurityContextRepository contextRepository;

    @Override
    public void setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        super.setSecurityContextRepository(securityContextRepository);
        this.contextRepository = securityContextRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException {

        logger.info("Request Data = " + request);

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        UsernamePasswordAuthenticationToken authRequest = null;
                CustomUserDetailService cu = ApplicationContextAccessor.getBean(CustomUserDetailService.class);
try {
    cu.processToken(httpRequest, request.getParameter("authToken"));
    String userName = (String)request.getAttribute("userName");
    logger.error("---------------------------(" + userName+")-----------");
    UserDetails userDetails = cu.getUserService().loadUserByUsername(userName);
    authRequest = new UsernamePasswordAuthenticationToken(userDetails, "",userDetails.getAuthorities());

    logger.debug("Helical set details method called!!!" + authRequest.isAuthenticated());
    return authRequest;
} catch (java.lang.Exception e) {
    throw new RuntimeException(e);
}

    }

    /**
     * This filter authenticates token-bearing requests targeting real
     * application endpoints (e.g. {@code /services}). It is NOT a login
     * endpoint, so after a successful authentication we must NOT delegate to
     * the configured {@code AuthenticationSuccessHandler} (which writes a
     * "Login success" JSON response and terminates the request). Instead, we
     * set the {@link SecurityContext}, persist it via the configured
     * {@link SecurityContextRepository}, and then continue the filter chain
     * so that the original request reaches its actual controller.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        if (this.contextRepository != null) {
            this.contextRepository.saveContext(context, request, response);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Authenticated via authToken; continuing filter chain to "
                    + request.getRequestURI() + " (skipping success handler).");
        }
        chain.doFilter(request, response);
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter("authToken");
        boolean authenticate;
        if (token == null) {
            authenticate = false;
        } else {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if ((existingAuth != null) && (existingAuth.isAuthenticated())) {
                authenticate = false;
            } else {
                authenticate = !isUrlExcluded(request);
            }
        }
        return authenticate;
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

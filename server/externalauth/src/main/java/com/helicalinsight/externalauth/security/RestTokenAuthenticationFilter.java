package com.helicalinsight.externalauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

/**
 * RestTokenAuthenticationFilter extends {@link GenericFilterBean}
 * This class RestTokenAuthenticationFilter serves as a custom filter for token-based authentication in a RESTful API.
 * It intercepts incoming requests, 
 * validates authentication tokens, and handles login and logout functionality.
 * Created by user on 3/08/2016.
 * @author Somen
 */

@Component("restAuthenticationFilter")
public class RestTokenAuthenticationFilter extends GenericFilterBean {

    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_PASSWORD = "X-Password";

    private static final String REQUEST_ATTR_DO_NOT_CONTINUE = "AuthenticationFilter-doNotContinue";

    private String logoutLink;
    @Autowired
    private RestAuthenticationService authenticationService;
    /**
     * Constructor a RestTokenAuthenticationFilter instance with the default logout link.
     */
    public RestTokenAuthenticationFilter() {
        this("/rest/logout.html");
    }
    /**
     * Constructor a RestTokenAuthenticationFilter instance with a specified logout link.
     * @param logoutLink The link for logging out.
     */
    public RestTokenAuthenticationFilter(String logoutLink) {
        this.logoutLink = logoutLink;
    }
    /**
     * doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     * @param request              used to check login,logout link ,token
     * @param response             used for checking login token
     * @param chain                filter chain takes the request and response objects to process these objects as they flow through the chain of filters.
     * @throws IOException         If an I/O error occurs during the filter chain.
     * @throws ServletException    If a servlet exception occurs during the filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        boolean authenticated = checkToken(httpRequest, httpResponse);

        if (canRequestProcessingContinue(httpRequest) && httpRequest.getMethod().equals("POST")) {
            // If we're not authenticated, we don't bother with logout at all.
            // Logout does not work in the same request with login - this does not make sense,
            // because logout works with token and login returns it.
            if (authenticated) {
                checkLogout(httpRequest);
            }

            // Login works just fine even when we provide token that is valid up to this request,
            // because then we get a new one.
            checkLogin(httpRequest, httpResponse);
        }

        if (canRequestProcessingContinue(httpRequest)) {
            chain.doFilter(request, response);
        }
    }

    /**
     * checkToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
     * Returns {@code true}, if request contains valid authentication token.otherwise returns {@code false}
     */
    private boolean checkToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String token = httpRequest.getHeader(HEADER_TOKEN);
        if (token == null) {
            return false;
        }

        if (authenticationService.checkToken(token)) {
            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return true;
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            doNotContinueWithRequestProcessing(httpRequest);
        }
        return false;
    }
    /**
     * canRequestProcessingContinue(HttpServletRequest httpRequest)
     * @param httpRequest   request for getting attribute
     * @return True if the request processing can continue, otherwise false.
     */
    private boolean canRequestProcessingContinue(HttpServletRequest httpRequest) {
        return httpRequest.getAttribute(REQUEST_ATTR_DO_NOT_CONTINUE) == null;
    }
    /**
     * checkLogout(HttpServletRequest httpRequest)
     * it checks the logout link with request link
     * @param httpRequest			request provides logout link
     */
    private void checkLogout(HttpServletRequest httpRequest) {
        if (currentLink(httpRequest).equals(logoutLink)) {
            String token = httpRequest.getHeader(HEADER_TOKEN);
            // we go here only authenticated, token must not be null
            authenticationService.logout(token);
            doNotContinueWithRequestProcessing(httpRequest);
        }
    }

    private void checkLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String authorization = httpRequest.getHeader("Authorization");
        String username = httpRequest.getHeader(HEADER_USERNAME);
        String password = httpRequest.getHeader(HEADER_PASSWORD);

        if (authorization != null) {
            checkBasicAuthorization(authorization, httpResponse);
            doNotContinueWithRequestProcessing(httpRequest);
        } else if (username != null && password != null) {
            checkUsernameAndPassword(username, password, httpResponse);
            doNotContinueWithRequestProcessing(httpRequest);
        }
    }
    /**
     * doNotContinueWithRequestProcessing(HttpServletRequest httpRequest)
     * @param httpRequest        request for setting attribute
     */
    private void doNotContinueWithRequestProcessing(HttpServletRequest httpRequest) {
        httpRequest.setAttribute(REQUEST_ATTR_DO_NOT_CONTINUE, "");
    }

    /**
     * currentLink(HttpServletRequest httpRequest)
     * @param httpRequest             request provides url link 
     * @return url in string format
     */
    // or use Springs util instead: new UrlPathHelper().getPathWithinApplication(httpRequest)
    // shame on Servlet API for not providing this without any hassle :-(
    private String currentLink(HttpServletRequest httpRequest) {
        if (httpRequest.getPathInfo() == null) {
            return httpRequest.getServletPath();
        }
        return httpRequest.getServletPath() + httpRequest.getPathInfo();
    }
    /**
     * checkBasicAuthorization(String authorization, HttpServletResponse httpResponse)
     * @param authorization           provides password and username
     * @param httpResponse            sets header content
     * @throws IOException            if io error occurs while fetching authorization
     */
    private void checkBasicAuthorization(String authorization, HttpServletResponse httpResponse) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(authorization);
        if (tokenizer.countTokens() < 2) {
            return;
        }
        if (!tokenizer.nextToken().equalsIgnoreCase("Basic")) {
            return;
        }

        String base64 = tokenizer.nextToken();
        String loginPassword = new String(Base64.decode(base64.getBytes(StandardCharsets.UTF_8)));

        tokenizer = new StringTokenizer(loginPassword, ":");
        checkUsernameAndPassword(tokenizer.nextToken(), tokenizer.nextToken(), httpResponse);
    }
    /**
     * checkUsernameAndPassword(String username, String password, HttpServletResponse httpResponse)
     * @param username            username  for a authentication
     * @param password			  password  for a authentication
     * @param httpResponse        sets content
     * @throws IOException		  if io error occurs while doing authorization
     */
    private void checkUsernameAndPassword(String username, String password, HttpServletResponse httpResponse) throws
            IOException {
        TokenInformationProvider tokenInfo = authenticationService.authenticate(username, password);
        if (tokenInfo != null) {
            httpResponse.setHeader(HEADER_TOKEN, tokenInfo.getToken());
            // TODO set other token information possible: IP, ...
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

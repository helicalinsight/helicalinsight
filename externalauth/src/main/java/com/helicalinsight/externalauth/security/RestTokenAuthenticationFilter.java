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

package com.helicalinsight.externalauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;


@Component("restAuthenticationFilter")
public class RestTokenAuthenticationFilter extends GenericFilterBean {

    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_PASSWORD = "X-Password";

    private static final String REQUEST_ATTR_DO_NOT_CONTINUE = "AuthenticationFilter-doNotContinue";

    private String logoutLink;
    @Autowired
    private RestAuthenticationService authenticationService;

    public RestTokenAuthenticationFilter() {
        this("/rest/logout.html");
    }

    public RestTokenAuthenticationFilter(String logoutLink) {
        this.logoutLink = logoutLink;
    }

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
     * Returns true, if request contains valid authentication token.
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

    private boolean canRequestProcessingContinue(HttpServletRequest httpRequest) {
        return httpRequest.getAttribute(REQUEST_ATTR_DO_NOT_CONTINUE) == null;
    }

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

    private void doNotContinueWithRequestProcessing(HttpServletRequest httpRequest) {
        httpRequest.setAttribute(REQUEST_ATTR_DO_NOT_CONTINUE, "");
    }

    // or use Springs util instead: new UrlPathHelper().getPathWithinApplication(httpRequest)
    // shame on Servlet API for not providing this without any hassle :-(
    private String currentLink(HttpServletRequest httpRequest) {
        if (httpRequest.getPathInfo() == null) {
            return httpRequest.getServletPath();
        }
        return httpRequest.getServletPath() + httpRequest.getPathInfo();
    }

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

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

package com.helicalinsight.externalauth.cas;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by user on 2/10/2016.
 *
 * @author Somen
 */
public class HiCasFilter extends CasAuthenticationFilter {

    private static final String CAS_TICKET_PARAM_NAME = "ticket";
    private String usernameParameter = "j_username";
    private String passwordParameter = "j_password";

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException, IOException {
        String password = obtainTicket(request);
        if ((password != null) && (password.trim().length() > 0)) {
            String username = "_cas_stateful_";
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken
                    ("_cas_stateful_", password);
            authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

            return getAuthenticationManager().authenticate(authRequest);
        }
        String username = obtainUsername(request);
        password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username != null ?
                username.trim() : "", password != null ? password : "");


        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainTicket(HttpServletRequest request) {
        return request.getParameter("ticket");
    }

    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    String getUsernameParameter() {
        return this.usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    String getPasswordParameter() {
        return this.passwordParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }
}

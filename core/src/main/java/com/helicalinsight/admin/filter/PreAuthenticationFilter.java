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

package com.helicalinsight.admin.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is sub class of spring framework's UsernamePasswordAuthenticationFilter,
 * this class is get invoked when user submit the form using his/her
 * credentials from login page, this class is responsible for whether user login
 * along with user name and password it combine user name and
 * UserDetailsServiceImpl in service layer for authentication.
 *
 * @author Muqtar Ahmed
 */
public class PreAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(PreAuthenticationFilter.class);


    /**
     * override method which takes HttpServletRequest and HttpServletResponse
     * arguments and bypass it to super class
     */

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException {
        logger.debug("Attempting for authentication. " + "j_username = " + request.getParameter("j_username") + ", " +
                "j_password = [*****]");
        return super.attemptAuthentication(request, response);
    }

    /**
     * override method which takes HttpServletRequest argument get the request
     * parameters from login page
     *
     * @return String combine user name
     */

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = request.getParameter(getUsernameParameter());
        String combinedUsername;
        combinedUsername = username;
        return combinedUsername;
    }


}

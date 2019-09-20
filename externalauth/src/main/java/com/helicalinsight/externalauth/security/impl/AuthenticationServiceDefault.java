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

package com.helicalinsight.externalauth.security.impl;

import com.helicalinsight.externalauth.security.RestAuthenticationService;
import com.helicalinsight.externalauth.security.RestTokenManager;
import com.helicalinsight.externalauth.security.TokenInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationServiceDefault implements RestAuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RestTokenManager tokenManager;
    private Logger logger = LoggerFactory.getLogger(AuthenticationServiceDefault.class);


    @Override
    public TokenInformationProvider authenticate(String login, String password) {
        // Here principal=username, credentials=password
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
        try {
            authentication = authenticationManager.authenticate(authentication);
            // Here principal=UserDetails (UserContext in our case), credentials=null (security reasons)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.getPrincipal() != null) {
                UserDetails userContext = (UserDetails) authentication.getPrincipal();
                TokenInformationProvider newToken = tokenManager.createNewToken(userContext);
                if (newToken == null) {
                    return null;
                }
                return newToken;
            }
        } catch (AuthenticationException ex) {
            logger.error("Exception ", ex);
        }
        return null;
    }

    @Override
    public boolean checkToken(String token) {

        UserDetails userDetails = tokenManager.getUserDetails(token);
        if (userDetails == null) {
            return false;
        }

        Authentication securityToken = new PreAuthenticatedAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(securityToken);

        return true;
    }

    @Override
    public void logout(String token) {
        UserDetails logoutUser = tokenManager.removeToken(token);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }
}

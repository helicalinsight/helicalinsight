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

package com.helicalinsight.externalauth.jwt;


import com.helicalinsight.admin.model.User;

import com.helicalinsight.datasource.managed.JsonUtils;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * @author Rajesh
 *         Created by author on 17/7/2019.
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rest")
public class AuthenticationController {

    Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenProvider jwtTokenUtil;
    @Autowired
    private TokenProvider tokenProvider;

    @RequestMapping(value = "/authToken", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User loginUser) throws AuthenticationException {
        String TOKEN_PREFIX = mapFromClasspathPropertiesFile.get("token_prefix");
        final Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword()
                    )
            );
        } catch (Exception e) {
            return JsonUtils.get500ErrorResponse(e);
        }
        //todo need to send 500 error type if authentication fails.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        Date issueDateFromToken = tokenProvider.getIssueDateFromToken(token);
        Date expirationDateFromToken = tokenProvider.getExpirationDateFromToken(token);
        return ResponseEntity.ok()
                .header("token-issue", issueDateFromToken.toString())
                .header("token-expiration", expirationDateFromToken.toString())
                .header("token", TOKEN_PREFIX + token)
                .body(new AuthToken(TOKEN_PREFIX + token, issueDateFromToken, expirationDateFromToken));
    }


}

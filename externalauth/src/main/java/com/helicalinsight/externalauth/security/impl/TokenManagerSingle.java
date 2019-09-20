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

import com.helicalinsight.externalauth.security.RestTokenManager;
import com.helicalinsight.externalauth.security.TokenInformationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * Implements simple token manager, that keeps a single token for each user. If user logs in again,
 * older token is invalidated.
 */
public class TokenManagerSingle implements RestTokenManager {

    private Map<String, UserDetails> validUsers = new HashMap<>();

    /**
     * This maps system users to tokens because equals/hashCode is delegated to User entity.
     * This can store either one token or list of them for each user, depending on what you want to do.
     * Here we store single token, which means, that any older tokens are invalidated.
     */
    private Map<UserDetails, TokenInformationProvider> tokens = new HashMap<>();

    @Override
    public TokenInformationProvider createNewToken(UserDetails userDetails) {
        String token;
        do {
            token = generateToken();
        } while (validUsers.containsKey(token));

        TokenInformationProvider tokenInfo = new TokenInformationProvider(token, userDetails);
        removeUserDetails(userDetails);
        UserDetails previous = validUsers.put(token, userDetails);
        if (previous != null) {
            return null;
        }
        tokens.put(userDetails, tokenInfo);

        return tokenInfo;
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        new SecureRandom().nextBytes(tokenBytes);
        return new String(Base64.encode(tokenBytes), StandardCharsets.UTF_8);
    }

    @Override
    public void removeUserDetails(UserDetails userDetails) {
        TokenInformationProvider token = tokens.remove(userDetails);
        if (token != null) {
            validUsers.remove(token.getToken());
        }
    }

    @Override
    public UserDetails removeToken(String token) {
        UserDetails userDetails = validUsers.remove(token);
        if (userDetails != null) {
            tokens.remove(userDetails);
        }
        return userDetails;
    }

    @Override
    public UserDetails getUserDetails(String token) {
        return validUsers.get(token);
    }

    @Override
    public Collection<TokenInformationProvider> getUserTokens(UserDetails userDetails) {
        return Arrays.asList(tokens.get(userDetails));
    }

    @Override
    public Map<String, UserDetails> getValidUsers() {
        return Collections.unmodifiableMap(validUsers);
    }
}

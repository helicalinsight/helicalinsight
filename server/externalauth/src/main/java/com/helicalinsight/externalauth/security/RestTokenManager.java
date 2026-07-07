package com.helicalinsight.externalauth.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

/**
 * Created by user on 3/08/2016.
 *
 * @author Somen
 */
public interface RestTokenManager {

    TokenInformationProvider createNewToken(UserDetails userDetails);

    void removeUserDetails(UserDetails userDetails);

    UserDetails removeToken(String token);

    UserDetails getUserDetails(String token);

    Collection<TokenInformationProvider> getUserTokens(UserDetails userDetails);

    Map<String, UserDetails> getValidUsers();
}

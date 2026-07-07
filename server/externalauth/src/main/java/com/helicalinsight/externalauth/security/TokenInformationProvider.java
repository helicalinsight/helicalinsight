package com.helicalinsight.externalauth.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * TokenInformationProvider
 * This class represents a container for token-related information, 
 * including the authentication token, associated user details
 * Created by user on 3/08/2016.
 *
 * @author Somen
 */
public class TokenInformationProvider {

    private final long created = System.currentTimeMillis();
    private final String token;
    private final UserDetails userDetails;

    public TokenInformationProvider(String token, UserDetails userDetails) {
        this.token = token;
        this.userDetails = userDetails;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "token='" + token + '\'' +
                ", userDetails" + userDetails +
                ", created=" + new Date(created) +
                '}';
    }
}

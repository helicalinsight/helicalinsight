package com.helicalinsight.externalauth.security.impl;

import com.helicalinsight.externalauth.security.RestTokenManager;
import com.helicalinsight.externalauth.security.TokenInformationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * TokenManagerSingle implements {@link RestTokenManager}
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
    /**
     * createNewToken(UserDetails userDetails)
     * Generates a new authentication token for the provided user details.
     *
     * @param userDetails The user details for whom the token is generated.
     * @return TokenInformationProvider object containing the new token and user details, or {@code null} on failure.
     */
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
    /**
     * generateToken()
     * @return generated token
     */
    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        new SecureRandom().nextBytes(tokenBytes);
        return new String(Base64.encode(tokenBytes), StandardCharsets.UTF_8);
    }
    /**
     * removeUserDetails(UserDetails userDetails)
     * Removes user details and associated token information.
     * @param userDetails The user details to remove
     */
    @Override
    public void removeUserDetails(UserDetails userDetails) {
        TokenInformationProvider token = tokens.remove(userDetails);
        if (token != null) {
            validUsers.remove(token.getToken());
        }
    }

    /**
     * removeToken(String token)
     * Removes a user's authentication token and associated token information.
     * @param token The authentication token to remove.
     * @return The UserDetails of the removed token, or {@code null} if the token was not found.
     */
    @Override
    public UserDetails removeToken(String token) {
        UserDetails userDetails = validUsers.remove(token);
        if (userDetails != null) {
            tokens.remove(userDetails);
        }
        return userDetails;
    }
    /**
     * getUserDetails(String token)
     * Retrieves user details associated with a given authentication token
     * @param token The authentication token to look up.
     * @return The UserDetails associated with the token, or {@code null} if not found.
     */
    @Override
    public UserDetails getUserDetails(String token) {
        return validUsers.get(token);
    }
    /**
     * getUserTokens(UserDetails userDetails)
     * Retrieves a collection of TokenInformationProvider instances associated with a given user.
     * @param userDetails The user details to retrieve tokens for.
     * @return A collection of TokenInformationProvider instances, or an empty list if not found.
     */
    @Override
    public Collection<TokenInformationProvider> getUserTokens(UserDetails userDetails) {
        return Arrays.asList(tokens.get(userDetails));
    }
    /**
     * getValidUsers()
     * Retrieves a map of valid users 
     * @return An unmodifiable map containing valid users and tokens.
     */
    @Override
    public Map<String, UserDetails> getValidUsers() {
        return Collections.unmodifiableMap(validUsers);
    }
}

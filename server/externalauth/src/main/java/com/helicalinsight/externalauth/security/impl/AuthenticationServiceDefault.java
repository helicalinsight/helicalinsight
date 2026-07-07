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

/**
 * AuthenticationServiceDefault implements {@link RestAuthenticationService }
 * this class is responsible for provide functionality for token-based authentication,
 * token management, and user-related operations. 
 * It facilitates user authentication, token validation, 
 * logout, and retrieval of user details.
 */
@Component
public class AuthenticationServiceDefault implements RestAuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RestTokenManager tokenManager;
    private Logger logger = LoggerFactory.getLogger(AuthenticationServiceDefault.class);

    /**
     * authenticate(String login, String password)
     * Authenticates a user with the provided login and password, generating and returning a new token.
     *
     * @param login    The user's login.
     * @param password The user's password.
     * @return TokenInformationProvider object containing the authentication token or {@code null} if authentication fails.
     */
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
    /**
     * checkToken(String token)
     * Checks if the provided token is valid and updates the SecurityContextHolder with the authenticated user.
     *
     * @param token The authentication token to check.
     * @return {@code true} if the token is valid, otherwise {@code false}
     */
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
    /**
     * logout(String token)
     * Logs out the user associated with the provided token by removing the token from the token manager.
     * @param token The authentication token to log out.
     */
    @Override
    public void logout(String token) {
        UserDetails logoutUser = tokenManager.removeToken(token);
        SecurityContextHolder.clearContext();
    }
    /**
     * currentUser()
     * Retrieves the currently authenticated user from the SecurityContextHolder.
     * @return UserDetails of the currently authenticated user, or {@code null} if no user is authenticated.
     */
    @Override
    public UserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }
}

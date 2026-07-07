package com.helicalinsight.externalauth.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by user on 3/08/2016.
 *
 * @author Somen
 */
public interface RestAuthenticationService {

    TokenInformationProvider authenticate(String login, String password);

    boolean checkToken(String token);

    void logout(String token);

    UserDetails currentUser();
}

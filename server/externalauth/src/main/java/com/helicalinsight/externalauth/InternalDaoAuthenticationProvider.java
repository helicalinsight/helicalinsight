package com.helicalinsight.externalauth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by user on 2/10/2016.
 * @author Somen
 */
 
/**
 * InternalDaoAuthenticationProvider class extends {@link DaoAuthenticationProvider}
 * This class is responsible to checking user details for authentication purpose.
 */

public class InternalDaoAuthenticationProvider extends DaoAuthenticationProvider {
	/**
	 * additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
     *
     * @param UserDetails userDetails                                consists all user credentials
     * @param UsernamePasswordAuthenticationToken authentication     password token
     * @throws AuthenticationException  if the authentication is <code>null</code> or password from authentication token and userDetails is not same ,
     * it throws AbstractUserDetailsAuthenticationProvider.badCredentials. 
	 */
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken
            authentication) throws AuthenticationException {
        /*if ((!(userDetails instanceof UserDetailsServiceImpl))) {
            throw new EfwServiceException("User needs to be internal to be authenticated by " + getClass());
        }*/

        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}

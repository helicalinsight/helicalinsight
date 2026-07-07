package com.helicalinsight.externalauth.rest;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RestAuthenticationEntryPoint implements {@link AuthenticationEntryPoint} 
 * This class handles common of authentication failures.
 * 
 * Created by user on 3/08/2016.
 * 
 * @author Somen
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
	 *  This method is called when an unauthenticated or unauthorized request is made to a secured endpoint.
	 *  and starts the authentication process if right/proper authentication is made.
	 *  handles or starts the authentication process if an <code>AuthenticationException</code> is detected.
     *  this may also switch the current protocol from http to https for an SSL login.
	 *  @param request         incoming HTTP request
	 *  @param response		   HTTP response that will sent back to the client
	 *  @param authException   exception if authentication failed
	 *  @throws IOException    if an I/O exception has occurred.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
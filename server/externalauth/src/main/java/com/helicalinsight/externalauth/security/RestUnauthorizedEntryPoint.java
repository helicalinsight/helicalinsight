package com.helicalinsight.externalauth.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RestUnauthorizedEntryPoint implementation of Spring Security's {@link AuthenticationEntryPoint}
 * When an unauthorized request is detected, an HTTP 401 Unauthorized status is sent in the response.
 *
 * Created by user on 3/08/2016.
 * @author Somen
 */
public class RestUnauthorizedEntryPoint implements AuthenticationEntryPoint {

	/**
	 * commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
	 *  This method is called when an unauthenticated or unauthorized request is made to a secured endpoint.
	 *  Handles an unauthorized request by sending an HTTP 401 Unauthorized status.
     *
	 *  @param request         request that resulted in an AuthenticationException
	 *  @param response		   response in which the 401 status will be sent.
	 *  @param authException   exception if authentication failed
	 *  @throws IOException    if an I/O exception has occurred.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
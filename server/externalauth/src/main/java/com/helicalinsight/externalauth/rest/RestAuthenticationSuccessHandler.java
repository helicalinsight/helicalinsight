package com.helicalinsight.externalauth.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RestAuthenticationSuccessHandler implements {@link AuthenticationSuccessHandler}
 * used to handle a successful user authentication.
 * Created by user on 3/08/2016.
 * @author Somen
 */
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	/**
	 * Called when a user has been successfully authenticated.
	 * @param request the request which caused the successful authentication
	 * @param response the response
	 * @param authentication the <tt>Authentication</tt> object which was created during
	 * the authentication process.
	 * @throws IOException       if an IO exception occurs
	 * @throws ServletException  if servlet exception occurs.
	 */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

    }
}
package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.core.Authentication;

import com.helicalinsight.externalauth.rest.RestAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestAuthenticationSuccessHandlerTest {
	
	@Test
	public void testOnAuthenticationSuccess() throws IOException, ServletException {
		RestAuthenticationSuccessHandler authenticationSuccessHandler = new RestAuthenticationSuccessHandler();
		HttpServletRequest request   = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Authentication authentication = mock(Authentication.class);
		authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}
	
}

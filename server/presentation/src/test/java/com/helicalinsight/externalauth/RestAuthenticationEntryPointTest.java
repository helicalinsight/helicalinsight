package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.core.AuthenticationException;

import com.helicalinsight.externalauth.rest.RestAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestAuthenticationEntryPointTest {

	@Test
	public void testCommence() throws IOException {
		RestAuthenticationEntryPoint authenticationEntryPoint = new RestAuthenticationEntryPoint();
		HttpServletRequest request   = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		AuthenticationException authException = mock(AuthenticationException.class);
		authenticationEntryPoint.commence(request, response, authException);
	}
}

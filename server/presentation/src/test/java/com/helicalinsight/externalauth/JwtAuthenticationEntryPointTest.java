package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.core.AuthenticationException;

import com.helicalinsight.externalauth.jwt.JwtAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationEntryPointTest {

	@Test
	public void testCommence() throws IOException {
		JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
		HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);
        
		authenticationEntryPoint.commence(request, response, authException);
	}
}

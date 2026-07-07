package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.core.AuthenticationException;

import com.helicalinsight.externalauth.security.RestUnauthorizedEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestUnauthorizedEntryPointTest {

	@Test
	public void testCommence() throws IOException {
		RestUnauthorizedEntryPoint entryPoint = new RestUnauthorizedEntryPoint();
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		AuthenticationException authException = mock(AuthenticationException.class);
		entryPoint.commence(httpRequest, httpResponse, authException);
	}
}

package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.core.Authentication;

import com.helicalinsight.externalauth.cas.CasSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CasSuccessHandlerTest {

	@Test
	public void testCasSuccessHandler() throws ServletException, IOException {
		CasSuccessHandler  casSuccessHandler = new CasSuccessHandler();
		Authentication authentication = mock(Authentication.class);
		HttpServletRequest request    = mock(HttpServletRequest.class);
		HttpServletResponse response  = mock(HttpServletResponse.class);
		
		casSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}
}

package com.helicalinsight.externalauth;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.helicalinsight.externalauth.cas.CasUserDetailService;
import com.helicalinsight.externalauth.cas.HiCasFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HiCasFilterTest extends HiCasFilter{

	@Test(expected = NullPointerException.class)
	public void testAttemptAuthentication_a1() throws AuthenticationException, IOException {
		HiCasFilter casFilter = new HiCasFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		AbstractAuthenticationProcessingFilter authenticationManager = mock(AbstractAuthenticationProcessingFilter.class);

		when(request.getParameter("ticket")).thenReturn("password");
		casFilter.attemptAuthentication(request, response);
	}
	@Test(expected = NullPointerException.class)
	public void testAttemptAuthentication_a2() throws AuthenticationException, IOException {
		HiCasFilter casFilter = new HiCasFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		AbstractAuthenticationProcessingFilter authenticationManager = mock(AbstractAuthenticationProcessingFilter.class);

		when(request.getParameter("ticket")).thenReturn("");
		casFilter.attemptAuthentication(request, response);
	}
	@Test(expected = NullPointerException.class)
	public void testAttemptAuthentication_a3() throws AuthenticationException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiCasFilter casFilter = new HiCasFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		AbstractAuthenticationProcessingFilter authenticationManager = mock(AbstractAuthenticationProcessingFilter.class);
		when(request.getParameter("ticket")).thenReturn(null);
		casFilter.attemptAuthentication(request, response);
	}
	
	@Test
	public void testSetUsernameParameter() {
		HiCasFilter casFilter = new HiCasFilter();
		casFilter.setUsernameParameter("hi_user");
		casFilter.setPasswordParameter("hi_password");

	}
}

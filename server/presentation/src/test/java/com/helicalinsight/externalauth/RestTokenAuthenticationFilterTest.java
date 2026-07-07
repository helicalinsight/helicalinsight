package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.helicalinsight.externalauth.security.RestAuthenticationService;
import com.helicalinsight.externalauth.security.RestTokenAuthenticationFilter;
import com.helicalinsight.externalauth.security.TokenInformationProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestTokenAuthenticationFilterTest {

	@Test(expected = NullPointerException.class)
	public void testDoFilter() throws IOException, ServletException {
		RestTokenAuthenticationFilter authenticationFilter = new RestTokenAuthenticationFilter();
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		authenticationFilter.doFilter(httpRequest, httpResponse, chain);
	}

	@Test(expected = NullPointerException.class)
	public void testDoFilter_a1() throws IOException, ServletException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		RestTokenAuthenticationFilter authenticationFilter = new RestTokenAuthenticationFilter();
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		RestAuthenticationService authenticationService = mock(RestAuthenticationService.class);
		when(authenticationService.checkToken(anyString())).thenReturn(false);
		when(httpRequest.getHeader(anyString())).thenReturn("token");

		Field field = RestTokenAuthenticationFilter.class.getDeclaredField("authenticationService");
		field.setAccessible(true);
		field.set(authenticationFilter, authenticationService);

		authenticationFilter.doFilter(httpRequest, httpResponse, chain);
	}

	@Test
	public void testDoFilter_a2() throws IOException, ServletException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		RestTokenAuthenticationFilter authenticationFilter = new RestTokenAuthenticationFilter();
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		RestAuthenticationService authenticationService = mock(RestAuthenticationService.class);
		SecurityContext context = mock(SecurityContext.class);
		Authentication authentication = mock(Authentication.class);

		Field field = RestTokenAuthenticationFilter.class.getDeclaredField("authenticationService");
		field.setAccessible(true);
		field.set(authenticationFilter, authenticationService);

		String link = "urllogoutLink";
		Field field1 = RestTokenAuthenticationFilter.class.getDeclaredField("logoutLink");
		field1.setAccessible(true);
		field1.set(authenticationFilter, link);

		when(httpRequest.getServletPath()).thenReturn("url");
		when(httpRequest.getPathInfo()).thenReturn("logoutLink");
		when(httpRequest.getAttribute(anyString())).thenReturn(null);
		when(httpRequest.getMethod()).thenReturn("POST");
		when(httpRequest.getHeader(anyString())).thenReturn("token");
		when(authenticationService.checkToken(anyString())).thenReturn(true);

		try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
			mockedStatic.when(() -> SecurityContextHolder.getContext()).thenReturn(context);
			when(context.getAuthentication()).thenReturn(authentication);
			when(authentication.getPrincipal()).thenReturn(new Object());
			authenticationFilter.doFilter(httpRequest, httpResponse, chain);
		}
		;

	}

	@Test
	public void testDoFilter_a3() throws IOException, ServletException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		RestTokenAuthenticationFilter authenticationFilter = new RestTokenAuthenticationFilter();
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		RestAuthenticationService authenticationService = mock(RestAuthenticationService.class);
		SecurityContext context = mock(SecurityContext.class);
		Authentication authentication = mock(Authentication.class);

		Field field = RestTokenAuthenticationFilter.class.getDeclaredField("authenticationService");
		field.setAccessible(true);
		field.set(authenticationFilter, authenticationService);

		when(httpRequest.getHeader("Authorization")).thenReturn(null);
		when(httpRequest.getHeader("X-Username")).thenReturn("username");
		when(httpRequest.getHeader("X-Password")).thenReturn("password");
		when(httpRequest.getServletPath()).thenReturn("url");
		when(httpRequest.getPathInfo()).thenReturn(null);
		when(httpRequest.getAttribute(anyString())).thenReturn(null);
		when(httpRequest.getMethod()).thenReturn("POST");
		when(authenticationService.checkToken(anyString())).thenReturn(true);

		try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
			mockedStatic.when(() -> SecurityContextHolder.getContext()).thenReturn(context);
			when(context.getAuthentication()).thenReturn(authentication);
			when(authentication.getPrincipal()).thenReturn(new Object());
			authenticationFilter.doFilter(httpRequest, httpResponse, chain);
		}

	}

	@Test
	public void testDoFilter_a4() throws IOException, ServletException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		RestTokenAuthenticationFilter authenticationFilter = new RestTokenAuthenticationFilter();
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		RestAuthenticationService authenticationService = mock(RestAuthenticationService.class);
		SecurityContext context = mock(SecurityContext.class);
		Authentication authentication = mock(Authentication.class);
		TokenInformationProvider tokenInfo = mock(TokenInformationProvider.class);

		Field field = RestTokenAuthenticationFilter.class.getDeclaredField("authenticationService");
		field.setAccessible(true);
		field.set(authenticationFilter, authenticationService);

		when(httpRequest.getHeader("X-Auth-Token")).thenReturn("token");

		when(httpRequest.getHeader("Authorization")).thenReturn(null);
		when(httpRequest.getHeader("X-Username")).thenReturn("username");
		when(httpRequest.getHeader("X-Password")).thenReturn("password");
		when(httpRequest.getServletPath()).thenReturn("url");
		when(httpRequest.getPathInfo()).thenReturn(null);
		when(httpRequest.getAttribute(anyString())).thenReturn(null);
		when(httpRequest.getMethod()).thenReturn("POST");
		when(authenticationService.checkToken(anyString())).thenReturn(true);
		when(authenticationService.authenticate("username", "password")).thenReturn(tokenInfo);

		try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
			mockedStatic.when(() -> SecurityContextHolder.getContext()).thenReturn(context);
			when(context.getAuthentication()).thenReturn(authentication);
			when(authentication.getPrincipal()).thenReturn(new Object());
			authenticationFilter.doFilter(httpRequest, httpResponse, chain);
		}

	}

	@Test
	public void testcanRequestProcessingContinue() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		RestTokenAuthenticationFilter authenticationFilter = new RestTokenAuthenticationFilter();
		
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		when(httpRequest.getAttribute(anyString())).thenReturn("AuthenticationFilter-doNotContinue");
		Method method = RestTokenAuthenticationFilter.class.getDeclaredMethod("canRequestProcessingContinue",
				HttpServletRequest.class);
		method.setAccessible(true);
		method.invoke(authenticationFilter, httpRequest);

	}

}

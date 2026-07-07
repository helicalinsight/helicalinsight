package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import com.helicalinsight.externalauth.saml.HISamlFilterChian;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class HISamlFilterChianTest {

	@Test(expected = ClassCastException.class)
	public void testDoFilter_a1() throws IOException, ServletException {
		HISamlFilterChian filter = new HISamlFilterChian();
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletRequest servletRequest = request; 
		HttpSession session = mock(HttpSession.class);
		when(request.getSession(false)).thenReturn(session);
		when(request.getRequestURI()).thenReturn("j_spring_");
		
		ServletResponse servletResponse = mock(ServletResponse.class);
		
		FilterChain chain = mock(FilterChain.class) ;
		filter.doFilter(servletRequest, servletResponse, chain);
	}
	@Test
	public void testDoFilter_a2() throws IOException, ServletException {
		HISamlFilterChian filter = new HISamlFilterChian();
		ServletRequest request = mock(ServletRequest.class);
		
		
	    HttpServletResponse response = mock(HttpServletResponse.class);
		ServletResponse servletResponse = response;
		FilterChain chain = mock(FilterChain.class);
		filter.doFilter(request, servletResponse, chain);
	}
	@Test
	public void testDoFilter_a3() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletRequest servletRequest = request;
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletResponse servletResponse = response;
		FilterChain chain = mock(FilterChain.class);
		HISamlFilterChian filter = spy(new HISamlFilterChian());
		filter.doFilter(servletRequest, servletResponse, chain);

	}

	
}

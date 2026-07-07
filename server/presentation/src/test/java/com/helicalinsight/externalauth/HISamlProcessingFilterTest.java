package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;

import com.helicalinsight.externalauth.saml.HISamlProcessingFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HISamlProcessingFilterTest {

	@Test
	public void test() throws IOException, ServletException {
		HISamlProcessingFilter filter = new HISamlProcessingFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletRequest servletRequest = request;
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletResponse servletResponse = response;
		FilterChain chain = mock(FilterChain.class);
	
		filter.doFilter(request, response, chain);
	}
}

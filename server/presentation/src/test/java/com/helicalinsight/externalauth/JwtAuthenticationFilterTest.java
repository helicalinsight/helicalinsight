package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helicalinsight.externalauth.jwt.JwtAuthenticationFilter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilterTest extends JwtAuthenticationFilter{

	@Test
	public void testSetCookieJwt() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
      
        Method method = JwtAuthenticationFilter.class.getDeclaredMethod("setCookieJwt", HttpServletRequest.class, HttpServletResponse.class, String.class);
        method.setAccessible(true);
      
       
        method.invoke(authenticationFilter, request, response, "token");


	}
	
	
	@Test
	public void testMatchPath() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter();
		List<String> ignoreUrls = new ArrayList<>();
		ignoreUrls.add("url");
		Field field = JwtAuthenticationFilter.class.getDeclaredField("ignoreUrls");
		field.setAccessible(true);
		field.set(authenticationFilter, ignoreUrls);
		authenticationFilter.matchPath("url");	
	}
	@Test
	public void testDestroy() {
		JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter();
		authenticationFilter.destroy();
		
	}
	@Test
	public void testEraseCookie() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
      
		Method method = JwtAuthenticationFilter.class.getDeclaredMethod("eraseCookie", HttpServletRequest.class, HttpServletResponse.class);
	    method.setAccessible(true);
	      
	    Cookie cookie = new Cookie("key", "value");
		Cookie[] cookies = new Cookie[] {cookie};
		when(request.getCookies()).thenReturn(cookies);
	    method.invoke(authenticationFilter, request, response);

	}
	@Test
	public void testGetIgnoreUrl() {
		JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter();
		authenticationFilter.getIgnoreUrls();
	}
}

package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.externalauth.jwt.AuthenticationController;
import com.helicalinsight.externalauth.jwt.TokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationControllerTest {
	
	//@Test
	public void testRegister_a1() {
		AuthenticationController authenticationController = new AuthenticationController();
		User loginUser = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(loginUser.getPassword()).thenReturn("");
		authenticationController.register(loginUser, request, response);
	}
	
	
	@Test
	public void testRegister_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationController authenticationController = new AuthenticationController();
		User loginUser = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(loginUser.getPassword()).thenReturn("hi_password");
		when(loginUser.getUsername()).thenReturn("hi_user");
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		Field field = AuthenticationController.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationController, authenticationManager);
		TokenProvider jwtTokenUtil = mock(TokenProvider.class);
		when(jwtTokenUtil.generateToken(any())).thenReturn("token");
		Field field1 = AuthenticationController.class.getDeclaredField("jwtTokenUtil");
		field1.setAccessible(true);
		field1.set(authenticationController, jwtTokenUtil);
		
		
		
		Date date = new Date();
		TokenProvider tokenProvider = mock(TokenProvider.class);
		when(tokenProvider.getIssueDateFromToken(anyString())).thenReturn(date);
		when(tokenProvider.getExpirationDateFromToken(anyString())).thenReturn(date);

		Field field2 = AuthenticationController.class.getDeclaredField("tokenProvider");
		field2.setAccessible(true);
		field2.set(authenticationController, tokenProvider);
		
		authenticationController.register(loginUser, request, response);
	}
	
	
	//@Test
	public void testRegister_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationController authenticationController = new AuthenticationController();
		User loginUser = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(loginUser.getPassword()).thenReturn("hi_password");
		when(loginUser.getUsername()).thenReturn("hi_user");
		when(loginUser.getAuthenticateUser()).thenReturn("hi_user");
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/rest/api"));
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		Field field = AuthenticationController.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationController, authenticationManager);
		TokenProvider jwtTokenUtil = mock(TokenProvider.class);
		when(jwtTokenUtil.generateToken(any())).thenReturn("token");
		Field field1 = AuthenticationController.class.getDeclaredField("jwtTokenUtil");
		field1.setAccessible(true);
		field1.set(authenticationController, jwtTokenUtil);
		
		
		
		Date date = new Date();
		TokenProvider tokenProvider = mock(TokenProvider.class);
		when(tokenProvider.getIssueDateFromToken(anyString())).thenReturn(date);
		when(tokenProvider.getExpirationDateFromToken(anyString())).thenReturn(date);

		Field field2 = AuthenticationController.class.getDeclaredField("tokenProvider");
		field2.setAccessible(true);
		field2.set(authenticationController, tokenProvider);
		
		authenticationController.register(loginUser, request, response);
	}

	@Test
	public void testRegister_a4() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClientProtocolException, IOException {
		AuthenticationController authenticationController = new AuthenticationController();
		User loginUser = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(loginUser.getPassword()).thenReturn("hi_password");
		when(loginUser.getUsername()).thenReturn("hi_user");
		when(loginUser.getAuthenticateUser()).thenReturn("hi_user");
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/rest/api"));
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		Field field = AuthenticationController.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationController, authenticationManager);
		TokenProvider jwtTokenUtil = mock(TokenProvider.class);
		when(jwtTokenUtil.generateToken(any())).thenReturn("token");
		Field field1 = AuthenticationController.class.getDeclaredField("jwtTokenUtil");
		field1.setAccessible(true);
		field1.set(authenticationController, jwtTokenUtil);
		
		
		
		Date date = new Date();
		TokenProvider tokenProvider = mock(TokenProvider.class);
		when(tokenProvider.getIssueDateFromToken(anyString())).thenReturn(date);
		when(tokenProvider.getExpirationDateFromToken(anyString())).thenReturn(date);

		Field field2 = AuthenticationController.class.getDeclaredField("tokenProvider");
		field2.setAccessible(true);
		field2.set(authenticationController, tokenProvider);
		StatusLine line= mock(StatusLine.class);
		
		
		
		try(MockedStatic<HttpClientBuilder> mockedStatic = mockStatic(HttpClientBuilder.class)){
			HttpClientBuilder builder = mock(HttpClientBuilder.class);
			mockedStatic.when(() -> HttpClientBuilder.create()).thenReturn(builder);
			CloseableHttpClient client = mock(CloseableHttpClient.class);
			when( builder.build()).thenReturn(client);
			
			CloseableHttpResponse execute  = mock(CloseableHttpResponse.class);
			when(execute.getStatusLine()).thenReturn(line);
			when(line.getStatusCode()).thenReturn(200);
			
			when(client.execute(any(HttpGet.class), any(HttpContext.class))).thenReturn(execute);
			authenticationController.register(loginUser, request, response);
		}
		
	}


	//@Test
	public void testRegister_a5() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClientProtocolException, IOException {
		AuthenticationController authenticationController = new AuthenticationController();
		User loginUser = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(loginUser.getPassword()).thenReturn("hi_password");
		when(loginUser.getUsername()).thenReturn("hi_user");
		when(loginUser.getAuthenticateUser()).thenReturn("hi_user");
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/rest/api"));
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		Field field = AuthenticationController.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationController, authenticationManager);
		
		
		try(MockedStatic<HttpClientBuilder> mockedStatic = mockStatic(HttpClientBuilder.class)){
			HttpClientBuilder builder = mock(HttpClientBuilder.class);
			mockedStatic.when(() -> HttpClientBuilder.create()).thenReturn(builder);
			CloseableHttpClient client = mock(CloseableHttpClient.class);
			when( builder.build()).thenReturn(client);
			
			when(client.execute(any(HttpGet.class), any(HttpContext.class))).thenThrow(new IOException("Simulated exception"));
			authenticationController.register(loginUser, request, response);
		}
		
	}
	@Test
	public void testDoLogout() {
		AuthenticationController.doLogout("123");
	}
	@Test
	public void testJwtLogin() {
		AuthenticationController authenticationController = new AuthenticationController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(request.getContextPath()).thenReturn("path");
		authenticationController.jwtLogin("token", request, response);
	}
	@Test
	public void testJwtLogout() {
		AuthenticationController authenticationController = new AuthenticationController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(request.getContextPath()).thenReturn("path");
		Cookie cookie = new Cookie("key", "value");
		Cookie[] cookies = new Cookie[] {cookie};
		when(request.getCookies()).thenReturn(cookies);
		authenticationController.jwtLogout(request, response);
	}
}

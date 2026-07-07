package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.helicalinsight.externalauth.security.RestTokenManager;
import com.helicalinsight.externalauth.security.TokenInformationProvider;
import com.helicalinsight.externalauth.security.impl.AuthenticationServiceDefault;

public class AuthenticationServiceDefaultTest {
	
	@Test
	public void testAuthenticate_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		UserDetails userContext = mock(UserDetails.class);
		RestTokenManager tokenManager = mock(RestTokenManager.class);
		TokenInformationProvider newToken = mock(TokenInformationProvider.class);
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		Field field = AuthenticationServiceDefault.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationServiceDefault, authenticationManager);
		
		Field field1 = AuthenticationServiceDefault.class.getDeclaredField("tokenManager");
		field1.setAccessible(true);
		field1.set(authenticationServiceDefault, tokenManager);
		when(tokenManager.createNewToken(userContext)).thenReturn(newToken);
		when(authentication.getPrincipal()).thenReturn(userContext);
		authenticationServiceDefault.authenticate("username", "password");
	}
	
	@Test
	public void testAuthenticate_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		UserDetails userContext = mock(UserDetails.class);
		RestTokenManager tokenManager = mock(RestTokenManager.class);
		TokenInformationProvider newToken = mock(TokenInformationProvider.class);
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		Field field = AuthenticationServiceDefault.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationServiceDefault, authenticationManager);
		
		Field field1 = AuthenticationServiceDefault.class.getDeclaredField("tokenManager");
		field1.setAccessible(true);
		field1.set(authenticationServiceDefault, tokenManager);
		when(tokenManager.createNewToken(userContext)).thenReturn(null);
		when(authentication.getPrincipal()).thenReturn(userContext);
		authenticationServiceDefault.authenticate("username", "password");
	}
	
	@Test
	public void testAuthenticate_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		UserDetails userContext = mock(UserDetails.class);
		RestTokenManager tokenManager = mock(RestTokenManager.class);
		TokenInformationProvider newToken = mock(TokenInformationProvider.class);
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		Field field = AuthenticationServiceDefault.class.getDeclaredField("authenticationManager");
		field.setAccessible(true);
		field.set(authenticationServiceDefault, authenticationManager);
		authenticationServiceDefault.authenticate("username", "password");
	}
	@Test
	public void testCheckToken_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		UserDetails userContext = mock(UserDetails.class);
		RestTokenManager tokenManager = mock(RestTokenManager.class);
		Field field1 = AuthenticationServiceDefault.class.getDeclaredField("tokenManager");
		field1.setAccessible(true);
		field1.set(authenticationServiceDefault, tokenManager);
		
		when(tokenManager.getUserDetails(anyString())).thenReturn(userContext);
		authenticationServiceDefault.checkToken("token");
	}
	@Test
	public void testCheckToken_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		UserDetails userContext = mock(UserDetails.class);
		RestTokenManager tokenManager = mock(RestTokenManager.class);
		Field field1 = AuthenticationServiceDefault.class.getDeclaredField("tokenManager");
		field1.setAccessible(true);
		field1.set(authenticationServiceDefault, tokenManager);
		
		when(tokenManager.getUserDetails(anyString())).thenReturn(null);
		authenticationServiceDefault.checkToken("token");
	}
	@Test
	public void testLogout() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		UserDetails userContext = mock(UserDetails.class);
		RestTokenManager tokenManager = mock(RestTokenManager.class);
		Field field1 = AuthenticationServiceDefault.class.getDeclaredField("tokenManager");
		field1.setAccessible(true);
		field1.set(authenticationServiceDefault, tokenManager);
		when(tokenManager.removeToken(anyString())).thenReturn(userContext);
		authenticationServiceDefault.logout("token");
	}
	
	@Test
	public void testCurrentUser_a1() {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		authenticationServiceDefault.currentUser();
	}
	@Test
	public void testCurrentUser_a2() {
		AuthenticationServiceDefault authenticationServiceDefault = new AuthenticationServiceDefault();
		SecurityContext context = mock(SecurityContext.class);
		
		try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
			mockedStatic.when(() -> SecurityContextHolder.getContext()).thenReturn(context);
			when(context.getAuthentication()).thenReturn(null);
			authenticationServiceDefault.currentUser();
		}
		
	}

}

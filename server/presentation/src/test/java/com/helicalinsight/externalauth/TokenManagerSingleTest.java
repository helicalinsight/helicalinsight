package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.helicalinsight.externalauth.security.TokenInformationProvider;
import com.helicalinsight.externalauth.security.impl.TokenManagerSingle;

public class TokenManagerSingleTest {

	@Test
	public void testCreateNewToken() {
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		UserDetails userDetails = mock(UserDetails.class);
		managerSingle.createNewToken(userDetails);
	}
	
	@Test
	public void testCreateNewToken_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		UserDetails userDetails = mock(UserDetails.class);
		Map<String, UserDetails> validUsers = spy(new HashMap<>());
		Map<UserDetails, TokenInformationProvider> tokens = spy(new HashMap<>());
		TokenInformationProvider provider = mock(TokenInformationProvider.class);
		
		when(validUsers.put(anyString(), any(UserDetails.class))).thenReturn(userDetails);
		when(tokens.remove(any(UserDetails.class))).thenReturn(provider);

		Field field = TokenManagerSingle.class.getDeclaredField("validUsers");
		field.setAccessible(true);
		field.set(managerSingle, validUsers);
		Field field1 = TokenManagerSingle.class.getDeclaredField("tokens");
		field1.setAccessible(true);
		field1.set(managerSingle, tokens);
		managerSingle.createNewToken(userDetails);
	}
	
	@Test
	public void testRemoveToken_a1() {
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		managerSingle.removeToken("token");
	}
	
	@Test
	public void testRemoveToken_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		UserDetails userDetails = mock(UserDetails.class);
		Map<String, UserDetails> validUsers = spy(new HashMap<>());
		Field field = TokenManagerSingle.class.getDeclaredField("validUsers");
		field.setAccessible(true);
		field.set(managerSingle, validUsers);
		when(validUsers.remove(anyString())).thenReturn(userDetails);
		managerSingle.removeToken("token");
	}
	
	@Test
	public void testGetUserDetails() {
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		managerSingle.getUserDetails("token");
	}
	@Test
	public void testGetUserTokens() {
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		UserDetails userDetails = mock(UserDetails.class);
		managerSingle.getUserTokens(userDetails);
	}
	@Test
	public void testGetValidUsers() {
		TokenManagerSingle managerSingle = new TokenManagerSingle();
		managerSingle.getValidUsers();
	}
}

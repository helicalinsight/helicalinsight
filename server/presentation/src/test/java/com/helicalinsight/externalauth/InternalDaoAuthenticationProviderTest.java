package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;

public class InternalDaoAuthenticationProviderTest {

	//@Test
	public void testAdditionalAuthenticationChecks() {
		InternalDaoAuthenticationProvider authenticationProvider =spy(new InternalDaoAuthenticationProvider());
		UserDetails  userDetails = mock(UserDetails.class);
		when(userDetails.getPassword()).thenReturn("password");
		UsernamePasswordAuthenticationToken authenticationToken = mock( UsernamePasswordAuthenticationToken.class);
		when(authenticationToken.getCredentials()).thenReturn("password");
		DaoAuthenticationProvider provider = spy(new DaoAuthenticationProvider());
		authenticationProvider.additionalAuthenticationChecks(userDetails, authenticationToken);
	
	}
	
}

package com.helicalinsight.externalauth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.helicalinsight.externalauth.security.TokenInformationProvider;

public class TokenInformationProviderTest {

	@Test
	public void test() {
		UserDetails userDetails = mock(UserDetails.class);
		String token = "token";
		TokenInformationProvider informationProvider= new TokenInformationProvider(token, userDetails);
		String token2 = informationProvider.getToken();
		informationProvider.toString();
		assertEquals(token2, token);
	}
}

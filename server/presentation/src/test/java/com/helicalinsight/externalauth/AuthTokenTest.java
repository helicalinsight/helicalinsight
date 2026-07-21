package com.helicalinsight.externalauth;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.helicalinsight.externalauth.jwt.AuthToken;

public class AuthTokenTest {

	@Test
	public void testGetToken() {
		AuthToken authToken = new AuthToken();
		authToken.setToken("token");
		String token = authToken.getToken();
		assertEquals(token, "token");
	}
	
	
	@Test
	public void testSetIssuedAt() {
	    AuthToken authToken = new AuthToken();
	    Date now = new Date();
	    authToken.setIssuedAt(now);
	    assertEquals(now, authToken.getIssuedAt());
	}

	@Test
	public void testSetExpiration() {
	    Date now = new Date();
	    AuthToken authToken = new AuthToken("token", new Date(), new Date());
	    authToken.setExpiration(now);
	    assertEquals(now, authToken.getExpiration());
	}
}

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
		authToken.setIssuedAt(new Date());
		Date issuedAt = authToken.getIssuedAt();
		assertEquals(issuedAt, new Date());
	}
	
	@Test
	public void testSetExpiration() {
		AuthToken authToken = new AuthToken("token",new Date(),new Date());
		authToken.setExpiration(new Date());
		Date expiration = authToken.getExpiration();
		assertEquals(expiration, new Date());
	}
}

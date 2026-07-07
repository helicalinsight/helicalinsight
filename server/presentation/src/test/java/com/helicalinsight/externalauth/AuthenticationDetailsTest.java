package com.helicalinsight.externalauth;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.externalauth.cas.AuthenticationDetails;

public class AuthenticationDetailsTest {
	
	@Test
	public void testEquals_a1() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field field = AuthenticationDetails.class.getDeclaredField("context");
		field.setAccessible(true);
		String context = "context";
		AuthenticationDetails authenticationDetails = mock(AuthenticationDetails.class);
		field.set(authenticationDetails, context);
		authenticationDetails = new AuthenticationDetails(context);
		authenticationDetails.equals(authenticationDetails);
	}
	
	@Test
	public void testEquals_a2() {
		AuthenticationDetails authenticationDetails = new AuthenticationDetails(getClass());
		JsonObject object = new JsonObject();
		boolean equals = authenticationDetails.equals(object);
		assertFalse(equals);
	}
	@Test
	public void testEquals_a3() {
		Object context = null;
		AuthenticationDetails authenticationDetails = new AuthenticationDetails(context);
	}
	@Test
	public void testToString() {
		Object context = "context";
		AuthenticationDetails authenticationDetails = new AuthenticationDetails(context);
		authenticationDetails.toString();
	}
	
}

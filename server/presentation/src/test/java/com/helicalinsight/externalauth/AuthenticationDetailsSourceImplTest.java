package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.JsonObject;
import com.helicalinsight.externalauth.cas.AuthenticationDetailsSourceImpl;
import com.helicalinsight.externalauth.cas.HiCasFilter;
import com.helicalinsight.externalauth.jwt.AuthToken;

public class AuthenticationDetailsSourceImplTest {

	@Test
	public void testBuildDetails_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationDetailsSourceImpl authenticationDetailsSourceImpl = new AuthenticationDetailsSourceImpl();
		authenticationDetailsSourceImpl.buildDetails("context");
	}
	@Test(expected = IllegalStateException.class)
	public void testBuildDetails_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationDetailsSourceImpl authenticationDetailsSourceImpl = new AuthenticationDetailsSourceImpl();
		Field field = AuthenticationDetailsSourceImpl.class.getDeclaredField("clazz");
		field.setAccessible(true);
		Class<?> clazz = AuthToken.class;
		field.set(authenticationDetailsSourceImpl, clazz);
		authenticationDetailsSourceImpl.buildDetails("context");
	}
	@Test(expected = IllegalStateException.class)
	public void testBuildDetails_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AuthenticationDetailsSourceImpl authenticationDetailsSourceImpl = new AuthenticationDetailsSourceImpl();
		Field field = AuthenticationDetailsSourceImpl.class.getDeclaredField("clazz");
		field.setAccessible(true);
		Class<?> clazz = AuthToken.class;
		field.set(authenticationDetailsSourceImpl, clazz);
		authenticationDetailsSourceImpl.buildDetails(null);
	}

	@Test
	public void testSetClazz() {
		AuthenticationDetailsSourceImpl authenticationDetailsSourceImpl = new AuthenticationDetailsSourceImpl();
		authenticationDetailsSourceImpl.setClazz(getClass());
	}
}

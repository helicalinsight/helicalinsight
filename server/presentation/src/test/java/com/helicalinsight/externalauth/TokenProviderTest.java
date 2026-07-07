package com.helicalinsight.externalauth;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.externalauth.jwt.TokenProvider;

public class TokenProviderTest {

	@Test
	public void testGenerateToken_a1() {
		TokenProvider tokenProvider = new TokenProvider();
		Authentication authentication = mock(Authentication.class);
		Principal principal = mock(Principal.class);
		when(authentication.getPrincipal()).thenReturn(principal);
		when(principal.getOrg_name()).thenReturn("org");
		tokenProvider.generateToken(authentication);

	}

	@Test
	public void testGenerateToken_a2() {
		TokenProvider tokenProvider = new TokenProvider();
		Authentication authentication = mock(Authentication.class);
		Principal principal = mock(Principal.class);
		when(authentication.getPrincipal()).thenReturn(principal);

		tokenProvider.generateToken(authentication);

	}
	
}

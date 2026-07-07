package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.context.SAMLMessageContext;
import com.helicalinsight.externalauth.saml.HISamlAuthenticationProvider;

public class HISamlAuthenticationProviderTest {

	@Test
	public void testAuthenticate_a1() {
		HISamlAuthenticationProvider authenticationProvider = new HISamlAuthenticationProvider();
		SAMLAuthenticationToken authentication = mock(SAMLAuthenticationToken.class);
		
		authenticationProvider.authenticate(authentication);	
	}
	@Test
	public void testAuthenticate_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HISamlAuthenticationProvider authenticationProvider = new HISamlAuthenticationProvider();
		SAMLAuthenticationToken authentication = mock(SAMLAuthenticationToken.class);
		SAMLMessageContext context = mock(SAMLMessageContext.class);
		when(context.getCommunicationProfileId()).thenReturn("urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser");
		when(authentication.getCredentials()).thenReturn(context);
		authenticationProvider.authenticate(authentication);	
	}
	@Test
	public void testAuthenticate_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HISamlAuthenticationProvider authenticationProvider = new HISamlAuthenticationProvider();
		SAMLAuthenticationToken authentication = mock(SAMLAuthenticationToken.class);
		SAMLMessageContext context = mock(SAMLMessageContext.class);
		when(context.getCommunicationProfileId()).thenReturn("urn:oasis:names:tc:SAML:2.0:profiles:holder-of-key:SSO:browser");
		when(authentication.getCredentials()).thenReturn(context);
		authenticationProvider.authenticate(authentication);	
	}
	@Test
	public void testAuthenticate_a4() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HISamlAuthenticationProvider authenticationProvider = new HISamlAuthenticationProvider();
		SAMLAuthenticationToken authentication = mock(SAMLAuthenticationToken.class);
		SAMLMessageContext context = mock(SAMLMessageContext.class);
		when(context.getCommunicationProfileId()).thenReturn("123");
		when(authentication.getCredentials()).thenReturn(context);
		authenticationProvider.authenticate(authentication);	
	}
	
}

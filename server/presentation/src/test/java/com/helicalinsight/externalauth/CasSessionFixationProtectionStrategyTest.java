package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import org.apereo.cas.client.session.SessionMappingStorage;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import com.helicalinsight.externalauth.cas.CasSessionFixationProtectionStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CasSessionFixationProtectionStrategyTest {

	@Test
	public void testOnAuthentication() {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		
		Authentication authentication = mock(Authentication.class);
		HttpServletRequest request    = mock(HttpServletRequest.class);
		HttpServletResponse response  = mock(HttpServletResponse.class);
		
		casSessionFixationProtectionStrategy.onAuthentication(authentication, request, response);
	}
	
	@Test
	public void testSetSessionMappingStorage() {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		SessionMappingStorage mappingStorage = mock(SessionMappingStorage.class);
		casSessionFixationProtectionStrategy.setSessionMappingStorage(mappingStorage);
	}
	
	@Test
	public void testSetArtifactParameterName() {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		casSessionFixationProtectionStrategy.setArtifactParameterName("artifactParameterName");
	}
	
	@Test
	public void testSetArtifactParameterOverPost() {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		casSessionFixationProtectionStrategy.setArtifactParameterOverPost(true);
	}
	@Test
	public void testSetLogoutParameterName() {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		casSessionFixationProtectionStrategy.setLogoutParameterName("logoutParameterName");
	}
	@Test
	public void testAfterPropertiesSet_a1() throws Exception {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		SessionMappingStorage mappingStorage = mock(SessionMappingStorage.class);
		casSessionFixationProtectionStrategy.setSessionMappingStorage(mappingStorage);
		casSessionFixationProtectionStrategy.setArtifactParameterOverPost(true);
		casSessionFixationProtectionStrategy.afterPropertiesSet();
	}
	@Test
	public void testAfterPropertiesSet_a2() throws Exception {
		CasSessionFixationProtectionStrategy casSessionFixationProtectionStrategy = new CasSessionFixationProtectionStrategy();
		SessionMappingStorage mappingStorage = mock(SessionMappingStorage.class);
		casSessionFixationProtectionStrategy.setSessionMappingStorage(mappingStorage);
		casSessionFixationProtectionStrategy.setArtifactParameterOverPost(false);
		casSessionFixationProtectionStrategy.afterPropertiesSet();
	}
}

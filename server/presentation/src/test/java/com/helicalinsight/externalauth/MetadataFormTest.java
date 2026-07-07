package com.helicalinsight.externalauth;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helicalinsight.externalauth.saml.MetadataForm;

public class MetadataFormTest {

	MetadataForm form = new MetadataForm();
	
	@Test
	public void testGetEntityId() {
		form.setEntityId("id");
		String entityId = form.getEntityId();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetAlias() {
		form.setAlias("id");
		String entityId = form.getAlias();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetSerializedMetadata() {
		form.setSerializedMetadata("id");
		String entityId = form.getSerializedMetadata();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetSigningKey() {
		form.setSigningKey("id");
		String entityId = form.getSigningKey();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetEncryptionKey() {
		form.setEncryptionKey("id");
		String entityId = form.getEncryptionKey();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetBaseURL() {
		form.setBaseURL("id");
		String entityId = form.getBaseURL();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetConfiguration() {
		form.setConfiguration("id");
		String entityId = form.getConfiguration();
		assertEquals(entityId, "id");
	}
	
	@Test
	public void testGetSecurityProfile() {
		form.setSecurityProfile("id");
		String entityId = form.getSecurityProfile();
		assertEquals(entityId, "id");
	}
	
	@Test
	public void testGetSslSecurityProfile() {
		form.setSslSecurityProfile("id");
		String entityId = form.getSslSecurityProfile();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetTlsKey() {
		form.setTlsKey("id");
		String entityId = form.getTlsKey();
		assertEquals(entityId, "id");
	}
	
	@Test
	public void testGetNameID() {
		String[] id = new String[] {"11","12"};
		form.setNameID(id);
		String[] entityId = form.getNameID();
		assertEquals(entityId, id);
	}
	
	@Test
	public void testGetCustomDiscoveryURL() {
		form.setCustomDiscoveryURL("id");
		String entityId = form.getCustomDiscoveryURL();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetCustomDiscoveryResponseURL() {
		form.setCustomDiscoveryResponseURL("id");
		String entityId = form.getCustomDiscoveryResponseURL();
		assertEquals(entityId, "id");
	}
	
	@Test
	public void testGetSsoDefaultBinding() {
		form.setSsoDefaultBinding("id");
		String entityId = form.getSsoDefaultBinding();
		assertEquals(entityId, "id");
	}
	
	@Test
	public void testGetSslHostnameVerification() {
		form.setSslHostnameVerification("id");
		String entityId = form.getSslHostnameVerification();
		assertEquals(entityId, "id");
	}
	@Test
	public void testGetSigningAlgorithm() {
		form.setSigningAlgorithm("id");
		String entityId = form.getSigningAlgorithm();
		assertEquals(entityId, "id");
	}
	
	@Test
	public void testGetSsoBindings() {
		String[] id = new String[] {"11","12"};
		form.setSsoBindings(id);
		String[] entityId = form.getSsoBindings();
		assertEquals(entityId, id);
	}
	@Test
	public void testIsIncludeDiscoveryExtension() {
		form.setIncludeDiscoveryExtension(true);
		boolean bool = form.isIncludeDiscoveryExtension();
		assertTrue(bool);
	}
	@Test
	public void testIsIncludeDiscovery() {
		form.setIncludeDiscovery(true);
		boolean bool = form.isIncludeDiscovery();
		assertTrue(bool);
	}
	@Test
	public void testIsStore() {
		form.setStore(true);
		boolean bool = form.isStore();
		assertTrue(bool);
	}
	
	@Test
	public void testIsRequireArtifactResolveSigned() {
		form.setRequireArtifactResolveSigned(true);
		boolean bool = form.isRequireArtifactResolveSigned();
		assertTrue(bool);
	}
	@Test
	public void testIsRequireLogoutResponseSigned() {
		form.setRequireLogoutResponseSigned(true);
		boolean bool = form.isRequireLogoutResponseSigned();
		assertTrue(bool);
	}
	@Test
	public void testIsRequireLogoutRequestSigned() {
		form.setRequireLogoutRequestSigned(true);
		boolean bool = form.isRequireLogoutRequestSigned();
		assertTrue(bool);
	}
	
	@Test
	public void testIsWantAssertionSigned() {
		form.setWantAssertionSigned(true);
		boolean bool = form.isWantAssertionSigned();
		assertTrue(bool);
	}
	@Test
	public void testIsRequestSigned() {
		form.setRequestSigned(true);
		boolean bool = form.isRequestSigned();
		assertTrue(bool);
	}
	@Test
	public void testIsSignMetadata() {
		form.setSignMetadata(true);
		boolean bool = form.isSignMetadata();
		assertTrue(bool);
	}
	
	@Test
	public void testIsLocal() {
		form.setLocal(true);
		boolean bool = form.isLocal();
		assertTrue(bool);
	}
}

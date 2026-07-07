package com.helicalinsight.externalauth;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.util.SAMLUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.externalauth.saml.AllowedSSOBindings;
import com.helicalinsight.externalauth.saml.SaveMDService;

public class SaveMDServiceTest {

	@Test
	public void testExecuteComponent_a1() throws MetadataProviderException {
		SaveMDService mdService = new SaveMDService();
		MetadataManager metadataManager = mock(MetadataManager.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		ExtendedMetadata extendedMetadata = mock(ExtendedMetadata.class);
		when(entityDescriptor.getEntityID()).thenReturn("id");
		when(metadataManager.getEntityDescriptor(anyString())).thenReturn(entityDescriptor);
		when(metadataManager.getExtendedMetadata(anyString())).thenReturn(extendedMetadata);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("action", "display");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataManager.class))
					.thenReturn(metadataManager);
			mdService.executeComponent(formData);
		}

	}

	@Test
	public void testExecuteComponent_a2() throws MetadataProviderException {
		SaveMDService mdService = new SaveMDService();
		MetadataManager metadataManager = mock(MetadataManager.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		ExtendedMetadata extendedMetadata = mock(ExtendedMetadata.class);
		when(entityDescriptor.getEntityID()).thenReturn("");
		when(metadataManager.getEntityDescriptor(anyString())).thenReturn(entityDescriptor);
		when(metadataManager.getExtendedMetadata(anyString())).thenReturn(extendedMetadata);
		when(extendedMetadata.getAlias()).thenReturn("alias");
		when(extendedMetadata.getSigningAlgorithm()).thenReturn("algorithm");
		when(extendedMetadata.getTlsKey()).thenReturn("tlskey");
		when(extendedMetadata.isIdpDiscoveryEnabled()).thenReturn(true);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("action", "display");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<SAMLUtil> saml = mockStatic(SAMLUtil.class)) {
				saml.when(() -> SAMLUtil.getMetadataAsString(metadataManager, null, entityDescriptor, extendedMetadata))
						.thenReturn("sample");

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataManager.class))
						.thenReturn(metadataManager);
				mdService.executeComponent(formData);
			}
		}

	}
	@Test(expected = EfwServiceException.class)
	public void testExecuteComponent_a3() throws MetadataProviderException {
		SaveMDService mdService = new SaveMDService();
		MetadataManager metadataManager = mock(MetadataManager.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		ExtendedMetadata extendedMetadata = mock(ExtendedMetadata.class);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("action", "display");
		String formData = json.toString();
		when(metadataManager.getEntityDescriptor(anyString())).thenReturn(null);
		when(metadataManager.getExtendedMetadata(anyString())).thenReturn(extendedMetadata);
		
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataManager.class))
					.thenReturn(metadataManager);
			mdService.executeComponent(formData);
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void testExecuteComponent_a4() {
		SaveMDService mdService = new SaveMDService();
		KeyManager keyManager = mock(KeyManager.class);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("baseURL", "url");
		json.addProperty("requestSigned", "yes");
		json.addProperty("wantAssertionSigned", "yes");
		json.addProperty("signingKey", "signingKey");
		json.addProperty("encryptionKey", "encryptionKey");
		json.addProperty("tlsKey", "tlsKey");
		json.addProperty("includeDiscovery", "yes");
		json.addProperty("includeDiscoveryExtension", "yes");
		json.addProperty("customDiscoveryURL", "url");
		json.addProperty("customDiscoveryResponseUrl", "url");
		json.addProperty("alias", "alias");
		json.addProperty("securityProfile", "securityProfile");
		json.addProperty("sslSecurityProfile", "sslSecurityProfile");
		json.addProperty("requireLogoutRequestSigned", "yes");
		json.addProperty("requireLogoutResponseSigned", "yes");
		json.addProperty("requireArtifactResolveSigned", "yes");
		json.addProperty("signMetadata", "yes");
		json.addProperty("sslHostnameVerification", "sslHostnameVerification");
		json.addProperty("signingAlgorithm", "signingAlgorithm");
		
		
		json.addProperty("ssoDefaultBinding", AllowedSSOBindings.SSO_POST.toString());
		JsonArray arr = new JsonArray();
		arr.add(AllowedSSOBindings.SSO_POST.toString());
		arr.add(AllowedSSOBindings.SSO_ARTIFACT.toString());
		arr.add(AllowedSSOBindings.SSO_PAOS.toString());
		arr.add(AllowedSSOBindings.HOKSSO_POST.toString());
		arr.add(AllowedSSOBindings.HOKSSO_ARTIFACT.toString());		
		json.add("ssoBindings", arr);
		JsonArray nameArr = new JsonArray();
		
		nameArr.add("object");
		json.add("nameID", nameArr);
		json.addProperty("action", "");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class))
			.thenReturn(keyManager);
		mdService.executeComponent(formData);
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void testExecuteComponent_a5() {
		SaveMDService mdService = new SaveMDService();
		KeyManager keyManager = mock(KeyManager.class);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("baseURL", "url");
		json.addProperty("requestSigned", "yes");
		json.addProperty("wantAssertionSigned", "yes");
		json.addProperty("signingKey", "signingKey");
		json.addProperty("encryptionKey", "encryptionKey");
		json.addProperty("tlsKey", "None");
		json.addProperty("includeDiscovery", "yes");
		json.addProperty("includeDiscoveryExtension", "yes");
		json.addProperty("customDiscoveryURL", "NA");
		json.addProperty("customDiscoveryResponseUrl", "");
		json.addProperty("alias", "");
		json.addProperty("securityProfile", "securityProfile");
		json.addProperty("sslSecurityProfile", "sslSecurityProfile");
		json.addProperty("requireLogoutRequestSigned", "yes");
		json.addProperty("requireLogoutResponseSigned", "yes");
		json.addProperty("requireArtifactResolveSigned", "yes");
		json.addProperty("signMetadata", "yes");
		json.addProperty("sslHostnameVerification", "sslHostnameVerification");
		json.addProperty("signingAlgorithm", "");
		
		
		json.addProperty("ssoDefaultBinding", AllowedSSOBindings.SSO_POST.toString());
		JsonArray arr = new JsonArray();
		arr.add(AllowedSSOBindings.SSO_POST.toString());
		arr.add(AllowedSSOBindings.SSO_ARTIFACT.toString());
		arr.add(AllowedSSOBindings.SSO_PAOS.toString());
		arr.add(AllowedSSOBindings.HOKSSO_POST.toString());
		arr.add(AllowedSSOBindings.HOKSSO_ARTIFACT.toString());		
		json.add("ssoBindings", arr);
		JsonArray nameArr = new JsonArray();
		
		nameArr.add("object");
		json.add("nameID", nameArr);
		json.addProperty("action", "");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class))
			.thenReturn(keyManager);
		mdService.executeComponent(formData);
		}
	}
	
	
	@Test(expected = NullPointerException.class)
	public void testExecuteComponent_a6() {
		SaveMDService mdService = new SaveMDService();
		KeyManager keyManager = mock(KeyManager.class);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("baseURL", "url");
		json.addProperty("requestSigned", "yes");
		json.addProperty("wantAssertionSigned", "yes");
		json.addProperty("signingKey", "signingKey");
		json.addProperty("encryptionKey", "encryptionKey");
		json.addProperty("tlsKey", "");
		json.addProperty("includeDiscovery", "yes");
		json.addProperty("includeDiscoveryExtension", "yes");
		json.addProperty("customDiscoveryURL", "");
		json.add("customDiscoveryResponseUrl", null);
		json.addProperty("alias", "NA");
		json.addProperty("securityProfile", "securityProfile");
		json.addProperty("sslSecurityProfile", "sslSecurityProfile");
		json.addProperty("requireLogoutRequestSigned", "yes");
		json.addProperty("requireLogoutResponseSigned", "yes");
		json.addProperty("requireArtifactResolveSigned", "yes");
		json.addProperty("signMetadata", "yes");
		json.addProperty("sslHostnameVerification", "sslHostnameVerification");
		json.addProperty("signingAlgorithm", "NA");
		
		
		json.addProperty("ssoDefaultBinding", AllowedSSOBindings.SSO_POST.toString());
		JsonArray arr = new JsonArray();
		arr.add(AllowedSSOBindings.SSO_POST.toString());
		arr.add(AllowedSSOBindings.SSO_ARTIFACT.toString());
		arr.add(AllowedSSOBindings.SSO_PAOS.toString());
		arr.add(AllowedSSOBindings.HOKSSO_POST.toString());
		arr.add(AllowedSSOBindings.HOKSSO_ARTIFACT.toString());		
		json.add("ssoBindings", arr);
		JsonArray nameArr = new JsonArray();
		
		nameArr.add("object");
		json.add("nameID", nameArr);
		json.addProperty("action", "");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class))
			.thenReturn(keyManager);
		mdService.executeComponent(formData);
		}
	}

	@Test(expected= UnsupportedOperationException.class)
	public void testExecuteComponent_a7() {
		SaveMDService mdService = new SaveMDService();
		KeyManager keyManager = mock(KeyManager.class);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("baseURL", "url");
		json.addProperty("requestSigned", "yes");
		json.addProperty("wantAssertionSigned", "yes");
		json.addProperty("signingKey", "signingKey");
		json.addProperty("encryptionKey", "encryptionKey");
		json.addProperty("tlsKey", "");
		json.addProperty("includeDiscovery", "yes");
		json.addProperty("includeDiscoveryExtension", "yes");
		json.add("customDiscoveryURL", null);
		json.add("customDiscoveryResponseUrl", null);
		json.addProperty("alias", "NA");
		json.addProperty("securityProfile", "securityProfile");
		json.addProperty("sslSecurityProfile", "sslSecurityProfile");
		json.addProperty("requireLogoutRequestSigned", "yes");
		json.addProperty("requireLogoutResponseSigned", "yes");
		json.addProperty("requireArtifactResolveSigned", "yes");
		json.addProperty("signMetadata", "yes");
		json.addProperty("sslHostnameVerification", "sslHostnameVerification");
		json.addProperty("signingAlgorithm", "NA");
		
		
		json.addProperty("ssoDefaultBinding", AllowedSSOBindings.SSO_POST.toString());
		JsonArray arr = new JsonArray();
		arr.add(AllowedSSOBindings.SSO_POST.toString());
		arr.add(AllowedSSOBindings.SSO_ARTIFACT.toString());
		arr.add(AllowedSSOBindings.SSO_PAOS.toString());
		arr.add(AllowedSSOBindings.HOKSSO_POST.toString());
		arr.add(AllowedSSOBindings.HOKSSO_ARTIFACT.toString());		
		json.add("ssoBindings", arr);
		JsonArray nameArr = new JsonArray();
		
		nameArr.add("object");
		json.add("nameID", nameArr);
		json.addProperty("action", "");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class))
			.thenReturn(keyManager);
		mdService.executeComponent(formData);
		}
	}
	@Test(expected = NullPointerException.class)
	public void testExecuteComponent_a8() {
		SaveMDService mdService = new SaveMDService();
		KeyManager keyManager = mock(KeyManager.class);
		JsonObject json = new JsonObject();
		json.addProperty("entityId", "id");
		json.addProperty("baseURL", "url");
		json.addProperty("requestSigned", "yes");
		json.addProperty("wantAssertionSigned", "yes");
		json.addProperty("signingKey", "signingKey");
		json.addProperty("encryptionKey", "encryptionKey");
		json.addProperty("tlsKey", "");
		json.addProperty("includeDiscovery", "NO");
		
		json.addProperty("alias", "NA");
		json.addProperty("securityProfile", "securityProfile");
		json.addProperty("sslSecurityProfile", "sslSecurityProfile");
		json.addProperty("requireLogoutRequestSigned", "yes");
		json.addProperty("requireLogoutResponseSigned", "yes");
		json.addProperty("requireArtifactResolveSigned", "yes");
		json.addProperty("signMetadata", "yes");
		json.addProperty("sslHostnameVerification", "sslHostnameVerification");
		json.addProperty("signingAlgorithm", "NA");
		
		
		json.addProperty("ssoDefaultBinding", AllowedSSOBindings.SSO_POST.toString());
		JsonArray arr = new JsonArray();
		arr.add(AllowedSSOBindings.SSO_POST.toString());
		arr.add(AllowedSSOBindings.SSO_ARTIFACT.toString());
		arr.add(AllowedSSOBindings.SSO_PAOS.toString());
		arr.add(AllowedSSOBindings.HOKSSO_POST.toString());
		arr.add(AllowedSSOBindings.HOKSSO_ARTIFACT.toString());		
		json.add("ssoBindings", arr);
		JsonArray nameArr = new JsonArray();
		
		nameArr.add("object");
		json.add("nameID", nameArr);
		json.addProperty("action", "");
		String formData = json.toString();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class))
			.thenReturn(keyManager);
		mdService.executeComponent(formData);
		}
	}
	
	@Test
	public void testIsThreadSafeToCache() {
		SaveMDService mdService = new SaveMDService();
		boolean threadSafeToCache = mdService.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}

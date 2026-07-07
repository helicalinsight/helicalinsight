package com.helicalinsight.externalauth;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.credential.Credential;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.externalauth.saml.AllowedSSOBindings;
import com.helicalinsight.externalauth.saml.MetadataController;
import com.helicalinsight.externalauth.saml.MetadataForm;

import jakarta.servlet.http.HttpServletRequest;

public class MetadataControllerTest {

	@Test
	public void testAfterSet_a1() {
		MetadataController controller = new MetadataController();
		KeyManager keyManager = mock(KeyManager.class);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class)).thenReturn(keyManager);
			controller.afterSet();
		}

	}

	@Test
	public void testAfterSet_a2() {
		MetadataController controller = new MetadataController();
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(KeyManager.class))
					.thenThrow(new NoSuchBeanDefinitionException("exception"));
			controller.afterSet();
		}

	}

	@Test
	public void testMetadataList() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, MetadataProviderException {
		MetadataController controller = new MetadataController();
		MetadataManager metadataManager = mock(MetadataManager.class);
		Field field;

		field = MetadataController.class.getDeclaredField("metadataManager");
		field.setAccessible(true);
		field.set(controller, metadataManager);
		controller.metadataList();

	}

	@Test
	public void testDisplayProvider()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		MetadataManager metadataManager = mock(MetadataManager.class);
		ExtendedMetadataDelegate delegate = mock(ExtendedMetadataDelegate.class);
		List<ExtendedMetadataDelegate> availableProviders = Arrays.asList(delegate);

		when(metadataManager.getAvailableProviders()).thenReturn((List<ExtendedMetadataDelegate>) availableProviders);
		Field field;

		field = MetadataController.class.getDeclaredField("metadataManager");
		field.setAccessible(true);
		field.set(controller, metadataManager);
		ModelAndView displayProvider = controller.displayProvider(0);
		assertNotNull(displayProvider);

	}

	@Test
	public void testGenerateMetadata_getAvailablePrivateKeys_a1() throws KeyStoreException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		KeyManager keyManager = mock(KeyManager.class);
		Credential credential = mock(Credential.class);
		PrivateKey privateKey = mock(PrivateKey.class);
		Field field = MetadataController.class.getDeclaredField("keyManager");
		field.setAccessible(true);
		field.set(controller, keyManager);

		Set<String> aliases = new HashSet<>();
		aliases.add("key");
		when(keyManager.getAvailableCredentials()).thenReturn(aliases);
		when(keyManager.getCredential(anyString())).thenReturn(credential);
		when(credential.getPrivateKey()).thenReturn(privateKey);
		when(credential.getEntityId()).thenReturn("123");
		controller.generateMetadata(request);
	}

	@Test
	public void testGenerateMetadata_getAvailablePrivateKeys_a2() throws KeyStoreException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		KeyManager keyManager = mock(KeyManager.class);
		Credential credential = mock(Credential.class);
		PrivateKey privateKey = mock(PrivateKey.class);
		Field field = MetadataController.class.getDeclaredField("keyManager");
		field.setAccessible(true);
		field.set(controller, keyManager);

		Set<String> aliases = new HashSet<>();
		aliases.add("key");
		when(keyManager.getAvailableCredentials()).thenReturn(aliases);
		when(keyManager.getCredential(anyString())).thenReturn(credential);
		when(credential.getPrivateKey()).thenReturn(null);
		when(credential.getEntityId()).thenReturn("123");
		controller.generateMetadata(request);
	}

	@Test
	public void testGenerateMetadata_getAvailablePrivateKeys_a3() throws KeyStoreException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		KeyManager keyManager = mock(KeyManager.class);
		Credential credential = mock(Credential.class);
		PrivateKey privateKey = mock(PrivateKey.class);
		Field field = MetadataController.class.getDeclaredField("keyManager");
		field.setAccessible(true);
		field.set(controller, keyManager);

		Set<String> aliases = new HashSet<>();
		aliases.add("key");
		when(keyManager.getAvailableCredentials()).thenReturn(aliases);
		when(keyManager.getCredential(anyString())).thenReturn(credential);
		when(credential.getPrivateKey()).thenThrow(new RuntimeException("exception"));
		when(credential.getEntityId()).thenReturn("123");
		controller.generateMetadata(request);
	}

	@Test
	public void testRefreshMetadata() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, MetadataProviderException {
		MetadataController controller = new MetadataController();
		MetadataManager metadataManager = mock(MetadataManager.class);
		Field field;

		field = MetadataController.class.getDeclaredField("metadataManager");
		field.setAccessible(true);
		field.set(controller, metadataManager);
		controller.refreshMetadata();
	}

	private String[] ssoBindings = new String[] { AllowedSSOBindings.SSO_POST.toString(),
			AllowedSSOBindings.SSO_ARTIFACT.toString(), AllowedSSOBindings.SSO_PAOS.toString(),
			AllowedSSOBindings.HOKSSO_POST.toString(), AllowedSSOBindings.HOKSSO_ARTIFACT.toString() };

	@Test(expected = NullPointerException.class)
	public void testCreateMetadata_a1() throws KeyStoreException, MetadataProviderException, MarshallingException {
		MetadataController controller = new MetadataController();
		MetadataForm metadata = mock(MetadataForm.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(metadata.getSsoBindings()).thenReturn(ssoBindings);
		when(metadata.getSsoDefaultBinding()).thenReturn(AllowedSSOBindings.SSO_POST.toString());
		String[] nameID = new String[] { "11", "12" };
		when(metadata.getNameID()).thenReturn(nameID);

		when(metadata.getEntityId()).thenReturn("11");
		when(metadata.getBaseURL()).thenReturn("www.example.com");
		when(metadata.getAlias()).thenReturn("alias");
		when(metadata.getSigningAlgorithm()).thenReturn("algorithm");
		when(metadata.getTlsKey()).thenReturn("tlskey");
		controller.createMetadata(metadata, bindingResult);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateMetadata_a2() throws KeyStoreException, MetadataProviderException, MarshallingException {
		MetadataController controller = new MetadataController();
		MetadataForm metadata = mock(MetadataForm.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(metadata.getSsoBindings()).thenReturn(ssoBindings);
		when(metadata.getSsoDefaultBinding()).thenReturn(AllowedSSOBindings.SSO_POST.toString());
		String[] nameID = new String[] { "11", "12" };
		when(metadata.getNameID()).thenReturn(nameID);

		when(metadata.getEntityId()).thenReturn("11");
		when(metadata.getBaseURL()).thenReturn("www.example.com");

		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn("www.example.com");
		when(metadata.getCustomDiscoveryURL()).thenReturn("www.example.com");
		controller.createMetadata(metadata, bindingResult);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateMetadata_a3() throws KeyStoreException, MetadataProviderException, MarshallingException {
		MetadataController controller = new MetadataController();
		MetadataForm metadata = mock(MetadataForm.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(metadata.getSsoBindings()).thenReturn(ssoBindings);
		when(metadata.getSsoDefaultBinding()).thenReturn(AllowedSSOBindings.SSO_POST.toString());
		String[] nameID = new String[] { "11", "12" };
		when(metadata.getNameID()).thenReturn(nameID);

		when(metadata.getEntityId()).thenReturn("11");
		when(metadata.getBaseURL()).thenReturn("www.example.com");

		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn(null);
		when(metadata.getCustomDiscoveryURL()).thenReturn(null);
		controller.createMetadata(metadata, bindingResult);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateMetadata_a4() throws KeyStoreException, MetadataProviderException, MarshallingException {
		MetadataController controller = new MetadataController();
		MetadataForm metadata = mock(MetadataForm.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(metadata.getSsoBindings()).thenReturn(ssoBindings);
		when(metadata.getSsoDefaultBinding()).thenReturn(AllowedSSOBindings.SSO_POST.toString());
		String[] nameID = new String[] { "11", "12" };
		when(metadata.getNameID()).thenReturn(nameID);

		when(metadata.getEntityId()).thenReturn("11");
		when(metadata.getBaseURL()).thenReturn("www.example.com");

		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn("");
		when(metadata.getCustomDiscoveryURL()).thenReturn("");
		controller.createMetadata(metadata, bindingResult);
	}

	@Test
	public void testCreateMetadata_a5() throws KeyStoreException, MetadataProviderException, MarshallingException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		MetadataForm metadata = mock(MetadataForm.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		KeyManager keyManager = mock(KeyManager.class);
		Credential credential = mock(Credential.class);
		PrivateKey privateKey = mock(PrivateKey.class);
		Field field = MetadataController.class.getDeclaredField("keyManager");
		field.setAccessible(true);
		field.set(controller, keyManager);

		Set<String> aliases = new HashSet<>();
		aliases.add("key");
		when(keyManager.getAvailableCredentials()).thenReturn(aliases);
		when(keyManager.getCredential(anyString())).thenReturn(credential);
		when(credential.getPrivateKey()).thenReturn(privateKey);
		when(credential.getEntityId()).thenReturn("123");

		controller.createMetadata(metadata, bindingResult);
	}

	@Test
	public void testDisplayMetadata_a1() throws MetadataProviderException, MarshallingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		MetadataManager metadataManager = mock(MetadataManager.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		ExtendedMetadata extendedMetadata = mock( ExtendedMetadata.class);
		Field field = MetadataController.class.getDeclaredField("metadataManager");
		field.setAccessible(true);
		field.set(controller, metadataManager);
		KeyManager keyManager = mock(KeyManager.class);
		Field field1 = MetadataController.class.getDeclaredField("keyManager");
		field1.setAccessible(true);
		field1.set(controller, keyManager);
		when(metadataManager.getEntityDescriptor(anyString())).thenReturn(entityDescriptor);
		when(metadataManager.getExtendedMetadata(anyString())).thenReturn(extendedMetadata);
		when(entityDescriptor.getEntityID()).thenReturn("entityId");
		try(MockedStatic<SAMLUtil> mockedStatic = mockStatic(SAMLUtil.class)){
			mockedStatic.when(() -> SAMLUtil.getMetadataAsString(metadataManager, keyManager, entityDescriptor, extendedMetadata)).thenReturn("SampleString");
		
			ModelAndView displayMetadata = controller.displayMetadata("12");
			assertNotNull(displayMetadata);
			
		}
		

	}
	
	@Test
	public void testDisplayMetadata_a2() throws MetadataProviderException, MarshallingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		MetadataManager metadataManager = mock(MetadataManager.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		ExtendedMetadata extendedMetadata = mock( ExtendedMetadata.class);
		Field field = MetadataController.class.getDeclaredField("metadataManager");
		field.setAccessible(true);
		field.set(controller, metadataManager);
		KeyManager keyManager = mock(KeyManager.class);
		Field field1 = MetadataController.class.getDeclaredField("keyManager");
		field1.setAccessible(true);
		field1.set(controller, keyManager);
		when(metadataManager.getEntityDescriptor(anyString())).thenReturn(entityDescriptor);
		when(metadataManager.getExtendedMetadata(anyString())).thenReturn(extendedMetadata);
		when(entityDescriptor.getEntityID()).thenReturn("");
		when(extendedMetadata.getAlias()).thenReturn("alias");
		when(extendedMetadata.getTlsKey()).thenReturn("tlskey");
		when(extendedMetadata.getSigningAlgorithm()).thenReturn("algorithm");
		when(extendedMetadata.isIdpDiscoveryEnabled()).thenReturn(true);
		try(MockedStatic<SAMLUtil> mockedStatic = mockStatic(SAMLUtil.class)){
			mockedStatic.when(() -> SAMLUtil.getMetadataAsString(metadataManager, keyManager, entityDescriptor, extendedMetadata)).thenReturn("SampleString");
		
			ModelAndView displayMetadata = controller.displayMetadata("12");
			assertNotNull(displayMetadata);
			
		}
		

	}
	@Test(expected = MetadataProviderException.class)
	public void testDisplayMetadata_a3() throws MetadataProviderException, MarshallingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataController controller = new MetadataController();
		MetadataManager metadataManager = mock(MetadataManager.class);
		
		Field field = MetadataController.class.getDeclaredField("metadataManager");
		field.setAccessible(true);
		field.set(controller, metadataManager);
		
		controller.displayMetadata("12");
	}

}

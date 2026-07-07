package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.validation.Errors;

import com.helicalinsight.externalauth.saml.MetadataForm;
import com.helicalinsight.externalauth.saml.MetadataValidator;

public class MetadataValidatorTest {

	@Test
	public void testSupports() {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		metadataValidator.supports(getClass());
	}
	
	@Test
	public void testValidate_a1() {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		Errors errors = mock(Errors.class);
		Object object = metadata;
		metadataValidator.validate(object, errors);
	}
	
	@Test
	public void testValidate_a2() {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		Errors errors = mock(Errors.class);
		Object object = metadata;
		when(metadata.getSecurityProfile()).thenReturn("profile");
		when(metadata.getSslSecurityProfile()).thenReturn("ssl");
		metadataValidator.validate(object, errors);
	}
	@Test
	public void testValidate_a3() {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		Errors errors = mock(Errors.class);
		Object object = metadata;
		when(metadata.getSecurityProfile()).thenReturn("pkix");
		when(metadata.getSslSecurityProfile()).thenReturn("pkix");
		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryURL()).thenReturn("https://www.example.com");
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn("https://www.example.com");
		when(metadata.isStore()).thenReturn(true);
		when(errors.hasErrors()).thenReturn(false);
		metadataValidator.validate(object, errors);
	}
	@Test
	public void testValidate_a4() {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		Errors errors = mock(Errors.class);
		Object object = metadata;
		when(metadata.getSecurityProfile()).thenReturn("metaiop");
		when(metadata.getSslSecurityProfile()).thenReturn("metaiop");
		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryURL()).thenReturn("www.example.com");
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn("www.example.com");
		when(metadata.isStore()).thenReturn(true);
		String[] id = new String[] {"11","12"};
		when(metadata.getSsoBindings()).thenReturn(id);
		
		when(metadata.getNameID()).thenReturn(id);
		when(errors.hasErrors()).thenReturn(false);
		metadataValidator.validate(object, errors);
	}
	@Test
	public void testValidate_a5() {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		Errors errors = mock(Errors.class);
		Object object = metadata;
		
		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryURL()).thenReturn("");
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn("");
		when(metadata.isStore()).thenReturn(true);
		when(metadata.getSsoBindings()).thenReturn(new String[0]);
		String[] id = new String[0] ;
		when(metadata.getNameID()).thenReturn(id);
		when(errors.hasErrors()).thenReturn(false);
		metadataValidator.validate(object, errors);
	}
	@Test
	public void testValidate_a6() throws MetadataProviderException {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		
		
		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryURL()).thenReturn(null);
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn(null);
		when(metadata.isStore()).thenReturn(true);
		when(metadata.getSsoBindings()).thenReturn(new String[] {"11"});
		when(metadata.getSsoDefaultBinding()).thenReturn("11");
		when(metadata.getEntityId()).thenReturn("id");
		when(metadata.getAlias()).thenReturn("alias");
		Errors errors = mock(Errors.class);
		Object object = metadata;
		when(manager.getEntityDescriptor(anyString())).thenReturn(entityDescriptor);
		when(manager.getEntityIdForAlias(anyString())).thenReturn("alias");
		metadataValidator.validate(object, errors);
	}
	@Test(expected = RuntimeException.class)
	public void testValidate_a7() throws MetadataProviderException {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
		
		
		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryURL()).thenReturn(null);
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn(null);
		when(metadata.isStore()).thenReturn(true);
		when(metadata.getSsoBindings()).thenReturn(new String[] {"11"});
		when(metadata.getSsoDefaultBinding()).thenReturn("12");
		when(metadata.getEntityId()).thenReturn("id");
		when(metadata.getAlias()).thenReturn("alias");
		Errors errors = mock(Errors.class);
		Object object = metadata;
		when(manager.getEntityDescriptor(anyString())).thenReturn(entityDescriptor);
		when(manager.getEntityIdForAlias(anyString())).thenThrow(new MetadataProviderException("exception"));
		metadataValidator.validate(object, errors);
	}
	@Test
	public void testValidate_a8() throws MetadataProviderException {
		MetadataManager manager = mock(MetadataManager.class);
		MetadataValidator metadataValidator = new MetadataValidator(manager);
		MetadataForm metadata = mock(MetadataForm.class);
		
		
		when(metadata.isIncludeDiscovery()).thenReturn(true);
		when(metadata.getCustomDiscoveryURL()).thenReturn(null);
		when(metadata.getCustomDiscoveryResponseURL()).thenReturn(null);
		when(metadata.isStore()).thenReturn(false);
		when(metadata.getSsoBindings()).thenReturn(null);
		when(metadata.getSsoDefaultBinding()).thenReturn("11");
		when(metadata.getEntityId()).thenReturn("id");
		when(metadata.getAlias()).thenReturn("alias");
		Errors errors = mock(Errors.class);
		Object object = metadata;
		when(errors.hasErrors()).thenReturn(true);
		metadataValidator.validate(object, errors);
	}
}

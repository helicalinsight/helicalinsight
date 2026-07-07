package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.util.SAMLUtil;
import org.w3c.dom.Element;

import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.UserSynchronizationUtils;
import com.helicalinsight.externalauth.saml.HISamlUserDetailService;

public class HISamlUserDetailServiceTest {

	@Test(expected =NullPointerException.class)
	public void testLoadUserBySAML_a1() {
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		detailService.loadUserBySAML(credential);
	}
	@Test(expected = UsernameNotFoundException.class)
	public void testLoadUserBySAML_a2() throws NoSuchFieldException, 
	SecurityException, IllegalArgumentException, IllegalAccessException {
		
		
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		
		Field field = HISamlUserDetailService.class.getDeclaredField("organizationKey");
		field.setAccessible(true);
		field.set(detailService, "key");
		
		UserService userService = mock(UserService.class);
		Field field1 = HISamlUserDetailService.class.getDeclaredField("userService");
		field1.setAccessible(true);
		field1.set(detailService,userService);
		
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		when(credential.getAttributeAsString(anyString())).thenReturn("org");
		when(userService.loadUserByUsername(anyString())).thenThrow(new  UsernameNotFoundException("exception"));
		detailService.loadUserBySAML(credential);
	}
	@Test
	public void testLoadUserBySAML_a3() throws NoSuchFieldException, 
	SecurityException, IllegalArgumentException, IllegalAccessException {
		
		
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		
		Field field = HISamlUserDetailService.class.getDeclaredField("organizationKey");
		field.setAccessible(true);
		field.set(detailService, "key");
		
		UserDetails userDetails = mock( UserDetails.class);
		UserService userService = mock(UserService.class);
		Field field1 = HISamlUserDetailService.class.getDeclaredField("userService");
		field1.setAccessible(true);
		field1.set(detailService,userService);
		
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		when(credential.getAttributeAsString(anyString())).thenReturn("org");
		when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);
		detailService.loadUserBySAML(credential);
	}
	
	@Test
	public void testLoadUserBySAML_a4() throws NoSuchFieldException, 
	SecurityException, IllegalArgumentException, IllegalAccessException {
		
		
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		
		Field field = HISamlUserDetailService.class.getDeclaredField("organizationKey");
		field.setAccessible(true);
		field.set(detailService, "key");
		
		
		UserService userService = mock(UserService.class);
		Field field1 = HISamlUserDetailService.class.getDeclaredField("userService");
		field1.setAccessible(true);
		field1.set(detailService,userService);
		
		Field field2 = HISamlUserDetailService.class.getDeclaredField("profileKey");
		field2.setAccessible(true);
		field2.set(detailService,"key,value");
		
		List<Attribute> attributesz = new ArrayList<>();
		Attribute attribute = mock(Attribute.class);
		attributesz.add(attribute);
		when(attribute.getName()).thenReturn("key");
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		when(credential.getAttributeAsString(anyString())).thenReturn("org");
		when(userService.loadUserByUsername(anyString())).thenReturn(null);
		when(credential.getAttributes()).thenReturn(attributesz);
		when(credential.getAttributeAsString(anyString())).thenReturn("value");
		
		Assertion assertion = mock(Assertion.class);
		when(credential.getAuthenticationAssertion()).thenReturn(assertion);
		try(MockedStatic<XMLHelper> xml = mockStatic(XMLHelper.class)){
			try(MockedStatic<SAMLUtil> saml = mockStatic(SAMLUtil.class)){
				Element element = mock(Element.class);
				saml.when(() -> SAMLUtil.marshallMessage(assertion)).thenReturn(element);
				xml.when(() ->XMLHelper.nodeToString(element)).thenReturn("yes");
				detailService.loadUserBySAML(credential);
			}
		}
		
		
	}
	
	
	@Test
	public void testLoadUserBySAML_a5() throws NoSuchFieldException, 
	SecurityException, IllegalArgumentException, IllegalAccessException {
		
		
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		
		Field field = HISamlUserDetailService.class.getDeclaredField("organizationKey");
		field.setAccessible(true);
		field.set(detailService, "key");
		
		
		UserService userService = mock(UserService.class);
		Field field1 = HISamlUserDetailService.class.getDeclaredField("userService");
		field1.setAccessible(true);
		field1.set(detailService,userService);
		
		Field field2 = HISamlUserDetailService.class.getDeclaredField("profileKey");
		field2.setAccessible(true);
		field2.set(detailService,"key,value");
		
		
		
		List<Attribute> attributesz = new ArrayList<>();
		Attribute attribute = mock(Attribute.class);
		attributesz.add(attribute);
		when(attribute.getName()).thenReturn("set");
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		when(credential.getAttributeAsString(anyString())).thenReturn("org");
		when(userService.loadUserByUsername(anyString())).thenReturn(null);
		when(credential.getAttributes()).thenReturn(attributesz);
		when(credential.getAttributeAsString(anyString())).thenReturn("value");
		
		Assertion assertion = mock(Assertion.class);
		when(credential.getAuthenticationAssertion()).thenReturn(assertion);
		try(MockedStatic<XMLHelper> xml = mockStatic(XMLHelper.class)){
			try(MockedStatic<SAMLUtil> saml = mockStatic(SAMLUtil.class)){
				Element element = mock(Element.class);
				saml.when(() -> SAMLUtil.marshallMessage(assertion)).thenThrow(new MessageEncodingException());
				xml.when(() ->XMLHelper.nodeToString(element)).thenReturn("yes");
				detailService.loadUserBySAML(credential);
			}
		}
		
		
	}
	@Test
	public void testLoadUserBySAML_a6() throws NoSuchFieldException, 
	SecurityException, IllegalArgumentException, IllegalAccessException {
		
		
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		
		Field field = HISamlUserDetailService.class.getDeclaredField("organizationKey");
		field.setAccessible(true);
		field.set(detailService, "key");
		
		
		UserService userService = mock(UserService.class);
		Field field1 = HISamlUserDetailService.class.getDeclaredField("userService");
		field1.setAccessible(true);
		field1.set(detailService,userService);
		
		Field field2 = HISamlUserDetailService.class.getDeclaredField("profileKey");
		field2.setAccessible(true);
		field2.set(detailService,"key,value");
		
		List<Attribute> attributesz = new ArrayList<>();
		Attribute attribute = mock(Attribute.class);
		attributesz.add(attribute);
		when(attribute.getName()).thenReturn(null);
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		when(credential.getAttributeAsString(anyString())).thenReturn("org");
		when(userService.loadUserByUsername(anyString())).thenReturn(null);
		when(credential.getAttributes()).thenReturn(attributesz);
		when(credential.getAttributeAsString(anyString())).thenReturn("value");
		
		
		
		detailService.loadUserBySAML(credential);
	}
	

@Test
	public void testLoadUserBySAML_a7() throws NoSuchFieldException, 
	SecurityException, IllegalArgumentException, IllegalAccessException {
		
		
		HISamlUserDetailService detailService = new HISamlUserDetailService();
		
		Field field = HISamlUserDetailService.class.getDeclaredField("organizationKey");
		field.setAccessible(true);
		field.set(detailService, "key");
		
		
		UserService userService = mock(UserService.class);
		Field field1 = HISamlUserDetailService.class.getDeclaredField("userService");
		field1.setAccessible(true);
		field1.set(detailService,userService);
		
		
		
	
		SAMLCredential credential = mock(SAMLCredential.class);
		NameID nameID = mock(NameID.class);
		when(credential.getNameID()).thenReturn(nameID);
		when(nameID.getValue()).thenReturn("username");
		when(credential.getAttributeAsString(anyString())).thenReturn("org");
		when(userService.loadUserByUsername(anyString())).thenReturn(null);
		when(credential.getAttributes()).thenReturn(null);
		when(credential.getAttributeAsString(anyString())).thenReturn("value");
		
		
		
		detailService.loadUserBySAML(credential);
	}
	
	
	
	

	

	
}

package com.helicalinsight.externalauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helicalinsight.externalauth.saml.AllowedSSOBindings;

public class AllowedSSOBindingsTest {

	
	@Test
    public void testEnumValues() {
        assertEquals(AllowedSSOBindings.values().length, 5);
        assertEquals(AllowedSSOBindings.SSO_POST, AllowedSSOBindings.valueOf("SSO_POST"));
        assertEquals(AllowedSSOBindings.SSO_PAOS, AllowedSSOBindings.valueOf("SSO_PAOS"));
        assertEquals(AllowedSSOBindings.SSO_ARTIFACT, AllowedSSOBindings.valueOf("SSO_ARTIFACT"));
        assertEquals(AllowedSSOBindings.HOKSSO_POST, AllowedSSOBindings.valueOf("HOKSSO_POST"));
        assertEquals(AllowedSSOBindings.HOKSSO_ARTIFACT, AllowedSSOBindings.valueOf("HOKSSO_ARTIFACT"));
    }

}

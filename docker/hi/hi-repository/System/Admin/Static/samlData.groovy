import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.externalauth.saml.MetadataForm;
import org.opensaml.xml.security.credential.Credential;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.MetadataGenerator;

		MetadataForm m = new MetadataForm();

        String availableKeys = "";
        KeyManager keyManager = ApplicationContextAccessor.getBean(KeyManager.class);
        Set<String> aliases = keyManager.getAvailableCredentials();
        for (String key : aliases) {
            try {
                Credential credential = keyManager.getCredential(key);
                if (credential.getPrivateKey() != null) {
					availableKeys=availableKeys+"'"+key+"_#_"+key+"',"
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		if(availableKeys.endsWith(",")){
		availableKeys=availableKeys.substring(0,availableKeys.length()-1);
		}
        m.setBaseURL(formData.hiBaseURL);
        m.setEntityId("localhost");
        m.setNameID(MetadataGenerator.defaultNameID.toArray(new String[MetadataGenerator.defaultNameID.size()]));


def jsonObj="""																		 
{
	"data": [
		{
			"type": "select",
			"label": "Store for the current session",
			"key":"store",
			"options": [
				"Yes_#_Yes",
				"No_#_No"
			],
			"desc":"When set to true the generated metadata will be stored in the local metadata manager. The value will be available only until restart of the application server."
		},
		{
			"type": "inputBox",
			"label": "Entity ID",
			"key":"entityId",
			"value": "${m.entityId}",
			"desc":"Entity ID is a unique identifier for an identity or service provider. Value is included in the generated metadata."
		},
		{
			"type": "inputBox",
			"label": "Entity base URL",
			"key":"baseURL",
			"value": "${m.baseURL}",
			"desc":"Base to generate URLs for this server. For example: https://myServer:443/saml-app. The public address your server will be accessed from should be used here."
		},
		{
			"type": "inputBox",
			"label": "Entity alias",
			"key":"alias",
			"value": "${m.alias?m.alias:'NA'}",
			"desc":"Alias is an internal mechanism allowing collocating multiple service providers on one server. When set, alias must be unique."
		},
		{
			"type": "select",
			"label": "Signing key",
			"key":"signingKey",
			"options": [
				${availableKeys}
			],
			"desc":"Key used for digital signatures of SAML messages. Public key will be included in the metadata."
		},
		{
			"type": "select",
			"label": "Encryption key",
			"key":"encryptionKey",
			"options": [
				${availableKeys}
			],
			"desc":"Key used for digital encryption of SAML messages. Public key will be included in the metadata."
		},
		{
			"type": "select",
			"label": "Signature security profile",
			"key":"securityProfile",
			"options": [
				"metaiop_#_metaIOP",
				"pkix_#_PKIX"
			],
			"desc":"Security profile determines how is trust of digital signatures handled:",
			"descs":[
				"In MetaIOP mode certificate is deemed valid when it's declared in the metadata or extended metadata of the peer entity. No validation of the certificate is performed (e.g. revocation) and no certificate chains are evaluated. The value is recommended as a default.",
				"PKIX profile verifies credentials against a set of trust anchors. Certificates present in the metadata or extended metadata of the peer entity are treated as trust anchors, together with all keys in the keystore. Certificate chains are verified in this mode."
			]
		},
		{
			"type": "select",
			"label": "SSL/TLS security profile",
			"key":"sslSecurityProfile",
			"options": [
				"pkix_#_PKIX",
				"metaiop_#_metaIOP"
			],
			"desc":"SSL/TLS Security profile determines how is trust of peer's SSL/TLS certificate (e.g. during Artifact resolution) handled",
			"descs":[
				"PKIX profile verifies peer's certificate against a set of trust anchors. All certificates defined in metadata, extended metadata or present in the keystore are considered as trusted anchors (certification authorities) for PKIX validation.",
				"In MetaIOP mode server's SSL/TLS certificate is trusted when it's explicitly declared in metadata or extended metadata of the peer."
			]
			
		},
		{
			"type": "select",
			"label": "SSL/TLS hostname verification",
			"key":"sslHostnameVerification",
			"options": [
				"default_#_Standard hostname verifier",
				"defaultAndLocalhost_#_Standard hostname verifier (skips verification for localhost)",
				"strict_#_Strict hostname verifier",
				"allowAll_#_Disable hostname verification (allow all)"
			],
			"desc":"Algorithm for verification of match between hostname in URL and hostname in the presented certificate."
		},
		{
			"type": "select",
			"label": "SSL/TLS client authentication",
			"key":"tlsKey",
			"options": [
				"None_#_None",${availableKeys}
			],
			"desc":"Key used to authenticate this instance for SSL/TLS connections."
		},
		{
			"type": "select",
			"label": "Sign metadata",
			"key":"signMetadata",
			"options": [
				"No_#_No",
				"Yes_#_Yes"
			],
			"desc":"If true the generated metadata will be digitally signed using the specified signature key."
		},
		{
			"type": "inputBox",
			"label": "Signing algorithm",
			"key":"signingAlgorithm",
			"value": "${m.signingAlgorithm?m.signingAlgorithm:'NA'}",
			"desc":"Algorithm used for creation of digital signature on metadata. Typical values are https://www.w3.org/2000/09/xmldsig#rsa-sha1, https://www.w3.org/2001/04/xmldsig-more#rsa-sha256 and https://www.w3.org/2001/04/xmldsig-more#rsa-sha512"
		},
		{
			"type": "select",
			"label": "Sign sent AuthNRequests",
			"key":"requestSigned",
			"options": [
				"Yes_#_Yes",
				"No_#_No"
			]
		},
		{
			"type": "select",
			"label": "Require signed authentication Assertion",
			"key":"wantAssertionSigned",
			"options": [
				
				"No_#_No","Yes_#_Yes"
			]
		},
		{
			"type": "select",
			"label": "Require signed LogoutRequest",
			"key":"requireLogoutRequestSigned",
			"options": [
				"No_#_No","Yes_#_Yes"
			]
		},
		{
			"type": "select",
			"label": "Require signed LogoutResponse",
			"key":"requireLogoutResponseSigned",
			"options": [
				"No_#_No","Yes_#_Yes"
			]
		},
		{
			"type": "select",
			"label": "Require signed ArtifactResolve",
			"key": "requireArtifactResolveSigned",
			"options": [
				"No_#_No","Yes_#_Yes"
			]
		},
		{
			"type": "select",
			"label": "Enable IDP discovery profile",
			"key": "includeDiscovery",
			"options": [
				"Yes_#_Yes",
				"No_#_No"
			],
			"desc":"Discovery profile enables service provider to determine which identity provider should be used for a particular user. Spring Security SAML contains it's own discovery service which presents user with an IDP list to select from."
		},
		{
			"type": "inputBox",
			"label": "Custom URL for IDP discovery",
			"key": "customDiscoveryURL",
			"value": "${m.customDiscoveryURL?m.customDiscoveryURL:'NA'}",
			"desc":"When not set local IDP discovery URL is automatically generated when IDP discovery is enabled."
		},
		{
			"type": "select",
			"label": "Include IDP discovery extension in metadata",
			"key": "includeDiscoveryExtension",
			"options": [
				"Yes_#_Yes",
				"No_#_No"
			]
		},
		
		{
			"type": "radio",
			"label": "Single sign-on bindings Default",
			"key": "ssoDefaultBinding",
			"options": [
				"SSO_POST_#_SSO HTTP-POST",
				"SSO_ARTIFACT_#_SSO Artifact",
				"SSO_PAOS_#_SSO PAOS",
				"HOKSSO_ARTIFACT_#_HoK SSO Artifact",
				"HOKSSO_POST_#_HoK SSO HTTP-POST"
			]
		},
		{
			"type": "checkbox",
			"label": "Single sign-on bindings",
			"key": "ssoBindings",
			"options": [
				"SSO_POST_#_SSO HTTP-POST",
				"SSO_ARTIFACT_#_SSO Artifact",
				"SSO_PAOS_#_SSO PAOS",
				"HOKSSO_ARTIFACT_#_HoK SSO Artifact",
				"HOKSSO_POST_#_HoK SSO HTTP-POST"
			]
		},
		{
			"type": "checkbox",
			"label": "Supported NameIDs",
			"key": "nameID",
			"options": [
				"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress_#_E-Mail",
				"urn:oasis:names:tc:SAML:2.0:nameid-format:transient_#_Transient",
				"urn:oasis:names:tc:SAML:2.0:nameid-format:persistent_#_Persistent",
				"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified_#_Unspecified",
				"urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName_#_X509 Subject"
			]
		}
		
	]
}"""
return jsonObj
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  

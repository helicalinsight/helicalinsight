package com.helicalinsight.externalauth.saml;

import static org.springframework.util.StringUtils.hasLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.io.MarshallingException;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.metadata.MetadataMemoryProvider;
import org.springframework.security.saml.util.SAMLUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * SaveMDService implements {@link IComponent}
 * this service class is responsible for SAML metadata configuration data and generates xml string.
 */
public class SaveMDService implements IComponent {
	
	/**
	 * executeComponent(String jsonFormData)
	 * method is responsible for processing SAML metadata configuration data. It generates, displays, and stores metadata based on the provided action.
	 * @param jsonFormData             formData in String format
	 * @return jsonObject in string form
	 */
    @Override
    public String executeComponent(String jsonFormData) {
       
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        String entityId = formData.get("entityId").getAsString();
        String action = optString(formData,"action");
        if (action.equalsIgnoreCase("display")) {
            MetadataManager metadataManager = ApplicationContextAccessor.getBean(MetadataManager.class);
            EntityDescriptor entityDescriptor = null;
            try {
                entityDescriptor = metadataManager.getEntityDescriptor(entityId);
                ExtendedMetadata extendedMetadata = metadataManager.getExtendedMetadata(entityId);
                if (entityDescriptor == null) {
                    throw new MetadataProviderException("Metadata with ID " + entityId + " not found");
                }
                return displayMetadata(entityDescriptor, extendedMetadata).toString();
            } catch (MetadataProviderException e) {
                e.printStackTrace();
                throw new EfwServiceException("Error occurred while editing metadata");
            }

        }


        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        MetadataGenerator generator = new MetadataGenerator();
        KeyManager keyManager = ApplicationContextAccessor.getBean(KeyManager.class);
        generator.setKeyManager(keyManager);
        generator.setExtendedMetadata(extendedMetadata);

        // Basic metadata properties
        generator.setEntityId(formData.get("entityId").getAsString());
        generator.setEntityBaseURL(formData.get("baseURL").getAsString());
        generator.setRequestSigned(formData.get("requestSigned").getAsString().equalsIgnoreCase("yes"));
        generator.setWantAssertionSigned(formData.get("wantAssertionSigned").getAsString().equalsIgnoreCase("yes"));

        Collection<String> bindingsSSO = new LinkedList<String>();
        Collection<String> bindingsHoKSSO = new LinkedList<String>();
        String defaultBinding = formData.get("ssoDefaultBinding").getAsString();
        int assertionConsumerIndex = 0;

        // Set default and included bindings
        for (JsonElement jsonObject : formData.getAsJsonArray("ssoBindings")) {
            String binding = jsonObject.getAsString();

            if (binding.equalsIgnoreCase(defaultBinding)) {
                assertionConsumerIndex = bindingsSSO.size() + bindingsHoKSSO.size();
            }
            if (AllowedSSOBindings.SSO_POST.toString().equalsIgnoreCase(binding)) {
                bindingsSSO.add(SAMLConstants.SAML2_POST_BINDING_URI);
            } else if (AllowedSSOBindings.SSO_ARTIFACT.toString().equalsIgnoreCase(binding)) {
                bindingsSSO.add(SAMLConstants.SAML2_ARTIFACT_BINDING_URI);
            } else if (AllowedSSOBindings.SSO_PAOS.toString().equalsIgnoreCase(binding)) {
                bindingsSSO.add(SAMLConstants.SAML2_PAOS_BINDING_URI);
            } else if (AllowedSSOBindings.HOKSSO_POST.toString().equalsIgnoreCase(binding)) {
                bindingsHoKSSO.add(SAMLConstants.SAML2_POST_BINDING_URI);
            } else if (AllowedSSOBindings.HOKSSO_ARTIFACT.toString().equalsIgnoreCase(binding)) {
                bindingsHoKSSO.add(SAMLConstants.SAML2_ARTIFACT_BINDING_URI);
            }
        }

        // Set bindings
        generator.setBindingsSSO(bindingsSSO);
        generator.setBindingsHoKSSO(bindingsHoKSSO);
        generator.setAssertionConsumerIndex(assertionConsumerIndex);

        // Name IDs
        JsonArray nameID = formData.getAsJsonArray("nameID");
        List<String> nameIdList = new ArrayList<>();
        for (JsonElement jsObject : nameID) {
            String item = jsObject.getAsString();
            nameIdList.add(item);
        }
        generator.setNameID(nameIdList);

        // Keys
        extendedMetadata.setSigningKey(formData.get("signingKey").getAsString());
        extendedMetadata.setEncryptionKey(formData.get("encryptionKey").getAsString());
        if (hasLength(formData.get("tlsKey").getAsString()) && !formData.get("tlsKey").getAsString().equalsIgnoreCase("None")) {
            extendedMetadata.setTlsKey(formData.get("tlsKey").getAsString());
        }


        // Discovery
        if (formData.get("includeDiscovery").getAsString().equalsIgnoreCase("Yes")) {
            extendedMetadata.setIdpDiscoveryEnabled(true);
            generator.setIncludeDiscoveryExtension(formData.get("includeDiscoveryExtension").getAsString().equalsIgnoreCase("Yes"));
            String customDiscoveryURL = formData.get("customDiscoveryURL").getAsString();
            if (customDiscoveryURL != null && customDiscoveryURL.length() > 0 && !customDiscoveryURL.equalsIgnoreCase("NA")) {
                extendedMetadata.setIdpDiscoveryURL(customDiscoveryURL);
            }
            String customDiscoveryResponseUrl = optString(formData,"customDiscoveryResponseUrl");
            if (customDiscoveryResponseUrl != null && customDiscoveryResponseUrl.length() > 0) {
                extendedMetadata.setIdpDiscoveryResponseURL(formData.get("customDiscoveryResponseUrl").getAsString());
            }
        } else {
            extendedMetadata.setIdpDiscoveryEnabled(false);
            generator.setIncludeDiscoveryExtension(false);
        }

        // Alias
        String alias = formData.get("alias").getAsString();
        if (hasLength(alias) && !alias.equalsIgnoreCase("NA")) {
            extendedMetadata.setAlias(alias);
        }

        // Security settings
        extendedMetadata.setSecurityProfile(formData.get("securityProfile").getAsString());
        extendedMetadata.setSslSecurityProfile(formData.get("sslSecurityProfile").getAsString());
        extendedMetadata.setRequireLogoutRequestSigned(formData.get("requireLogoutRequestSigned").getAsString().equalsIgnoreCase("Yes"));
        extendedMetadata.setRequireLogoutResponseSigned(formData.get("requireLogoutResponseSigned").getAsString().equalsIgnoreCase("Yes"));
        extendedMetadata.setRequireArtifactResolveSigned(formData.get("requireArtifactResolveSigned").getAsString().equalsIgnoreCase("Yes"));
        extendedMetadata.setSslHostnameVerification(formData.get("sslHostnameVerification").getAsString());

        // Metadata signing
        extendedMetadata.setSignMetadata(formData.get("signMetadata").getAsString().equalsIgnoreCase("Yes"));
        String signingAlgorithm = formData.get("signingAlgorithm").getAsString();
        if (hasLength(signingAlgorithm) && !signingAlgorithm.equalsIgnoreCase("NA")) {
            extendedMetadata.setSigningAlgorithm(signingAlgorithm);
        }

        // Generate values
        EntityDescriptor generatedDescriptor = generator.generateMetadata();
        ExtendedMetadata generatedExtendedMetadata = generator.generateExtendedMetadata();

        if (formData.get("store").getAsString().equalsIgnoreCase("yes")) {

            MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(generatedDescriptor);
            try {
                memoryProvider.initialize();
            } catch (MetadataProviderException e) {
                e.printStackTrace();
            }
            MetadataProvider metadataProvider = new ExtendedMetadataDelegate(memoryProvider, generatedExtendedMetadata);
            MetadataManager metadataManager = ApplicationContextAccessor.getBean(MetadataManager.class);
            try {
                metadataManager.addMetadataProvider(metadataProvider);
            } catch (MetadataProviderException e) {
                e.printStackTrace();
            }
            metadataManager.setHostedSPName(generatedDescriptor.getEntityID());
            metadataManager.setRefreshRequired(true);
            metadataManager.refreshMetadata();

        }


        return displayMetadata(generatedDescriptor, generatedExtendedMetadata).toString();
    }

    private static String optString(JsonObject jsonObject, String key) {
    	if (jsonObject.has(key)) {
            JsonElement element = jsonObject.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            } 
            else if(element.isJsonNull()) {
            	return "";
            }
            else {
                return element.toString();
            }
        } else {
            return "";
        }
	}
    /**
     * getFileName(EntityDescriptor entityDescriptor)
     * Generates an XML file name based on entityDescriptor
     * @param entityDescriptor       provides entity id
     * @return XML file name or "default_sp.xml" if no valid characters are found.
     */
	protected String getFileName(EntityDescriptor entityDescriptor) {
        StringBuilder fileName = new StringBuilder();
        for (Character c : entityDescriptor.getEntityID().toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                fileName.append(c);
            }
        }
        if (fileName.length() > 0) {
            fileName.append("_sp.xml");
            return fileName.toString();
        } else {
            return "default_sp.xml";
        }
    }

	/**
     * getConfiguration(String fileName, ExtendedMetadata metadata)
     * It returns XML String using provides metadata
     * @param fileName      filename for the metadata
     * @param metadata		extended metadata information for the entity
     * @return  XML string.
     */
    protected String getConfiguration(String fileName, ExtendedMetadata metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append("<beans:bean class=\"org.springframework.security.saml.metadata.ExtendedMetadataDelegate\">\n" +
                "    <beans:constructor-arg>\n" +
                "        <beans:bean class=\"org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider\">\n" +
                "            <beans:constructor-arg>\n" +
                "                <beans:bean class=\"java.util.Timer\"/>\n" +
                "            </beans:constructor-arg>\n" +
                "            <beans:constructor-arg>\n" +
                "                <beans:bean class=\"org.opensaml.util.resource.ClasspathResource\">\n" +
                "                    <beans:constructor-arg value=\"/metadata/").append(fileName).append("\"/>\n" +
                "                </beans:bean>\n" +
                "            </beans:constructor-arg>\n" +
                "            <beans:property name=\"parserPool\" ref=\"parserPool\"/>\n" +
                "        </beans:bean>\n" +
                "    </beans:constructor-arg>\n" +
                "    <beans:constructor-arg>\n" +
                "        <beans:bean class=\"org.springframework.security.saml.metadata.ExtendedMetadata\">\n" +
                "           <beans:property name=\"local\" value=\"true\"/>\n");
        if (metadata.getAlias() != null) {
            sb.append("           <beans:property name=\"alias\" value=\"").append(metadata.getAlias()).append("\"/>\n");
        }
        sb.append("           <beans:property name=\"securityProfile\" value=\"").append(metadata.getSecurityProfile()).append("\"/>\n" +
                "           <beans:property name=\"sslSecurityProfile\" value=\"").append(metadata.getSslSecurityProfile()).append("\"/>\n" +
                "           <beans:property name=\"sslHostnameVerification\" value=\"").append(metadata.getSslHostnameVerification()).append("\"/>\n" +
                "           <beans:property name=\"signMetadata\" value=\"").append(metadata.isSignMetadata()).append("\"/>\n" +
                "           <beans:property name=\"signingKey\" value=\"").append(metadata.getSigningKey()).append("\"/>\n" +
                "           <beans:property name=\"encryptionKey\" value=\"").append(metadata.getEncryptionKey()).append("\"/>\n");
        if (metadata.getTlsKey() != null) {
            sb.append("           <beans:property name=\"tlsKey\" value=\"").append(metadata.getTlsKey()).append("\"/>\n");
        }
        if (metadata.getSigningAlgorithm() != null) {
            sb.append("           <beans:property name=\"signingAlgorithm\" value=\"").append(metadata.getSigningAlgorithm()).append("\"/>\n");
        }
        sb.append("           <beans:property name=\"requireArtifactResolveSigned\" value=\"").append(metadata.isRequireArtifactResolveSigned()).append("\"/>\n" +
                "           <beans:property name=\"requireLogoutRequestSigned\" value=\"").append(metadata.isRequireLogoutRequestSigned()).append("\"/>\n" +
                "           <beans:property name=\"requireLogoutResponseSigned\" value=\"").append(metadata.isRequireLogoutResponseSigned()).append("\"/>\n");
        sb.append("           <beans:property name=\"idpDiscoveryEnabled\" value=\"").append(metadata.isIdpDiscoveryEnabled()).append("\"/>\n");
        if (metadata.isIdpDiscoveryEnabled()) {
            sb.append("           <beans:property name=\"idpDiscoveryURL\" value=\"").append(metadata.getIdpDiscoveryURL()).append("\"/>\n" +
                    "           <beans:property name=\"idpDiscoveryResponseURL\" value=\"").append(metadata.getIdpDiscoveryResponseURL()).append("\"/>\n");
        }
        sb.append("        </beans:bean>\n" +
                "    </beans:constructor-arg>\n" +
                "</beans:bean>");
        return sb.toString();
    }

    /**
     * displayMetadata(EntityDescriptor entityDescriptor, ExtendedMetadata extendedMetadata)
     * it returns JsonObject  consisting metadata details
     * @param entityDescriptor      provides filename, id 
     * @param extendedMetadata      provides data required for MetadataForm
     * @return jsonObject of metadataForm details
     */
    private JsonObject displayMetadata(EntityDescriptor entityDescriptor, ExtendedMetadata extendedMetadata) {

        MetadataForm metadata = new MetadataForm();
        String fileName = getFileName(entityDescriptor);

        metadata.setLocal(extendedMetadata.isLocal());
        metadata.setSecurityProfile(extendedMetadata.getSecurityProfile());
        metadata.setSslSecurityProfile(extendedMetadata.getSslSecurityProfile());
        metadata.setSerializedMetadata(getMetadataAsString(entityDescriptor, extendedMetadata));
        metadata.setConfiguration(getConfiguration(fileName, extendedMetadata));
        metadata.setEntityId(entityDescriptor.getEntityID());
        metadata.setAlias(extendedMetadata.getAlias());
        metadata.setRequireArtifactResolveSigned(extendedMetadata.isRequireArtifactResolveSigned());
        metadata.setRequireLogoutRequestSigned(extendedMetadata.isRequireLogoutRequestSigned());
        metadata.setRequireLogoutResponseSigned(extendedMetadata.isRequireLogoutResponseSigned());
        metadata.setEncryptionKey(extendedMetadata.getEncryptionKey());
        metadata.setSigningKey(extendedMetadata.getSigningKey());
        metadata.setTlsKey(extendedMetadata.getTlsKey());
        metadata.setSslHostnameVerification(extendedMetadata.getSslHostnameVerification());

        metadata.setSignMetadata(extendedMetadata.isSignMetadata());
        metadata.setSigningAlgorithm(extendedMetadata.getSigningAlgorithm());

        metadata.setIncludeDiscovery(extendedMetadata.isIdpDiscoveryEnabled());
        metadata.setCustomDiscoveryURL(extendedMetadata.getIdpDiscoveryResponseURL());
        metadata.setCustomDiscoveryResponseURL(extendedMetadata.getIdpDiscoveryURL());

        JsonObject response = new JsonObject();

        response.addProperty("entityId", metadata.getEntityId());
        response.addProperty("localEntity", metadata.isLocal());
        response.addProperty("alias", metadata.getAlias());
        response.addProperty("signingKey", metadata.getSigningKey());
        response.addProperty("encryptionKey", metadata.getEncryptionKey());
        response.addProperty("metadata", metadata.getSerializedMetadata());
        response.addProperty("configuration", metadata.getConfiguration());
        return response;


    }
    /**
     * getMetadataAsString(EntityDescriptor descriptor, ExtendedMetadata extendedMetadata)
     * @param descriptor					descriptor to sign and serialize
     * @param extendedMetadata				information about metadata
     * @return serialized and signed metadata or emptyString
     */
    protected String getMetadataAsString(EntityDescriptor descriptor, ExtendedMetadata extendedMetadata) {
        MetadataManager metadataManager = ApplicationContextAccessor.getBean(MetadataManager.class);
        KeyManager keyManager = ApplicationContextAccessor.getBean(KeyManager.class);
        try {
            return SAMLUtil.getMetadataAsString(metadataManager, keyManager, descriptor, extendedMetadata);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * isThreadSafeToCache() 
     * @return always true.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

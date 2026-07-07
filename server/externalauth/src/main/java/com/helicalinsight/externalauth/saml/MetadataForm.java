/* Copyright 2011 Vladimir Schafer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helicalinsight.externalauth.saml;

/**
 * MetadataForm
 * Form able to store UI data related to metadata.
 */
public class MetadataForm {

    private boolean store;
    private String entityId;
    private String securityProfile;
    private String sslSecurityProfile;
    private String sslHostnameVerification;
    private String baseURL;
    private String alias;
    private boolean signMetadata;
    private String serializedMetadata;
    private String configuration;
    private String signingAlgorithm;
    private String[] nameID;
    
    private String[] ssoBindings = new String[] {
            AllowedSSOBindings.SSO_POST.toString(),
            AllowedSSOBindings.SSO_ARTIFACT.toString()
    };
    private String ssoDefaultBinding = AllowedSSOBindings.SSO_POST.toString();

    private String signingKey;
    private String encryptionKey;
    private String tlsKey;

    private boolean local;

    private boolean includeDiscovery = true;
    private boolean includeDiscoveryExtension = false;

    private String customDiscoveryURL;
    private String customDiscoveryResponseURL;

    private boolean requestSigned = true;
    private boolean wantAssertionSigned;
    private boolean requireLogoutRequestSigned;
    private boolean requireLogoutResponseSigned;
    private boolean requireArtifactResolveSigned;

    public MetadataForm() {
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isSignMetadata() {
        return signMetadata;
    }

    public void setSignMetadata(boolean signMetadata) {
        this.signMetadata = signMetadata;
    }

    public boolean isRequestSigned() {
        return requestSigned;
    }

    public void setRequestSigned(boolean requestSigned) {
        this.requestSigned = requestSigned;
    }

    public boolean isWantAssertionSigned() {
        return wantAssertionSigned;
    }

    public void setWantAssertionSigned(boolean wantAssertionSigned) {
        this.wantAssertionSigned = wantAssertionSigned;
    }

    public boolean isRequireLogoutRequestSigned() {
        return requireLogoutRequestSigned;
    }

    public void setRequireLogoutRequestSigned(boolean requireLogoutRequestSigned) {
        this.requireLogoutRequestSigned = requireLogoutRequestSigned;
    }

    public boolean isRequireLogoutResponseSigned() {
        return requireLogoutResponseSigned;
    }

    public void setRequireLogoutResponseSigned(boolean requireLogoutResponseSigned) {
        this.requireLogoutResponseSigned = requireLogoutResponseSigned;
    }

    public boolean isRequireArtifactResolveSigned() {
        return requireArtifactResolveSigned;
    }

    public void setRequireArtifactResolveSigned(boolean requireArtifactResolveSigned) {
        this.requireArtifactResolveSigned = requireArtifactResolveSigned;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public String getSerializedMetadata() {
        return serializedMetadata;
    }

    public void setSerializedMetadata(String serializedMetadata) {
        this.serializedMetadata = serializedMetadata;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getSecurityProfile() {
        return securityProfile;
    }

    public void setSecurityProfile(String securityProfile) {
        this.securityProfile = securityProfile;
    }

    public String getSslSecurityProfile() {
        return sslSecurityProfile;
    }

    public void setSslSecurityProfile(String sslSecurityProfile) {
        this.sslSecurityProfile = sslSecurityProfile;
    }

    public String getTlsKey() {
        return tlsKey;
    }

    public void setTlsKey(String tlsKey) {
        this.tlsKey = tlsKey;
    }

    public boolean isIncludeDiscovery() {
        return includeDiscovery;
    }

    public void setIncludeDiscovery(boolean includeDiscovery) {
        this.includeDiscovery = includeDiscovery;
    }

    public boolean isIncludeDiscoveryExtension() {
        return includeDiscoveryExtension;
    }

    public void setIncludeDiscoveryExtension(boolean includeDiscoveryExtension) {
        this.includeDiscoveryExtension = includeDiscoveryExtension;
    }

    public String[] getNameID() {
        return nameID;
    }

    public void setNameID(String[] nameID) {
        this.nameID = nameID;
    }

    public String getCustomDiscoveryURL() {
        return customDiscoveryURL;
    }

    public void setCustomDiscoveryURL(String customDiscoveryURL) {
        this.customDiscoveryURL = customDiscoveryURL;
    }

    public String getCustomDiscoveryResponseURL() {
        return customDiscoveryResponseURL;
    }

    public void setCustomDiscoveryResponseURL(String customDiscoveryResponseURL) {
        this.customDiscoveryResponseURL = customDiscoveryResponseURL;
    }

    public String[] getSsoBindings() {
        return ssoBindings;
    }

    public void setSsoBindings(String[] ssoBindings) {
        this.ssoBindings = ssoBindings;
    }

    public String getSsoDefaultBinding() {
        return ssoDefaultBinding;
    }

    public void setSsoDefaultBinding(String ssoDefaultBinding) {
        this.ssoDefaultBinding = ssoDefaultBinding;
    }

    public String getSslHostnameVerification() {
        return sslHostnameVerification;
    }

    public void setSslHostnameVerification(String sslHostnameVerification) {
        this.sslHostnameVerification = sslHostnameVerification;
    }

    public String getSigningAlgorithm() {
        return signingAlgorithm;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }

}

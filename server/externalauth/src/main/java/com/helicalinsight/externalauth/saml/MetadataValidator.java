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
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * MetadataValidator implements {@link Validator}
 * Validator for metadata from.
 */
public class MetadataValidator implements Validator {

    MetadataManager manager;

    /**
     * MetadataValidator(MetadataManager manager)
     * Constructs a new instance of MetadataValidator with the specified MetadataManager.
     * @param manager The MetadataManager used for validation.
     */
    public MetadataValidator(MetadataManager manager) {
        this.manager = manager;
    }
    /**
     * supports(Class<?> clazz)
     * Determines whether the validator supports the given class type.
     *
     * @param clazz         class to check for support.
     * @return {@code true } if the class is supported, {@code false} otherwise.
     */
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MetadataForm.class);
    }
    /**
     * validate(Object target, Errors errors)
     * @param target          provide all MetadataForm object details
     * @param errors		  Stores and exposes information about validation errors for a specific object.
     */
    public void validate(Object target, Errors errors) {

        MetadataForm metadata = (MetadataForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "entityId", "required", "Entity id must be set.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "baseURL", "required", "Base URL is required.");

        if (metadata.getSecurityProfile() == null) {
            errors.rejectValue("securityProfile", null, "Security profile must be specified.");
        } else if (!"pkix".equalsIgnoreCase(metadata.getSecurityProfile()) && !"metaiop".equals(metadata.getSecurityProfile())) {
            errors.rejectValue("securityProfile", null, "Selected value is not supported.");
        }

        if (metadata.getSslSecurityProfile() == null) {
            errors.rejectValue("sslSecurityProfile", null, "SSL/TLS Security profile must be specified.");
        } else if (!"pkix".equalsIgnoreCase(metadata.getSslSecurityProfile()) && !"metaiop".equals(metadata.getSslSecurityProfile())) {
            errors.rejectValue("sslSecurityProfile", null, "Selected value is not supported.");
        }

        if (metadata.isIncludeDiscovery() && metadata.getCustomDiscoveryURL() != null && metadata.getCustomDiscoveryURL().length() > 0) {
            try {
                new URL(metadata.getCustomDiscoveryURL());
            } catch (MalformedURLException e) {
                errors.rejectValue("customDiscoveryURL", null, "Value is not a valid URL.");
            }
        }

        if (metadata.isIncludeDiscovery() && metadata.getCustomDiscoveryResponseURL() != null && metadata.getCustomDiscoveryResponseURL().length() > 0) {
            try {
                new URL(metadata.getCustomDiscoveryResponseURL());
            } catch (MalformedURLException e) {
                errors.rejectValue("customDiscoveryResponseURL", null, "Value is not a valid URL.");
            }
        }

        // Bindings
        if (metadata.getSsoBindings() == null || metadata.getSsoBindings().length == 0) {
            errors.rejectValue("ssoBindings", null, "At least one binding must be specified.");
        }

        // Default binding
        if (metadata.getSsoDefaultBinding() != null && metadata.getSsoBindings() != null) {
            boolean found = false;
            for (String binding : metadata.getSsoBindings()) {
                if (binding.equals(metadata.getSsoDefaultBinding())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                errors.rejectValue("ssoDefaultBinding", null, "Default binding must be selected as included.");
            }
        }

        if (metadata.getNameID() == null || metadata.getNameID().length == 0) {
            errors.rejectValue("nameID", null, "At least one NameID must be selected.");
        }

        try {
            if (!errors.hasErrors() && metadata.isStore()) {
                EntityDescriptor entityDescriptor = manager.getEntityDescriptor(metadata.getEntityId());
                if (entityDescriptor != null) {
                    errors.rejectValue("entityId", null, "Selected entity ID is already used.");
                }
                String idForAlias = manager.getEntityIdForAlias(metadata.getAlias());
                if (idForAlias != null) {
                    errors.rejectValue("alias", null, "Selected alias is already used.");
                }
            }
        } catch (MetadataProviderException e) {
            throw new RuntimeException("Error loading alias data", e);
        }

    }

}
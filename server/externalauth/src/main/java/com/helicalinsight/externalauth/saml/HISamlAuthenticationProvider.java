package com.helicalinsight.externalauth.saml;

import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLRuntimeException;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;

import java.util.Collection;
import java.util.Date;

/**
 * HISamlAuthenticationProvider extends {@link SAMLAuthenticationProvider}
 * this class responsible of  SAML authentication provider for handling SAML-based authentication.
 * HISamlAuthenticationProvider is capable of verifying validity of a SAMLAuthenticationToken and in case
 * the token is valid to create an authenticated UsernamePasswordAuthenticationToken.
 *
 * Created by helical021 on 2/9/2021.
 */
public class HISamlAuthenticationProvider extends SAMLAuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(HISamlAuthenticationProvider.class);

	/**
	 * authenticate(Authentication authentication) 
	 * this method performs authentication of an
	 * Authentication object. The authentication must be of type
	 * SAMLAuthenticationToken and must contain filled SAMLMessageContext. 
	 * @param authentication     SAML authentication token  
	 * @return Authenticated authentication token.
	 * @throws AuthenticationException If an error occurs during authentication.
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SAMLAuthenticationToken token = (SAMLAuthenticationToken) authentication;
        SAMLMessageContext credentials = token.getCredentials();
        try {
            Authentication authenticate = superAuthenticate(authentication);
            logger.error("Authenticate " + authenticate + " " + authenticate.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticate);
            return authenticate;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error occurred ", ex);

        }
        logger.error("Authenticaion returned is " + authentication);
        return authentication;

    }
    /**
     * superAuthenticate(Authentication authentication)
     * Performs the actual SAML authentication and returns an authenticated authentication token.
     *
     * @param authentication     SAML authentication credentials 
	 * @return Authenticated authentication token of UsernamePasswordAuthenticationToken.
	 * @throws AuthenticationException If an error occurs during authentication.
     */
    private Authentication superAuthenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            throw new IllegalArgumentException("Only SAMLAuthenticationToken is supported, " + authentication.getClass() + " was attempted");
        }

        SAMLAuthenticationToken token = (SAMLAuthenticationToken) authentication;
        SAMLMessageContext context = token.getCredentials();

        if (context == null) {
            throw new AuthenticationServiceException("SAML message context is not available in the authentication token");
        }

        SAMLCredential credential;

        try {
            if (SAMLConstants.SAML2_WEBSSO_PROFILE_URI.equals(context.getCommunicationProfileId())) {
                credential = consumer.processAuthenticationResponse(context);
            } else if (SAMLConstants.SAML2_HOK_WEBSSO_PROFILE_URI.equals(context.getCommunicationProfileId())) {
                credential = hokConsumer.processAuthenticationResponse(context);
            } else {
                throw new SAMLException("Unsupported profile encountered in the context " + context.getCommunicationProfileId());
            }
        } catch (SAMLRuntimeException | SAMLException e) {
            logger.debug("Error validating SAML message", e);
            samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
            throw new AuthenticationServiceException("Error validating SAML message", e);
        } catch (ValidationException | SecurityException e) {
            logger.debug("Error validating signature", e);
            samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
            throw new AuthenticationServiceException("Error validating SAML message signature", e);
        } catch (DecryptionException e) {
            logger.debug("Error decrypting SAML message", e);
            samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
            throw new AuthenticationServiceException("Error decrypting SAML message", e);
        }

        Object userDetails = getUserDetails(credential);
        Collection<? extends GrantedAuthority> entitlements = getEntitlements(credential, userDetails);

         Date tokenExpiration = getExpirationDate(credential);
        if (tokenExpiration != null && new Date().compareTo(tokenExpiration) >= 0) {
            throw new AuthenticationServiceException("The token is expired");
        }

        SAMLCredential authenticationCredential = isExcludeCredential() ? null : credential;
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDetails,authenticationCredential, entitlements);
        result.setDetails(userDetails);

        samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.SUCCESS, context, result, null);
        return result;

    }
    /**
     * getUserDetails(SAMLCredential credential)
     * Retrieves the user details from the SAML credential
     * @param credential      SAML credential.
     * @return User details Object extracted from the credential
     * 
     */
    protected Object getUserDetails(SAMLCredential credential) {
        Object userDetails1 = super.getUserDetails(credential);
        return userDetails1;
    }
    /**
     * getPrincipal(SAMLCredential credential, java.lang.Object userDetail)
     * Retrieves the principal from the SAML credential and user details
     * @param credential         SAML credential.
     * @param userDetail         user details.
     * @return The principal extracted from the credential and user details.
     */
    protected Object getPrincipal(SAMLCredential credential, java.lang.Object userDetail) {
        return super.getPrincipal(credential, userDetail);
    }
    /**
     * getEntitlements(SAMLCredential credential, Object userDetail)
     * @param credential         SAML credential.
     * @param userDetail         user details.
     * @return Collection of granted permission for authentication.
     */
    protected java.util.Collection<? extends GrantedAuthority> getEntitlements(SAMLCredential credential, Object userDetail) {
        return super.getEntitlements(credential, userDetail);
    }
    /**
     * getExpirationDate(SAMLCredential credential)
     * @param credential         SAML credential.
     * @return The expiration date of the SAML credential.
     */
    protected java.util.Date getExpirationDate(SAMLCredential credential) {
        return super.getExpirationDate(credential);
    }


}

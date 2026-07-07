package com.helicalinsight.externalauth.cas;

import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serializable;

/**
 * AuthenticationDetails class implements {@link Serializable}
 * A holder of the context as a string.
 *
 * @author Ruud Senden
 * @since 2.0
 */
public class AuthenticationDetails implements Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    //~ Instance fields ================================================================================================

    private final String context;

    //~ Constructors ===================================================================================================

    /**
     * Constructor.
     *
     * @param context that the authentication request is initiated from
     */
    public AuthenticationDetails(Object context) {
        this.context = context == null ? "" : context.toString();
        doPopulateAdditionalInformation(context);
    }

    //~ Methods ========================================================================================================

    /**
     * doPopulateAdditionalInformation(Object context)
     * Provided so that subclasses can populate additional information.
     *
     * @param context the existing contextual information
     */
    @SuppressWarnings("UnusedParameters")
    protected void doPopulateAdditionalInformation(Object context) {
    }

    /**
     * equals(Object obj)
     * provide user details checks with authentication request
     * @param Object obj     user details 
     * @return  {@code true} if the details match the request, {@code false} otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof AuthenticationDetails) {
            AuthenticationDetails rhs = (AuthenticationDetails) obj;
            // this.context cannot be null
            return context.equals(rhs.getContext());
        }

        return false;
    }

    /**
     * getContext()
     * Indicates the context.
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * toString()
     * @return string representation of the authentication request(context)
     */
    public String toString() {
        return (super.toString() + ": ") + "Context: " + this.getContext();
    }
}

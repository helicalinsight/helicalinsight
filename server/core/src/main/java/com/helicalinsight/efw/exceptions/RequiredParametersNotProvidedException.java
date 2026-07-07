package com.helicalinsight.efw.exceptions;

/**
 * Thrown when the user of a particular service doesn't provide the requisite
 * parameters that are vital for processing
 *
 * @author Rajasekhar
 * @since 1.0
 */
public class RequiredParametersNotProvidedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * No-Arg constructor
     */

    public RequiredParametersNotProvidedException() {
    }

    /**
     * Convenient constructor that explains the exception with a user provided
     * message
     *
     * @param message The user's custom message
     */
    public RequiredParametersNotProvidedException(String message) {
        super(message);
    }
}

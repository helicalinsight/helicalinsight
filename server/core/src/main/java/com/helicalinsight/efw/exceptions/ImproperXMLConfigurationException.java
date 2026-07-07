package com.helicalinsight.efw.exceptions;

/**
 * Thrown when the setting.xml(in the System/Admin directory) is malformed or
 * consists or inadequate information
 *
 * @author Rajasekhar
 * @since 1.1
 */
public class ImproperXMLConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * No-Arg constructor
     */
    public ImproperXMLConfigurationException() {
        super();
    }

    /**
     * Convenient constructor that explains the exception with a user provided
     * message, and cause
     *
     * @param message user provided message
     * @param cause   The cause of the exception
     */
    public ImproperXMLConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenient constructor that explains the exception with a user provided
     * message
     *
     * @param message user provided message
     */
    public ImproperXMLConfigurationException(String message) {
        super(message);
    }

    /**
     * Convenient constructor that explains the cause
     *
     * @param cause The cause of the exception
     */
    public ImproperXMLConfigurationException(Throwable cause) {
        super(cause);
    }
}

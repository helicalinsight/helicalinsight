package com.helicalinsight.efw.exceptions;

/**
 * Thrown when the xml files such as efwsr in the solution directory are mal
 * formed or consists or inadequate information
 *
 * @author Rajasekhar
 * @since 1.0
 */
public class MalformedXmlException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Convenient constructor that explains the exception with a user provided
     * message
     *
     * @param message user provided message
     */
    public MalformedXmlException(String message) {
        super(message);
    }

}

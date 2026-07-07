package com.helicalinsight.efw.exceptions;

/**
 * @author Somen
 *         Created on 6/26/2015.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

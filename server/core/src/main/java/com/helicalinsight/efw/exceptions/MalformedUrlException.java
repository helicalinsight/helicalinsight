package com.helicalinsight.efw.exceptions;

public class MalformedUrlException extends Exception {
	 private static final long serialVersionUID = 1L;

	    /**
	     * Convenient constructor that explains the exception with a user provided
	     * message
	     *
	     * @param message user provided message
	     */
	    public MalformedUrlException(String message) {
	        super(message);
	    }

}

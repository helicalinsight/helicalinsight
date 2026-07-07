package com.helicalinsight.efw.exceptions;

/**
 * @author Rajasekhar M
 *
 */
public class SingleInstancePhantomServiceException extends RuntimeException {

	public SingleInstancePhantomServiceException(String exceptionMessage) {
		super(exceptionMessage);
	}

}

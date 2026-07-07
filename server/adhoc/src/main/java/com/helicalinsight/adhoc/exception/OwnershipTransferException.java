package com.helicalinsight.adhoc.exception;

/**
 * An exception indicating a failure during the transfer of ownership for a resource.
 * It extends the {@code RuntimeException} class, making it an unchecked exception.
 */
public class OwnershipTransferException extends RuntimeException {
	
	
	private static final long serialVersionUID = 1L;

	public OwnershipTransferException(String message) {
		super(message);
	}
	
	public OwnershipTransferException(String message, Throwable cause) {
		super(message,cause);
	}
	
	public OwnershipTransferException(Throwable cause) {
		super(cause);
	}

}

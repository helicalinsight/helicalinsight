package com.helicalinsight.export.exception;

public class TicketInProgressException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TicketInProgressException(String message) {
		super(message);
	}

	public TicketInProgressException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public TicketInProgressException(Throwable cause) {
		super(cause);
	}

}

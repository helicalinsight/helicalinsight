package com.helicalinsight.export.exception;

public class InvalidTicketException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidTicketException(String message) {
		super(message);
	}

	public InvalidTicketException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public InvalidTicketException(Throwable cause) {
		super(cause);
	}

}

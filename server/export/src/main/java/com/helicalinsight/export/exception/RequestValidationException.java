package com.helicalinsight.export.exception;

/**
 * Custom exception class for handling validation errors in export and import requests.
 * Extends {@link ResourceException} to provide a common base for resource-related exceptions.
 */
public class RequestValidationException extends ResourceException {

	private static final long serialVersionUID = 1L;

	public RequestValidationException(String message) {
		super(message);
	}
}

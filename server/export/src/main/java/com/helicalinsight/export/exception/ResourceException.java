package com.helicalinsight.export.exception;


/**
 * {@code ResourceException} is an abstract base class for custom exceptions in the context of resource management.
 * It extends the standard {@link RuntimeException}, providing a common structure for handling and propagating
 * exceptions related to resource operations.
 * <p>
 * Subclasses of this exception are expected to be specific to different aspects of resource management, allowing
 * for more granular error handling.
 * </p>
 * @see RuntimeException
 */
public abstract class ResourceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ResourceException(String message) {
		super(message);
	}
}

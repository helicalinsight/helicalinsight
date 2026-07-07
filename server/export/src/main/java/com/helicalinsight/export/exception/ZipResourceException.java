package com.helicalinsight.export.exception;

/**
 * {@code ZipResourceException} is a custom exception class specifically designed for handling errors
 * related to ZIP archive operations in the context of resource management. It extends the base
 * {@link ResourceException} class, providing a consistent way to handle and propagate ZIP-related exceptions.
 * <p>
 * This exception is thrown when there are issues encountered during ZIP archive or extraction processes.
 * It signifies problems such as errors in archiving a directory, validating the integrity of a ZIP file,
 * or any other ZIP-related errors during resource management operations.
 * </p>
 */
public class ZipResourceException  extends ResourceException{
	
	private static final long serialVersionUID = 1L;

	public ZipResourceException(String message) {
		super(message);
	}
}

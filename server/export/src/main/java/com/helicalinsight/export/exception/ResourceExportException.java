package com.helicalinsight.export.exception;

/**
 * {@code ResourceExportException} is a custom exception class specifically designed for handling errors
 * related to resource export operations. It extends the base {@link ResourceException} class, providing
 * a consistent way to handle and propagate export-related exceptions.
 * <p>
 * This exception is thrown when there are issues encountered during resource export processes,
 * such as cleaning directories, encoding content, creating folder schemas, or if resource does not exists etc.
 * </p>
 */
public class ResourceExportException  extends ResourceException{
	

	private static final long serialVersionUID = 1L;

	public ResourceExportException(String message) {
		super(message);
	}
}

package com.helicalinsight.export.exception;


/**
 * {@code ResourceImportException} is a custom exception class specifically designed for handling errors
 * related to resource import operations. It extends the base {@link ResourceException} class, providing
 * a consistent way to handle and propagate import-related exceptions.
 * <p>
 * This exception is thrown when there are issues encountered during resource import processes.
 * It signifies problems in importing resources, such as validating the imported file, processing conflicts,decoding the content,
 * or any other import-related errors.
 * </p>
 * @see ResourceException
 */
public class ResourceImportException  extends ResourceException{
	

	private static final long serialVersionUID = 1L;

	public ResourceImportException(String message) {
		super(message);
	}
}

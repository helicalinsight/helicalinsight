package com.helicalinsight.export.exception;

/**
 * Exception thrown when there is an issue related to the manifest file in the export module.
 * This exception is a specific type of {@link ResourceException} and is used to signal problems
 * specifically related to manifest files during resource handling.
 * <p>
 * Common scenarios that may lead to a ManifestException include malformed manifest files,
 * missing required information, or other issues specific to manifest handling.
 * </p>
 */
public class ManifestException extends ResourceException {

	private static final long serialVersionUID = 1L;
	/**
     * Constructs a new ManifestException with the specified detail message.
     * @param message the detail message.
     */
	public ManifestException(String message) {
		super(message);
	}
}

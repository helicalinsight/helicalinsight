package com.helicalinsight.datasource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.helicalinsight.efw.exceptions.EfwServiceException;

/**
 * Markers written by {@code StreamingCacheProvider} and consumed by {@link ChunkIterator}.
 */
public final class StreamingCacheMarkers {

	public static final String COMPLETE_FILE = ".cache_complete";
	public static final String ERROR_FILE = ".cache_error";

	private StreamingCacheMarkers() {
	}

	public static File completeFile(File directory) {
		return new File(directory, COMPLETE_FILE);
	}

	public static File errorFile(File directory) {
		return new File(directory, ERROR_FILE);
	}

	/**
	 * Persists the root cause message body (not {@code ClassName: message}) so a later
	 * {@link SQLException} can surface the same text via {@link ExceptionUtils#getRootCauseMessage}.
	 */
	public static void writeError(File directory, Throwable error) throws IOException {
		if (directory == null) {
			return;
		}
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Unable to create streaming cache directory: " + directory);
		}
		Throwable root = error == null ? null : ExceptionUtils.getRootCause(error);
		if (root == null) {
			root = error;
		}
		String message;
		if (root == null) {
			message = "Unknown streaming cache failure";
		} else if (root.getMessage() != null && !root.getMessage().isBlank()) {
			message = root.getMessage();
		} else {
			message = ExceptionUtils.getRootCauseMessage(error);
		}
		Files.writeString(errorFile(directory).toPath(), message, StandardCharsets.UTF_8);
	}

	public static String readErrorMessage(File directory) throws IOException {
		File file = errorFile(directory);
		if (!file.exists()) {
			return null;
		}
		return Files.readString(file.toPath(), StandardCharsets.UTF_8).trim();
	}

	/**
	 * Throws if a stream producer recorded a failure for this cache directory.
	 * Wraps a {@link SQLException} so {@link ExceptionUtils#getRootCauseMessage} reports the
	 * stored DB message (same body as non-streaming), without an extra IllegalStateException prefix.
	 */
	public static void throwIfError(File directory) {
		File file = errorFile(directory);
		if (!file.exists()) {
			return;
		}
		String message;
		try {
			message = readErrorMessage(directory);
		} catch (IOException e) {
			throw new EfwServiceException("Streaming cache failed and error details could not be read", e);
		}
		if (message == null || message.isBlank()) {
			message = "Streaming cache failed";
		}
		throw new EfwServiceException(new SQLException(message));
	}
}

package com.helicalinsight.efw.utility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public final class HttpRangeFileStreamer {

	private static final int BUFFER_SIZE = 8192;
	private static final String BYTES_UNIT = "bytes";

	private HttpRangeFileStreamer() {
	}

	public static void stream(File file, HttpServletRequest request, HttpServletResponse response, String contentType,
			String contentDisposition) throws IOException {

		long fileLength = file.length();
		response.setHeader("Accept-Ranges", BYTES_UNIT);
		response.setHeader("Content-Type", contentType != null ? contentType : "application/octet-stream");
		if (contentDisposition != null) {
			response.setHeader("Content-Disposition", contentDisposition);
		}

		String rangeHeader = request.getHeader("Range");
		if (rangeHeader == null) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Content-Length", Long.toString(fileLength));
			Files.copy(file.toPath(), response.getOutputStream());
			return;
		}

		// Only: bytes=start-end | bytes=start-
		if (!rangeHeader.startsWith("bytes=")) {
			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			response.setHeader("Content-Range", "bytes */" + fileLength);
			return;
		}

		String[] parts = rangeHeader.substring(6).split("-", 2);
		long start;
		long end;
		try {
			start = Long.parseLong(parts[0]);
			end = (parts.length < 2 || parts[1].isEmpty()) ? fileLength - 1 : Long.parseLong(parts[1]);
		} catch (NumberFormatException ex) {
			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			response.setHeader("Content-Range", "bytes */" + fileLength);
			return;
		}

		if (start < 0 || start >= fileLength || end < start) {
			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			response.setHeader("Content-Range", "bytes */" + fileLength);
			return;
		}
		end = Math.min(end, fileLength - 1);
		long contentLength = end - start + 1;

		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
		response.setHeader("Content-Length", Long.toString(contentLength));

		try (RandomAccessFile raf = new RandomAccessFile(file, "r"); OutputStream out = response.getOutputStream()) {
			raf.seek(start);
			byte[] buffer = new byte[BUFFER_SIZE];
			long remaining = contentLength;
			while (remaining > 0) {
				int read = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining));
				if (read == -1) {
					break;
				}
				out.write(buffer, 0, read);
				remaining -= read;
			}
		}
	}
}
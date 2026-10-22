package com.helicalinsight.export.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

/**
 * MultipartFile backed by an already-assembled path (no extra buffering of the whole file).
 */
public final class PathMultipartFile implements MultipartFile {

	private final Path path;
	private final String name;
	private final String originalFilename;
	private final String contentType;

	public PathMultipartFile(Path path, String originalFilename, String contentType) {
		this.path = path;
		this.name = "file";
		this.originalFilename = originalFilename != null ? originalFilename : path.getFileName().toString();
		this.contentType = contentType != null ? contentType : "application/octet-stream";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		try {
			return !Files.exists(path) || Files.size(path) == 0;
		} catch (IOException e) {
			return true;
		}
	}

	@Override
	public long getSize() {
		try {
			return Files.size(path);
		} catch (IOException e) {
			return 0;
		}
	}

	@Override
	public byte[] getBytes() throws IOException {
		return Files.readAllBytes(path);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(path.toFile());
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		Files.copy(path, dest.toPath());
	}
}

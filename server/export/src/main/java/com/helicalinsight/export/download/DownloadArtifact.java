package com.helicalinsight.export.download;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class DownloadArtifact {
	private final File file;
	private final String fileName;
	private final String contentType;
	private final Set<Path> trackedPaths;

	public DownloadArtifact(File file, String fileName, String contentType, Set<Path> trackedPaths) {
		this.file = file;
		this.fileName = fileName;
		this.contentType = contentType != null ? contentType : "application/octet-stream";
		this.trackedPaths = trackedPaths != null
				? Collections.unmodifiableSet(new LinkedHashSet<>(trackedPaths))
				: Collections.emptySet();
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public Set<Path> getTrackedPaths() {
		return trackedPaths;
	}
}

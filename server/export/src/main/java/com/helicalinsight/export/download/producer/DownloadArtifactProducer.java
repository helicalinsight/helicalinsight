package com.helicalinsight.export.download.producer;

import com.helicalinsight.export.download.DownloadArtifact;

@FunctionalInterface
public interface DownloadArtifactProducer {
	DownloadArtifact produce() throws Exception;
}
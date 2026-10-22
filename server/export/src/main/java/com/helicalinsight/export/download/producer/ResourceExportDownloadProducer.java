package com.helicalinsight.export.download.producer;

import com.helicalinsight.export.download.DownloadArtifact;
import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.handler.ResourceExportHandler;

public final class ResourceExportDownloadProducer implements DownloadArtifactProducer {

	private final ResourceExportHandler exportHandler;
	private final ResourceExportRequest exportRequest;
	private final String ticketId;

	public ResourceExportDownloadProducer(ResourceExportHandler exportHandler, ResourceExportRequest exportRequest) {
		this(exportHandler, exportRequest, null);
	}

	public ResourceExportDownloadProducer(ResourceExportHandler exportHandler, ResourceExportRequest exportRequest,
			String ticketId) {
		this.exportHandler = exportHandler;
		this.exportRequest = exportRequest;
		this.ticketId = ticketId;
	}

	@Override
	public DownloadArtifact produce() throws Exception {
		return exportHandler.exportToArtifact(exportRequest, ticketId);
	}
}

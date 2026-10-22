package com.helicalinsight.export.download;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.download.producer.DownloadArtifactProducer;
import com.helicalinsight.export.ticket.Status;
import com.helicalinsight.export.ticket.TransferTicket;
import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ResumableDownloadService {

	private static final Logger logger = LoggerFactory.getLogger(ResumableDownloadService.class);

	@Autowired
	private TransferTicketService transferTicketService;

	@Autowired
	private TaskExecutorService taskExecutorService;

	public JsonObject prepare(String ticketId, String requestId, String userId, DownloadArtifactProducer producer) {
		TransferTicket ticket = transferTicketService.begin(ticketId, requestId, userId, 60);

		taskExecutorService.submit(() -> {
			try {
				// Wait for SSE so abort/ready events are not lost (fresh export = live subscriber).
				if (transferTicketService.isAborted(ticket.ticketId)
						|| (JsonUtils.isStreamResponseEnabled()
								&& !transferTicketService.awaitSubscriberSeconds(ticket.ticketId, 60))) {
					transferTicketService.abortByRequestId(requestId, "Timed out waiting for event subscriber");
					return;
				}
				DownloadArtifact artifact = producer.produce();
				for (Path p : artifact.getTrackedPaths()) {
					transferTicketService.track(ticket.ticketId, p);
				}
				transferTicketService.track(ticket.ticketId, artifact.getFile().toPath());
				if (Thread.currentThread().isInterrupted()
						|| transferTicketService.isAborted(ticket.ticketId)) {
					logger.info("Export aborted after produce for requestId={}; cleaning temp files", requestId);
					transferTicketService.abortByRequestId(requestId);
					return;
				}
				transferTicketService.complete(
						ticket.ticketId,
						artifact.getFile().toPath(),
						artifact.getFileName(),
						artifact.getContentType());
			} catch (Exception e) {
				boolean aborted = e.getMessage() != null && e.getMessage().toLowerCase().contains("aborted");
				if (aborted) {
					logger.info("Export aborted for requestId={}; cleaning temp files", requestId);
				} else {
					logger.error("Resumable prepare failed for requestId={}", requestId, e);
				}
				transferTicketService.abortByRequestId(requestId, e.getMessage());
			}
		}, requestId);

		JsonObject body = new JsonObject();
		body.addProperty("ticket", ticket.ticketId);
		body.addProperty("requestId", requestId);
		body.addProperty("downloadUrl", "/fileDownload?ticket=" + ticket.ticketId);
		body.addProperty("eventsUrl", TransferTicketService.eventsUrl(ticket.ticketId, requestId));
		body.addProperty("status", Status.PREPARING.name());
		return body;
	}

	public void writePreparing(HttpServletResponse response, JsonObject body) throws java.io.IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.getWriter().write(body.toString());
	}
}

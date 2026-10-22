package com.helicalinsight.export.upload;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.validation.ImportRequestValidator;
import com.helicalinsight.export.handler.importres.ImportResourceManager;
import com.helicalinsight.export.ticket.Status;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.export.ticket.TicketProgressEventSink;
import com.helicalinsight.export.ticket.TransferTicket;
import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;
import com.helicalinsight.stream.ProgressEventSink;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ResumableUploadService {

	private static final long TTL_MINUTES = 60;
	private static final Gson GSON = new Gson();

	@Autowired
	private TransferTicketService ticketService;

	@Autowired
	private ImportResourceManager importManager;

	@Autowired
	private JsonMapperUtils jsonMapperUtils;

	@Autowired
	private TaskExecutorService taskExecutorService;

	/**
	 * Prepare/resume upload session from existing import endpoint params ({@code resumable=true}).
	 * Returns JSON payload (ticket, chunkSize, receivedChunks, eventsUrl).
	 */
	public String prepareFromHttpRequest(HttpServletRequest request, String targetUrl) throws Exception {
		String userId = AuthenticationUtils.getUserId();
		String requestId = firstNonBlank(request.getParameter("requestId"),
				(String) request.getAttribute("requestId"));
		String fileName = request.getParameter("fileName");
		String contentType = request.getParameter("contentType");
		String checksum = request.getParameter("checksumSha256");
		long fileSize = parseLong(request.getParameter("fileSize"), -1);

		Map<String, String> metadata = new HashMap<>();
		copyParam(request, metadata, "destination");
		copyParam(request, metadata, "type");
		copyParam(request, metadata, "config");
		copyParam(request, metadata, "fileExtension");
		copyParam(request, metadata, "formData");

		TransferTicket ticket = init(userId, requestId, fileName, contentType, fileSize, checksum, targetUrl, metadata);
		return GSON.toJson(ticketService.uploadSessionPayload(ticket));
	}

	public TransferTicket init(String ownerUserId, String requestId, String fileName, String contentType, long fileSize,
			String checksumSha256, String targetUrl, Map<String, String> metadata) throws Exception {
		if (StringUtils.isBlank(requestId)) {
			throw new IllegalArgumentException("requestId is required");
		}
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("fileName is required");
		}
		if (StringUtils.isBlank(targetUrl)) {
			throw new IllegalArgumentException("targetUrl is required");
		}
		String normalizedTarget = normalizeTarget(targetUrl);
		int chunkSize = JsonUtils.getUploadChunkSize();
		String ticketId = TransferTicketService.computeUploadTicketId(ownerUserId, fileName, fileSize, checksumSha256,
				requestId);
		Map<String, String> meta = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
		return ticketService.beginUpload(ticketId, requestId, ownerUserId, fileName, contentType, fileSize,
				checksumSha256, chunkSize, normalizedTarget, meta, TTL_MINUTES);
	}

	/**
	 * When all chunks are present, assemble + import asynchronously; result on {@code /downloadEvents}.
	 */
	public void completeAsyncIfNeeded(String ticketId, String ownerUserId) {
		try {
			completeAsync(ticketId, ownerUserId);
		} catch (Exception ignored) {
			// chunk response already succeeded; SSE aborted on failure inside completeAsync
		}
	}

	public JsonObject completeAsync(String ticketId, String ownerUserId) throws Exception {
		TransferTicket ticket = ticketService.requireOwned(ticketId, ownerUserId);
		if (!ticket.upload) {
			throw new IllegalArgumentException("Not an upload ticket");
		}
		if (ticket.status == Status.READY && ticket.importResultJson != null) {
			return sessionBody(ticket);
		}
		synchronized (ticket) {
			if (ticket.finalizeStarted) {
				return sessionBody(ticket);
			}
			if (!ticketService.allChunksReceived(ticket)) {
				throw new IllegalStateException("Missing chunks");
			}
			ticket.finalizeStarted = true;
		}

		Map<String, String> metadataSnapshot = new HashMap<>(ticket.metadata);
		String requestId = ticket.requestId;
		String target = normalizeTarget(ticket.targetUrl);
		String fileName = ticket.fileName;
		String contentType = ticket.contentType;

		taskExecutorService.submit(() -> {
			try {
				if (ticketService.isAborted(ticketId)
						|| (JsonUtils.isStreamResponseEnabled()
								&& !ticketService.awaitSubscriberSeconds(ticketId, 60))) {
					ticketService.abortByRequestId(requestId);
					return;
				}
				Path assembled = ticketService.assembleAndVerify(ticketId, ownerUserId);
				MultipartFile multipart = new PathMultipartFile(assembled, fileName, contentType);
				String result;
				if ("/importResource".equals(target)) {
					result = completeImportResource(multipart, metadataSnapshot, ticketId, requestId);
				} else if ("/importFile".equals(target)) {
					result = completeImportFile(multipart, metadataSnapshot);
				} else {
					throw new IllegalArgumentException("Unsupported targetUrl: " + target);
				}
				if (ticketService.isAborted(ticketId)) {
					ticketService.abortByRequestId(requestId);
					return;
				}
				ticketService.completeUpload(ticketId, result);
			} catch (Exception e) {
				ticket.finalizeStarted = false;
				ticketService.abortByRequestId(requestId, e.getMessage());
			}
		}, requestId);

		return sessionBody(ticket);
	}

	private JsonObject sessionBody(TransferTicket ticket) {
		JsonObject body = new JsonObject();
		body.addProperty("ticket", ticket.ticketId);
		body.addProperty("requestId", ticket.requestId);
		body.addProperty("status", ticket.status.name());
		if (JsonUtils.isStreamResponseEnabled()) {
			body.addProperty("eventsUrl", TransferTicketService.eventsUrl(ticket.ticketId, ticket.requestId));
		}
		if (ticket.importResultJson != null) {
			body.addProperty("importResult", ticket.importResultJson);
		}
		return body;
	}

	private String completeImportResource(MultipartFile multipart, Map<String, String> metadata, String ticketId,
			String requestId) throws Exception {
		String formData = metadata.get("formData");
		if (StringUtils.isBlank(formData)) {
			throw new IllegalArgumentException("formData is required for importResource");
		}
		ImportRequest importRequest = jsonMapperUtils.mapToDTO(formData, ImportRequest.class);
		ApplicationContextAccessor.getBean(ImportRequestValidator.class).validate(multipart, importRequest);
		ImportResponse resp = new ImportResponse();
		// Analyze: every milestone. Commit: batch by eventBatchSize.
		ProgressEventSink sink = null;
		if (JsonUtils.isStreamResponseEnabled()) {
			sink = Boolean.TRUE.equals(importRequest.getUpload())
					? TicketProgressEventSink.batched(ticketService, ticketId, requestId)
					: TicketProgressEventSink.of(ticketService, ticketId, requestId);
		}
		try {
			String result = importManager.importFile(multipart, importRequest, resp, sink);
			if (!Boolean.TRUE.equals(importRequest.getUpload())) {
				return result;
			}
			ObjectNode responseNode = JsonNodeFactory.instance.objectNode();
			responseNode.put("status", 1);
			resp.setMessage(result);
			responseNode.set("response", JacksonUtility.mapToJson(resp));
			return responseNode.toString();
		} finally {
			if (sink != null) {
				sink.flush();
			}
		}
	}

	private String completeImportFile(MultipartFile multipart, Map<String, String> metadata) throws Exception {
		// destination may be blank (csv / datasource driver uploads); match /importFile behavior
		String destination = metadata.get("destination");
		if (destination == null) {
			destination = "";
		}
		Map<String, String> overrides = new HashMap<>(metadata);
		overrides.put("destination", destination);
		HttpServletRequest wrapped = MetadataHttpServletRequest.from(overrides);
		CapturingHttpServletResponse capturing = new CapturingHttpServletResponse();
		FileSystemOperationsController controller = ApplicationContextAccessor
				.getBean(FileSystemOperationsController.class);
		controller.executeImportOperation(multipart, destination, wrapped, capturing);
		return capturing.getBody();
	}

	private static String normalizeTarget(String targetUrl) {
		if (targetUrl == null) {
			return "";
		}
		String t = targetUrl.trim();
		int q = t.indexOf('?');
		if (q >= 0) {
			t = t.substring(0, q);
		}
		if (!t.startsWith("/")) {
			t = "/" + t;
		}
		return t;
	}

	private static void copyParam(HttpServletRequest request, Map<String, String> metadata, String name) {
		String value = request.getParameter(name);
		if (StringUtils.isNotBlank(value)) {
			metadata.put(name, value);
		}
	}

	private static String firstNonBlank(String a, String b) {
		if (StringUtils.isNotBlank(a)) {
			return a;
		}
		return b;
	}

	private static long parseLong(String value, long fallback) {
		if (StringUtils.isBlank(value)) {
			return fallback;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return fallback;
		}
	}
}

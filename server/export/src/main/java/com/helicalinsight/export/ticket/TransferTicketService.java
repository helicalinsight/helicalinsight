package com.helicalinsight.export.ticket;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SplitterUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.exception.InvalidTicketException;
import com.helicalinsight.export.exception.TicketInProgressException;
import com.helicalinsight.stream.ProgressEvent;
import com.helicalinsight.stream.SseEmitterRegistry;

/**
 * Shared in-memory ticket registry for resumable download and upload.
 * TTL purge is driven by Quartz ({@code TransferTicketPurgeJob}), not an internal scheduler.
 */
@Service
public class TransferTicketService {

	private static final Logger logger = LoggerFactory.getLogger(TransferTicketService.class);

	public static final String CHECKSUM_HEADER = "X-Checksum-SHA256";
	public static final String EVENTS_PATH = "/downloadEvents";

	public static String eventsUrl(String ticketId) {
		return EVENTS_PATH + "?ticket=" + ticketId;
	}
	
	
	public static String eventsUrl(String ticketId, String requestId) {
		String url = EVENTS_PATH + "?ticket=" + ticketId;
		if (StringUtils.isNotBlank(requestId)) {
			url += "&requestId=" + requestId;
		}
		return url;
	}

	private static final int DEFAULT_UPLOAD_CHUNK_SIZE = 1048576;

	private final ConcurrentHashMap<String, TransferTicket> byTicket = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> requestIdToTicket = new ConcurrentHashMap<>();

	@Autowired
	private SseEmitterRegistry sseEmitterRegistry;

	/**
	 * Ticket id from export identity + requestId (unique per export — no reuse/cache).
	 */
	public static String computeTicketId(String ownerUserId, String dir, String reportFile, String format,
			String reportType, String reportParameters, String printOptions, String html, String requestId) {
		String material = String.join("\u0001",
				nullToEmpty(ownerUserId),
				nullToEmpty(dir),
				nullToEmpty(reportFile),
				nullToEmpty(format),
				nullToEmpty(reportType),
				nullToEmpty(reportParameters),
				nullToEmpty(printOptions),
				nullToEmpty(html),
				nullToEmpty(requestId));
		return SplitterUtils.prepareServiceId(material);
	}

	public static String computeUploadTicketId(String ownerUserId, String fileName, long fileSize, String checksum,
			String requestId) {
		String material = String.join("\u0001",
				nullToEmpty(ownerUserId),
				"upload",
				nullToEmpty(fileName),
				String.valueOf(fileSize),
				nullToEmpty(checksum),
				nullToEmpty(requestId));
		return SplitterUtils.prepareServiceId(material);
	}

	public TransferTicket begin(String ticketId, String requestId, String ownerUserId, long ttlMinutes) {
		TransferTicket ticket = new TransferTicket(ticketId, requestId, ownerUserId,
				Instant.now().plusSeconds(ttlMinutes * 60));
		byTicket.put(ticketId, ticket);
		requestIdToTicket.put(requestId, ticketId);
		return ticket;
	}

	/** Lightweight upload-style ticket for streaming job progress (no chunk assembly). */
	public TransferTicket beginImportStream(String ticketId, String requestId, String ownerUserId, long ttlMinutes) {
		TransferTicket ticket = begin(ticketId, requestId, ownerUserId, ttlMinutes);
		ticket.upload = true;
		ticket.fileName = "import";
		return ticket;
	}

	private static Map<String, Object> progressEventData(ProgressEvent event) {
		Map<String, Object> data = new HashMap<>();
		data.put("channel", event.getChannel());
		data.put("path", event.getPath());
		data.put("message", event.getMessage());
		data.put("ts", event.getTs());
		return data;
	}

	public void emitProgress(String ticketId, ProgressEvent event) {
		if (event == null || !JsonUtils.isStreamResponseEnabled()) {
			return;
		}
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			return;
		}
		t.progressEvents.add(event);
		sseEmitterRegistry.send(ticketId, event.getChannel(), progressEventData(event));
	}

	/** Buffer + send a batch of progress events as one SSE {@code batch} payload. */
	public void emitProgressBatch(String ticketId, List<ProgressEvent> events) {
		if (events == null || events.isEmpty() || !JsonUtils.isStreamResponseEnabled()) {
			return;
		}
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			return;
		}
		List<Map<String, Object>> payload = new ArrayList<>(events.size());
		for (ProgressEvent event : events) {
			if (event == null) {
				continue;
			}
			t.progressEvents.add(event);
			payload.add(progressEventData(event));
		}
		if (!payload.isEmpty()) {
			sseEmitterRegistry.send(ticketId, "batch", payload);
		}
	}

	/** Block until /downloadEvents has a subscriber (or timeout). */
	public boolean awaitSubscriber(String ticketId, long timeout, TimeUnit unit) throws InterruptedException {
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			return false;
		}
		return t.subscriberReady.await(timeout, unit);
	}

	/** Like {@link #awaitSubscriber} but restores interrupt flag and returns false on interrupt. */
	public boolean awaitSubscriberSeconds(String ticketId, long timeoutSeconds) {
		try {
			return awaitSubscriber(ticketId, timeoutSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	private void emitFileLog(String ticketId, String message) {
		emitProgress(ticketId, ProgressEvent.of(ProgressEvent.CHANNEL_LOG, "file", message));
	}

	private void emitFileError(String ticketId, String message) {
		emitProgress(ticketId, ProgressEvent.of(ProgressEvent.CHANNEL_ERROR, "file", message));
	}

	/**
	 * Start or resume an upload ticket for the same requestId (idempotent resume).
	 */
	public TransferTicket beginUpload(String ticketId, String requestId, String ownerUserId, String fileName,
			String contentType, long totalSize, String expectedChecksum, int chunkSize, String targetUrl,
			Map<String, String> metadata, long ttlMinutes) throws Exception {
		TransferTicket existing = findByRequestId(requestId);
		// Resume PREPARING, or return READY so non-SSE clients can poll prepare for importResult.
		if (existing != null && existing.upload && existing.ownerUserId.equals(ownerUserId)
				&& (existing.status == Status.PREPARING || existing.status == Status.READY)) {
			return existing;
		}

		if (totalSize < 0) {
			throw new IllegalArgumentException("fileSize must be >= 0");
		}
		int size = chunkSize > 0 ? chunkSize : DEFAULT_UPLOAD_CHUNK_SIZE;
		int totalChunks = totalSize == 0 ? 1 : (int) ((totalSize + size - 1) / size);

		TransferTicket ticket = begin(ticketId, requestId, ownerUserId, ttlMinutes);
		ticket.upload = true;
		ticket.fileName = fileName;
		ticket.contentType = contentType;
		ticket.size = totalSize;
		ticket.expectedChecksumSha256 = expectedChecksum;
		ticket.chunkSize = size;
		ticket.totalChunks = totalChunks;
		ticket.targetUrl = targetUrl;
		if (metadata != null) {
			ticket.metadata.putAll(metadata);
		}
		Path base = TempDirectoryCleaner.getTempDirectory().toPath().resolve("upload-" + ticketId);
		Files.createDirectories(base);
		ticket.tempDir = base.toAbsolutePath().normalize();
		track(ticketId, ticket.tempDir);
		return ticket;
	}

	public TransferTicket findByRequestId(String requestId) {
		if (requestId == null) {
			return null;
		}
		String ticketId = requestIdToTicket.get(requestId);
		return ticketId == null ? null : byTicket.get(ticketId);
	}

	public boolean isAborted(String ticketId) {
		TransferTicket t = byTicket.get(ticketId);
		return t == null || t.status == Status.ABORTED || t.status == Status.EXPIRED;
	}

	public void track(String ticketId, Path path) {
		TransferTicket t = byTicket.get(ticketId);
		if (path == null) {
			return;
		}
		Path normalized = path.toAbsolutePath().normalize();
		if (t == null || t.status == Status.ABORTED || t.status == Status.EXPIRED) {
			deleteTracked(Set.of(normalized));
			return;
		}
		synchronized (t) {
			if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				deleteTracked(Set.of(normalized));
				return;
			}
			t.trackedPaths.add(normalized);
		}
	}

	public void complete(String ticketId, Path finalFile, String fileName, String contentType) {
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			if (finalFile != null) {
				deleteTracked(Set.of(finalFile.toAbsolutePath().normalize()));
			}
			return;
		}
		synchronized (t) {
			if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				if (finalFile != null) {
					deleteTracked(Set.of(finalFile.toAbsolutePath().normalize()));
				}
				deleteTracked(t.trackedPaths);
				t.trackedPaths.clear();
				return;
			}
			t.finalFile = finalFile.toAbsolutePath().normalize();
			t.trackedPaths.add(t.finalFile);
			t.fileName = fileName;
			t.contentType = contentType;
			t.size = Files.exists(t.finalFile) ? t.finalFile.toFile().length() : 0L;
			t.checksumSha256 = sha256File(t.finalFile);
			t.status = Status.READY;
		}
		notifyReady(ticketId, t);
	}

	/** Mark upload import finished and push SSE ready (includes importResultJson). */
	public void completeUpload(String ticketId, String importResultJson) {
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			return;
		}
		synchronized (t) {
			if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				return;
			}
			t.importResultJson = importResultJson;
			t.status = Status.READY;
		}
		notifyReady(ticketId, t);
	}

	public boolean putChunk(String ticketId, String currentUserId, int index, InputStream body, long contentLength)
			throws Exception {
		TransferTicket t = requireOwned(ticketId, currentUserId);
		if (!t.upload) {
			throw new IllegalArgumentException("Not an upload ticket");
		}
		synchronized (t) {
			if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				throw new InvalidTicketException("Upload aborted");
			}
			if (t.status != Status.PREPARING) {
				throw new InvalidTicketException("Upload not accepting chunks");
			}
			if (index < 0 || index >= t.totalChunks) {
				throw new IllegalArgumentException("Invalid chunk index");
			}
			if (t.receivedChunks.get(index)) {
				notifyUploadProgress(ticketId, t);
				return allChunksReceived(t);
			}
			Path chunkPath = chunkPath(t, index);
			long expected = expectedChunkLength(t, index);
			if (contentLength >= 0 && contentLength != expected) {
				throw new IllegalArgumentException("Unexpected chunk length");
			}
			long written = 0;
			try (OutputStream out = Files.newOutputStream(chunkPath, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
				byte[] buf = new byte[8192];
				int n;
				while ((n = body.read(buf)) >= 0) {
					if (n == 0) {
						continue;
					}
					written += n;
					if (written > expected) {
						throw new IllegalArgumentException("Chunk larger than expected");
					}
					out.write(buf, 0, n);
				}
			}
			if (written != expected) {
				Files.deleteIfExists(chunkPath);
				throw new IllegalArgumentException("Incomplete chunk");
			}
			t.receivedChunks.set(index);
			track(ticketId, chunkPath);
		}
		notifyUploadProgress(ticketId, t);
		return allChunksReceived(t);
	}

	public boolean allChunksReceived(TransferTicket t) {
		synchronized (t) {
			return t.upload && t.totalChunks > 0 && t.receivedChunks.cardinality() >= t.totalChunks;
		}
	}

	public List<Integer> receivedChunkIndices(TransferTicket t) {
		List<Integer> list = new ArrayList<>();
		synchronized (t) {
			for (int i = t.receivedChunks.nextSetBit(0); i >= 0; i = t.receivedChunks.nextSetBit(i + 1)) {
				list.add(i);
			}
		}
		return list;
	}

	public Path assembleAndVerify(String ticketId, String currentUserId) throws Exception {
		TransferTicket t = requireOwned(ticketId, currentUserId);
		if (!t.upload) {
			throw new IllegalArgumentException("Not an upload ticket");
		}
		synchronized (t) {
			if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				throw new InvalidTicketException("Upload aborted");
			}
			if (t.finalFile != null && Files.isRegularFile(t.finalFile)) {
				emitFileLog(ticketId, "File assembly skipped; assembled file already available.");
				return t.finalFile;
			}
			for (int i = 0; i < t.totalChunks; i++) {
				if (!t.receivedChunks.get(i)) {
					throw new IllegalStateException("Missing chunk " + i);
				}
			}
			emitFileLog(ticketId, "File assembly started (" + t.totalChunks + " chunk(s))…");
			Path assembled = t.tempDir.resolve(safeFileName(t.fileName));
			try (OutputStream out = Files.newOutputStream(assembled, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
				for (int i = 0; i < t.totalChunks; i++) {
					Files.copy(chunkPath(t, i), out);
				}
			}
			emitFileLog(ticketId, "File assembly complete (" + Files.size(assembled) + " bytes).");

			emitFileLog(ticketId, "Checksum verification started (SHA-256)…");
			String actual = sha256File(assembled);
			if (t.expectedChecksumSha256 != null && !t.expectedChecksumSha256.isEmpty()) {
				if (actual == null || !t.expectedChecksumSha256.equalsIgnoreCase(actual)) {
					Files.deleteIfExists(assembled);
					emitFileError(ticketId, "Checksum verification failed; mismatch.");
					throw new IllegalStateException("Checksum mismatch");
				}
				emitFileLog(ticketId, "Checksum verification complete.");
			} else {
				emitFileLog(ticketId, "Checksum computed (no client checksum to verify).");
			}
			t.finalFile = assembled.toAbsolutePath().normalize();
			t.checksumSha256 = actual;
			track(ticketId, t.finalFile);
			return t.finalFile;
		}
	}

	public TransferTicket requireOwned(String ticketId, String currentUserId) {
		TransferTicket t = require(ticketId);
		assertOwner(t, currentUserId);
		assertNotExpired(t);
		return t;
	}

	public Map<String, Object> uploadSessionPayload(TransferTicket t) {
		Map<String, Object> body = new HashMap<>();
		body.put("ticket", t.ticketId);
		body.put("requestId", t.requestId);
		body.put("status", t.status.name());
		body.put("chunkSize", t.chunkSize);
		body.put("totalChunks", t.totalChunks);
		body.put("receivedChunks", receivedChunkIndices(t));
		body.put("fileName", t.fileName);
		body.put("fileSize", t.size);
		if (JsonUtils.isStreamResponseEnabled()) {
			body.put("eventsUrl", eventsUrl(t.ticketId, t.requestId));
		}
		if (t.importResultJson != null) {
			body.put("importResult", t.importResultJson);
		}
		return body;
	}

	/**
	 * Open an SSE stream for ticket readiness. Sends READY immediately if already
	 * complete; otherwise keeps the connection until complete/abort.
	 */
	public SseEmitter openEvents(String ticketId, String currentUserId) {
		TransferTicket t = require(ticketId);
		assertOwner(t, currentUserId);
		assertNotExpired(t);

		SseEmitter emitter = sseEmitterRegistry.register(ticketId);
		t.subscriberReady.countDown();
		replayProgressEvents(ticketId, t);

		synchronized (t) {
			if (t.status == Status.READY && (t.upload ? t.importResultJson != null : t.finalFile != null)) {
				sendReadyEvent(ticketId, t);
				sseEmitterRegistry.complete(ticketId);
			} else if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				sendAbortedEvent(ticketId, "Ticket not available");
				sseEmitterRegistry.complete(ticketId);
			}
		}
		return emitter;
	}

	private void replayProgressEvents(String ticketId, TransferTicket t) {
		if (!JsonUtils.isStreamResponseEnabled() || t.progressEvents.isEmpty()) {
			return;
		}
		int batchSize = Math.max(1, JsonUtils.getResponseEventBatchSize());
		List<Map<String, Object>> payload = new ArrayList<>(Math.min(batchSize, t.progressEvents.size()));
		for (ProgressEvent event : t.progressEvents) {
			payload.add(progressEventData(event));
			if (payload.size() >= batchSize) {
				sseEmitterRegistry.send(ticketId, "batch", payload);
				payload = new ArrayList<>(batchSize);
			}
		}
		if (!payload.isEmpty()) {
			sseEmitterRegistry.send(ticketId, "batch", payload);
		}
	}

	public TransferTicket resolveReady(String ticketId, String currentUserId) {
		TransferTicket t = require(ticketId);
		assertNotExpired(t);
		assertOwner(t, currentUserId);
		if (t.status == Status.PREPARING) {
			throw new TicketInProgressException("Ticket not ready");
		}
		if (t.status != Status.READY || t.finalFile == null || !Files.exists(t.finalFile)) {
			throw new TicketInProgressException("Ticket not ready");
		}
		return t;
	}

	/**
	 * Mark ABORTED, delete tracked files, notify SSE — keep ticket briefly so late
	 * track/complete from the producer can no-op and delete orphans instead of throwing.
	 */
	public void abortByRequestId(String requestId) {
		abortByRequestId(requestId, null);
	}

	/**
	 * Same as {@link #abortByRequestId(String)}; {@code message} is sent on the SSE
	 * {@code aborted} event so the UI can show the underlying failure reason.
	 */
	public void abortByRequestId(String requestId, String message) {
		String ticketId = requestIdToTicket.get(requestId);
		if (ticketId == null) {
			logger.debug("abortByRequestId: no ticket for requestId={}", requestId);
			return;
		}
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			requestIdToTicket.remove(requestId);
			return;
		}
		synchronized (t) {
			if (t.status == Status.ABORTED || t.status == Status.EXPIRED) {
				logger.info("Ticket {} already {}; cleaning remaining temp files ({})",
						ticketId, t.status, t.trackedPaths.size());
				deleteTracked(t.trackedPaths);
				t.trackedPaths.clear();
				return;
			}
			t.status = Status.ABORTED;
		}
		logger.info("Transfer aborted for requestId={} ticketId={}; cleaning temp files ({} path(s))",
				requestId, ticketId, t.trackedPaths.size());
		notifyAborted(ticketId, StringUtils.isNotBlank(message) ? message : "Request aborted");
		deleteTracked(t.trackedPaths);
		t.trackedPaths.clear();
		logger.info("Transfer abort cleanup finished for requestId={} ticketId={}", requestId, ticketId);
	}

	/** Invoked by Quartz {@code TransferTicketPurgeJob}. */
	public void purgeExpired() {
		Instant now = Instant.now();
		Iterator<Map.Entry<String, TransferTicket>> it = byTicket.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, TransferTicket> e = it.next();
			TransferTicket t = e.getValue();
			if (!now.isAfter(t.expiresAt) && t.status != Status.EXPIRED) {
				continue;
			}
			synchronized (t) {
				t.status = Status.EXPIRED;
			}
			deleteTracked(t.trackedPaths);
			if (t.finalFile != null) {
				deleteTracked(Set.of(t.finalFile));
			}
			if (t.tempDir != null) {
				deleteTracked(Set.of(t.tempDir));
			}
			t.trackedPaths.clear();
			requestIdToTicket.remove(t.requestId, e.getKey());
			it.remove();
			sseEmitterRegistry.complete(e.getKey());
			logger.debug("Purged transfer ticket {}", e.getKey());
		}
	}

	private void notifyReady(String ticketId, TransferTicket t) {
		sendReadyEvent(ticketId, t);
		sseEmitterRegistry.complete(ticketId);
	}

	private void notifyAborted(String ticketId, String message) {
		sendAbortedEvent(ticketId, message);
		sseEmitterRegistry.complete(ticketId);
	}

	private void notifyUploadProgress(String ticketId, TransferTicket t) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "PREPARING");
		data.put("ticket", ticketId);
		data.put("receivedChunks", receivedChunkIndices(t));
		data.put("totalChunks", t.totalChunks);
		sseEmitterRegistry.send(ticketId, "progress", data);
	}

	private void sendReadyEvent(String ticketId, TransferTicket t) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "READY");
		data.put("ticket", ticketId);
		data.put("fileName", t.fileName);
		data.put("contentType", t.contentType);
		data.put("contentLength", t.size);
		data.put("checksumAlgorithm", "SHA-256");
		data.put("checksum", t.checksumSha256);
		data.put("eventsUrl", eventsUrl(ticketId, t.requestId));
		if (t.upload) {
			data.put("importResult", t.importResultJson);
			data.put("receivedChunks", receivedChunkIndices(t));
			data.put("totalChunks", t.totalChunks);
		} else {
			data.put("downloadUrl", "/fileDownload?ticket=" + ticketId);
		}
		sseEmitterRegistry.send(ticketId, "ready", data);
	}

	private void sendAbortedEvent(String ticketId, String message) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "ABORTED");
		data.put("ticket", ticketId);
		data.put("message", message);
		sseEmitterRegistry.send(ticketId, "aborted", data);
	}

	private void assertOwner(TransferTicket t, String currentUserId) {
		if (!t.ownerUserId.equals(currentUserId)) {
			throw new SecurityException("Forbidden");
		}
	}

	private void assertNotExpired(TransferTicket t) {
		if (t.status == Status.EXPIRED || Instant.now().isAfter(t.expiresAt)) {
			t.status = Status.EXPIRED;
			throw new InvalidTicketException("Ticket not available");
		}
		if (t.status == Status.ABORTED) {
			throw new InvalidTicketException(t.upload ? "Upload aborted" : "Ticket not available");
		}
	}

	static String sha256File(Path file) {
		if (file == null || !Files.isRegularFile(file)) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] buf = new byte[8192];
			try (InputStream in = Files.newInputStream(file)) {
				int n;
				while ((n = in.read(buf)) >= 0) {
					if (n > 0) {
						md.update(buf, 0, n);
					}
				}
			}
			return HexFormat.of().formatHex(md.digest());
		} catch (Exception e) {
			logger.warn("Failed to compute SHA-256 for {}", file, e);
			return null;
		}
	}

	private static Path chunkPath(TransferTicket t, int index) {
		return t.tempDir.resolve(String.format("chunk-%05d", index));
	}

	private static long expectedChunkLength(TransferTicket t, int index) {
		if (t.size == 0) {
			return 0;
		}
		long start = (long) index * t.chunkSize;
		return Math.min(t.chunkSize, t.size - start);
	}

	private static String safeFileName(String name) {
		if (name == null || name.isBlank()) {
			return "upload.bin";
		}
		String base = name.replace("\\", "/");
		int slash = base.lastIndexOf('/');
		if (slash >= 0) {
			base = base.substring(slash + 1);
		}
		return base.isBlank() ? "upload.bin" : base;
	}

	private static void deleteTracked(Set<Path> paths) {
		if (paths == null || paths.isEmpty()) {
			return;
		}
		paths.stream().filter(Files::isRegularFile).forEach(p -> {
			try {
				Files.deleteIfExists(p);
			} catch (Exception ignored) {
			}
		});
		paths.stream().filter(Files::isDirectory).sorted(Comparator.comparingInt(Path::getNameCount).reversed())
				.forEach(dir -> {
					try (Stream<Path> walk = Files.walk(dir)) {
						walk.sorted(Comparator.reverseOrder()).forEach(p -> {
							try {
								Files.deleteIfExists(p);
							} catch (Exception ignored) {
							}
						});
					} catch (Exception ignored) {
					}
				});
	}

	private TransferTicket require(String ticketId) {
		TransferTicket t = byTicket.get(ticketId);
		if (t == null) {
			throw new InvalidTicketException("Unknown ticket");
		}
		return t;
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}
}

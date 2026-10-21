package com.helicalinsight.export.ticket;

import java.nio.file.Path;
import java.time.Instant;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import com.helicalinsight.stream.ProgressEvent;

import lombok.Data;

/**
 * Shared transfer ticket for resumable download and resumable upload.
 */
@Data
public final class TransferTicket {
	public final String ticketId;
	public final String requestId;
	public final String ownerUserId;
	public volatile Status status;
	public volatile String fileName;
	public volatile String contentType;
	public volatile long size;
	/** Hex-encoded SHA-256 of finalFile (set on download complete / upload verify). */
	public volatile String checksumSha256;
	public volatile Path finalFile;
	public final Instant expiresAt;
	public final Set<Path> trackedPaths = ConcurrentHashMap.newKeySet();

	/** When true, ticket is an upload session (chunks → assemble → import). */
	public volatile boolean upload;
	public volatile String expectedChecksumSha256;
	public volatile int chunkSize;
	public volatile int totalChunks;
	public volatile Path tempDir;
	public volatile String targetUrl;
	public volatile String importResultJson;
	/** When true, assemble+import already submitted for this upload ticket. */
	public volatile boolean finalizeStarted;

	public final Map<String, String> metadata = new ConcurrentHashMap<>();
	public final BitSet receivedChunks = new BitSet();
	public final List<ProgressEvent> progressEvents = new CopyOnWriteArrayList<>();
	/** Released when an SSE subscriber registers on /downloadEvents for this ticket. */
	public final CountDownLatch subscriberReady = new CountDownLatch(1);

	TransferTicket(String ticketId, String requestId, String ownerUserId, Instant expiresAt) {
		this.ticketId = ticketId;
		this.requestId = requestId;
		this.ownerUserId = ownerUserId;
		this.status = Status.PREPARING;
		this.expiresAt = expiresAt;
	}
}

package com.helicalinsight.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Buffers {@link ProgressEvent}s and flushes when {@code eventBatchSize}
 * (setting.xml) is reached, or on complete/abort. No timer — same count-based
 * approach as report fill streaming.
 */
public class BatchingProgressEventSink implements ProgressEventSink {

	private final ProgressEventSink delegate;
	private final int batchSize;
	private final List<ProgressEvent> buffer = new ArrayList<>();
	private final Object lock = new Object();
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final AtomicBoolean firstFlush = new AtomicBoolean(true);

	public BatchingProgressEventSink(ProgressEventSink delegate) {
		this.delegate = delegate;
		this.batchSize = Math.max(1, JsonUtils.getResponseEventBatchSize());
	}

	@Override
	public void emit(ProgressEvent event) {
		if (event == null || closed.get()) {
			return;
		}
		List<ProgressEvent> toSend = null;
		synchronized (lock) {
			buffer.add(event);
			// First event goes out immediately (same idea as report fill's first page).
			if (firstFlush.compareAndSet(true, false) || buffer.size() >= batchSize) {
				toSend = takeBufferUnlocked();
			}
		}
		dispatch(toSend);
	}

	@Override
	public void flush() {
		List<ProgressEvent> toSend;
		synchronized (lock) {
			toSend = takeBufferUnlocked();
		}
		dispatch(toSend);
	}

	@Override
	public void complete(Object result) {
		closeAndFlush();
		delegate.complete(result);
	}

	@Override
	public void abort(String message) {
		closeAndFlush();
		delegate.abort(message);
	}

	private void closeAndFlush() {
		closed.set(true);
		flush();
	}

	private List<ProgressEvent> takeBufferUnlocked() {
		if (buffer.isEmpty()) {
			return null;
		}
		List<ProgressEvent> batch = new ArrayList<>(buffer);
		buffer.clear();
		return batch;
	}

	private void dispatch(List<ProgressEvent> batch) {
		if (batch == null || batch.isEmpty()) {
			return;
		}
		if (delegate instanceof BatchAwareProgressEventSink batchAware) {
			batchAware.emitBatch(batch);
		} else {
			for (ProgressEvent event : batch) {
				delegate.emit(event);
			}
		}
	}
}

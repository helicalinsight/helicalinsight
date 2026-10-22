package com.helicalinsight.stream;

import java.util.List;

/**
 * Optional sink capability: emit a flushed batch as one SSE payload.
 */
public interface BatchAwareProgressEventSink extends ProgressEventSink {

	void emitBatch(List<ProgressEvent> events);
}

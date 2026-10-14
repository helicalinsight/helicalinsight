package com.helicalinsight.stream;

public interface ProgressEventSink {

	void emit(ProgressEvent event);

	void complete(Object result);

	void abort(String message);

	/** Flush any buffered events without completing the sink. */
	default void flush() {
	}
}

package com.helicalinsight.stream;

public interface StreamSession {

	String getRequestId();

	void send(StreamEvent event);

	void complete();

	void error(Throwable t);
}
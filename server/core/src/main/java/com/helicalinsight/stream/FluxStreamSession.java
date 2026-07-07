package com.helicalinsight.stream;

import java.util.Map;

import org.springframework.http.codec.ServerSentEvent;

import reactor.core.publisher.FluxSink;

public class FluxStreamSession implements StreamSession {

	private final String requestId;
	private final FluxSink<Object> sink;

	public FluxStreamSession(String requestId, FluxSink<Object> sink) {
		this.requestId = requestId;
		this.sink = sink;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public void send(StreamEvent event) {
//		sink.next(Map.of("event", event.name(), "data", event.data()));
		
//		new ServerSentEvent.<String>builder().event(event.name()).data(event.data());
		
		ServerSentEvent<String> serverSentEvent = ServerSentEvent.<String>builder()
				.event(event.name())
				.data(event.data())
				.build();
		synchronized (sink) {
			sink.next(serverSentEvent);
		}
	}

	@Override
	public void complete() {
		sink.complete();
	}

	@Override
	public void error(Throwable t) {
		sink.error(t);
	}
}
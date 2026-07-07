package com.helicalinsight.stream;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.FluxSink;

public class StreamManager {

	private final IStreamRegistry registry;

	public StreamManager(IStreamRegistry registry) {
		this.registry = registry;
	}

	public StreamSession createSseStream(String requestId, SseEmitter emitter) {
		StreamSession session = new SseStreamSession(requestId, emitter);
		registry.register(requestId, session);
		return session;
	}

	public StreamSession createFluxStream(String requestId, FluxSink<Object> sink) {
		StreamSession session = new FluxStreamSession(requestId, sink);
		registry.register(requestId, session);
		return session;
	}

	public void send(String requestId, String event, String data) {
		StreamSession session = registry.get(requestId);
		if (session != null) {
			session.send(new StreamEvent(event, data.toString()));
		}
	}

	public void complete(String requestId) {
		StreamSession session = registry.get(requestId);
		if (session != null) {
			session.complete();
			registry.remove(requestId);
		}
	}
}
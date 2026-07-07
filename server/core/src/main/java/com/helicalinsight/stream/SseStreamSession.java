package com.helicalinsight.stream;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseStreamSession implements StreamSession {

	private static final Logger logger = LoggerFactory.getLogger(SseStreamSession.class);

	private final String requestId;
	private final SseEmitter emitter;

	public SseStreamSession(String requestId, SseEmitter emitter) {
		this.requestId = requestId;
		this.emitter = emitter;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public void send(StreamEvent event) {
		try {
			synchronized (emitter) {
				emitter.send(SseEmitter.event()
						.name(event.name())
						.data(event.data(), MediaType.APPLICATION_JSON));
			}
		} catch (IOException | IllegalStateException e) {
			logger.warn("Emitter closed  {}", requestId);
		}
	}

	@Override
	public void complete() {
		send(new StreamEvent(EventType.COMPLETED.value(), "{\"status\":\"DONE\"}"));
		emitter.complete();
	}

	@Override
	public void error(Throwable t) {
			send(new StreamEvent("error", t.getMessage()));
			complete();
	}

	public SseEmitter getEmitter() {
		return emitter;
	}
}
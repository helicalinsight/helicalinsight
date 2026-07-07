package com.helicalinsight.stream;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;



/**
 * Registry to manage the SseEmitter life cycle.
 */



@Component
public class SseEmitterRegistry implements StreamRegistry<SseEmitter> {
	
	
	private static final long SSE_TIMEOUT = 0L;
	private static final Logger logger  = LoggerFactory.getLogger(SseEmitterRegistry.class);
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	
	@Override
	public SseEmitter register(String requestId) {
		
		SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
		emitters.put(requestId, emitter);
		
		emitter.onCompletion(() -> {
			logger.debug("Emitter completed : {}", requestId);
			remove(requestId);
		});
		
		emitter.onTimeout(() -> {
			logger.warn("Emitter timeout : {}", requestId);
			remove(requestId);
		});
		
		emitter.onError(e -> {
			logger.error("Emitter error for requestId {}", requestId, e);
			emitter.completeWithError(e);
			remove(requestId);
		});
		
		return emitter;
	}

	@Override
	public void send(String requestId, String eventName,  Object data) {
		SseEmitter emitter = emitters.get(requestId);
		if ( emitter == null) return ;
			try {
				synchronized (emitter) {
					emitter.send(SseEmitter.event()
						.name(eventName)
						.data(data,MediaType.APPLICATION_JSON));
				}
			}
			catch (IOException | IllegalStateException e) {
				logger.warn("Emitter closed  {}", requestId);
				remove(requestId);
			}
	}

	@Override
	public void complete(String requestId) {
		SseEmitter emitter = emitters.get(requestId);
		if ( emitter != null) {
			try {
				emitter.complete();
			}
			catch (Exception ignore) {
			}
			finally {
				remove(requestId);
			}
		}
	}

	@Override
	public void remove(String requestId) {
		emitters.remove(requestId);
	}

	@Override
	public int getActiveConnections() {
		return emitters.size();
	}

	

}

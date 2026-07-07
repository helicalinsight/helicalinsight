package com.helicalinsight.stream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStreamRegistry implements IStreamRegistry {

	private final Map<String, StreamSession> sessions = new ConcurrentHashMap<>();

	@Override
	public StreamSession register(String requestId, StreamSession session) {
		sessions.put(requestId, session);
		return session;
	}

	@Override
	public StreamSession get(String requestId) {
		return sessions.get(requestId);
	}

	@Override
	public void remove(String requestId) {
		sessions.remove(requestId);
	}

	@Override
	public int getActiveConnections() {
		return sessions.size();
	}
}
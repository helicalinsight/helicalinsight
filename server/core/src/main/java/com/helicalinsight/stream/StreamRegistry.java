package com.helicalinsight.stream;

public interface StreamRegistry<T> {

	 T register(String requestId);
	 void send(String requestId, String eventName, Object data);
	 void complete(String requestId);
	 void remove(String requestId);
	 int getActiveConnections();
}

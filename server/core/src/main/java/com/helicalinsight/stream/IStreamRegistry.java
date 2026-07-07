package com.helicalinsight.stream;

public interface IStreamRegistry {

    StreamSession register(String requestId, StreamSession session);

    StreamSession get(String requestId);

    void remove(String requestId);

    int getActiveConnections();
}
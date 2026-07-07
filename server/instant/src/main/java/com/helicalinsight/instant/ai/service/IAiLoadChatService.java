package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiLoadChatService extends IInstantBIService {

    void execute(String chatSeqId, String formData, HttpServletRequest request, HttpServletResponse response)
            throws IOException;
}

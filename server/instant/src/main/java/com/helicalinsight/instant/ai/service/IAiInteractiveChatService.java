package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiInteractiveChatService extends IInstantBIService {

    void execute(String input, String chatid, String chatSeqId, String subject,
                 HttpServletRequest request, HttpServletResponse response) throws IOException;
}

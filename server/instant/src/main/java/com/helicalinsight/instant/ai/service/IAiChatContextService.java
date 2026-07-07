package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiChatContextService extends IInstantBIService {

    void execute(String input, HttpServletRequest request, HttpServletResponse response) throws IOException;
}

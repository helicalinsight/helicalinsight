package com.helicalinsight.instant.ai.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiLlmUsageAuditService extends IInstantBIService {

    void execute(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

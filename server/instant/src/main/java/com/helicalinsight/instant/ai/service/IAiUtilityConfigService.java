package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Proxies InstantBI runtime configuration utility endpoints
 * ({@code /utility/*}) to the Python InstantBI service.
 */
public interface IAiUtilityConfigService extends IInstantBIService {

    void execute(String utilityPath, HttpServletRequest request, HttpServletResponse response)
            throws IOException;
}

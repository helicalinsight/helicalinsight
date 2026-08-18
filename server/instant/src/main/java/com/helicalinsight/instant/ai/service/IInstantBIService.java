package com.helicalinsight.instant.ai.service;

import com.helicalinsight.instant.ai.payload.IInstantBIPayload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IInstantBIService {

    void execute(IInstantBIPayload payload, HttpServletRequest request, HttpServletResponse response)
            throws IOException;
}

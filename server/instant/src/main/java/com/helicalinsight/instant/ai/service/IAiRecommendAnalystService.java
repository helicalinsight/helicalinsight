package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiRecommendAnalystService extends IInstantBIService {

    void execute(String model, String domain, HttpServletRequest request, HttpServletResponse response) throws IOException;
}

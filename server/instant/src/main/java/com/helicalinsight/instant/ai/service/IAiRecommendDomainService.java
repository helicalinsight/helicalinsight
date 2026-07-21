package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiRecommendDomainService extends IInstantBIService {

    void execute(String model, HttpServletRequest request, HttpServletResponse response) throws IOException;
}

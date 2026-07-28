package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiListChartsService extends IInstantBIService {

    void execute(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiConvertChartService extends IInstantBIService {

    void execute(String vfTemplate, String selectedChart, String chatId, String chatSequence,
                 HttpServletRequest request, HttpServletResponse response) throws IOException;
}

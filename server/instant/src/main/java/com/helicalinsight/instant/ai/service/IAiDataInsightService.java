package com.helicalinsight.instant.ai.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IAiDataInsightService extends IInstantBIService {

    void execute(String chatSeqId, String chatid, String inputParam, String formData, String subjectString,
                 HttpServletRequest request, HttpServletResponse response) throws IOException;
}

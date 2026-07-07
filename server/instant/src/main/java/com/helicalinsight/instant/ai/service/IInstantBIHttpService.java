package com.helicalinsight.instant.ai.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.Callable;

public interface IInstantBIHttpService extends IInstantBIService {

    String callHttp(String endpoint, JsonObject body);

    String executeCancellableCall(HttpServletRequest request, Callable<JsonObject> bodyPreparer, String endpoint);
}

package com.helicalinsight.instant.ai.service;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FrameworkObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.Callable;

public interface IInstantBIHttpService {

    String callHttp(String endpoint, JsonObject body);

    String executeCancellableCall(HttpServletRequest request, Callable<JsonObject> bodyPreparer, String endpoint);
}

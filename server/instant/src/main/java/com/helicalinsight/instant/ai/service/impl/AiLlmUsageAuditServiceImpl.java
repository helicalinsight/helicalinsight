package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import com.helicalinsight.admin.service.LlmUsageAuditService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.instant.ai.service.IAiLlmUsageAuditService;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Collectors;

public class AiLlmUsageAuditServiceImpl implements IAiLlmUsageAuditService {

    private static final Logger logger = LoggerFactory.getLogger(AiLlmUsageAuditServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject mainObject = new JsonObject();
        try {
            String body = readRequestBody(request);
            if (StringUtils.isBlank(body)) {
                mainObject.addProperty("status", 0);
                mainObject.addProperty("message", "Request body is required.");
                InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
                return;
            }

            JsonObject payload = GsonUtility.parseString(body, JsonObject.class);
            String payloadUsername = GsonUtility.optString(payload, "username");
            String authenticatedUsername = AuthenticationUtils.getUserName();

            if (StringUtils.isBlank(payloadUsername)
                    || StringUtils.isBlank(authenticatedUsername)
                    || !payloadUsername.equals(authenticatedUsername)) {
                mainObject.addProperty("status", 0);
                mainObject.addProperty("message", "Authenticated user does not match audit payload.");
                InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
                return;
            }

            JsonObject tokenUsage = GsonUtility.optJsonObject(payload, "tokenUsage");
            int totalTokens = tokenUsage == null ? 0 : GsonUtility.optIntValue(tokenUsage, "total_tokens", 0);
            if (totalTokens <= 0) {
                mainObject.addProperty("status", 1);
                mainObject.addProperty("message", "Skipped audit with no LLM token usage.");
                InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
                return;
            }

            HILlmUsageAudit audit = buildAuditRecord(payload, tokenUsage, totalTokens);
            ApplicationContextAccessor.getBean(LlmUsageAuditService.class).save(audit);

            mainObject.addProperty("status", 1);
            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (Exception exception) {
            logger.error("Failed to persist LLM usage audit", exception);
            mainObject.addProperty("status", 0);
            mainObject.addProperty("message", "Failed to persist LLM usage audit.");
            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        }
    }

    private static HILlmUsageAudit buildAuditRecord(JsonObject payload, JsonObject tokenUsage, int totalTokens) {
        HILlmUsageAudit audit = new HILlmUsageAudit();
        audit.setUsername(GsonUtility.optString(payload, "username"));
        audit.setEndpoint(GsonUtility.optString(payload, "endpoint"));
        audit.setUserQuery(GsonUtility.optString(payload, "userQuery"));
        audit.setInputTokens(GsonUtility.optIntValue(tokenUsage, "input_tokens", 0));
        audit.setOutputTokens(GsonUtility.optIntValue(tokenUsage, "output_tokens", 0));
        audit.setTotalTokens(totalTokens);
        audit.setInputCost(toBigDecimal(GsonUtility.optDouble(tokenUsage, "input_cost")));
        audit.setOutputCost(toBigDecimal(GsonUtility.optDouble(tokenUsage, "output_cost")));
        audit.setTotalCost(toBigDecimal(GsonUtility.optDouble(tokenUsage, "total_cost")));
        audit.setModelName(GsonUtility.optString(tokenUsage, "model_name"));
        audit.setRequestStatus(GsonUtility.optString(payload, "requestStatus"));
        audit.setErrorMessage(GsonUtility.optString(payload, "errorMessage"));
        audit.setChatId(GsonUtility.optString(payload, "chatId"));
        audit.setChatSeqId(GsonUtility.optString(payload, "chatSeqId"));
        audit.setCreatedAt(new Date());
        return audit;
    }

    private static BigDecimal toBigDecimal(Double value) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return null;
        }
        return BigDecimal.valueOf(value);
    }

    private static String readRequestBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}

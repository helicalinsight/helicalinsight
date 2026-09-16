package com.helicalinsight.instant.report;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Loads an Instant report for edit and hydrates InstantBI chat memory from saved turns.
 * {@code /updateMemory} runs on the current service thread so history is ready before
 * getReportForEdit returns. InstantBI failures are logged and do not fail the open.
 */
@SuppressWarnings("unused")
public class InstantReportForEditReaderComponent extends InstantReportReaderComponent {

    private static final Logger logger = LoggerFactory.getLogger(InstantReportForEditReaderComponent.class);

    @Override
    public String executeComponent(String jsonFormData) {
        String reportJson = super.executeComponent(jsonFormData);
        JsonObject reportContent = JsonParser.parseString(reportJson).getAsJsonObject();
        hydrateInstantBiChatMemory(reportContent);
        aliasActiveChatId(reportContent);
        return reportContent.toString();
    }

    private void hydrateInstantBiChatMemory(JsonObject reportContent) {
        try {
            HttpServletRequest request = currentRequest();
            if (request == null || reportContent == null) {
                logger.warn("Could not hydrate InstantBI chat memory after getReportForEdit: missing request context");
                return;
            }
            JsonObject reportPayload = unwrapReportData(reportContent);
            JsonObject body = new JsonObject();
            JsonObject userInput = new JsonObject();
            InstantBIUtils.addSessionContext(request, userInput);
            String chatId = resolveChatId(reportPayload);
            if (StringUtils.isNotBlank(chatId)) {
                userInput.addProperty("chatid", chatId);
            }
            userInput.add("report", reportPayload);
            body.add("input", userInput);
            InstantBIServiceFactory.getHttpService().callHttp("/updateMemory", body);
        } catch (Exception exception) {
            logger.warn("Could not hydrate InstantBI chat memory after getReportForEdit", exception);
        }
    }

    public static JsonObject unwrapReportData(JsonObject reportContent) {
        JsonObject data = GsonUtility.optJsonObject(reportContent, "data");
        return data != null ? data : reportContent;
    }

    public static String resolveChatId(JsonObject reportPayload) {
        JsonObject state = GsonUtility.optJsonObject(reportPayload, "state");
        if (state == null) {
            return "";
        }
        String chatId = GsonUtility.optString(state, "activeChatId");
        if (StringUtils.isBlank(chatId)) {
            chatId = GsonUtility.optString(state, "activeChatID");
        }
        return chatId;
    }

    /**
     * The InstantBI UI reads {@code state.activeChatID}. Saved reports store
     * {@code activeChatId}. Copy the saved id so data-insight uses the hydrated thread.
     */
    public static void aliasActiveChatId(JsonObject reportContent) {
        JsonObject data = unwrapReportData(reportContent);
        JsonObject state = GsonUtility.optJsonObject(data, "state");
        if (state == null) {
            return;
        }
        String chatId = resolveChatId(data);
        if (StringUtils.isBlank(chatId)) {
            return;
        }
        state.addProperty("activeChatId", chatId);
        state.addProperty("activeChatID", chatId);
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }
}

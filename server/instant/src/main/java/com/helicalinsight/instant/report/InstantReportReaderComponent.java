package com.helicalinsight.instant.report;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * InstantReportReaderComponent is responsible for reading an instant report from the file system
 * and returning its content as JSON.
 *
 * Open and edit both hydrate InstantBI chat memory (parent turns and think-mode
 * supporting questions) so data-insight can resolve SQL after the report is opened.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class InstantReportReaderComponent implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(InstantReportReaderComponent.class);

	/**
     * Executes the component to read an instant report.
     *
     * @param jsonFormData 		 JSON string containing form data with directory and file name.
     * @return A string representing the content of the instant report in JSON format.
     * @throws IllegalArgumentException If the specified file doesn't exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formJson.get("dir").getAsString();
        String fileName = formJson.get("file").getAsString();


        final AdhocReport adhocReport = (AdhocReport) ReportOpenHelper.getInstantReportDb(directory, fileName);
        if (adhocReport == null) {
            throw new IllegalArgumentException("The file " + fileName + " doesn't exists. " +
                    "Aborting operation.");
        }

        JsonObject reportContent = ReportOpenHelper.reportContentAsJson(adhocReport);
        hydrateInstantBiChatMemory(reportContent);
        aliasActiveChatId(reportContent);
        return reportContent.toString();
    }

    /**
     * Loads saved InstantBI turns into Python chat memory before the report UI runs.
     * InstantBI failures are logged and do not fail the open.
     */
    void hydrateInstantBiChatMemory(JsonObject reportContent) {
        try {
            HttpServletRequest request = currentRequest();
            if (request == null || reportContent == null) {
                logger.warn("Could not hydrate InstantBI chat memory after report open: missing request context");
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
            logger.warn("Could not hydrate InstantBI chat memory after report open", exception);
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

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

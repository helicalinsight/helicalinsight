package com.helicalinsight.instant.report;

import com.google.gson.JsonObject;

/**
 * Loads an Instant report for edit. Chat memory hydration (parent turns and
 * think-mode supporting questions) runs in {@link InstantReportReaderComponent}
 * so data-insight is ready before the report returns.
 */
@SuppressWarnings("unused")
public class InstantReportForEditReaderComponent extends InstantReportReaderComponent {

    public static JsonObject unwrapReportData(JsonObject reportContent) {
        return InstantReportReaderComponent.unwrapReportData(reportContent);
    }

    public static String resolveChatId(JsonObject reportPayload) {
        return InstantReportReaderComponent.resolveChatId(reportPayload);
    }

    public static void aliasActiveChatId(JsonObject reportContent) {
        InstantReportReaderComponent.aliasActiveChatId(reportContent);
    }
}

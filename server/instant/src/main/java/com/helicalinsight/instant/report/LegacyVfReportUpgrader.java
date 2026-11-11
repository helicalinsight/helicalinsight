package com.helicalinsight.instant.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Older Instant reports store a visualization function on {@code vf_template}
 * (or legacy {@code vf} / {@code vf_string}) without a Helical report model.
 * Opening those reports converts the saved SQL into {@code report_model} and
 * shows it as a table.
 */
final class LegacyVfReportUpgrader {

    private static final Logger logger = LoggerFactory.getLogger(LegacyVfReportUpgrader.class);

    static final String SQL_TO_REPORT_MODEL = "/sql-to-report-model";

    private LegacyVfReportUpgrader() {
    }

    static void upgrade(JsonObject reportContent) {
        try {
            JsonObject data = InstantReportReaderComponent.unwrapReportData(reportContent);
            if (data == null || !containsVfTemplate(data)) {
                return;
            }
            HttpServletRequest request = currentRequest();
            if (request == null) {
                logger.warn("Could not convert VF SQL while opening the report: missing request context");
                return;
            }
            JsonObject metadata = GsonUtility.optJsonObject(data, "metadata");
            String location = primitiveText(metadata, "location");
            String metadataFileName = primitiveText(metadata, "metadataFileName");
            if (StringUtils.isAnyBlank(location, metadataFileName)) {
                logger.warn("Could not convert VF SQL while opening the report: metadata location is missing");
                return;
            }
            JsonObject session = new JsonObject();
            InstantBIUtils.addSessionContext(request, session);
            Map<String, JsonObject> convertedBySql = new LinkedHashMap<>();
            Map<String, JsonObject> convertedById = new LinkedHashMap<>();
            Map<String, String> hreportBySeq = new LinkedHashMap<>();
            JsonObject state = GsonUtility.optJsonObject(data, "state");
            upgradeChatResponses(state, location, metadataFileName, session, convertedBySql, convertedById, hreportBySeq);
            applyConvertedModels(GsonUtility.optJsonObject(state, "dashboard_model"), convertedById, hreportBySeq);
        } catch (Exception exception) {
            logger.warn("Could not convert VF SQL while opening the report", exception);
        }
    }

    /**
     * True when a saved visualization function is present.
     * A JSON null or blank {@code vf_template} is the current empty default.
     * Older saves may put that function on {@code vf} or {@code vf_string}.
     */
    static boolean vfTemplatePresent(JsonObject chat) {
        if (chat == null) {
            return false;
        }
        JsonObject viz = GsonUtility.optJsonObject(chat, "viz");
        return nonBlank(viz, "vf_template")
                || nonBlank(chat, "vf_template")
                || nonBlank(viz, "vf")
                || nonBlank(chat, "vf")
                || nonBlank(viz, "vf_string")
                || nonBlank(chat, "vf_string");
    }

    private static boolean containsVfTemplate(JsonObject data) {
        JsonObject state = GsonUtility.optJsonObject(data, "state");
        if (state == null) {
            return false;
        }
        if (anyVf(GsonUtility.optJsonArray(state, "chat_responses"))) {
            return true;
        }
        JsonObject dashboard = GsonUtility.optJsonObject(state, "dashboard_model");
        return dashboard != null && anyVf(GsonUtility.optJsonArray(dashboard, "items"));
    }

    private static boolean anyVf(JsonArray nodes) {
        if (nodes == null) {
            return false;
        }
        for (JsonElement element : nodes) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject node = element.getAsJsonObject();
            if (vfTemplatePresent(node) || vfTemplatePresent(nestedChat(node))) {
                return true;
            }
            JsonArray history = history(node);
            if (history == null) {
                continue;
            }
            for (JsonElement stepElement : history) {
                if (stepElement == null || !stepElement.isJsonObject()) {
                    continue;
                }
                JsonObject step = stepElement.getAsJsonObject();
                if (vfTemplatePresent(step) || vfTemplatePresent(nestedChat(step))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void upgradeChatResponses(JsonObject state, String location, String metadataFileName,
                                             JsonObject session, Map<String, JsonObject> convertedBySql,
                                             Map<String, JsonObject> convertedById, Map<String, String> hreportBySeq) {
        if (state == null) {
            return;
        }
        JsonArray responses = GsonUtility.optJsonArray(state, "chat_responses");
        if (responses == null) {
            return;
        }
        for (JsonElement element : responses) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject response = element.getAsJsonObject();
            remember(response, upgradeNode(response, location, metadataFileName, session, convertedBySql), convertedById);
            rememberHreport(response, hreportBySeq);
            JsonArray history = history(response);
            if (history == null) {
                continue;
            }
            for (JsonElement stepElement : history) {
                if (stepElement == null || !stepElement.isJsonObject()) {
                    continue;
                }
                JsonObject step = stepElement.getAsJsonObject();
                JsonObject stepChat = nestedChat(step);
                JsonObject converted = upgradeNode(stepChat, location, metadataFileName, session, convertedBySql);
                if (converted == null) {
                    converted = upgradeNode(step, location, metadataFileName, session, convertedBySql);
                }
                if (converted == null) {
                    continue;
                }
                if (stepChat != null) {
                    stepChat.add("report_model", converted.deepCopy());
                }
                step.add("report_model", converted.deepCopy());
                ensureHreportId(stepChat, step);
                remember(step, converted, convertedById);
                remember(stepChat, converted, convertedById);
                rememberHreport(step, hreportBySeq);
                rememberHreport(stepChat, hreportBySeq);
            }
        }
    }

    private static void applyConvertedModels(JsonObject dashboard, Map<String, JsonObject> convertedById,
                                             Map<String, String> hreportBySeq) {
        if (dashboard == null || convertedById.isEmpty()) {
            return;
        }
        JsonArray items = GsonUtility.optJsonArray(dashboard, "items");
        if (items == null) {
            return;
        }
        for (JsonElement element : items) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject item = element.getAsJsonObject();
            JsonObject converted = lookup(item, convertedById);
            if (converted == null) {
                continue;
            }
            item.add("report_model", converted.deepCopy());
            JsonObject dataModel = GsonUtility.optJsonObject(converted, "data_model");
            if (dataModel != null && item.has("data_model")) {
                item.add("data_model", dataModel.deepCopy());
            }
            String hreportId = lookupText(item, hreportBySeq);
            if (StringUtils.isNotBlank(hreportId) && StringUtils.isBlank(primitiveText(item, "hreportId"))) {
                item.addProperty("hreportId", hreportId);
            }
        }
    }

    /**
     * The report viewer starts a chart only when this id is already on the
     * getReport payload. Keep a saved id; otherwise assign one and share it
     * across the step and its nested chat response.
     */
    private static void ensureHreportId(JsonObject... nodes) {
        String existing = "";
        if (nodes != null) {
            for (JsonObject node : nodes) {
                existing = primitiveText(node, "hreportId");
                if (StringUtils.isNotBlank(existing)) {
                    break;
                }
            }
        }
        if (StringUtils.isBlank(existing)) {
            existing = UUID.randomUUID().toString();
        }
        if (nodes == null) {
            return;
        }
        for (JsonObject node : nodes) {
            if (node != null && StringUtils.isBlank(primitiveText(node, "hreportId"))) {
                node.addProperty("hreportId", existing);
            }
        }
    }

    private static JsonObject upgradeNode(JsonObject node, String location, String metadataFileName,
                                          JsonObject session, Map<String, JsonObject> convertedBySql) {
        if (node == null || !vfTemplatePresent(node)) {
            return null;
        }
        normalizeVizTemplate(node);
        if (alreadyTable(node)) {
            ensureHreportId(node);
            return GsonUtility.optJsonObject(node, "report_model");
        }
        String sql = extractSql(node);
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        JsonObject cached = convertedBySql.get(sql);
        JsonObject reportModel = cached != null
                ? cached.deepCopy().getAsJsonObject()
                : convertSql(sql, location, metadataFileName, dialect(node), session);
        if (reportModel == null) {
            return null;
        }
        forceTableChart(reportModel);
        if (cached == null) {
            convertedBySql.put(sql, reportModel.deepCopy().getAsJsonObject());
        }
        node.add("report_model", reportModel);
        ensureHreportId(node);
        return reportModel;
    }

    private static JsonObject convertSql(String sql, String location, String metadataFileName, String dialect,
                                         JsonObject session) {
        try {
            JsonObject userInput = session.deepCopy().getAsJsonObject();
            userInput.addProperty("sql", sql);
            userInput.addProperty("location", location);
            userInput.addProperty("metadataFileName", metadataFileName);
            userInput.addProperty("defaultChart", "table");
            if (StringUtils.isNotBlank(dialect)) {
                userInput.addProperty("dialect", dialect);
            }
            JsonObject body = new JsonObject();
            body.add("input", userInput);
            IInstantBIHttpService httpService = InstantBIServiceFactory.getHttpService();
            String raw = httpService.callHttp(SQL_TO_REPORT_MODEL, body);
            if (StringUtils.isBlank(raw)) {
                return null;
            }
            JsonObject parsed = JsonParser.parseString(raw).getAsJsonObject();
            String error = primitiveText(parsed, "error");
            if (StringUtils.isNotBlank(error)) {
                logger.warn("SQL to report model conversion failed: {}", error);
                return null;
            }
            return GsonUtility.optJsonObject(parsed, "report_model");
        } catch (Exception exception) {
            logger.warn("SQL to report model conversion failed", exception);
            return null;
        }
    }

    private static boolean alreadyTable(JsonObject node) {
        JsonObject reportModel = GsonUtility.optJsonObject(node, "report_model");
        JsonObject vizModel = GsonUtility.optJsonObject(reportModel, "viz_model");
        JsonObject chart = GsonUtility.optJsonObject(vizModel, "chart");
        if (!"table".equalsIgnoreCase(primitiveText(chart, "mark"))) {
            return false;
        }
        JsonObject dataModel = GsonUtility.optJsonObject(reportModel, "data_model");
        if (StringUtils.isNotBlank(primitiveText(dataModel, "sql"))) {
            return true;
        }
        JsonArray columns = GsonUtility.optJsonArray(dataModel, "columns");
        return columns != null && columns.size() > 0;
    }

    static void forceTableChart(JsonObject reportModel) {
        if (reportModel == null) {
            return;
        }
        JsonObject vizModel = GsonUtility.optJsonObject(reportModel, "viz_model");
        if (vizModel == null) {
            vizModel = new JsonObject();
            reportModel.add("viz_model", vizModel);
        }
        JsonObject chart = GsonUtility.optJsonObject(vizModel, "chart");
        if (chart == null) {
            chart = new JsonObject();
            vizModel.add("chart", chart);
        }
        chart.addProperty("viz", "");
        chart.addProperty("mark", "Table");
        chart.addProperty("dualaxis", false);
        chart.add("measures", new JsonArray());
        if (GsonUtility.optJsonObject(vizModel, "data") == null) {
            JsonObject data = new JsonObject();
            data.add("rows", new JsonArray());
            data.add("columns", new JsonArray());
            vizModel.add("data", data);
        }
    }

    private static void normalizeVizTemplate(JsonObject chat) {
        JsonObject viz = GsonUtility.optJsonObject(chat, "viz");
        if (viz == null) {
            viz = new JsonObject();
            chat.add("viz", viz);
        }
        if (StringUtils.isNotBlank(primitiveText(viz, "vf_template"))) {
            chat.remove("vf");
            chat.remove("vf_string");
            viz.remove("vf");
            viz.remove("vf_string");
            return;
        }
        String legacy = firstNonBlank(
                primitiveText(chat, "vf_template"),
                primitiveText(viz, "vf"),
                primitiveText(chat, "vf"),
                primitiveText(viz, "vf_string"),
                primitiveText(chat, "vf_string"));
        if (StringUtils.isBlank(legacy)) {
            return;
        }
        viz.addProperty("vf_template", toTemplateValue(legacy));
        chat.remove("vf");
        chat.remove("vf_string");
        viz.remove("vf");
        viz.remove("vf_string");
    }

    private static String toTemplateValue(String raw) {
        String value = raw.trim();
        if (value.contains(" ") || value.contains("\n") || value.contains("function") || value.contains("{")) {
            return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }
        return value;
    }

    private static String extractSql(JsonObject chat) {
        if (chat != null && chat.has("sql") && !chat.get("sql").isJsonNull()) {
            JsonElement sql = chat.get("sql");
            if (sql.isJsonPrimitive()) {
                return sql.getAsString();
            }
            if (sql.isJsonObject()) {
                JsonObject sqlObject = sql.getAsJsonObject();
                String raw = firstNonBlank(
                        primitiveText(sqlObject, "raw_sql"),
                        primitiveText(sqlObject, "query"),
                        primitiveText(sqlObject, "sql"));
                if (StringUtils.isNotBlank(raw)) {
                    return raw;
                }
            }
        }
        JsonObject reportModel = GsonUtility.optJsonObject(chat, "report_model");
        JsonObject dataModel = GsonUtility.optJsonObject(reportModel, "data_model");
        return primitiveText(dataModel, "sql");
    }

    private static String dialect(JsonObject chat) {
        JsonElement sql = chat == null ? null : chat.get("sql");
        if (sql != null && sql.isJsonObject()) {
            return primitiveText(sql.getAsJsonObject(), "dialect");
        }
        return "";
    }

    private static JsonObject nestedChat(JsonObject node) {
        if (node == null) {
            return null;
        }
        JsonObject nested = GsonUtility.optJsonObject(node, "chat_response");
        if (nested == null) {
            nested = GsonUtility.optJsonObject(node, "fullChatResponse");
        }
        if (nested == null) {
            nested = GsonUtility.optJsonObject(node, "chatResponse");
        }
        return nested;
    }

    private static JsonArray history(JsonObject node) {
        JsonArray history = GsonUtility.optJsonArray(node, "question_history");
        if (history == null) {
            history = GsonUtility.optJsonArray(node, "questionHistory");
        }
        return history;
    }

    private static void rememberHreport(JsonObject node, Map<String, String> hreportBySeq) {
        if (node == null || hreportBySeq == null) {
            return;
        }
        String hreportId = primitiveText(node, "hreportId");
        if (StringUtils.isBlank(hreportId)) {
            return;
        }
        for (String key : new String[]{"chat_sequence_id", "chat_seq_id", "chatSeqId", "id", "itemId"}) {
            String id = primitiveText(node, key);
            if (StringUtils.isNotBlank(id)) {
                hreportBySeq.putIfAbsent(id, hreportId);
            }
        }
    }

    private static String lookupText(JsonObject item, Map<String, String> hreportBySeq) {
        for (String key : new String[]{"chat_sequence_id", "chat_seq_id", "id", "itemId"}) {
            String id = primitiveText(item, key);
            if (StringUtils.isNotBlank(id) && hreportBySeq.containsKey(id)) {
                return hreportBySeq.get(id);
            }
        }
        return "";
    }

    private static void remember(JsonObject node, JsonObject model, Map<String, JsonObject> convertedById) {
        if (node == null || model == null) {
            return;
        }
        for (String key : new String[]{"chat_sequence_id", "chat_seq_id", "chatSeqId", "id", "itemId"}) {
            String id = primitiveText(node, key);
            if (StringUtils.isNotBlank(id)) {
                convertedById.put(id, model);
            }
        }
    }

    private static JsonObject lookup(JsonObject item, Map<String, JsonObject> convertedById) {
        for (String key : new String[]{"chat_sequence_id", "chat_seq_id", "id", "itemId"}) {
            String id = primitiveText(item, key);
            if (StringUtils.isNotBlank(id) && convertedById.containsKey(id)) {
                return convertedById.get(id);
            }
        }
        return null;
    }

    private static boolean nonBlank(JsonObject obj, String key) {
        if (obj == null || !obj.has(key)) {
            return false;
        }
        JsonElement element = obj.get(key);
        if (element == null || element.isJsonNull()) {
            return false;
        }
        if (element.isJsonPrimitive()) {
            return StringUtils.isNotBlank(element.getAsString());
        }
        return true;
    }

    private static String primitiveText(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return "";
        }
        JsonElement element = obj.get(key);
        if (element.isJsonPrimitive()) {
            return element.getAsString().trim();
        }
        return "";
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }
}

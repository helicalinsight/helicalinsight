package com.helicalinsight.instant.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceAIModel;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.LegacyInstantMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Older instant files store the conversation on {@code state.chats} and
 * {@code state.previews}, and a JSON null metadata location. The report viewer
 * reads {@code inputs}, {@code chat_responses}, and a non-null metadata file
 * name. This rewrites that saved shape while the report is opened.
 * Metadata comes from {@code state.subject.agent} or {@code state.subject.model}
 * when that file is a {@code .model}. A {@code .agent} file is no longer opened.
 */
final class LegacyInstantStructureNormalizer {

    private static final Logger logger = LoggerFactory.getLogger(LegacyInstantStructureNormalizer.class);

    private LegacyInstantStructureNormalizer() {
    }

    static boolean normalize(JsonObject reportContent) {
        JsonObject data = InstantReportReaderComponent.unwrapReportData(reportContent);
        JsonObject state = GsonUtility.optJsonObject(data, "state");
        if (state == null || hasEntries(state, "chat_responses") || hasEntries(state, "inputs")) {
            return false;
        }
        JsonArray chats = GsonUtility.optJsonArray(state, "chats");
        if (chats == null || chats.size() == 0) {
            return false;
        }
        JsonArray inputs = new JsonArray();
        JsonArray responses = new JsonArray();
        int sequence = 1;
        for (JsonElement chatElement : chats) {
            if (chatElement == null || !chatElement.isJsonObject()) {
                continue;
            }
            JsonObject chat = chatElement.getAsJsonObject();
            JsonArray messages = GsonUtility.optJsonArray(chat, "messageList");
            if (messages == null || messages.size() == 0) {
                continue;
            }
            List<JsonObject> chronological = new ArrayList<>();
            for (int index = messages.size() - 1; index >= 0; index--) {
                JsonElement messageElement = messages.get(index);
                if (messageElement != null && messageElement.isJsonObject()) {
                    chronological.add(messageElement.getAsJsonObject());
                }
            }
            for (JsonObject message : chronological) {
                JsonObject preview = matchingPreview(state, chat, message);
                if (isUser(message)) {
                    JsonObject input = new JsonObject();
                    input.addProperty("chat_sequence_id", sequence);
                    input.addProperty("input", textOf(message, preview));
                    inputs.add(input);
                } else {
                    responses.add(toChatResponse(message, preview, sequence));
                }
                sequence++;
            }
        }
        if (inputs.size() == 0 && responses.size() == 0) {
            return false;
        }
        state.add("inputs", inputs);
        state.add("chat_responses", responses);
        logger.info("Rewrote a legacy instant conversation into {} input(s) and {} response(s)",
                inputs.size(), responses.size());
        return true;
    }

    /**
     * Fills a missing metadata location from {@code state.subject.agent}
     * or {@code state.subject.model} when the file is {@code .model}.
     * The model resource points at the metadata file. A {@code .agent}
     * reference cannot be opened.
     */
    static void attachMetadata(JsonObject reportContent, boolean rewritten) {
        JsonObject data = InstantReportReaderComponent.unwrapReportData(reportContent);
        if (data == null || (!data.has("state") && !data.has("metadata"))) {
            return;
        }
        JsonObject state = GsonUtility.optJsonObject(data, "state");
        JsonObject linkedFile = linkedFile(state);
        if (isAgentFile(linkedFile)) {
            throw new EfwServiceException("This file is no longer supported");
        }
        JsonObject metadata = GsonUtility.optJsonObject(data, "metadata");
        if (metadata == null) {
            metadata = new JsonObject();
        }
        String location = LegacyInstantMetadata.text(metadata, "location");
        String fileName = LegacyInstantMetadata.text(metadata, "metadataFileName");
        boolean resolved = StringUtils.isNoneBlank(location, fileName)
                && !LegacyInstantMetadata.isPlaceholder(fileName);
        if (!resolved && isModelFile(linkedFile)) {
            String[] found = metadataFromModel(linkedFile);
            location = found[0];
            fileName = found[1];
            resolved = true;
        }
        if (!resolved) {
            if (rewritten) {
                stripCharts(state);
            }
            return;
        }
        metadata.addProperty("location", location);
        metadata.addProperty("metadataFileName", fileName);
        data.add("metadata", metadata);
        stampModels(state, location, fileName);
    }

    /**
     * Copies an already resolved metadata file onto report models created
     * after the first pass, such as a VF SQL conversion.
     */
    static void stampAttachedMetadata(JsonObject reportContent) {
        JsonObject data = InstantReportReaderComponent.unwrapReportData(reportContent);
        JsonObject metadata = GsonUtility.optJsonObject(data, "metadata");
        String location = LegacyInstantMetadata.text(metadata, "location");
        String fileName = LegacyInstantMetadata.text(metadata, "metadataFileName");
        if (StringUtils.isAnyBlank(location, fileName) || LegacyInstantMetadata.isPlaceholder(fileName)) {
            return;
        }
        stampModels(GsonUtility.optJsonObject(data, "state"), location, fileName);
    }

    private static JsonObject linkedFile(JsonObject state) {
        JsonObject subject = GsonUtility.optJsonObject(state, "subject");
        if (subject == null) {
            return null;
        }
        JsonObject agent = GsonUtility.optJsonObject(subject, "agent");
        if (hasFile(agent)) {
            return agent;
        }
        JsonObject model = GsonUtility.optJsonObject(subject, "model");
        return hasFile(model) ? model : null;
    }

    private static boolean hasFile(JsonObject reference) {
        return reference != null && StringUtils.isNotBlank(LegacyInstantMetadata.text(reference, "file"));
    }

    private static boolean isAgentFile(JsonObject reference) {
        return hasFile(reference) && LegacyInstantMetadata.text(reference, "file")
                .toLowerCase(Locale.ROOT).endsWith(".agent");
    }

    private static boolean isModelFile(JsonObject reference) {
        return hasFile(reference) && LegacyInstantMetadata.text(reference, "file")
                .toLowerCase(Locale.ROOT).endsWith(".model");
    }

    /**
     * The model row stores the metadata resource id. That resource URL is
     * the metadata directory and file the report viewer requests.
     */
    private static String[] metadataFromModel(JsonObject reference) {
        String dir = LegacyInstantMetadata.text(reference, "dir");
        String file = LegacyInstantMetadata.text(reference, "file");
        String url = StringUtils.isBlank(dir) ? file : dir + "/" + file;
        HIResourceServiceDB resources = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResource modelResource = resources.getResourceByUrl(url);
        HIResourceAIModel model = modelResource == null ? null : modelResource.getAiModel();
        Integer metadataId = model == null ? null : model.getHiResourceMetadata();
        HIResource metadataResource = metadataId == null ? null : resources.getHIResourceById(metadataId);
        if (metadataResource == null || StringUtils.isBlank(metadataResource.getResourceURL())) {
            throw new EfwServiceException("The metadata does not exists for this report");
        }
        String resourceURL = metadataResource.getResourceURL();
        int separator = resourceURL.lastIndexOf('/');
        if (separator <= 0 || separator >= resourceURL.length() - 1) {
            throw new EfwServiceException("The metadata does not exists for this report");
        }
        String location = resourceURL.substring(0, separator);
        String urlName = resourceURL.substring(separator + 1);
        String fileName = urlName.toLowerCase(Locale.ROOT).endsWith(".metadata")
                ? urlName
                : StringUtils.defaultIfBlank(metadataResource.getResourcePath(), urlName);
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".metadata")) {
            fileName = fileName + "." + JsonUtils.getMetadataExtension();
        }
        return new String[]{location, fileName};
    }

    private static JsonObject toChatResponse(JsonObject message, JsonObject preview, int sequence) {
        String sql = stripSql(firstNonBlank(rawSql(message), rawSql(preview)));
        String vf = firstNonBlank(primitive(message, "vf"), primitive(preview, "vf"),
                primitive(message, "vf_string"), primitive(preview, "vf_string"));
        String template = toTemplateValue(vf);
        JsonArray columns = columnDescriptors(message, preview);
        JsonObject response = new JsonObject();
        response.addProperty("chat_sequence_id", sequence);
        JsonObject summary = new JsonObject();
        summary.addProperty("insight", textOf(message, preview));
        response.add("summary", summary);
        JsonObject sqlObject = new JsonObject();
        sqlObject.addProperty("raw_sql", sql);
        response.add("sql", sqlObject);
        JsonArray savedData = firstArray(message, preview, "data");
        if (savedData != null) {
            response.add("data", savedData.deepCopy());
        }
        if (StringUtils.isNotBlank(template) || columns.size() > 0) {
            if (StringUtils.isNotBlank(template)) {
                JsonObject viz = new JsonObject();
                viz.addProperty("vf_template", template);
                viz.addProperty("chart_name", chartName(vf));
                response.add("viz", viz);
            }
            response.addProperty("hreportId", stableId(preview, message));
            response.add("report_model", reportModel(columns, sql, template, vf));
        }
        return response;
    }

    private static JsonObject reportModel(JsonArray columns, String sql, String template, String vf) {
        JsonArray rows = new JsonArray();
        JsonArray measures = new JsonArray();
        JsonArray groupBy = new JsonArray();
        JsonArray detailed = new JsonArray();
        for (JsonElement element : columns) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject column = element.getAsJsonObject();
            String alias = primitive(column, "alias");
            if (StringUtils.isBlank(alias)) {
                continue;
            }
            detailed.add(column);
            if (column.has("measure") && column.get("measure").getAsBoolean()) {
                measures.add(alias);
            } else {
                rows.add(alias);
                JsonObject grouped = new JsonObject();
                grouped.addProperty("column", alias);
                groupBy.add(grouped);
            }
            column.remove("measure");
        }
        boolean chart = rows.size() > 0 && measures.size() > 0 && isBar(vf);
        JsonObject dataModel = new JsonObject();
        dataModel.addProperty("sql", sql);
        dataModel.add("columns", detailed);
        dataModel.add("filters", new JsonArray());
        JsonObject functions = new JsonObject();
        functions.add("groupBy", chart ? groupBy : new JsonArray());
        dataModel.add("functions", functions);

        JsonObject shelf = new JsonObject();
        shelf.add("rows", chart ? rows : new JsonArray());
        JsonArray shelfColumns = chart ? measures : aliasesOf(detailed);
        shelf.add("columns", shelfColumns);
        JsonObject chartObject = new JsonObject();
        chartObject.addProperty("viz", chart ? "Bar" : "");
        chartObject.addProperty("mark", chart ? "Chart" : "Table");
        chartObject.addProperty("dualaxis", false);
        chartObject.add("measures", new JsonArray());
        JsonObject vizModel = new JsonObject();
        vizModel.add("data", shelf);
        vizModel.add("chart", chartObject);

        JsonObject model = new JsonObject();
        model.add("data_model", dataModel);
        model.add("viz_model", vizModel);
        if (StringUtils.isNotBlank(template)) {
            JsonObject viz = new JsonObject();
            viz.addProperty("vf_template", template);
            model.add("viz", viz);
        }
        return model;
    }

    private static JsonArray columnDescriptors(JsonObject message, JsonObject preview) {
        JsonArray columns = new JsonArray();
        Set<String> seen = new LinkedHashSet<>();
        collectColumns(message == null ? null : message.get("metadata"), columns, seen);
        collectColumns(preview == null ? null : preview.get("metadata"), columns, seen);
        if (columns.size() == 0) {
            JsonArray data = firstArray(message, preview, "data");
            if (data != null) {
                for (JsonElement rowElement : data) {
                    if (rowElement == null || !rowElement.isJsonObject()) {
                        continue;
                    }
                    for (String name : rowElement.getAsJsonObject().keySet()) {
                        JsonObject described = describe(name, rowElement.getAsJsonObject().get(name), "");
                        addDescribed(described, columns, seen);
                    }
                    break;
                }
            }
        }
        return columns;
    }

    private static void collectColumns(JsonElement metadata, JsonArray columns, Set<String> seen) {
        if (metadata == null || metadata.isJsonNull()) {
            return;
        }
        if (metadata.isJsonArray()) {
            for (JsonElement element : metadata.getAsJsonArray()) {
                collectColumns(element, columns, seen);
            }
            return;
        }
        if (!metadata.isJsonObject()) {
            return;
        }
        JsonObject object = metadata.getAsJsonObject();
        if (object.has("columns") && !object.get("columns").isJsonNull()) {
            JsonElement nested = object.get("columns");
            if (nested.isJsonObject()) {
                for (String key : nested.getAsJsonObject().keySet()) {
                    addDescribed(describeElement(nested.getAsJsonObject().get(key), key), columns, seen);
                }
            } else {
                collectColumns(nested, columns, seen);
            }
            return;
        }
        if (object.has("name") || object.has("alias")) {
            addDescribed(describeElement(object, ""), columns, seen);
        }
    }

    private static JsonObject describeElement(JsonElement element, String fallbackName) {
        if (element == null || element.isJsonNull()) {
            return describe(fallbackName, null, "");
        }
        if (element.isJsonPrimitive()) {
            return describe(element.getAsString(), null, "");
        }
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject object = element.getAsJsonObject();
        String name = firstNonBlank(primitive(object, "name"), primitive(object, "alias"), fallbackName);
        return describe(name, null, primitive(object, "type"));
    }

    private static JsonObject describe(String name, JsonElement sample, String type) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        String alias = name.trim();
        boolean measure = isMeasure(type, sample);
        JsonObject column = new JsonObject();
        column.addProperty("name", alias);
        column.addProperty("id", alias);
        JsonObject described = new JsonObject();
        described.addProperty("alias", alias);
        described.add("column", column);
        described.addProperty("measure", measure);
        if (measure) {
            JsonObject databaseFunction = new JsonObject();
            databaseFunction.addProperty("functionName", "SUM");
            databaseFunction.addProperty("key", "SUM");
            described.add("databaseFunction", databaseFunction);
        }
        return described;
    }

    private static void addDescribed(JsonObject described, JsonArray columns, Set<String> seen) {
        if (described == null) {
            return;
        }
        String alias = primitive(described, "alias");
        if (StringUtils.isBlank(alias) || !seen.add(alias.toLowerCase(Locale.ROOT))) {
            return;
        }
        columns.add(described);
    }

    private static void stampModels(JsonObject state, String location, String fileName) {
        JsonArray responses = GsonUtility.optJsonArray(state, "chat_responses");
        if (responses == null) {
            return;
        }
        for (JsonElement element : responses) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject dataModel = GsonUtility.optJsonObject(
                    GsonUtility.optJsonObject(element.getAsJsonObject(), "report_model"), "data_model");
            if (dataModel == null) {
                continue;
            }
            dataModel.addProperty("location", location);
            dataModel.addProperty("metadataFileName", fileName);
        }
    }

    private static void stripCharts(JsonObject state) {
        JsonArray responses = GsonUtility.optJsonArray(state, "chat_responses");
        if (responses == null) {
            return;
        }
        for (JsonElement element : responses) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject response = element.getAsJsonObject();
            response.remove("hreportId");
            response.remove("report_model");
            response.remove("viz");
        }
    }

    private static JsonObject matchingPreview(JsonObject state, JsonObject chat, JsonObject message) {
        JsonArray previews = GsonUtility.optJsonArray(state, "previews");
        if (previews == null) {
            return null;
        }
        String chatId = primitive(chat, "chatID");
        String messageId = primitive(message, "id");
        JsonObject fallback = null;
        int botCount = 0;
        JsonArray messages = GsonUtility.optJsonArray(chat, "messageList");
        if (messages != null) {
            for (JsonElement element : messages) {
                if (element != null && element.isJsonObject() && !isUser(element.getAsJsonObject())) {
                    botCount++;
                }
            }
        }
        for (JsonElement element : previews) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject preview = element.getAsJsonObject();
            if (StringUtils.isNotBlank(messageId) && messageId.equals(primitive(preview, "id"))) {
                return preview;
            }
            if (StringUtils.isNotBlank(chatId) && chatId.equals(primitive(preview, "chatID"))) {
                fallback = preview;
            }
        }
        if (fallback != null) {
            return fallback;
        }
        if (botCount == 1 && previews.size() == 1 && previews.get(0).isJsonObject()) {
            return previews.get(0).getAsJsonObject();
        }
        return null;
    }

    private static String stableId(JsonObject preview, JsonObject message) {
        String id = firstNonBlank(primitive(preview, "id"), primitive(message, "id"));
        if (id.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            return id;
        }
        return UUID.randomUUID().toString();
    }

    private static boolean isUser(JsonObject message) {
        return bool(message, "isUser") || bool(message, "user");
    }

    private static boolean bool(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return false;
        }
        JsonElement element = object.get(key);
        return element.isJsonPrimitive()
                && element.getAsJsonPrimitive().isBoolean()
                && element.getAsBoolean();
    }

    private static String textOf(JsonObject message, JsonObject preview) {
        return firstNonBlank(primitive(message, "text"), primitive(message, "input"),
                primitive(preview, "text"), primitive(preview, "summary"));
    }

    private static String rawSql(JsonObject object) {
        if (object == null || !object.has("sql") || object.get("sql").isJsonNull()) {
            return "";
        }
        JsonElement sql = object.get("sql");
        if (sql.isJsonPrimitive()) {
            return sql.getAsString();
        }
        if (sql.isJsonObject()) {
            JsonObject sqlObject = sql.getAsJsonObject();
            return firstNonBlank(primitive(sqlObject, "raw_sql"), primitive(sqlObject, "query"),
                    primitive(sqlObject, "sql"));
        }
        return "";
    }

    private static String stripSql(String sql) {
        String value = sql == null ? "" : sql.trim();
        if (value.startsWith("```")) {
            int newline = value.indexOf('\n');
            value = newline >= 0 ? value.substring(newline + 1) : value.substring(3);
            if (value.endsWith("```")) {
                value = value.substring(0, value.length() - 3);
            }
            value = value.trim();
        }
        return value;
    }

    private static String toTemplateValue(String raw) {
        if (StringUtils.isBlank(raw)) {
            return "";
        }
        String value = raw.trim();
        if (value.contains(" ") || value.contains("\n") || value.contains("function") || value.contains("{")) {
            return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }
        return value;
    }

    private static boolean isBar(String vf) {
        String value = vf == null ? "" : vf.toLowerCase(Locale.ROOT);
        return value.contains("drawbar") || value.contains("xfield") || value.contains("bar");
    }

    private static String chartName(String vf) {
        return isBar(vf) ? "bar" : "table";
    }

    private static boolean isMeasure(String type, JsonElement sample) {
        String value = type == null ? "" : type.toLowerCase(Locale.ROOT);
        if (value.contains("num") || value.contains("int") || value.contains("dec")
                || value.contains("float") || value.contains("double") || value.contains("money")
                || value.contains("measure")) {
            return true;
        }
        return sample != null && sample.isJsonPrimitive() && sample.getAsJsonPrimitive().isNumber();
    }

    private static JsonArray aliasesOf(JsonArray detailed) {
        JsonArray aliases = new JsonArray();
        for (JsonElement element : detailed) {
            if (element != null && element.isJsonObject()) {
                String alias = primitive(element.getAsJsonObject(), "alias");
                if (StringUtils.isNotBlank(alias)) {
                    aliases.add(alias);
                }
            }
        }
        return aliases;
    }

    private static JsonArray firstArray(JsonObject first, JsonObject second, String key) {
        JsonArray found = GsonUtility.optJsonArray(first, key);
        if (found != null && found.size() > 0) {
            return found;
        }
        found = GsonUtility.optJsonArray(second, key);
        return found != null && found.size() > 0 ? found : null;
    }

    private static boolean hasEntries(JsonObject state, String key) {
        JsonArray array = GsonUtility.optJsonArray(state, key);
        return array != null && array.size() > 0;
    }

    private static String primitive(JsonObject object, String key) {
        return LegacyInstantMetadata.text(object, key);
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
}

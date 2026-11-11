package com.helicalinsight.efw.parameters;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Writes resolved dates onto a canned-report form.
 *
 * A schedule calls {@link #carryScheduleContext} before the form is sent, so the timezone and anchor
 * dates travel with it. Report generation then calls {@link #resolve}. The function list and the date
 * math are not here: see {@link RelativeFunctionCatalog} and {@link ParameterEvaluationService}.
 */
public final class HcrParameterEvaluator {

    public static final String TIME_ZONE = "parameterTimeZone";
    public static final String ANCHORS = "parameterAnchors";
    public static final String ANCHOR_PREFIX = "__anchor__";

    private static final Logger logger = LoggerFactory.getLogger(HcrParameterEvaluator.class);
    private static final String JASPER_DATE_TIME = "dd-MM-yyyy HH:mm:ss";

    private HcrParameterEvaluator() {
    }

    public static void carryScheduleContext(JsonObject formData, JsonObject reportParameters, String timeZone) {
        if (formData == null) {
            return;
        }
        if (timeZone != null && !timeZone.isBlank()) {
            formData.addProperty(TIME_ZONE, timeZone);
        }
        JsonObject anchors = anchorValues(reportParameters);
        if (anchors.size() > 0) {
            formData.add(ANCHORS, anchors);
        }
    }

    public static void resolve(JsonObject formData) {
        resolve(formData, ParameterEvaluationService.shared(), null);
    }

    static void resolve(JsonObject formData, ParameterEvaluationService service, Clock clock) {
        JsonArray parameters = parameterArray(formData);
        if (parameters == null || service == null) {
            return;
        }
        ZoneId zone = zone(formData);
        Clock evaluationClock = clock == null ? Clock.system(zone) : clock.withZone(zone);
        JsonObject connectionDetails = GsonUtility.optJsonObject(formData, "connectionDetails");
        JsonObject anchors = GsonUtility.optJsonObject(formData, ANCHORS);
        for (JsonElement element : parameters) {
            if (element != null && element.isJsonObject()) {
                resolveParameter(service, element.getAsJsonObject(), connectionDetails, anchors, zone, evaluationClock);
            }
        }
        formData.remove(ANCHORS);
    }

    private static JsonObject anchorValues(JsonObject reportParameters) {
        JsonObject anchors = new JsonObject();
        if (reportParameters == null) {
            return anchors;
        }
        for (Map.Entry<String, JsonElement> entry : reportParameters.entrySet()) {
            if (entry.getKey().startsWith(ANCHOR_PREFIX)) {
                anchors.add(entry.getKey(), entry.getValue());
            }
        }
        return anchors;
    }

    private static JsonArray parameterArray(JsonObject formData) {
        if (formData == null) {
            return null;
        }
        JsonObject designer = GsonUtility.optJsonObject(formData, "designerProperties");
        if (designer == null || !designer.has("parameters") || !designer.get("parameters").isJsonArray()) {
            return null;
        }
        return designer.getAsJsonArray("parameters");
    }

    private static void resolveParameter(ParameterEvaluationService service, JsonObject parameter,
            JsonObject connectionDetails, JsonObject anchors, ZoneId zone, Clock clock) {
        if (!parameter.has("name") || parameter.get("name").isJsonNull()) {
            return;
        }
        String name = parameter.get("name").getAsString();
        String pattern = outputPattern(parameter);
        String anchor = anchorDate(parameter, anchors, name);
        resolveField(service, parameter, "defaultExpression", pattern, zone, clock, anchor);
        resolveField(service, parameter, "value", pattern, zone, clock, anchor);
        if (parameter.has(name)) {
            resolveField(service, parameter, name, pattern, zone, clock, anchor);
        }
        if (connectionDetails != null && connectionDetails.has(name)) {
            resolveField(service, connectionDetails, name, pattern, zone, clock, anchor);
        }
    }

    private static void resolveField(ParameterEvaluationService service, JsonObject object, String key, String pattern,
            ZoneId zone, Clock clock, String anchor) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return;
        }
        object.add(key, resolveValue(service, object.get(key), pattern, zone, clock, anchor));
    }

    private static JsonElement resolveValue(ParameterEvaluationService service, JsonElement element, String pattern,
            ZoneId zone, Clock clock, String anchor) {
        if (element.isJsonArray()) {
            return resolveArray(service, element.getAsJsonArray(), pattern, zone, clock, anchor);
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            EvaluationRequest request = EvaluationRequest.single(zone, clock, pattern).withAnchor(anchor);
            return new JsonPrimitive(service.evaluate(element.getAsString(), request));
        }
        return element;
    }

    private static JsonArray resolveArray(ParameterEvaluationService service, JsonArray source, String pattern,
            ZoneId zone, Clock clock, String anchor) {
        JsonArray resolved = new JsonArray();
        for (int index = 0; index < source.size(); index++) {
            JsonElement item = source.get(index);
            if (item == null || !item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                resolved.add(item);
                continue;
            }
            EvaluationRequest request = requestFor(source.size(), index, zone, clock, pattern).withAnchor(anchor);
            resolved.add(service.evaluate(item.getAsString(), request));
        }
        return resolved;
    }

    private static EvaluationRequest requestFor(int size, int index, ZoneId zone, Clock clock, String pattern) {
        if (size == 1) {
            return EvaluationRequest.single(zone, clock, pattern);
        }
        if (index == 0) {
            return EvaluationRequest.rangeStart(zone, clock, pattern);
        }
        return EvaluationRequest.rangeEnd(zone, clock, pattern);
    }

    private static String outputPattern(JsonObject parameter) {
        String className = GsonUtility.optString(parameter, "className");
        if ("java.util.Date".equals(className) || "java.sql.Timestamp".equals(className)) {
            return JASPER_DATE_TIME;
        }
        if ("java.sql.Time".equals(className)) {
            return "HH:mm:ss";
        }
        String valueFormat = GsonUtility.optString(parameter, "valueFormat");
        if (valueFormat != null && !valueFormat.isBlank()) {
            return momentPatternToJava(valueFormat);
        }
        return "yyyy-MM-dd";
    }

    static String momentPatternToJava(String momentPattern) {
        return momentPattern.replace("YYYY", "yyyy").replace("DD", "dd").replace("A", "a");
    }

    private static String anchorDate(JsonObject parameter, JsonObject anchors, String name) {
        String onParameter = GsonUtility.optString(parameter, "anchorDate");
        if (onParameter != null && !onParameter.isBlank()) {
            return onParameter;
        }
        String anchorKey = ANCHOR_PREFIX + name;
        if (anchors != null && anchors.has(anchorKey) && anchors.get(anchorKey).isJsonPrimitive()) {
            return anchors.get(anchorKey).getAsString();
        }
        return null;
    }

    private static ZoneId zone(JsonObject formData) {
        String timeZone = GsonUtility.optString(formData, TIME_ZONE);
        if (timeZone == null || timeZone.isBlank()) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(timeZone);
        } catch (Exception ex) {
            logger.warn("Unknown parameter timezone {}. Using the JVM default zone.", timeZone);
            return ZoneId.systemDefault();
        }
    }
}

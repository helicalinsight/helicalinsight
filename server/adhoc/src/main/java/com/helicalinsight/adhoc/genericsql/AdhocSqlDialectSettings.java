package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Dialect helpers for adhoc SQL.
 * <ul>
 *   <li>{@code ansiGroupByDialects} from {@code adhocSqlSettings.xml} — strict ANSI GROUP BY rules</li>
 *   <li>{@code adhocRollupSettings.json} — rollup style by SqlFunctions reference
 *       (same keys as {@code LimitOffsetAppender} / {@code sqlFunctionsXmlMapping.properties},
 *       e.g. {@code mysql}, {@code oracle})</li>
 * </ul>
 */
final class AdhocSqlDialectSettings {

    private static final String SYNTAX_WITH = "with";

    private AdhocSqlDialectSettings() {
    }

    /**
     * True when the dialect is listed under {@code ansiGroupByDialects}
     * (same intent as the original GroupByClause lookup).
     */
    static boolean isAnsiGroupByDialect(String dialect) {
        return findAnsiDialectEntry(dialect) != null;
    }

    /**
     * True when rollup is allowed for this SqlFunctions reference.
     * Explicit {@code "rollup": false} in {@code adhocRollupSettings.json} disables rollup
     * even if the UI sends {@code subTotals: true}.
     * Missing reference / missing {@code rollup} key defaults to enabled ({@code true}).
     */
    static boolean isRollupEnabled(String reference) {
        JsonObject entry = getRollupEntry(reference);
        if (entry == null) {
            return true;
        }
        return GsonUtility.optBooleanValue(entry, "rollup", true);
    }

    /**
     * True when rollup should use ANSI {@code GROUP BY ROLLUP(...)}.
     * Looks up {@code reference} (e.g. mysql, oracle) in {@code adhocRollupSettings.json}.
     * Defaults to ANSI when the reference is missing or {@code syntax} is not {@code with}.
     */
    static boolean usesAnsiRollupSyntax(String reference) {
        JsonObject entry = getRollupEntry(reference);
        if (entry == null) {
            return true;
        }
        String syntax = firstNonEmpty(entry, "syntax");
        return !SYNTAX_WITH.equalsIgnoreCase(syntax);
    }

    private static JsonObject getRollupEntry(String reference) {
        if (reference == null || reference.isEmpty()) {
            return null;
        }
        JsonObject settings = JsonUtils.getAdhocRollupSettings();
        if (settings == null || !settings.has(reference) || !settings.get(reference).isJsonObject()) {
            return null;
        }
        return settings.getAsJsonObject(reference);
    }

    private static JsonElement findAnsiDialectEntry(String dialect) {
        if (dialect == null) {
            return null;
        }
        JsonArray dialects = getAnsiGroupByDialects();
        if (dialects == null) {
            return null;
        }
        for (JsonElement element : dialects) {
            if (dialectEquals(element, dialect)) {
                return element;
            }
        }
        return null;
    }

    private static JsonArray getAnsiGroupByDialects() {
        JsonObject adhocSqlSettings = JsonUtils.newGetAdhocSqlSettings();
        if (adhocSqlSettings == null || !adhocSqlSettings.has("ansiGroupByDialects")) {
            return null;
        }
        JsonElement dialectsElement = adhocSqlSettings.getAsJsonObject("ansiGroupByDialects").get("dialect");
        if (dialectsElement == null || dialectsElement.isJsonNull()) {
            return null;
        }
        if (dialectsElement.isJsonArray()) {
            return dialectsElement.getAsJsonArray();
        }
        JsonArray single = new JsonArray();
        single.add(dialectsElement);
        return single;
    }

    private static boolean dialectEquals(JsonElement element, String dialect) {
        if (element == null) {
            return false;
        }
        if (element.isJsonPrimitive()) {
            return dialect.equals(element.getAsString());
        }
        if (element.isJsonObject()) {
            return dialect.equals(dialectName(element.getAsJsonObject()));
        }
        return false;
    }

    private static String dialectName(JsonObject dialectObject) {
        String name = firstNonEmpty(dialectObject, "", "#text");
        if (name != null) {
            return name;
        }
        for (java.util.Map.Entry<String, JsonElement> entry : dialectObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value != null && value.isJsonPrimitive()) {
                String candidate = value.getAsString();
                if (candidate != null && !candidate.isEmpty()) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static String firstNonEmpty(JsonObject dialectObject, String... keys) {
        for (String key : keys) {
            String value = GsonUtility.optString(dialectObject, key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }
}

package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Reads {@code ansiGroupByDialects} from adhocSqlSettings.xml.
 * Handles both plain dialect strings and dialect elements with attributes
 * (Jackson XmlMapper uses {@code rollup} / {@code ""}; legacy XMLSerializer uses {@code @rollup} / {@code #text}).
 */
final class AdhocSqlDialectSettings {

    private AdhocSqlDialectSettings() {
    }

    /**
     * True when the dialect is listed under {@code ansiGroupByDialects}
     * (same intent as the original GroupByClause lookup).
     */
    static boolean isAnsiGroupByDialect(String dialect) {
        return findDialectEntry(dialect) != null;
    }

    /**
     * True when the matching dialect entry has {@code rollup="true"}
     * (ANSI {@code GROUP BY ROLLUP(...)}). Otherwise use non-ANSI {@code WITH ROLLUP}.
     */
    static boolean supportsAnsiRollup(String dialect) {
        JsonElement entry = findDialectEntry(dialect);
        if (entry == null || !entry.isJsonObject()) {
            return false;
        }
        JsonObject dialectObject = entry.getAsJsonObject();
        return GsonUtility.optBooleanValue(dialectObject, "rollup", false)
                || GsonUtility.optBooleanValue(dialectObject, "@rollup", false);
    }

    private static JsonElement findDialectEntry(String dialect) {
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
            String key = entry.getKey();
            if ("rollup".equals(key) || "@rollup".equals(key)) {
                continue;
            }
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

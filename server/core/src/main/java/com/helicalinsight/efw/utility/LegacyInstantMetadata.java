package com.helicalinsight.efw.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Name used when an older instant report has no metadata file.
 * metadata/get and getFunctions recognize it and return an empty success
 * instead of calling {@code getAsString} on a JSON null.
 */
public final class LegacyInstantMetadata {

    public static final String FILE_NAME = "legacy-instant.metadata";

    private LegacyInstantMetadata() {
    }

    public static boolean isPlaceholder(String fileName) {
        return FILE_NAME.equals(fileName == null ? "" : fileName.trim());
    }

    public static String text(JsonObject object, String key) {
        if (object == null || key == null || !object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }
        JsonElement element = object.get(key);
        if (!element.isJsonPrimitive()) {
            return "";
        }
        String value = element.getAsString();
        return value == null ? "" : value.trim();
    }

    /**
     * True when both metadata keys are present but cannot name a file.
     * A missing key is left for the caller to validate, and a missing pair
     * stays on the type/id metadata path. A JSON null, a blank value, or
     * {@link #FILE_NAME} is unbound.
     */
    public static boolean isUnbound(JsonObject formJson) {
        if (formJson == null) {
            return false;
        }
        if (!formJson.has("location") || !formJson.has("metadataFileName")) {
            return false;
        }
        String fileName = text(formJson, "metadataFileName");
        if (isPlaceholder(fileName)) {
            return true;
        }
        return text(formJson, "location").isEmpty() || fileName.isEmpty();
    }
}

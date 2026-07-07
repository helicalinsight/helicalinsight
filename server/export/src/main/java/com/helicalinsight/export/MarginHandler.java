package com.helicalinsight.export;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MarginHandler {
    private static final double DEFAULT_MARGIN = 0.39; // 10mm in inches

    /**
     * Applies margin settings for Chrome printToPDF parameters.
     *
     * @param bodyElement JsonObject containing user print config
     * @param params      Map<String, Object> for printToPDF parameters
     */
    public void applyMargins(JsonObject bodyElement, Map<String, Object> params) {

        // If "margin" not present → use default 10mm
        if (!bodyElement.has("margin")) {
            setDefaultMargins(params);
            return;
        }

        String marginType = bodyElement.get("margin").getAsString().trim().toLowerCase();

        switch (marginType) {

            case "none":
            case "zero":
                // Zero margins → valid for PDF
                setZeroMargins(params);
                return;

            case "default":
                // Chrome-like default
                setDefaultMargins(params);
                return;

            case "custom":
                // Custom margins inside "margins"
                applyCustomMargins(bodyElement, params);
                return;

            default:
                // Unknown margin value → fallback to default
                setDefaultMargins(params);
        }
    }

    private void setZeroMargins(Map<String, Object> params) {
        params.put("marginTop", 0);
        params.put("marginBottom", 0);
        params.put("marginLeft", 0);
        params.put("marginRight", 0);
    }
    private void setDefaultMargins(Map<String, Object> params) {
        params.put("marginTop", DEFAULT_MARGIN);
        params.put("marginBottom", DEFAULT_MARGIN);
        params.put("marginLeft", DEFAULT_MARGIN);
        params.put("marginRight", DEFAULT_MARGIN);
    }
    private void applyCustomMargins(JsonObject bodyElement, Map<String, Object> params) {

        if (bodyElement.has("margins")) {
            JsonObject margins = bodyElement.getAsJsonObject("margins");

            params.put("marginTop",     margins.has("top")    ? margins.get("top").getAsDouble()    : DEFAULT_MARGIN);
            params.put("marginBottom",  margins.has("bottom") ? margins.get("bottom").getAsDouble() : DEFAULT_MARGIN);
            params.put("marginLeft",    margins.has("left")   ? margins.get("left").getAsDouble()   : DEFAULT_MARGIN);
            params.put("marginRight",   margins.has("right")  ? margins.get("right").getAsDouble()  : DEFAULT_MARGIN);
        }
        else {
            // If "margins" missing → fallback to default
            setDefaultMargins(params);
        }
    }


}

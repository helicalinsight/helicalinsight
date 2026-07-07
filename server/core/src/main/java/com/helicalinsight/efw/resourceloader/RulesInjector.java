package com.helicalinsight.efw.resourceloader;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 15-05-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("rawtypes")
public final class RulesInjector {

    @NotNull
    private final Map<String, String> rules;
    private final List listOfKeys;
    private final JsonObject extensions;
    private static final String RULES_PACKAGE = "com.helicalinsight.efw.resourceloader.rules.";

    public RulesInjector(List listOfKeys, JsonObject extensions) {
        this.listOfKeys = listOfKeys;
        this.extensions = extensions;
        Map<String, String> properties = ConfigurationFileReader.getProjectPropertiesFile();
        this.rules = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().contains(RULES_PACKAGE)) {
                rules.put(entry.getKey().replace(RULES_PACKAGE, ""), entry.getValue());
            }
        }
    }

    public void injectRules() {
        if (this.listOfKeys != null) {
            for (Map.Entry<String, String> entry : this.rules.entrySet()) {
                String key = entry.getKey();
                if (containsCaseInsensitive(key, this.listOfKeys)) {
                    String text = this.extensions.get(key).getAsString();
                    this.extensions.remove(key);
                    JsonObject rule = new JsonObject();
                    rule.addProperty("visible", "true");
                    if ("image".equalsIgnoreCase(key)) {
                        rule.addProperty("", JsonUtils.supportedImageExtensions.toString());
                    } else {
                        rule.addProperty("rule", entry.getValue());
                        rule.addProperty("", text);
                    }
                    this.extensions.add(key, rule);
                }
            }
        }
    }

    private boolean containsCaseInsensitive(String strToCompare, @NotNull List list) {
        for (Object object : list) {
            String str = (String) object;
            if (str.equalsIgnoreCase(strToCompare)) {
                return true;
            }
        }
        return false;
    }
}

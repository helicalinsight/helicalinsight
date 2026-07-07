package com.helicalinsight.efw.resourceloader.rules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by author on 12-01-2015.
 *
 * @author Rajasekhar
 */
public class ConfigurableRulesHelper {

    @Nullable
    public static List<String> getConfigurableRulesList(@NotNull JsonObject settings) {
        List<String> rules = new ArrayList<>();
        if (settings.has("security")) {
            JsonObject jsonObject = settings.getAsJsonObject("security");
            if (jsonObject.has("rules")) {
                JsonObject rulesJson = jsonObject.getAsJsonObject("rules");
                return getRules(rules, rulesJson);
            }
        }
        return null;
    }

    @NotNull
    private static List<String> getRules(@NotNull List<String> rules, @NotNull JsonObject rulesJson) {
        try {
            rules.add(0, rulesJson.getAsJsonObject("rule").get("class").getAsString());
        } catch (JsonSyntaxException ex) {
            try {
                JsonArray rulesArray = rulesJson.getAsJsonArray("rule");
                Iterator<?> iterator = rulesArray.iterator();
                //noinspection WhileLoopReplaceableByForEach
                while (iterator.hasNext()) {
                    rules.add(((JsonObject) iterator.next()).get("class").getAsString());
                }
            } catch (Exception e) {
                throw new XmlConfigurationException("The configuration of setting.xml is " +
                        "incorrect. Expected class attribute(s) for the rules node child node(s) " +
                        "of security tag.", e);
            }
        }
        return rules;
    }

    public static String getConfigurableRulesMode(@NotNull JsonObject settings) {
        String mode;
        try {
            mode = settings.getAsJsonObject("security").getAsJsonObject("rules").get("mode").getAsString();
        } catch (Exception e) {
            mode = "and";
        }
        return mode;
    }
}

package com.helicalinsight.efw.resourceloader.rules;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by author on 20-02-2026.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public final class AiAgentRule extends AbstractResourceRule implements IResourceRule {

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IResourceRule getInstance() {
        return AiAgentRuleHanlder.INSTANCE;
    }

    @Override
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        return validationResult(fileAsJson);
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(@NotNull JsonObject fileAsJson, String extensionKey, String path,
                                              String name, String lastModified) {
        return include(fileAsJson, extensionKey, path, name, lastModified);
    }

    @Override
    protected void addContentOfJson(@NotNull Map<String, String> foldersMap, @NotNull JsonObject fileAsJson,
                                    @NotNull Iterator<?> keys) {
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (("security".equals(key)) || "share".equals(key)) {
                continue;
            }
            if (key.equals("reportName")) {
                foldersMap.put("title", fileAsJson.get("reportName").getAsString());
                continue;
            }
            if ((fileAsJson.has("metadata") && key.equals("metadata")) || (fileAsJson.has("state") && key.equals("state"))) {
                continue;
            }
            foldersMap.put(key.toLowerCase(), fileAsJson.get(key).getAsString());
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class AiAgentRuleHanlder {
        public static final IResourceRule INSTANCE = new AiAgentRule();
    }
}
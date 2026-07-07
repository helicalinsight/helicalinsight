package com.helicalinsight.efw.resourceloader.rules;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * The interface is designed to prepare the json object being sent to the
 * browser, which is being displayed as the file tree, based on various
 * conditions.
 *
 * Implementations are used by directory loader.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public interface IResourceRule extends IRule {
    /**
     * True if satisfying the conditions. Otherwise false.
     *
     * @param fileAsJson The file under concern
     * @return true if satisfying the conditions
     */
    boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException;

    /**
     * Returns a map of the validated file content
     *
     * @param fileAsJson   The file under concern
     * @param extensionKey The extension of the file type. The tag key and not the value
     * @return <code>Map</code> of the file
     */
    Map<String, String> getResourceMap(JsonObject fileAsJson, String extensionKey, String path, String name,
                                       String lastModified);
}
package com.helicalinsight.resourcedb.processor.iresource;

import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.IRule;
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
public interface IResourceRuleDB extends IRule {

    boolean validateMap(Map<String,Object> resourceMap) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException;


    Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path, String name,
                                       String lastModified);
}
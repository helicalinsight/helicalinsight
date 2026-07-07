package com.helicalinsight.efw.resourceloader.rules;

import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Created by author on 03-12-2014.
 * <p/>
 * Implementations validate a file based on various conditions or rules.
 *
 * @author Rajasekhar
 */
@Deprecated
public interface IResourceSecurityRule extends IRule {

    /**
     * Validates a file based on rules
     *
     * @param fileAsJson The file under concern
     * @return <code>true</code> if the file is validated
     */
    boolean validate(JsonObject fileAsJson);

    default boolean validateMap(Map<String, Object> resourceMap) {
        return false;
    }

}

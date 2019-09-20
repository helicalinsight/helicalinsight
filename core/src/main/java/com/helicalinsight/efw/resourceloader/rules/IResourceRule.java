/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.resourceloader.rules;

import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * The interface is designed to prepare the json object being sent to the
 * browser, which is being displayed as the file tree, based on various
 * conditions.
 *
 * @author Rajasekhar
 */
public interface IResourceRule extends IRule {
    /**
     * True if satisfying the conditions. Otherwise false.
     *
     * @param fileAsJson The file under concern
     * @return true if satisfying the conditions
     */
    boolean validateFile(JSONObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException;

    /**
     * Returns a map of the validated file content
     *
     * @param fileAsJson   The file under concern
     * @param extensionKey The extension of the file type. The tag key and not the value
     * @return <code>Map</code> of the file
     */
    Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path, String name,
                                       String lastModified);
}
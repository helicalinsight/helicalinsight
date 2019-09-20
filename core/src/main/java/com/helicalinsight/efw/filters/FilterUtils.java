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

package com.helicalinsight.efw.filters;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Somen
  */
@Component
public class FilterUtils {

    public static String getValidationConfigurationFilesDirectory() {
        return ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "Validation";
    }

    public static JSONArray getExcludeUlrPattern() {
        String validationXmlPath = getValidationFilePath();
        File validationFile = new File(validationXmlPath);
        JSONObject validationXmlJson;
        if (validationFile.exists()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            validationXmlJson = processor.getJSONObject(validationXmlPath, false);
            return validationXmlJson.optJSONArray("excludePatterns");
        }
        return null;
    }

    public static String getValidationFilePath() {
        return ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "validation.xml";
    }

    public static boolean isExcludePattern(JSONArray excludeArray, String requestedUrl) {
        if (excludeArray != null) {
            for (int index = 0; index < excludeArray.size(); index++) {
                String string = excludeArray.getString(index);
                if (requestedUrl.matches(string)) {
                    return true;
                }
            }
        }
        return false;
    }
}

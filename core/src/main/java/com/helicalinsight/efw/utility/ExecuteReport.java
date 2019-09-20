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

package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A template file referenced by the EFW file may have parameters of varying
 * value. They will be replaced by the corresponding values from the JSON
 * parametersJSON.
 *
 * @author Rajasekhar
 */
public class ExecuteReport {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteReport.class);

    /**
     * <p>
     * The variable components in the template file will be replaced from the
     * parametersJSON appropriately.
     * </p>
     *
     * @param dirPath        directory of the EFW file
     * @param efwFile        The name of the EFW file
     * @param parametersJSON The json of the parameters
     * @return The list of html file content in the first index and dirPath in
     * the second index
     */
    public List<String> execute(String dirPath, String efwFile, JSONObject parametersJSON) {

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        String solutionDirectory = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String templateData = null;
        String template;
        JSONObject efwFileJsonObject;

        efwFileJsonObject = processor.getJSONObject(solutionDirectory + File.separator + dirPath +
                File.separator + efwFile, false);
        logger.debug("jsonObject = " + efwFileJsonObject);
        template = efwFileJsonObject.getString("template");

        if (template == null || template.isEmpty() || template.length() == 0) {
            logger.error("EFW file has no template element. HTML file not found.");
        } else {
            String templateFile;
            if (template.contains("solution:")) {
                templateFile = template.replaceFirst("solution:", solutionDirectory + File.separator);
            } else {
                templateFile = solutionDirectory + File.separator + dirPath + File.separator +
                        template;
            }

            TemplateReader templateReader = new TemplateReader(new File(templateFile));
            templateData = templateReader.readTemplate();
        }
        templateData = replaceParameters(templateData, parametersJSON);
        List<String> list = new ArrayList<>();
        list.add(0, templateData);
        list.add(1, dirPath);
        return list;
    }

    /**
     * <p>
     * The parameters in the templateData will be replaced with the
     * corresponding values from the parametersJSON
     * </p>
     *
     * @param templateData   The content of the template file as string
     * @param parametersJSON The json of the parameters
     * @return Updated template html file as string
     */
    String replaceParameters(String templateData, JSONObject parametersJSON) {
        Iterator<?> keys = parametersJSON.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (templateData.contains("${" + key + "}")) {
                try {
                    JSONArray array = parametersJSON.getJSONArray(key);
                    String value = array.toString();
                    logger.debug("value = " + value);
                    value = value.replace("[", "").replace("]", "").replace("\"", "'");
                    logger.debug("The value for key " + key + "is " + value);
                    templateData = templateData.replace("${" + key + "}", value);
                } catch (JSONException ex) {
                    logger.debug("key " + key + "is not an array" + ". Key value = " +
                            parametersJSON.getString(key));
                    templateData = templateData.replace("${" + key + "}", parametersJSON.getString(key));
                }
            }
        }
        return templateData;
    }
}

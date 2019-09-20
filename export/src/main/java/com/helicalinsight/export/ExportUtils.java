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

package com.helicalinsight.export;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created  on 12/7/2016.
 *
 * @author Somen
 */
public class ExportUtils {
    public static final String JSON_EXTENSION = ".json";
    public static final String GROOVY_EXTENSION = ".groovy";

    public static String getTemplatesDirectory() {
        return getReportDirectory() + File.separator + "ExportTemplates";
    }

    public static String getTemplatesTempDirectory() {
        String tempDirectory = getTemplatesDirectory() + File.separator + "Temp";
        File tempFolder = new File(tempDirectory);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        return tempDirectory;
    }

    public static String getReportDirectory() {
        return ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Reports";
    }

    public static String getFileAsString(String settingFilePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(settingFilePath)), ControllerUtils.defaultCharSet());
        } catch (IOException ioe) {
            throw new RuntimeException("There was a problem in the operation" + " " + ioe.getMessage());
        }
    }

    public static String setPrintOptionsAndDiscardFromReportParameters(String reportParameters,
                                                                       JSONObject printOptionsJson) {
        if (reportParameters != null && !reportParameters.isEmpty()) {
            JSONObject reportParameterAsJson = JSONObject.fromObject(reportParameters);

            if (reportParameterAsJson.has("printOptions")) {
                printOptionsJson.putAll(reportParameterAsJson.getJSONObject("printOptions"));
                reportParameterAsJson.discard("printOptions");
                reportParameters = reportParameterAsJson.toString();
            }
        }
        return reportParameters;
    }
}

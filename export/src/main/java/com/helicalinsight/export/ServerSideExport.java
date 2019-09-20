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
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by user on 3/3/2016.
 *
 * @author Rajasekhar
 */
public class ServerSideExport {
    private static final Logger logger = LoggerFactory.getLogger(ServerSideExport.class);

    private String format;
    private String reportName;
    private String reportParameters;
    private String reportType;
    private String dir;
    private String reportFileName;
    private JSONObject settings;

    public ServerSideExport(String format, String reportName, String reportParameters, String reportType, String dir,
                            String reportFileName, JSONObject settings) {
        this.format = format;
        this.reportName = reportName;
        this.reportParameters = reportParameters;
        this.reportType = reportType;
        this.dir = dir;
        this.reportFileName = reportFileName;
        this.settings = settings;
    }

    public List<String> listOfLocations() {
        ReportsProcessor reportsProcessor = new ReportsProcessor();
        List<String> locationsList;
        String parameters = "";
        if (this.reportParameters != null && !this.reportParameters.isEmpty()) {
            JSONObject parametersJson = JSONObject.fromObject(this.reportParameters);
            parameters = ControllerUtils.concatenateParameters(parametersJson);
            if (parameters.length() > 0) {
                parameters = parameters.substring(0, parameters.length() - 1);
            }
        }

        String baseUrl = ApplicationProperties.getInstance().getDomain();
        String url = baseUrl + "?" + "file=" + this.reportFileName + "&dir=" + this.dir + "&" + parameters;
        String encodedData = null;
        try {
            encodedData = URLEncoder.encode(url, ApplicationUtilities.getEncoding());
        } catch (UnsupportedEncodingException ignore) {
            logger.error("Exception ", ignore);
        }
        settings.putAll(reportsProcessor.phantomCredentials());

        locationsList = reportsProcessor.generateReportFromURI(encodedData, this.format, this.reportName, settings);
        return locationsList;
    }
}

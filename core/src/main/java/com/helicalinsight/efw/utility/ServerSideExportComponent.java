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

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.EfwResult;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used for copying downloaded report file from temp to user
 * specified location and creating file in user specified location
 * <p/>
 * Code was cleaned on 21-07-2015 by Rajasekhar
 *
 * @author Muqtar Ahmed
 */
public class ServerSideExportComponent {

    private static final Logger logger = LoggerFactory.getLogger(ServerSideExportComponent.class);
    private ApplicationProperties properties = ApplicationProperties.getInstance();
    private String reportName;
    private String reportType;
    private String resultName;
    private String resultDirectory;
    private File fileToBeSaved;
    private String dirReportPath;
    private String fileReportPath;

    /**
     * @param reportName      name of the report from request parameter
     * @param reportType      type of report from request parameter
     * @param resultName      result name from request parameter
     * @param resultDirectory location of directory where file to be copied and saved
     * @param fileToBeSaved   source report file which to be copied to destination
     * @param dirReportPath   location of directory from request parameter as string
     * @param fileReportPath  source report file from request parameter as string
     */
    public ServerSideExportComponent(String reportName, String reportType, String resultName, String resultDirectory,
                                     File fileToBeSaved, String dirReportPath, String fileReportPath) {
        this.reportName = reportName;
        this.reportType = reportType;
        this.resultName = resultName;
        this.resultDirectory = resultDirectory;
        this.fileToBeSaved = fileToBeSaved;
        this.dirReportPath = dirReportPath;
        this.fileReportPath = fileReportPath;
    }

    /**
     * This method is used for copying report file from source to destination
     */
    public void copyReportFromTemp() {
        Principal principal = AuthenticationUtils.getUserDetails();
        String destinationFile = properties.getSolutionDirectory() + File.separator +
                resultDirectory +
                File.separator + principal.getUsername() + "_" +
                fileToBeSaved.getName();
        try {
            File destination = new File(destinationFile);
            if (!destination.exists()) {
                final boolean newFile = destination.createNewFile();
                if (logger.isDebugEnabled()) {
                    logger.debug("The new file is created ? " + newFile);
                }
            }
            FileUtils.copyFile(fileToBeSaved, destination);
        } catch (Exception ex) {
            logger.error("Exception stack trace is ", ex);
        }
    }

    /**
     * This method is used for creating file
     *
     * @return string success or failure
     */

    public String saveEfwResultFile() {
        Principal principal = AuthenticationUtils.getUserDetails();
        String resultDirectoryLocation = properties.getSolutionDirectory() + File.separator +
                resultDirectory;
        String username = principal.getUsername();
        String outputFleName = resultDirectoryLocation + File.separator + username + "_" + fileToBeSaved.getName();
        String resultFile = username + "_" + fileToBeSaved.getName();

        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDate = formatter.format(new Date());

        EfwResult efwResult = ApplicationContextAccessor.getBean(EfwResult.class);

        efwResult.setReportName(reportName);
        efwResult.setReportDir(dirReportPath);

        efwResult.setReportFile(fileReportPath);
        efwResult.setReportType(reportType);

        efwResult.setResultName(resultName);
        efwResult.setResultDirectory(resultDirectory);

        efwResult.setResultFile(resultFile);
        efwResult.setRunDate(currentDate);

        efwResult.setSecurity(SecurityUtils.securityObject());

        String extension = JsonUtils.getEfwResultExtension();
        String[] file = outputFleName.split("\\.(?=[^\\.]+$)");
        String resultFileName = "";

        if (file.length > 1) {
            resultFileName = file[0];
        } else {
            logger.warn("File has no extension");
        }
        File xmlFile = new File(resultFileName + "." + extension);

        try {
            synchronized (ServerSideExportComponent.class) {
                JaxbUtils.marshal(efwResult, xmlFile);
            }
        } catch (Exception ex) {
            logger.error("Stack trace: ", ex);
            return "Couldn't save the file";
        }

        return "Success";
    }


    /**
     * this method responsible for validating the parameters
     *
     * @return boolean value
     */

    public boolean validateRequestParameters() {
        if (!validate(dirReportPath, "Dir")) {
            return false;
        }

        if (!validate(fileReportPath, "File")) {
            return false;
        }

        if (!validate(reportType, "Report Type")) {
            return false;
        }

        if (!validate(resultDirectory, "ResultDirectory")) {
            return false;
        }

        if (!validate(dirReportPath, "Dir")) {
            return false;
        }

        File file = new File(properties.getSolutionDirectory() + File.separator +
                resultDirectory);

        return file.isDirectory();
    }

    private boolean validate(String parameter, String name) {
        if ((parameter == null) || "".equals(parameter) || (parameter.trim().length() < 0)) {
            logger.error(name + " parameter is invalid.");
            return false;
        }
        return true;
    }
}

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

package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwfav;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A utility class which is being used by rest of the file operation module.
 *
 * @author Rajasekhar
 */
public class FileOperationsUtility {

    private static final Logger logger = LoggerFactory.getLogger(FileOperationsUtility.class);
    /**
     * Instance of singleton class <code>ApplicationProperties</code>
     */
    private final ApplicationProperties properties;
    /**
     * The EFW solution directory
     */
    private final String solutionDirectory;

    /**
     * Initializes the member variables properties and solutionDirectory
     */
    public FileOperationsUtility() {
        this.properties = ApplicationProperties.getInstance();
        this.solutionDirectory = properties.getSolutionDirectory();
    }

    /**
     * Marks file as favourite or not favourite based on isFavourite boolean.
     * The xml content of the file will be modified to mark or un mark. While
     * marking as favourite, the favFileName will be assigned to the tag
     * favourite. While un marking false will be written to the tag.
     *
     * @param reportFile      The name of the file for which favourite is to be created
     * @param reportDirectory The location of reportFile
     * @param isFavourite     Indicates true or false
     * @param favFileName     Specifies favourite file name
     * @return A string that represents the result of the operation
     */
    public String markFavourite(String reportFile, String reportDirectory, boolean isFavourite, String favFileName) {
        File xmlFile = new File(this.solutionDirectory + File.separator + reportDirectory + File.separator +
                reportFile);

        Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, xmlFile);
        String favourite = efwsr.getFavourite();
        if (isFavourite) {
            if ("false".equalsIgnoreCase(favourite)) {
                efwsr.setFavourite(favFileName);
                write(xmlFile, efwsr);
                return "success";
            } else {
                return "alreadyFavourite";
            }
        } else {
            if (favourite != null) {
                efwsr.setFavourite("false");
                write(xmlFile, efwsr);
                return "unmarked";
            } else {
                return "wasNotAFavourite";
            }
        }
    }

    private void write(File xmlFile, Efwsr efwsr) {
        synchronized (FileOperationsUtility.class) {
            JaxbUtils.marshal(efwsr, xmlFile);
        }
    }

    /**
     * Creates favourite file in the specified location with extension from
     * setting.xml
     *
     * @param reportFile        The efwsr file
     * @param reportDirectory   The location of reportFile
     * @param favouriteLocation A <code>String</code> which specifies favourite file location
     * @return The name of the favourite file
     */
    public String createFavouriteFile(String reportFile, String reportDirectory, String favouriteLocation) {
        Efwfav efwfav = ApplicationContextAccessor.getBean(Efwfav.class);
        efwfav.setSavedReportFileName(reportFile);
        efwfav.setVisible("true");
        efwfav.setReportName(getReportName(reportFile, reportDirectory));
        efwfav.setSecurity(SecurityUtils.securityObject());

        String extension = JsonUtils.getEfwfavExtension();
        String[] array = reportFile.split("\\.(?=[^\\.]+$)");
        String fileName = array[0] + "." + extension;
        String path = new File(this.solutionDirectory + File.separator + favouriteLocation + File.separator +
                fileName).toURI().getPath();
        File xmlFile = new File(path);

        synchronized (FileOperationsUtility.class) {
            JaxbUtils.marshal(efwfav, xmlFile);
        }

        return fileName;
    }

    /**
     * Get the reportName from the file reportFile in reportDirectory
     *
     * @param reportFile      A <code>String</code> which specifies report file name
     * @param reportDirectory A <code>String</code> which specifies directory where report
     *                        file exists
     * @return The reportName tag from the specified file
     */
    String getReportName(String reportFile, String reportDirectory) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonObject = processor.getJSONObject(this.properties.getSolutionDirectory() + File.separator +
                reportDirectory + File.separator + reportFile, false);

        return jsonObject.getString("reportName");
    }

    /**
     * Deletes the specified favourite file
     *
     * @param reportFile      The name of the file
     * @param reportDirectory The location of reportFile
     */
    public void deleteFavouriteFile(String reportFile, String reportDirectory) {
        File xmlFile = new File(this.solutionDirectory + File.separator + reportDirectory + File.separator +
                reportFile);
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonObject = processor.getJSONObject(xmlFile.toString(), false);

        try {
            String fileTobeSearched = jsonObject.getString("favourite");
            logger.info("Trying to delete the file " + fileTobeSearched);
            boolean result = deleteFile(this.solutionDirectory, fileTobeSearched);
            logger.debug("File " + fileTobeSearched + " deleted status " + result);
        } catch (JSONException ex) {
            logger.error("JSONException", ex);
        }
    }

    /**
     * Searches the solution directory and deletes the file passed as parameter
     * fileTobeSearched. The base path is absolute.
     *
     * @param basePath         The location to be searched
     * @param fileTobeSearched The name of file which has to be deleted
     * @return true if successfully deleted
     */
    boolean deleteFile(String basePath, String fileTobeSearched) {
        String path = search(basePath, fileTobeSearched);
        logger.debug("Search result for the path is " + path);
        File file = new File(path);
        if (file.delete()) {
            logger.debug("The file " + fileTobeSearched + " is successfully deleted!");
            return true;
        }
        return false;
    }

    /**
     * Searches for the file(fileTobeSearched) in input location(basePath).
     * Returns <code>null</code> if the file is not found.
     *
     * @param basePath         The location where the file to be searched
     * @param fileTobeSearched Name of file which has to be searched for
     * @return The file path as string
     */
    public String search(String basePath, String fileTobeSearched) {
        File[] files = new File(basePath).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && fileTobeSearched.equals(file.getName())) {
                    logger.debug("The file " + fileTobeSearched + " is found and its path is " +
                            file.toString());
                    return file.toString();
                } else if (file.isDirectory() && !file.getName().equalsIgnoreCase("system") &&
                        !file.getName().equalsIgnoreCase("images")) {
                    logger.debug("file is " + file + " is directory");
                    String path = search(file.toString(), fileTobeSearched);
                    if (path != null) {
                        return path;
                    }
                }
            }
        }
        return null;
    }
}

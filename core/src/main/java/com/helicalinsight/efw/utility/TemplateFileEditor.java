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
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This class is responsible for reading and modifying template file of the EFW
 * and add version number at the top of html file
 * </p>
 *
 * @author Prashansa
 */
public class TemplateFileEditor {
    private static final Logger logger = LoggerFactory.getLogger(TemplateFileEditor.class);

    /**
     * Creates a new file with updated version number and updated data; or
     * updates existing file on the basis of some condition.
     *
     * @param htmlString   a <code>String</code> which specifies updated html data which
     *                     is taken from front end
     * @param templatePath a <code>String</code> which specifies template file path.
     * @return a string
     */
    public String createFileAccordingToCondition(String htmlString, String templatePath) {
        logger.debug("templatePath:  " + templatePath);
        /*
         * createFile = false then existing file will be updated with updated data
		 * createFile = true then create new file with updated data and updated
		 * version number
		 */

        boolean createFile = this.isNewFileCreated();
        String dataAfterDecode = "";
        String encoding = ApplicationUtilities.getEncoding();

        ReportsUtility reportsUtility = new ReportsUtility();

        Pattern pattern = Pattern.compile("[%]");
        Matcher matcher = pattern.matcher(htmlString);
        boolean containsPercentSymbol = matcher.find();
        Long startTimeToDecode = System.currentTimeMillis();
        logger.debug("startTimeToDecode:  " + startTimeToDecode);
        if (containsPercentSymbol) {
            dataAfterDecode = reportsUtility.decodeURLEncoding(htmlString, encoding);
            dataAfterDecode = reportsUtility.decodeBase64Encoding(dataAfterDecode, encoding);
        }

        @SuppressWarnings("rawtypes") List modifiedVersion;
        File file = new File(templatePath);
        String ext = FilenameUtils.getExtension(templatePath);
        String fileName = FilenameUtils.removeExtension(templatePath);
        logger.debug("fileName:  " + fileName);
        // check file exist or not
        if (file.exists()) {
            if (createFile) {
                logger.debug("create new file..");
                modifiedVersion = readFile(templatePath);
                int fileNumber = (Integer) modifiedVersion.get(0) - 1;
                String filename = fileName + "." + fileNumber + "." + ext;
                logger.debug("filename:  " + filename);
                File newFile = new File(filename);
                try {
                    //noinspection ResultOfMethodCallIgnored
                    newFile.createNewFile();
                    File sourceFile = new File(templatePath);
                    FileUtils.copyFile(sourceFile, newFile);
                } catch (IOException e) {
                    logger.error("IOException occurred during file creation ", e);
                    //handle error
                }
            }

            FileStatusInspectionUtility checkFileStatus = new FileStatusInspectionUtility();

            modifiedVersion = readFile(templatePath);

            String data = (String) modifiedVersion.get(1);
            logger.debug("@version information : " + data);

            if (dataAfterDecode.contains("<!-- @version=")) {
                dataAfterDecode = dataAfterDecode.replace(dataAfterDecode.substring(dataAfterDecode.indexOf("<!-- " +
                        "@version="), dataAfterDecode.indexOf("\n")), data);
                logger.debug("Latest data : " + dataAfterDecode);
            } else {
                dataAfterDecode = data + dataAfterDecode;
            }
            boolean result = checkFileStatus.isCompletelyWritten(file, dataAfterDecode, encoding);
            logger.debug("File completely written status : " + result);
            System.gc();
        }
        return "ok";
    }

    public boolean isNewFileCreated() {
        String settingPath = ApplicationProperties.getInstance().getSettingPath();
        JSONObject jsonObject = ResourceProcessorFactory.getIProcessor().getJSONObject(settingPath, false);
        String newFileForBackup = jsonObject.optString("createNewBackupFile");
        return "true".equalsIgnoreCase(newFileForBackup);
    }

    /**
     * <p>
     * This method is responsible to read updated template.html from given path;
     * if version number is not available in it then adds version number as 1
     * and if present then increments version number with 1.
     * </p>
     *
     * @param filePath a <code>String</code> which specifies template file path
     * @return a <code>List</code> of Strings which specifies the modified
     * template data at index 0 and modified version number at index 1
     */
    @SuppressWarnings("unchecked")
    private List<String> readFile(String filePath) {
        logger.debug("Reading File");
        @SuppressWarnings("rawtypes") List fileData = new ArrayList();
        // if version number is not present then create version number =1 and
        // store in this variable.
        String version;

        RandomAccessFile randomAccessFile = null;
        // if version number present it will return true else it will return
        // false
        boolean isVersionPresent = false;
        try {
            String currentLine;
            int valueAfterIncrement = 0;
            randomAccessFile = new RandomAccessFile(new File(filePath), "rw");
            while ((currentLine = randomAccessFile.readLine()) != null) {
                logger.info("current line is " + currentLine);
                if (currentLine.contains("@version=")) {
                    isVersionPresent = true;


                    valueAfterIncrement = Integer.parseInt(currentLine.substring(currentLine.lastIndexOf("@version=")
                            + 9, currentLine.lastIndexOf("-->")).trim());
                    valueAfterIncrement += 1;
                    logger.debug("Incremented Count : " + valueAfterIncrement);
                    break;
                } else {
                    isVersionPresent = false;
                }
            }

            if (!isVersionPresent) {
                version = "<!-- @version=1 -->\n";
                fileData.add(1);
                fileData.add(version);
            }
            if (isVersionPresent) {
                version = "<!-- @version=" + valueAfterIncrement + "-->";
                fileData.add(valueAfterIncrement);
                fileData.add(version);
            }

        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException ", e);
        } catch (IOException e) {
            logger.error("IOException ", e);
            //handle error
        } finally {

            ApplicationUtilities.closeResource(randomAccessFile);
        }

        return fileData;
    }
}

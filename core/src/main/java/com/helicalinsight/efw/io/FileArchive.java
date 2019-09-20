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
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * This class can archive files with extension of efwExport from the setting.xml
 * in System/Temp directory.
 * </p>
 *
 * @author Rajasekhar
 */

public class FileArchive {

    private static final Logger logger = LoggerFactory.getLogger(FileArchive.class);

    /**
     * Archives the files in fileArray as zipFile
     *
     * @param zipFile   File to be zipped. The location of the file to be created
     * @param fileArray An array of efwsr files
     * @return true or false based on the conditions
     */
    public boolean archive(File zipFile, File[] fileArray) {
        ZipOutputStream zipOutputStream = null;
        try {
            FileInputStream fileInputStream;
            try {
                zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
                for (File file : fileArray) {
                    ZipEntry entry;
                    try {
                        entry = new ZipEntry(file.getName());
                        zipOutputStream.putNextEntry(entry);
                    } catch (ZipException ex) {
                        logger.error("ZipException occurred due to duplicate entry. Prefixing " +
                                "time stamp to the file " + file);
                        entry = new ZipEntry(System.currentTimeMillis() + file.getName());
                        zipOutputStream.putNextEntry(entry);
                    }
                    fileInputStream = new FileInputStream(file);
                    byte[] byteBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(byteBuffer)) != -1) {
                        zipOutputStream.write(byteBuffer, 0, bytesRead);
                    }
                    zipOutputStream.closeEntry();
                    ApplicationUtilities.closeResource(fileInputStream);
                }
            } catch (FileNotFoundException ex) {
                logger.error("FileNotFoundException ", ex);
                return false;
            } catch (IOException ex) {
                logger.error("IOException ", ex);
                return false;
            } finally {
                if (zipOutputStream != null) {
                    zipOutputStream.flush();
                }
            }
        } catch (IOException ex) {
            logger.error("IOException ", ex);
            return false;
        } finally {
            ApplicationUtilities.closeResource(zipOutputStream);
        }
        return true;
    }

    /**
     * Obtains the extension of the zip file from setting.xml
     *
     * @return returns the efwExport key value from the setting.xml
     */

    public String getExtensionOfZipFile() {
        ApplicationProperties properties = ApplicationProperties.getInstance();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject json = processor.getJSONObject(properties.getSettingPath(), false);

        String extension = null;
        try {
            extension = json.getJSONObject("Extentions").getString("efwExport");
            if (extension == null) {
                throw new ApplicationException();
            }
            logger.debug("efwExport text value = " + extension);
        } catch (JSONException ex) {
            logger.error("Please provide efwExport tag in Extensions of settings", ex);
        } catch (ApplicationException e) {
            logger.error("The extension is null.");
        }
        return extension;
    }

    /**
     * Unzips the source in to the destination directory
     *
     * @param source      The source of the zip
     * @param destination The destination where the zip has to be unzipped
     * @param password    The password of the zip file (if any)
     * @return true if the operation is successful
     */
    public boolean unzip(String source, String destination, String password) {
        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (net.lingala.zip4j.exception.ZipException e) {
            logger.error("ZipException", e);
            return false;
        }
        return true;
    }
}
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

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.io.delete.DeleteOperationUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * <p>
 * EFWSR files import operation is handled by this particular object. The class
 * supports importing of multiples formats of files like efw or efwFav also if
 * proper configuration is provided in the setting.xml.
 * <p/>
 * Note:
 * <p/>
 * Currently only importing a file with extension crt that consists of only
 * efwsr files but no directories is tested.
 * <p/>
 * </p>
 *
 * @author Rajasekhar
 */

@Component
@Scope("prototype")
public class ImportOperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ImportOperationHandler.class);

    /**
     * <p>
     * Imports zipped file into System/Temp location. Zip file extension should
     * be same as setting.xml node efwExport key value, which is part of
     * Extentions tag. Supports importing of password protected zip files also,
     * in which case request parameter zipPassword should be sent with the
     * request.
     * <p/>
     * The uploaded file is received as a request attribute.
     * <code>MultipartFilter</code> takes care of putting the uploaded file as a
     * request attribute.
     * </p>
     * <p/>
     * The imported files will consist of matching user credentials with the
     * logged in user after importing.
     *
     * @param request                         A <code>HttpServletRequest</code> object
     * @param fileObject                      A <code>FileItem</code>
     * @param destination                     A <code>String</code> which contains destination
     *                                        where the
     *                                        file has to be uploaded
     * @param extensionOfFileTypeToBeImported A <code>String</code> which specifies the extension
     *                                        of file to
     *                                        be uploaded
     * @return true if the operation is successful
     */

    public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination,
                                        String extensionOfFileTypeToBeImported) {
        FileArchive fileArchive = new FileArchive();
        String extension = fileArchive.getExtensionOfZipFile();
        String fileName = FilenameUtils.getName(fileObject.getName());
        String suffix = FilenameUtils.getExtension(fileName);
        if (!extension.equalsIgnoreCase(suffix)) {
            logger.error("Selected file to upload has a different extension than " + extension +
                    ". Aborting the process.");
            request.setAttribute("response", "Select a file only with extension" + extension);
            throw new OperationFailedException("Select a file only with extension " + extension);
            //return false;
        }
        Principal principal = AuthenticationUtils.getUserDetails();
        String prefix = FilenameUtils.getBaseName(fileName) + "_" + principal.getUsername();
        /*
         * Save and process the uploaded content from temp directory
		 */
        String folderName = TempDirectoryCleaner.getTempDirectory() + File.separator + prefix;
        File destinationFile = new File(folderName + "." + suffix);
        try {
            // Prepare unique local file based on file name of uploaded file.
            if (!destinationFile.exists()) {
                if (destinationFile.createNewFile()) {
                    logger.debug("Created the file " + destinationFile);
                } else {
                    logger.debug("Couldn't create the file " + destinationFile);
                }
            }
            // Write uploaded file to local file.
            fileObject.write(destinationFile);
            /*
             * If there is any password, use it to unzip it
			 */
            String password = request.getParameter("zipFilePassword");
            logger.debug("zipFilePassword : " + password);
            File directory = new File(folderName);
            if (!directory.mkdir()) {
                logger.debug("Created the location to be unzipped successfully");
            } else {
                logger.debug("Couldn't create the location to be unzipped");
            }
            if (!fileArchive.unzip(destinationFile.getAbsolutePath(), directory.toString(), password)) {
                logger.error("Selected file to upload couldn't be unzipped. Aborting the process.");
                request.setAttribute("response", "Selected file to upload couldn't be unzipped.");
                return false;
            }

            if (!directory.exists()) {
                logger.warn("directory " + directory + " doesn't exist!");
                return false;
            }
            DeleteOperationUtility utility = new DeleteOperationUtility();
            String clazz = utility.findCorrespondingClass(utility.findCorrespondingKey
                    (extensionOfFileTypeToBeImported), "import");

            if (clazz != null) {
                IImportRule rule = FactoryMethodWrapper.getTypedInstance(clazz, IImportRule.class);
                if (rule != null) {
                    if (!rule.validateDirectory(directory)) {
                        logger.error("Selected file to upload has other formats other than " +
                                extension + ". Aborting the process.");
                        request.setAttribute("response", "Selected file to upload has other " +
                                "formats other than " + extension);
                        throw new OperationFailedException("Selected file to upload has other " +
                                "formats other than " + extension);
                        //return false;
                    }
                }
                /*
                 * All is well. Directory is validated. Try importing all files.
				 */
                if (rule != null) {
                    if (!rule.importFile(directory, destination)) {
                        /*
                         * Only in case of IOException the method returns false
                         */
                        logger.error("Couldn't modify the efwsr files with the session user credentials. Aborting " +
                                "the process.");
                        request.setAttribute("response", "Couldn't modify the efwsr files with the session user " +
                                "credentials");
                        throw new OperationFailedException("Couldn't modify the efwsr files with the session user " +
                                "credentials");
                    }
                }
            } else {
                logger.error("Couldn't import the file as rule class is not provided. Aborting " + "the operation.");
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return true;
    }
}

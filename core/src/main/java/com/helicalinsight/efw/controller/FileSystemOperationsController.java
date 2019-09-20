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

package com.helicalinsight.efw.controller;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParametersNotProvidedException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.io.*;
import com.helicalinsight.efw.io.delete.DeleteOperationHandler;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResponseUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * The controller has methods that have mapping for /fileSystemOperations and
 * /importFile. The operations that it can do are newFolder creation, move,
 * delete, export, renaming of files and folders along with importing files.
 * <p/>
 * <p/>
 * A typical setting.xml referenced through out the application will be present
 * in the location System/Admin directory. The System directory is part of the
 * solution directory which consists of the framework related files.
 * <p/>
 * The configuration path of setting.xml can be found in project.properties file
 * in the class path.
 * <p/>
 *
 * @author Rajasekhar
 */
@Component
@Controller
public class FileSystemOperationsController implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemOperationsController.class);

    /**
     * The singleton instance of the class ApplicationProperties
     */
    private ApplicationContext applicationContext;

    /**
     * The request parameter is of the form array([]). The destination parameter
     * is a string. The files and folders are relative path names and not
     * absolute.
     * <p/>
     * Along with the response that is either success or failure always a JSP
     * request attribute 'response' is set for the purpose of the view
     *
     * @param sourceArray The request parameter that consists of files and folders names
     * @param action      The type of action to be completed
     * @param request     The http request object
     * @param response    The http response object
     */
    @RequestMapping(value = "/fileSystemOperations", method = RequestMethod.POST)
    public void executeFileSystemOperations(@RequestParam("sourceArray") String sourceArray,
                                            @RequestParam("action") String action, HttpServletRequest request,
                                            HttpServletResponse response) throws
            UnSupportedRuleImplementationException, IOException {
        boolean isAjax = ControllerUtils.isAjax(request);

        try {
            String message;
            if ("newFolder".equalsIgnoreCase(action)) {
                NewFolderHandler newFolderHandler = (NewFolderHandler) applicationContext.getBean("newFolderHandler");
                String folderName = request.getParameter("folderName");
                if ((folderName == null) || "".equals(folderName) || (folderName.trim().length() == 0)) {
                    request.setAttribute("response", "Parameter folderName is null or empty");
                    throw new RequiredParametersNotProvidedException("Parameter folderName is " + "null or empty");
                }

                if (newFolderHandler.handle(sourceArray, folderName)) {
                    logger.info("Completed action newFolder");
                    message = "A new folder is created successfully";
                } else {
                    logger.error("New folder couldn't be created");
                    throw new OperationFailedException("New folder couldn't be created");
                }
            } else if ("rename".equalsIgnoreCase(action)) {
                RenameOperationHandler renameOperationHandler = (RenameOperationHandler) applicationContext.getBean
                        ("renameOperationHandler");
                if (renameOperationHandler.handle(sourceArray)) {
                    request.setAttribute("response", "Completed action rename");
                    message = "Rename is successful";
                } else {
                    request.setAttribute("response", "Rename operation is not " + "successful");
                    throw new OperationFailedException("Rename operation is not " + "successful");
                }
            } else if ("move".equalsIgnoreCase(action)) {
                MoveToOperationHandler moveToOperationHandler = (MoveToOperationHandler) applicationContext.getBean
                        ("moveToOperationHandler");
                String destination = request.getParameter("destination");
                if ((destination == null)) {
                    request.setAttribute("response", "Parameter destination is null");
                    throw new RequiredParametersNotProvidedException("Parameter destination is " + "null");
                }

                if (moveToOperationHandler.handle(sourceArray, destination)) {
                    logger.info("Moving is successful");
                    message = "Moving is successful";
                } else {
                    logger.error("Move operation is not successful");
                    throw new OperationFailedException("Move operation is not successful");
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                DeleteOperationHandler deleteOperationHandler = (DeleteOperationHandler) applicationContext.getBean
                        ("deleteOperationHandler");
                if ((sourceArray == null) || "[]".equals(sourceArray) || (sourceArray.trim().length() == 0)) {
                    logger.error("Parameter sourceArray is null or empty");
                    throw new FormValidationException("Parameter sourceArray is null or empty");
                }
                if (deleteOperationHandler.handle(sourceArray)) {
                    logger.info("Delete operation is successful");
                    message = "Delete operation is successful";
                } else {
                    logger.error("Access denied. Delete operation failed.");
                    throw new OperationFailedException("Access denied. Delete operation failed.");
                }
            } else if ("export".equalsIgnoreCase(action)) {
                ExportOperationHandler exportOperationHandler = (ExportOperationHandler) applicationContext.getBean
                        ("exportOperationHandler");
                if ((sourceArray == null) || "[]".equals(sourceArray) || (sourceArray.trim().length() == 0)) {
                    logger.error("Parameter sourceArray is null or empty");
                    throw new OperationFailedException("Parameter sourceArray is null or empty");
                }
                List<File> listOfFilesToBeZipped = exportOperationHandler.validateSource(sourceArray);
                if (listOfFilesToBeZipped == null) {
                    logger.error("Invalid source. Only EFWSR type files are supported for " + "exporting.");

                    throw new OperationFailedException("Invalid source. Only EFWSR type files " + "are supported for " +
                            "exporting.");
                }
                if (!export(request, response, listOfFilesToBeZipped)) {
                    logger.info("Export operation failed. Only saved reports are exportable.");
                    throw new OperationFailedException("Export operation failed. Only saved " + "reports are " +
                            "exportable.");
                } else {
                    logger.info("The export operation is successful");
                    message = "The export operation is successful";
                }
            } else {
                throw new OperationFailedException("Unknown user action. Operation aborted.");
            }
            if (!"export".equalsIgnoreCase(action)) {
                String result = ResponseUtils.createJsonResponse(message);
                ControllerUtils.handleSuccess(response, isAjax, result);
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    /**
     * Exports the zip file from the Temp location. The list of files to be
     * zipped are zipped and stored in the Temp directory
     *
     * @param request               The http request object
     * @param response              The http response object
     * @param listOfFilesToBeZipped The list of files to be zipped
     * @return true or false based on the input processing
     */
    private boolean export(HttpServletRequest request, HttpServletResponse response, List<File> listOfFilesToBeZipped) {
        logger.debug("listOfFilesToBeZipped : " + listOfFilesToBeZipped);
        File[] files = new File[listOfFilesToBeZipped.size()];
        files = listOfFilesToBeZipped.toArray(files);
        FileArchive fileArchive = new FileArchive();
        String extension = fileArchive.getExtensionOfZipFile();
        if (extension != null) {
            Principal principal = AuthenticationUtils.getUserDetails();
            String attachmentName = principal.getUsername() + "_" + System.currentTimeMillis() +
                    "." + extension;
            File fileToBeExported = new File(TempDirectoryCleaner.getTempDirectory().getAbsolutePath() + File
                    .separator + attachmentName);
            if (!fileArchive.archive(fileToBeExported, files)) {
                logger.error("File couldn't be written to the temporary location. Aborting the " + "operation.");
                request.setAttribute("response", "File couldn't be written to the temporary " + "location!");
                return false;
            }
            OutputStream outputStream = null;
            FileInputStream fileInputStream = null;
            try {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", String.format("attachment; " + "filename=\"%s\"",
                        attachmentName));
                fileInputStream = new FileInputStream(fileToBeExported);
                outputStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                // Flush and close the outputStream
                outputStream.flush();
            } catch (IOException e) {
                logger.error("IOException", e);
            } finally {
                ApplicationUtilities.closeResource(fileInputStream);
                ApplicationUtilities.closeResource(outputStream);
            }
        } else {
            logger.error("Improper settings configuration. Couldn't find extension for efwExport.");
            return false;
        }
        return true;
    }

    /**
     * Imports a file on to the file system. The destination has to exist on the
     * file system.
     * <p/>
     * Along with the response that is either success or failure always a JSP
     * request attribute 'response' is set for the purpose of the view.
     *
     * @param destination The destination of the import
     * @param request     The http request object
     */
    @RequestMapping(value = "/importFile", method = RequestMethod.POST)
    public void executeImportOperation(@RequestParam("destination") String destination, HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        String message;
        try {
            logger.info("Destination parameter : " + destination);
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            File destinationFile = new File(applicationProperties.getSolutionDirectory() + File.separator +
                    destination);
            //The below if condition was removed to let users import a file into root directory
            //of solution repository on 20-07-2015
            //if (destination == null || ApplicationUtilities.isEmpty(destination) ||
            //!destinationFile.exists()) {
            if (!destinationFile.exists()) {
                logger.error("The parameter 'destination' value is incorrect. Its value is null " +
                        "or " + "empty or it " +
                        "doesn't exist on the file system.");
                request.setAttribute("response", "The parameter 'destination' value is null or " + "empty or it " +
                        "doesn't exist on the file system.");
                throw new OperationFailedException("The parameter 'destination' value is null or" + " empty or it " +
                        "doesn't exist on the file system.");
            }

            ImportOperationHandler importOperationHandler = (ImportOperationHandler) applicationContext.getBean
                    ("importOperationHandler");
            Object fileObject = request.getAttribute("file");

            if (fileObject == null) {
                logger.error("There is no file to upload! Please select a file to upload");
                throw new OperationFailedException("There is no file to upload! Please select a " + "file to upload.");
            } else if (fileObject instanceof FileUploadException) {
                FileUploadException fileUploadException = (FileUploadException) fileObject;
                logger.error("Please select a smaller file to upload.", fileUploadException.getMessage());
                request.setAttribute("response", "Please select a smaller file to upload.");
                throw new OperationFailedException("Please select a smaller file to upload.");
            }

            String extensionOfFileTypeToBeImported = request.getParameter("fileExtension");
            if (extensionOfFileTypeToBeImported == null) {
                extensionOfFileTypeToBeImported = JsonUtils.getEFWSRExtension();
            }
            if (!importOperationHandler.processMultipartItem(request, (FileItem) fileObject, destination,
                    extensionOfFileTypeToBeImported)) {
                JSONObject statusZeroJson = ControllerUtils.statusZeroJson("Error. The file " + "couldn't be imported" +
                        " properly. Only Saved reports are importable.");
                ControllerUtils.handleSuccess(response, isAjax, statusZeroJson.toString());
            } else {
                message = "The import operation is successful";
                String result = ResponseUtils.createJsonResponse(message);
                ControllerUtils.handleSuccess(response, isAjax, result);
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    /**
     * Sets the property with the application context
     *
     * @param applicationContext The ApplicationContext of spring
     * @throws BeansException If something goes wrong ):
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
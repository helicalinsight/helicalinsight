package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.copypaste.CopyPasteHandler;
import com.helicalinsight.adhoc.copypaste.CutPasteHandler;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.*;
import com.helicalinsight.efw.io.delete.DeleteOperationHandler;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.resourcedb.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
import java.util.HashMap;
import java.util.List;

import static com.helicalinsight.resourcedb.processor.DBProcessor.checkAndReplaceSpecialChars;

/**
 * The controller has methods that have mapping for /fileSystemOperations and
 * /importFile. The operations that it can do are newFolder creation, move,
 * delete, export, renaming of files and folders along with importing files.
 * <p>
 * <p>
 * A typical setting.xml referenced through out the application will be present
 * in the location System/Admin directory. The System directory is part of the
 * solution directory which consists of the framework related files.
 * <p>
 * The configuration path of setting.xml can be found in project.properties file
 * in the class path.
 * <p>
 *
 * @author Rajasekhar
 * @since 1.1
 */
@Component
@Controller
public class FileSystemOperationsController implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemOperationsController.class);


    /**
     * The singleton instance of the class ApplicationProperties
     */
    private ApplicationContext applicationContext;

    @Autowired
    private HIResourceServiceDB serviceDB;


    @Autowired
    ResourceTypeServiceDB resourceTypeServiceDB;

    @RequestMapping(value = "/d/fileSystemOperations", method = RequestMethod.POST)
    public void executeFileSystemOperations(@RequestParam("sourceArray") String sourceArray,
                                            @RequestParam("action") String action, HttpServletRequest request,
                                            HttpServletResponse response) throws
            UnSupportedRuleImplementationException, IOException {
        boolean isAjax = ControllerUtils.isAjax(request);

        JSONObject data = new JSONObject();
        try {
            String message;
            logger.debug("LockList  start :" + ApplicationUtilities.lockPathForFileBrowser);
            if ("newFolder".equalsIgnoreCase(action)) {
                NewFolderHandler newFolderHandler = (NewFolderHandler) applicationContext.getBean("newFolderHandler");
                String folderName = request.getParameter("folderName");
                if ((folderName == null) || "".equals(folderName) || (folderName.trim().length() == 0)) {
                    request.setAttribute("response", "Parameter folderName is null or empty");
                    throw new RequiredParametersNotProvidedException("Parameter folderName is " + "null or empty");
                }

                JSONArray sourceArrayJson = JSONArray.fromObject(sourceArray);
                String handle[] = newFolderHandler.handle(sourceArray, folderName);
                if (handle != null && handle.length > 0) {
                    logger.info("Completed action newFolder");

                    message = "A new folder is created successfully";
                    data.put("name", folderName);
                    data.put("children", "[]");
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("selectable", "true");
                    data.put("options", hashMap);
                    data.put("type", "folder");
                    data.put("lastModified", handle[0]);
                    data.put("path", sourceArrayJson.get(0) + "/" + handle[0]);
                    ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = new ResourcePermissionLevelsHolder();
                    data.put("permission", resourcePermissionLevelsHolder.ownerAccessLevel());

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
                if (!data.isEmpty()) {
                    JSONObject resultJson = JSONObject.fromObject(result);
                    resultJson.getJSONObject("response").put("data", data);
                    result = resultJson.toString();
                }
                ControllerUtils.handleSuccess(response, isAjax, result);
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
        logger.debug("LockList end :" + ApplicationUtilities.lockPathForFileBrowser);
    }


    public static FileItem convert(MultipartFile multipartFile) throws IOException {
        DiskFileItem fileItem = new DiskFileItem(
                multipartFile.getName(),
                multipartFile.getContentType(),
                multipartFile.isEmpty(),
                multipartFile.getOriginalFilename(),
                (int) multipartFile.getSize(),
                new File(System.getProperty("java.io.tmpdir"))
        );

        try (OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(multipartFile.getInputStream(), os);
        }

        return fileItem;
    }


    /**
     * Exports the zip file from the Temp location. The list of files to be
     * zipped are zipped and stored in the Temp directory
     *
     * @param payLoad parameters which required to perform filesystem operation are populated here
     * @return response             response of the action performed
     */
    @RequestMapping(value = "/fileSystemOperations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> executeFileSystemOperations(@ModelAttribute @Valid FileOperationDTO payLoad) {
        AbstractResourceAction resourceAction = AbstractResourceAction.getActionClass(payLoad);
        Boolean actionResponse = resourceAction.performAction();
        String message = resourceAction.getMessage();
        HIResponse hiResponse = null;
        if (actionResponse) {
            hiResponse = new HISuccessResponse();
        } else {
            hiResponse = new HIFailedResponse();
        }
        if (StringUtils.isNotBlank(payLoad.getIsPublic())) {
            hiResponse.setResponse("public", Boolean.valueOf(payLoad.getIsPublic()));
            ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
            if (Boolean.valueOf(payLoad.getIsPublic()))
                hiResponse.setResponse("permissionLevel", resourcePermissionLevelsHolder.publicResourceAccessLevel());
            else
                hiResponse.setResponse("permissionLevel", resourcePermissionLevelsHolder.ownerAccessLevel());
        }
        if (StringUtils.isNotBlank(payLoad.getIsVisible())) {
            hiResponse.setResponse("isVisible", Boolean.valueOf(payLoad.getIsVisible()));
        }
        hiResponse.setMessage(message);
        if (resourceAction instanceof NewFolderAction
                || resourceAction instanceof CutPasteHandler || resourceAction instanceof CopyPasteHandler) {
            hiResponse.setData(resourceAction.getData());
        }
        return ResponseEntity.ok().body(hiResponse);
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
     * <p>
     * Along with the response that is either success or failure always a JSP
     * request attribute 'response' is set for the purpose of the view.
     *
     * @param destination The destination of the import
     * @param request     The http request object
     */
    @RequestMapping(value = "/importFile", method = RequestMethod.POST)
    public void executeImportOperation(@RequestParam("file") MultipartFile mfile,
                                       @RequestParam("destination") String destination,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        //;

        logger.debug("Lock_List_Start  :" + ApplicationUtilities.lockPathForFileBrowser);
        boolean isAjax = ControllerUtils.isAjax(request);
        //  String destination = request.getParameter("destination");
        String message = null;
        try {

            logger.info("Destination parameter : " + destination);


            String stringConfig = request.getParameter("config");
            DiskFileItem item = null;
            if (mfile != null) {
                item = (DiskFileItem) convert(mfile);
                if (StringUtils.isBlank(FilenameUtils.getExtension(mfile.getOriginalFilename()))) {
                    throw new EfwdServiceException("Extension should not be blank.");
                }
            }

            if (mfile == null && StringUtils.isNotBlank(stringConfig)) {
                String fileName = AdhocUtils.getUuid() + ".json";
                File tempFile = new File(TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
                tempFile.deleteOnExit();
                item = new DiskFileItem("file", "multipart/file", true, fileName, 0, tempFile);
                try (OutputStream outputStream = item.getOutputStream()) {
                    outputStream.write(stringConfig.getBytes());
                }
            }


            String extensionOfFileTypeToBeImported = request.getParameter("fileExtension");
            String type = request.getParameter("type");
            if (type == null) {
                extensionOfFileTypeToBeImported = JsonUtils.getEFWSRExtension();

            } else {
                extensionOfFileTypeToBeImported = type;
            }

            IUpload importOperationHandler = UploadClassFactory.getIUploadClass(type);

            if (type.equals("image")) {
                HIResource destinationResource = serviceDB.getResourceByUrl(destination);
                if (destinationResource == null) {
                    throw new ResourceNotFoundException("Resource named with " + destination + " not found");
                }

                String fileName = item.getName();

                HIResourceImages entity = new HIResourceImages();
                entity.setContent(getByteArrayFromDiskFileItem(item));
                String extension = FileUtils.getExtension(fileName);
                entity.setContentType(extension != null ? extension.toLowerCase() : "");
                String imagePath = fileName.replace("." + extension, "");
                String newName = checkAndReplaceSpecialChars(imagePath);
                String imageUrl = destination + "/" + newName+ ".image";;


                HIResource imageResource = ResourceUtils.newHIResource(destination, false, newName, newName, imageUrl, JsonUtils.getImageExtension());
                ResourceType imageExtension = resourceTypeServiceDB.getResourceTypeByTypeAndExtension("image", ".image");

                imageResource.setResourceType(imageExtension);
                imageResource.setHiResourceImages(entity);
                serviceDB.addHIResource(imageResource);

                JsonObject result = new JsonObject();
                result.addProperty("message","The import operation is successful");

                ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
                FileInfo fileInfo = bean.prepareFileInfo(destination, imageResource.getResourcePath()+ ".image");

                JsonArray data = new JsonArray();
                String json = new Gson().toJson(fileInfo);
                JsonObject fileInfoObject = new Gson().fromJson(json, JsonObject.class);
                data.add(fileInfoObject);
                result.add("data", data);

                ControllerUtils.handleSuccess(response, isAjax, result.toString());
            } else {
                if (!importOperationHandler.processMultipartItem(request, (FileItem) item, destination,
                        extensionOfFileTypeToBeImported)) {
                    JsonObject statusZeroJson = ControllerUtils.statusZeroJson("Error. The file "
                            + "couldn't be imported" + " properly. Only Saved reports are importable.");
                    ControllerUtils.handleSuccess(response, isAjax, statusZeroJson.toString());
                } else {
                    Object unknown = request.getAttribute("message");
                    String result;
                    if (unknown instanceof JSONObject) {
                        result = ResponseUtils.handleSuccessMassage((JSONObject) (unknown)).toString();
                    } else if (unknown instanceof JsonObject) {
                        result = ResponseUtils.handleSuccessMassage((JsonObject) (unknown)).toString();
                    } else {
                        message = (String) unknown;
                        result = ResponseUtils
                                .createJsonResponse(message == null ? "The import operation is successful" : message);
                    }
                    ControllerUtils.handleSuccess(response, isAjax, result);
                }
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
        logger.debug("Lock_List_End  :" + ApplicationUtilities.lockPathForFileBrowser);
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

    private byte[] getByteArrayFromDiskFileItem(DiskFileItem fileItem) {
        // Check if the item is a form field or a file upload
        if (!fileItem.isFormField()) {
            // Create an InputStream from the file item
            try (InputStream inputStream = fileItem.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                // Copy the input stream to the output stream
                IOUtils.copy(inputStream, outputStream);
                // Return the byte array
                return outputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null; // or handle the case where it's a form field
    }
}
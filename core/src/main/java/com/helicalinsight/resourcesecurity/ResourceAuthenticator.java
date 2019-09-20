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

package com.helicalinsight.resourcesecurity;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.*;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import com.helicalinsight.resourcesecurity.jaxb.DecisionState;
import com.helicalinsight.resourcesecurity.jaxb.LookupParameters;
import com.helicalinsight.resourcesecurity.jaxb.SubContext;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Created by author on 31-07-2015.
 *
 * @author Rajasekhar
 */
@Component
public class ResourceAuthenticator implements IResourceAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAuthenticator.class);

    private static final String EQUAL_TO = "eq";

    private static final String NOT_EQUAL_TO = "ne";

    private static final String DEFAULT_LOOKUP = "default";

    private static final String DIRECTORY_LOOKUP = "directory";

    private static final String ARRAY_LOOKUP = "array";

    private static final String OPTIONAL_PARAMETERS = "optional";

    private static final String INDEX_FILE_EXTENSION = JsonUtils.getFolderFileExtension();

    @Autowired
    private ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    @Autowired
    private ResourcePermissionFactory factory;

    private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private IProcessor processor = ResourceProcessorFactory.getIProcessor();

    private Integer publicResourceAccessLevel;

    private Integer readWriteAccessLevel;

    private File root;

    private String solutionDirectory;

    @PostConstruct
    public void init() {
        this.publicResourceAccessLevel = this.resourcePermissionLevelsHolder.publicResourceAccessLevel();
        this.readWriteAccessLevel = this.resourcePermissionLevelsHolder.readWriteAccessLevel();
        resetSolutionDirectory();
    }

    private void resetSolutionDirectory() {
        this.solutionDirectory = this.applicationProperties.getSolutionDirectory();
        this.root = new File(this.solutionDirectory);
    }

    @Override
    public boolean authenticate(HttpServletRequest request, Context urlContext) {
        if (urlContext == null) {
            throw new IllegalArgumentException("The url context is null.");
        }

        Integer minimumPermissionRequired = urlContext.getPermission();
        //Reset directory as it it request scoped.
        resetSolutionDirectory();

        if (urlContext.getLookupParameters() == null && minimumPermissionRequired == null) {
            //The resource information is present in json. The mapping is /services
            return processServicesUrlRequest(request, urlContext, this.solutionDirectory);
        } else {
            boolean optionalParameters = areOptionalParameters(urlContext.getParameters());

            if (minimumPermissionRequired != null) {//Default lookup
                boolean result = normalUrlContexts(request, urlContext, minimumPermissionRequired,
                        this.solutionDirectory, optionalParameters);
                log(urlContext, result);
                return result;
            } else { //Decisions need to be taken based on the conditions
                //Minimum permission is not available directly.
                LookupParameters lookupParameters = getLookupParameters(urlContext);
                boolean arrayLookup = isArrayLookup(lookupParameters);
                boolean defaultLookup = isDefaultLookup(lookupParameters);

                if (defaultLookup) {
                    String directory = lookupParameters.getDirectory();
                    String file = lookupParameters.getFile();

                    String directoryName = request.getParameter(directory);
                    String fileName = request.getParameter(file);

                    String resource = this.solutionDirectory + File.separator + directoryName +
                            File.separator + fileName;
                    File actualFile = new File(resource);
                    if (!actualFile.exists()) {
                        throw new ResourceNotFoundException("The requested resource does not " + "exists. Aborting " +
                                "the operation.");
                    }

                    final String operation = request.getParameter("operation");

                    if (operation == null && directoryName != null && fileName != null) {
                        return saveReport(request, urlContext, resource);
                    }

                    if ("favourite".equalsIgnoreCase(operation)) {
                        String favouriteLocation = request.getParameter("favouriteLocation");
                        if (favouriteLocation != null) {
                            boolean result = processMarkFavouriteRequest(request, urlContext, this.solutionDirectory,
                                    resource, favouriteLocation);
                            log(urlContext, result);
                            return result;
                        } else {
                            throw new RequiredParameterIsNullException("Request is missing " + "favouriteLocation.");
                        }
                    }

                    boolean result = defaultLookupWithOutPermission(request, urlContext, resource);
                    log(urlContext, result);
                    return result;
                } else if (arrayLookup) {
                    if ("move".equalsIgnoreCase(request.getParameter("action")) && request.getParameter
                            ("destination") != null) {
                        boolean result = handleMoveRequest(request, this.solutionDirectory);
                        log(urlContext, result);
                        return result;
                    }

                    LookupParameters.Parameter parameter = lookupParameters.getParameter();
                    String parameterName = parameter.getParameter();
                    String requestParameterValue = request.getParameter(parameterName);
                    if (!"java.lang.String".equalsIgnoreCase(parameter.getDataType())) {
                        return error();
                    }

                    JSON source = JSONSerializer.toJSON(requestParameterValue);
                    if (source instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) source;
                        try {
                            if (!processArray(request, urlContext, this.solutionDirectory, jsonArray)) {
                                log(urlContext, false);
                                return false;
                            }
                        } catch (Exception ex) {
                            throw new MalformedJsonException("The Json Object is incorrect. " + "Expecting an array " +
                                    "of files and directories only.", ex);
                        }
                    } else {
                        return error();
                    }
                } else {
                    throw new EfwServiceException("Only default, directory and array lookUps are " +
                            "" + "supported.");
                }
            }
        }
        log(urlContext, true);
        return true;
    }

    private boolean saveReport(HttpServletRequest request, Context urlContext, String resource) {
        String location = request.getParameter("location");
        if (permission(resource) < this.publicResourceAccessLevel) {
            return false;
        }
        final boolean result;
        if (location != null) {
            resource = this.solutionDirectory + File.separator + location;
            if (this.root.equals(new File(resource))) {
                log(urlContext, true);
                return true;
            } else {
                resource = getIndexFile(resource);
                if (!new File(resource).exists()) {
                    result = processPublicDirectoryWritePermissionRequest();
                } else {
                    result = processRequest(this.readWriteAccessLevel, resource);
                }
            }
            log(urlContext, result);
            return result;
        } else {
            throw new RequiredParameterIsNullException("Request is missing " + "location.");
        }
    }

    private boolean processMarkFavouriteRequest(HttpServletRequest request, Context urlContext,
                                                String solutionDirectory, String resource, String favouriteLocation) {
        if (!defaultLookupWithOutPermission(request, urlContext, resource)) {
            return false;
        }
        resource = solutionDirectory + File.separator + favouriteLocation;
        File favouriteDirectory = new File(resource);
        if (!favouriteDirectory.exists()) {
            throw new ResourceNotFoundException("The requested directory directory does not " + "exist.");
        }
        resource = getIndexFile(resource);
        File indexFile = new File(resource);
        if (!indexFile.exists()) {
            return (this.root.equals(favouriteDirectory)) || processPublicDirectoryWritePermissionRequest();
        } else {
            return processRequest(this.readWriteAccessLevel, resource);
        }
    }

    private void log(Context urlContext, boolean result) {
        if (logger.isInfoEnabled()) {
            logger.info("The url context is " + urlContext + ". The result of verification is " +
                    (result ? "successful." : "not successful."));
        }
    }

    private boolean processArray(HttpServletRequest request, Context urlContext, String solutionDirectory,
                                 JSONArray jsonArray) {
        String file;
        for (Object object : jsonArray) {
            if (object instanceof String) {
                file = (String) object;
            } else if (object instanceof JSONArray) {
                JSONArray array = (JSONArray) object;
                file = (String) array.get(0);
            } else {
                throw new IllegalStateException();
            }

            String resource = solutionDirectory + File.separator + file;
            File fileOrFolder = new File(resource);
            if (fileOrFolder.isDirectory()) {
                resource = getIndexFile(resource);
                File indexFile = new File(resource);
                if (indexFile.exists()) {
                    if (!processResource(request, urlContext, resource)) {
                        return false;
                    }
                } else {
                    if (!this.root.equals(fileOrFolder)) {
                        if (!handlePublicDirectories(request, urlContext)) {
                            return false;
                        }
                    }
                }
            } else {
                if (!processResource(request, urlContext, resource)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean processResource(HttpServletRequest request, Context urlContext, String resource) {
        Integer logicalIfPermission = logicalIfPermission(request, urlContext);
        return processRequest(logicalIfPermission, resource);
    }

    private boolean handlePublicDirectories(HttpServletRequest request, Context urlContext) {
        Integer logicalIfPermission = logicalIfPermission(request, urlContext);
        return (this.publicResourceAccessLevel >= logicalIfPermission);
    }

    private Integer logicalIfPermission(HttpServletRequest request, Context urlContext) {
        DecisionState decisionState = getDecisionState(urlContext);
        String decisionStateParameter = decisionState.getParameter();

        String requestParameter = request.getParameter(decisionStateParameter);
        String defaultParameterValue = decisionState.getDefaultValue();
        Integer defaultPermission = decisionState.getDefaultPermission();

        if (requestParameter == null) {
            if ((defaultParameterValue == null && defaultPermission == null) || (defaultParameterValue == null ||
                    defaultPermission == null)) {
                throw new RequiredParameterIsNullException(String.format("Parameter %s is " + "missing and default " +
                        "configuration is not provided. Invalid request.", decisionStateParameter));
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Decision state parameter value is missing. Treating it " +
                            "as " + defaultParameterValue + " request.");
                }
                return defaultPermission;
            }
        }

        DecisionState.LogicalIf logicalIf = requiredIfBlock(requestParameter, decisionState);
        if (logicalIf == null) {
            throw new ConfigurationException(String.format("Unknown parameter value %s for the " + "parameter %s.",
                    requestParameter, decisionStateParameter));
        }

        Integer logicalIfPermission = logicalIf.getPermission();

        if (logicalIfPermission == null) {
            throw new ConfigurationException("Required configuration permission is missing " + "from the logical " +
                    "state.");
        }
        return logicalIfPermission;
    }

    private String getIndexFile(String resource) {
        resource = resource + File.separator + "index." + INDEX_FILE_EXTENSION;
        return resource;
    }

    private boolean handleMoveRequest(HttpServletRequest request, String solutionDirectory) {
        String destination = request.getParameter("destination");
        if (destination != null) {
            String resource = solutionDirectory + File.separator + destination;
            File destinationFile = new File(resource);
            if (destinationFile.exists() && destinationFile.isDirectory()) {
                resource = getIndexFile(resource);
                File file = new File(resource);
                if (file.exists()) {
                    return processRequest(this.readWriteAccessLevel, resource);
                } else {
                    return (this.root.equals(destinationFile)) || processPublicDirectoryWritePermissionRequest();
                }
            } else {
                throw new EfwServiceException("The destination you are trying to move doesn't " + "exist.");
            }
        } else {
            throw new RequiredParameterIsNullException("Missing request parameter destination. A " +
                    "" + "directory is expected.");
        }
    }

    private boolean processPublicDirectoryWritePermissionRequest() {
        return (this.publicResourceAccessLevel >= this.readWriteAccessLevel);
    }

    private boolean error() {
        throw new ConfigurationException("In-case of array type lookUpParameters," +
                "" + " only java.lang.String(JSON Array of Strings) dataType is supported.");
    }

    private boolean processServicesUrlRequest(HttpServletRequest request, Context urlContext,
                                              String solutionDirectory) {
        Integer minimumPermissionRequired;
        List<SubContext> subContexts = urlContext.getSubContexts();
        if (subContexts == null) {
            throw new ConfigurationException("The lookupParameters and permission are not " + "configured. Expecting " +
                    "subContexts.");
        }

        SubContext subContext = findSubContext(subContexts, request);
        if (subContext == null) {
            //The subContext is to be allowed as there is no such resource authentication
            if (logger.isInfoEnabled()) {
                logger.info("No subContext is found. Allowing request.");
            }
            return true;
        }

        boolean optionalParameters = areOptionalParameters(subContext.getParameters());

        minimumPermissionRequired = subContext.getPermission();
        SubContext.FormData formData = subContext.getFormData();
        if (formData == null) {
            throw new ConfigurationException("The lookupParameters and permission are not " + "configured. Expecting " +
                    "subContexts with formData.");
        }

        SubContext.Directory directory = formData.getDirectory();
        SubContext.File file = formData.getFile();

        String directoryParameter = null;
        String fileParameter = null;

        if (directory != null) {
            String optional = directory.getOptional();
            if (optional != null) {
                if (!"true".equalsIgnoreCase(optional)) {
                    return throwError();
                } else {
                    directoryParameter = actualDirectory(directory);
                }
            } else {
                directoryParameter = actualDirectory(directory);
            }
        }

        if (file != null) {
            String optional = file.getOptional();
            if (optional != null) {
                if (!"true".equalsIgnoreCase(optional)) {
                    throwError();
                } else {
                    fileParameter = actualFile(file);
                }
            } else {
                fileParameter = actualFile(file);
            }
        }

        final String data = request.getParameter("formData");
        if (data == null || data.length() == 0) {
            throw new RequiredParameterIsNullException("Parameter formData is missing in the " + "request.");
        }

        JSONObject jsonFormData = JSONObject.fromObject(data);

        String resource;
        String directoryRequestParameter = null;
        if (directoryParameter != null) {
            if (jsonFormData.has(directoryParameter)) {
                directoryRequestParameter = jsonFormData.getString(directoryParameter);
            }
        }

        String fileRequestParameter = null;
        if (fileParameter != null) {
            if (jsonFormData.has(fileParameter)) {
                fileRequestParameter = jsonFormData.getString(fileParameter);
            }
        }

        if (directoryRequestParameter == null && fileRequestParameter == null) {
            if (optionalParameters) {
                if (logger.isInfoEnabled()) {
                    logger.info("Optional parameters subContext is found. Allowing request.");
                }
                return true;
            } else {
                throw new RequiredParameterIsNullException("The request is missing " + "required " +
                        "parameters representing directory and file.");
            }
        } else if (directoryRequestParameter == null) {
            //File
            FileOperationsUtility utility = new FileOperationsUtility();
            String completePath = utility.search(solutionDirectory, fileRequestParameter);
            if (completePath == null) {
                throw new ResourceNotFoundException("The parameter " + fileRequestParameter +
                        " is not found");
            }
            resource = completePath;
        } else if (fileRequestParameter == null) {
            //Directory
            resource = solutionDirectory + File.separator + directoryRequestParameter;
            File filePath = new File(resource);
            if (!filePath.exists()) {
                throw new ResourceNotFoundException("Requested resource is not found.");
            }
            resource = getIndexFile(resource);
            if (!new File(resource).exists()) {
                return check(minimumPermissionRequired, filePath);
            }
        } else {
            resource = solutionDirectory + File.separator + directoryRequestParameter +
                    File.separator + fileRequestParameter;
        }

        if (!new File(resource).exists()) {
            throw new ResourceNotFoundException("Requested resource is not found.");
        }
        final boolean result = processRequest(minimumPermissionRequired, resource);
        if (logger.isInfoEnabled()) {
            logger.info("SubContext " + subContext + " is " + (result ? "" : " not " + "validated" +
                    "."));
        }
        return result;
    }

    private boolean check(Integer minimumPermissionRequired, File filePath) {
        return (this.root.equals(filePath)) || (this.publicResourceAccessLevel >= minimumPermissionRequired);
    }

    private boolean throwError() {
        throw new ConfigurationException("The value of formData optional parameter " + "should be" +
                " true or null");
    }

    private String actualDirectory(SubContext.Directory directory) {
        String actualDirectory;
        actualDirectory = directory.getDirectory();
        if (actualDirectory == null) {
            throw new IllegalStateException("Empty directory tag in formData.");
        }
        return actualDirectory;
    }

    private String actualFile(SubContext.File file) {
        String actualFile;
        actualFile = file.getFile();
        if (actualFile == null) {
            throw new IllegalStateException("Empty file tag in formData");
        }
        return actualFile;
    }

    private boolean areOptionalParameters(String parameters) {
        boolean optionalParameters = false;
        if (OPTIONAL_PARAMETERS.equalsIgnoreCase(parameters)) {
            optionalParameters = true;
        }
        return optionalParameters;
    }

    private SubContext findSubContext(List<SubContext> subContexts, HttpServletRequest request) {
        String type = request.getParameter("type");
        String serviceType = request.getParameter("serviceType");
        String service = request.getParameter("service");
        if (type == null || serviceType == null || service == null) {
            throw new RequiredParameterIsNullException("The request is missing either or more" + " of type, " +
                    "serviceType, service parameters.");
        }

        for (SubContext subContext : subContexts) {
            if ((type.equalsIgnoreCase(subContext.getType())) && (serviceType.equalsIgnoreCase(subContext
                    .getServiceType())) && (service.equalsIgnoreCase(subContext.getService()))) {
                return subContext;
            }
        }
        return null;
    }

    private boolean defaultLookupWithOutPermission( HttpServletRequest request,  Context urlContext,
                                                    String resource) {
        Integer minimumPermissionRequired;
        //Now get the required minimum permission
        DecisionState decisionState = getDecisionState(urlContext);

        String decisionStateParameter = decisionState.getParameter();
        String requestParameterValue = request.getParameter(decisionStateParameter);

        String defaultParameterValue = decisionState.getDefaultValue();
        Integer defaultPermission = decisionState.getDefaultPermission();

        if (requestParameterValue == null) {
            if ((defaultParameterValue == null && defaultPermission == null) || (defaultParameterValue == null ||
                    defaultPermission == null)) {
                throw new RequiredParameterIsNullException(String.format("Parameter %s is " + "missing and default " +
                        "configuration is not provided. Invalid request.", decisionStateParameter));
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Decision state parameter value is missing. Treating it " +
                            "as " + defaultParameterValue + " request.");
                }
                return processRequest(defaultPermission, resource);
            }
        }

        DecisionState.LogicalIf logicalIf = requiredIfBlock(requestParameterValue, decisionState);

        if (logicalIf == null) {
            throw new ConfigurationException(String.format("Unknown parameter value %s for " + "the parameter %s.",
                    requestParameterValue, decisionStateParameter));
        }

        Integer logicalIfPermission = logicalIf.getPermission();
        if (logicalIfPermission == null) {
            DecisionState secondLevelDecisionState = logicalIf.getDecisionState();
            if (secondLevelDecisionState == null) {
                throw new ConfigurationException("Permission level required is not configured " +
                        "properly. Expecting a decisionStare as permission is not found in the " +
                        "logical block.");
            }
            String secondLevelDecisionStateParameter = secondLevelDecisionState.getParameter();
            String secondLevelDecisionStateParameterValue = request.getParameter(secondLevelDecisionStateParameter);
            DecisionState.LogicalIf secondLevelLogicalIf = requiredIfBlock(secondLevelDecisionStateParameterValue,
                    secondLevelDecisionState);

            if (secondLevelLogicalIf == null) {
                throw new ConfigurationException(String.format("Unknown parameter " +
                                "value %s for the parameter %s. Nesting of only one " +
                                "level DecisionState is supported.", secondLevelDecisionStateParameterValue,
                        secondLevelDecisionStateParameter));
            }
            Integer secondLevelLogicalIfPermission = secondLevelLogicalIf.getPermission();
            if (secondLevelLogicalIfPermission == null) {
                throw new ConfigurationException("Required configuration permission is missing " + "from the logical " +
                        "state.");
            }
            minimumPermissionRequired = secondLevelLogicalIfPermission;
            return processRequest(minimumPermissionRequired, resource);
        } else {
            minimumPermissionRequired = logicalIfPermission;
            return processRequest(minimumPermissionRequired, resource);
        }
    }

    private DecisionState getDecisionState(Context urlContext) {
        DecisionState decisionState = urlContext.getDecisionState();
        if (decisionState == null || decisionState.getParameter() == null) {
            throw new ConfigurationException("Minimum permission is not defined at the " + "context level. Expecting " +
                    "decision state with a parameter.");
        }
        return decisionState;
    }

    private boolean normalUrlContexts( HttpServletRequest request,  Context urlContext,
                                      Integer minimumPermissionRequired, String solutionDirectory,
                                      boolean optionalParameters) {
        // LookupParameters are configured and resource is available by reading the
        // parameters
        LookupParameters lookupParameters = getLookupParameters(urlContext);

        if (isDefaultLookup(lookupParameters)) {//Expect directory and file name
            // configuration
            return defaultContext(request, optionalParameters, minimumPermissionRequired, solutionDirectory,
                    lookupParameters);
        }

        if (isDirectoryLookup(lookupParameters)) {//Expect directory configuration
            return processDirectoryLookup(request, minimumPermissionRequired, solutionDirectory, lookupParameters,
                    optionalParameters);
        } else {
            // Very unfortunate if code reaches here. Just be quiet as no such use case is
            // found so far.
            throw new IllegalStateException("A minimum permission level is configured but " + "the lookupParameters " +
                    "type is neither default nor directory.");
        }
    }

    private boolean processDirectoryLookup( HttpServletRequest request, Integer minimumPermissionRequired,
                                           String solutionDirectory,  LookupParameters lookupParameters,
                                           boolean optionalParameters) {
        LookupParameters.Parameter parameter = lookupParameters.getParameter();
        String requestParameterName = parameter.getParameter();
        String directory = request.getParameter(requestParameterName);

        if ("resultDirectory".equalsIgnoreCase(requestParameterName)) {
            if (!"true".equalsIgnoreCase(this.applicationProperties.getEnableSavedResult())) {
                return true;
            }
        }

        if (directory == null) {
            if (optionalParameters) {
                return true;
            }
            throw new RequiredParameterIsNullException(String.format("Parameter %s is missing. " + "Invalid request" +
                    ".", requestParameterName));
        }

        String resource = solutionDirectory + File.separator + directory + File.separator +
                "index." + INDEX_FILE_EXTENSION;
        File actualFile = new File(resource);
        if (actualFile.exists()) {
            return processRequest(minimumPermissionRequired, resource);
        } else {
            final String pathname = solutionDirectory + File.separator + directory;
            return check(minimumPermissionRequired, new File(pathname));
        }
    }

    private DecisionState.LogicalIf requiredIfBlock(String requestParameterValue,
                                                     DecisionState decisionState) {
        List<DecisionState.LogicalIf> logicalIfList = decisionState.getLogicalIfList();
        if (logicalIfList == null) {
            throw new ConfigurationException("DecisionState is wrongly configured. Expecting " + "logical state(if).");
        }
        for (DecisionState.LogicalIf logicalIf : logicalIfList) {
            String parameterValue = logicalIf.getParameterValue();
            if (parameterValue == null) {
                throw new ConfigurationException("The configuration of the logical state is " +
                        "wrong" + ". Missing parameter value.");
            }
            String testOperator = logicalIf.getTestOperator();
            if (testOperator == null) {
                if (parameterValue.equalsIgnoreCase(requestParameterValue)) {
                    return logicalIf;
                }
            } else {
                if (!EQUAL_TO.equalsIgnoreCase(testOperator) && !NOT_EQUAL_TO.equalsIgnoreCase(testOperator)) {
                    throw new ConfigurationException(String.format("Unknown testOperator %s. " + "Expecting only eq " +
                            "or ne.", testOperator));
                }
                if (EQUAL_TO.equalsIgnoreCase(testOperator)) {
                    if (parameterValue.equalsIgnoreCase(requestParameterValue)) {
                        return logicalIf;
                    }
                }
                if (NOT_EQUAL_TO.equalsIgnoreCase(testOperator)) {
                    if (!parameterValue.equalsIgnoreCase(requestParameterValue)) {
                        return logicalIf;
                    }
                }
            }
        }
        return null;
    }

    private LookupParameters getLookupParameters( Context urlContext) {
        LookupParameters lookupParameters = urlContext.getLookupParameters();
        if (lookupParameters == null) {//Wrong configuration
            throw new ConfigurationException("Url context is defined with permission but " +
                    "lookUpParameters are not defined. Expecting directory and/or file " +
                    "parameter configuration.");
        }
        return lookupParameters;
    }

    private boolean defaultContext( HttpServletRequest request, boolean optionalParameters,
                                   Integer minimumPermissionRequired, String solutionDirectory,
                                    LookupParameters lookupParameters) {
        String lookupParametersDirectory = lookupParameters.getDirectory();
        String directory = request.getParameter(lookupParametersDirectory);
        String fileLookUpParameter = lookupParameters.getFile();
        String file = request.getParameter(fileLookUpParameter);

        if (directory == null && file == null) {
            if (optionalParameters) {
                return true;
            } else {
                throw new RequiredParameterIsNullException(String.format("The request is missing " +
                        "" + "%s, %s.", lookupParametersDirectory, fileLookUpParameter));
            }
        } else if (directory == null || file == null) {
            throw new RequiredParameterIsNullException(String.format("The request is missing " + "either one or both " +
                    "of %s, %s.", lookupParametersDirectory, fileLookUpParameter));
        }

        String resource = solutionDirectory + File.separator + directory + File.separator +
                file;

        return processRequest(minimumPermissionRequired, resource);
    }

    private boolean isArrayLookup( LookupParameters lookupParameters) {
        return ARRAY_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }

    private boolean isDirectoryLookup( LookupParameters lookupParameters) {
        return DIRECTORY_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }

    private boolean isDefaultLookup( LookupParameters lookupParameters) {
        return DEFAULT_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }

    private boolean processRequest(Integer minimumPermissionRequired, String resource) {
        if (!SecurityUtils.isTargetReachable(new File(resource))) {
            return false;
        }
        if (minimumPermissionRequired == null) {
            throw new IllegalArgumentException("Minimum permission required is null.");
        }

        int permissionLevelFromResource = permission(resource);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Maximum permission obtained from the resource %s is %s ", resource,
                    permissionLevelFromResource));
        }
        return (permissionLevelFromResource >= minimumPermissionRequired);
    }

    private int permission(String resource) {
        JSONObject fileAsJson = this.processor.getJSONObject(resource, false);
        fileAsJson.put("absolutePath", resource);
        return maxPermissionOnResource(fileAsJson);
    }

    @Override
    public int maxPermissionOnResource(JSONObject fileAsJson) {
        IResourcePermission resourcePermission = this.factory.resourcePermission(fileAsJson);
        return resourcePermission.maximumPermissionLevelOnResource();
    }
}
package com.helicalinsight.resourcedb.processor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.management.URLContextManager;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.GroovyUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.processor.iresource.IResourceAuthenticatorDB;
import com.helicalinsight.resourcesecurity.CheckConstant;
import com.helicalinsight.resourcesecurity.IResourcePermission;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import com.helicalinsight.resourcesecurity.jaxb.DecisionState;
import com.helicalinsight.resourcesecurity.jaxb.LookupParameters;
import com.helicalinsight.resourcesecurity.jaxb.SubContext;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class ResourceAuthenticatorDB implements IResourceAuthenticatorDB, CheckConstant {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAuthenticatorDB.class);


    private static final String INDEX_FILE_EXTENSION = JsonUtils.getFolderFileExtension();

    @Autowired
    protected ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    @Autowired
    private ResourcePermissionFactory factory;
    
    @Autowired
    private URLContextManager urlContextManager;
    
    @Autowired
    private HIResourceServiceDB hiResourceServiceDB;

    private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();


    private Integer publicResourceAccessLevel;

    private Integer readWriteAccessLevel;

    private File root;

    protected String solutionDirectory;

    private Map<String, Object> resourceMap = new HashMap<String, Object>();


    @PostConstruct
    public void init() {
        this.publicResourceAccessLevel = this.resourcePermissionLevelsHolder.publicResourceAccessLevel();
        this.readWriteAccessLevel = this.resourcePermissionLevelsHolder.readWriteAccessLevel();
        this.solutionDirectory = this.applicationProperties.getSolutionDirectory();
        this.root = new File(this.solutionDirectory);
    }

    public static boolean isRoot(String url) {
        return StringUtils.isBlank(url) || url.equals("/");
    }


    @Override
    public boolean authenticate(@NotNull HttpServletRequest request, @Nullable Context urlContext) {

        if (urlContext == null) {
            throw new IllegalArgumentException("The url context is null.");
        }

        Integer minimumPermissionRequired = urlContext.getPermission();
        //Reset directory as it it request scoped.

        if (urlContext.getLookupParameters() == null && minimumPermissionRequired == null) {
            //The resource information is present in json. The mapping is /services
            return processServicesUrlRequest(request, urlContext);
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

                LookupParameters.Parameter lookupParam = lookupParameters.getParameter();
                if (defaultLookup) {
                    String directory = lookupParameters.getDirectory();
                    String file = lookupParameters.getFile();


                    String parameter = lookupParam.getParameter();
                    JsonObject jsonRequest = new JsonObject();
                    if (parameter != null && (directory != null && directory.contains(".")) || (file != null && file.contains("."))) {
                        String jsonString = request.getParameter(parameter);
                        jsonRequest = JsonParser.parseString(jsonString).getAsJsonObject();
                    }
                    String directoryName = directory != null && directory.contains(".") ? getMultilevelFormData(directory, jsonRequest) : request.getParameter(directory);
                    String fileName = file != null && file.contains(".") ? getMultilevelFormData(file, jsonRequest) : request.getParameter(file);


                    String resource = this.solutionDirectory + File.separator + directoryName;
                    if (StringUtils.isNoneBlank(fileName)) {
                        resource = resource + File.separator + fileName;
                    }
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

                    LookupParameters.Parameter parameter = lookupParam;
                    String parameterName = parameter.getParameter();
                    String requestParameterValue = request.getParameter(parameterName);
                    if (!"java.lang.String".equalsIgnoreCase(parameter.getDataType())) {
                        return error();
                    }

                    JsonElement source = new Gson().fromJson(requestParameterValue,JsonElement.class);
                    if (source instanceof JsonArray) {
                        JsonArray jsonArray = (JsonArray) source;
                        try {
                            if (!processArray(request, urlContext, jsonArray)) {
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
        if (permission(resourceMap) < this.publicResourceAccessLevel) {
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
                    result = processRequest(this.readWriteAccessLevel, resourceMap);
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
            return processRequest(this.readWriteAccessLevel, resourceMap);
        }
    }

    private void log(Context urlContext, boolean result) {
        if (logger.isInfoEnabled()) {
            logger.info("The url context is " + urlContext + ". The result of verification is " +
                    (result ? "successful." : "not successful."));
        }
    }

    private boolean processArray(HttpServletRequest request, Context urlContext, JsonArray jsonArray) {
        String file = "";
        for (Object object : jsonArray) {
            if (object instanceof String) {
                file = (String) object;
            } else if (object instanceof JsonArray) {
                JsonArray array = (JsonArray) object;
                file = (String) array.get(0).getAsString();
            }


            if (StringUtils.isEmpty(file)) {
                return true;
            }

            Integer minimumPermission = logicalIfPermission(request, urlContext);
            if (minimumPermission == null) {
                throw new IllegalArgumentException("Minimum permission required is null.");
            }
            Integer permissionResource = null;
            Map<String, Object> resourcePermission =  getPermissionMap(file);  
            if (null != resourcePermission.get(file) && !resourcePermission.get(file).equals("null")) {
                permissionResource = Integer.valueOf("" + resourcePermission.get(file));
            }
            return null != permissionResource && permissionResource >= minimumPermission;
        }
        return false;
    }


    protected boolean processRequest(@Nullable Integer minimumPermissionRequired, Map<String, Object> resourceMap) {
        //use Security Utils

        if (!SecurityUtilsDB.isTargetReachable(resourceMap)) {
            return false;
        }
        if (minimumPermissionRequired == null) {
            throw new IllegalArgumentException("Minimum permission required is null.");
        }

        int permissionLevelFromResource = permission(resourceMap);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Maximum permission obtained from the resource %s is %s ", resourceMap,
                    permissionLevelFromResource));
        }
        return (permissionLevelFromResource >= minimumPermissionRequired);
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

    protected String getIndexFile(String resource) {
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
                    return processRequest(this.readWriteAccessLevel, resourceMap);
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

    private boolean processServicesUrlRequest(HttpServletRequest request, Context urlContext) {
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

        final String data = request.getParameter("formData");
        if (data == null || data.length() == 0) {
            throw new RequiredParameterIsNullException("Parameter formData is missing in the " + "request.");
        }
        return checkPermission(data, urlContext, subContext);
    }
    
    public Boolean checkPermission(String data, Context urlContext, SubContext subContext) {
    	
    	  JsonObject jsonFormData = JsonParser.parseString(data).getAsJsonObject();
          Integer permissionLevelFromResource = null;
          String directory = "dir";
          String filest = "file";
          String lookdir = subContext.getFormData().getDirectory().getDirectory();
          directory = lookdir != null ? lookdir : directory;
          String lookfile = subContext.getFormData().getFile().getFile();
          filest = lookfile != null ? lookfile : filest;
          JsonElement dir = jsonFormData.get(directory);
          dir = dir== null ? jsonFormData.get("newLocation"):dir;
          LookupParameters lookupParameters = urlContext.getLookupParameters();
          boolean dataSourceLookup = lookupParameters==null?"true".equals(subContext.getIsDataSource()):isDataSourceLookup(lookupParameters);

          if (dataSourceLookup) {
              return evaluatePermission(subContext, jsonFormData);
          }

    	
    	boolean entered=false;
        String dirFile=null;
           if (null != GsonUtility.optStringValue(jsonFormData,filest, null) && !dataSourceLookup) {
               String file = GsonUtility.optString(jsonFormData,filest);
               if(file.equalsIgnoreCase("true")) return  true;
   			dirFile=dir.getAsString() + "/" + file;
   			Map<String,Object> resourcePermission =  getPermissionMap(dirFile);
               Object o = resourcePermission.get(dirFile);
               if (o != null)
                   permissionLevelFromResource = Integer.valueOf("" + o);
               entered=true;
           }
           if (null != dir && permissionLevelFromResource == null && !dataSourceLookup) {
        	   dirFile= dir.getAsString();
        	   Map<String,Object> resourcePermission =  getPermissionMap(dirFile);
               Object o = resourcePermission.get(dirFile);
               if (o != null)
                   permissionLevelFromResource = Integer.valueOf("" + o);
               entered=true;
           }
           if(permissionLevelFromResource==null && !entered) return  true;
           boolean isCore = subContext.getType().equalsIgnoreCase("core");
           boolean isShare = subContext.getServiceType().equalsIgnoreCase("share");
           boolean isPublic = permissionLevelFromResource != null
                   && permissionLevelFromResource.equals(publicResourceAccessLevel);
           final String isPub=dirFile;
   if(isPublic){
       HIResource hiResource =  hiResourceServiceDB.getResourceByUrl(isPub,true);
       isPublic=hiResource.getCreatedBy()==null;
   }
           if (isCore && isShare && isPublic) {
   			throw new EfwServiceException("Public resource can not be shared.");
   		}
           return permissionLevelFromResource != null && permissionLevelFromResource >= subContext.getPermission();
    	
    }   

    protected boolean check(Integer minimumPermissionRequired, File filePath) {
        return (this.root.equals(filePath)) || (this.publicResourceAccessLevel >= minimumPermissionRequired);
    }


    protected boolean areOptionalParameters(String parameters) {
        boolean optionalParameters = false;
        if (CheckConstant.OPTIONAL_PARAMETERS.equalsIgnoreCase(parameters)) {
            optionalParameters = true;
        }
        return optionalParameters;
    }

    private SubContext findSubContext(List<SubContext> subContexts, HttpServletRequest request) {
        String type = request.getParameter("type");
        String serviceType = request.getParameter("serviceType");
        String service = request.getParameter("service");
        
        return urlContextManager.findSubContext(subContexts, type, serviceType, service);
    }

    private boolean defaultLookupWithOutPermission(@NotNull HttpServletRequest request, @NotNull Context urlContext,
                                                   @NotNull String resource) {
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
                return processRequest(defaultPermission, resourceMap);
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
            return processRequest(minimumPermissionRequired, resourceMap);
        } else {
            minimumPermissionRequired = logicalIfPermission;
            return processRequest(minimumPermissionRequired, resourceMap);
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

    private boolean normalUrlContexts(@NotNull HttpServletRequest request, @NotNull Context urlContext,
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
        if (isDataSourceLookup(lookupParameters)) {
            JsonObject jsonFormData = new JsonObject();
            jsonFormData.addProperty("access", this.resourcePermissionLevelsHolder.getAccessName(minimumPermissionRequired).replace("AccessLevel", ""));
            DataSourceSecurityUtility.isDataSourceAuthenticated(jsonFormData);
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

    private boolean processDirectoryLookup(@NotNull HttpServletRequest request, Integer minimumPermissionRequired,
                                           String solutionDirectory, @NotNull LookupParameters lookupParameters,
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
            return processRequest(minimumPermissionRequired, resourceMap);
        } else {
            final String pathname = solutionDirectory + File.separator + directory;
            return check(minimumPermissionRequired, new File(pathname));
        }
    }

    @Nullable
    private DecisionState.LogicalIf requiredIfBlock(String requestParameterValue,
                                                    @NotNull DecisionState decisionState) {
        List<DecisionState.LogicalIf> logicalIfList = decisionState.getLogicalIfList();
        if (logicalIfList == null) {
            throw new ConfigurationException("DecisionState is wrongly configured. Expecting " + "logical state(if).");
        }
        for (DecisionState.LogicalIf logicalIf : logicalIfList) {
            String parameterValue = logicalIf.getParameterValue();
            if (parameterValue == null) {
                throw new ConfigurationException("The configuration of the logical state is " +
                        "wrong" + ". Missing parameter value");
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

    protected LookupParameters getLookupParameters(@NotNull Context urlContext) {
        LookupParameters lookupParameters = urlContext.getLookupParameters();
        if (lookupParameters == null) {//Wrong configuration
            throw new ConfigurationException("Url context is defined with permission but " +
                    "lookUpParameters are not defined. Expecting directory and/or file " +
                    "parameter configuration.");
        }
        return lookupParameters;
    }

    private boolean defaultContext(@NotNull HttpServletRequest request, boolean optionalParameters,
                                   Integer minimumPermissionRequired, String solutionDirectory,
                                   @NotNull LookupParameters lookupParameters) {
        String lookupParametersDirectory = lookupParameters.getDirectory();
        String directory = request.getParameter(lookupParametersDirectory);
        String fileLookUpParameter = lookupParameters.getFile();
        String file = request.getParameter(fileLookUpParameter);

        LookupParameters.Parameter lookupParametersParameter = lookupParameters.getParameter();
        String parameter = lookupParametersParameter != null ? lookupParametersParameter.getParameter() : null;
        JsonObject jsonRequest = new JsonObject();
        if (parameter != null && (lookupParametersDirectory != null && lookupParametersDirectory.contains(".")) || (fileLookUpParameter != null && fileLookUpParameter.contains("."))) {
            String jsonString = request.getParameter(parameter);
            jsonRequest.add(parameter, JsonParser.parseString(jsonString).getAsJsonObject());
        }
        directory = lookupParametersDirectory != null && lookupParametersDirectory.contains(".") ? getMultilevelFormData(lookupParametersDirectory, jsonRequest) : directory;
        file = fileLookUpParameter != null && fileLookUpParameter.contains(".") ? getMultilevelFormData(fileLookUpParameter, jsonRequest) : file;


        if (StringUtils.isBlank(directory) && StringUtils.isBlank(file)) {
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

        String resource = solutionDirectory + File.separator + directory;
        if (StringUtils.isNoneBlank(file)) {
            resource = resource + File.separator + file;
        }


        return processRequest(minimumPermissionRequired, resourceMap);
    }

    private boolean isArrayLookup(@NotNull LookupParameters lookupParameters) {
        return ARRAY_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }

    private boolean isDirectoryLookup(@NotNull LookupParameters lookupParameters) {
        return DIRECTORY_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }

    private boolean isDataSourceLookup(@NotNull LookupParameters lookupParameters) {
        return DATASOURCE_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }

    private boolean isDefaultLookup(@NotNull LookupParameters lookupParameters) {
        return DEFAULT_LOOKUP.equalsIgnoreCase(lookupParameters.getType());
    }


    private int permission(Map<String, Object> resourceMap) {
        return maxPermissionOnResource(resourceMap);
    }

    //TODO need to change
    public int maxPermissionOnResource(Map<String, Object> resourceMap) {
        IResourcePermission resourcePermission = this.factory.resourcePermissionDB(resourceMap);
        return resourcePermission.maximumPermissionLevelOnResource();
    }

    public boolean evaluatePermission(SubContext subContext, JsonObject jsonFormData) {

        Integer minimumPermissionRequired = subContext.getPermission();
        SubContext.FormData formData = subContext.getFormData();
        if (jsonFormData == null) {
            throw new ConfigurationException("The lookupParameters and permission are not " + "configured. Expecting " +
                    "subContexts with formData.");
        }
        /*return permissionLevelFromResource >= minimumPermissionRequired;*/
        SubContext.Directory directory = formData.getDirectory();
        SubContext.File file = formData.getFile();

        String directoryParameter = null;
        String fileParameter = null;


        if ("true".equalsIgnoreCase(subContext.getIsDataSource())) {
            if (jsonFormData.has("directory") && !jsonFormData.has("dir")) {
                jsonFormData.addProperty("dir", jsonFormData.get("directory").getAsString());
            }

            jsonFormData.addProperty("access", this.resourcePermissionLevelsHolder.getAccessName(minimumPermissionRequired).replace("AccessLevel", ""));
            try {
                String lookupKey = subContext.getLookupKey();
                if (StringUtils.isNoneBlank(lookupKey)) {
                    JsonObject jsonObject = jsonFormData.getAsJsonObject(lookupKey);
                    jsonObject.add("access", jsonFormData.get("access"));
                    DataSourceSecurityUtility.isDataSourceAuthenticated(jsonObject);
                } else {
                    DataSourceSecurityUtility.isDataSourceAuthenticated(jsonFormData);
                }
            } catch (AccessDeniedException ex) {
                return false;
            }
        }


        if (directory != null) {
            String optional = directory.getOptional();
            if (optional != null && !"true".equalsIgnoreCase(optional)) {
                return throwError();
            }
            directoryParameter = actualDirectory(directory);

        }

        if (file != null) {
            String optional = file.getOptional();
            if (optional != null && !"true".equalsIgnoreCase(optional)) {
                return throwError();
            }
            fileParameter = actualFile(file);
        }


        String resource;
        String directoryRequestParameter = null;
        if (directoryParameter != null) {
            if (jsonFormData.has(directoryParameter) || directoryParameter.contains(".")) {
                directoryRequestParameter = getMultilevelFormData(directoryParameter, jsonFormData);
            }
        }

        String fileRequestParameter = null;
        if (fileParameter != null) {
            if (jsonFormData.has(fileParameter) || fileParameter.contains(".")) {
                fileRequestParameter = getMultilevelFormData(fileParameter, jsonFormData);
                String extension = FileUtils.getExtension(fileRequestParameter);
                if (StringUtils.isBlank(extension) && StringUtils.isNotBlank(fileRequestParameter)) {
                    extension = file.getDefaultExtension();
                    fileRequestParameter = fileRequestParameter + "." + extension;
                }
            }
        }

        if (StringUtils.isBlank(directoryRequestParameter) && StringUtils.isBlank(fileRequestParameter)) {
            if (areOptionalParameters(subContext.getParameters())) {
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
                return true;
                //throw new ResourceNotFoundException("The parameter " + fileRequestParameter +
                //       " is not found");
            }
            resource = completePath;
        } else if (fileRequestParameter == null) {
            //Directory
            resource = directoryRequestParameter;
            File filePath = new File(resource);
            // Throw error only if file is not optional.
            if (!filePath.exists() && !"true".equalsIgnoreCase(file.getOptional())) {
                throw new ResourceNotFoundException("Requested resource is not found.");
            }
            String dirPath = GsonUtility.optString(jsonFormData,"directory");
            String filePath2 = GsonUtility.optString(jsonFormData,"file");
            String url = String.join(dirPath, filePath2);
            url = FilenameUtils.getFullPathNoEndSeparator(url);
            if(StringUtils.isBlank(filePath2)){
                url=dirPath;
            }
            if (StringUtils.isNotBlank(url)) {
            	Map<String,Object> permissionLevelMap =  getPermissionMap(url);
            	Object o = permissionLevelMap.get(url);
            	if(o != null  && (Integer.parseInt(o.toString())>=minimumPermissionRequired)) {
            		return true;
            	}
            	return false;
            }
            else {
            	return true;
            }
        } else {
            resource = solutionDirectory + File.separator + directoryRequestParameter +
                    File.separator + fileRequestParameter;
        }

        if (!new File(resource).exists()) {
            throw new ResourceNotFoundException("Requested resource is not found.");
        }
        final boolean result = processRequest(minimumPermissionRequired, resourceMap);
        if (logger.isInfoEnabled()) {
            logger.info("SubContext " + subContext + " is " + (result ? "" : " not " + "validated" +
                    "."));
        }
        //return result;


        DecisionState decisionState = subContext.getDecisionState();
        if (decisionState != null) {
            String requestParameter = decisionState.getParameter();
            String isScripted = isScripted(jsonFormData, decisionState);
            if (!"noScript".equals(isScripted)) {
                return Boolean.valueOf(isScripted);
            }


            List<DecisionState.LogicalIf> logicalIfList = decisionState.getLogicalIfList();
            if (logicalIfList == null) {
                throw new ConfigurationException("DecisionState is wrongly configured. Expecting " + "logical state(if).");
            }


            String requestParameterValue = jsonFormData.get(requestParameter).getAsString();
            DecisionState.LogicalIf logicalIf = getLogicalIf(logicalIfList, requestParameterValue, jsonFormData);
            if (logicalIf != null) {
                Integer logicalIfPermission = logicalIf.getPermission();
                return processRequest(logicalIfPermission, resourceMap);
            }


        }

        return result;

    }

    public String getMultilevelFormData(String requestParameter, JsonObject jsonFormData) {
        if (requestParameter == null) {
            return "";
        }
        String split[] = requestParameter.split("\\.");
        JsonObject jsonObject = JsonParser.parseString(jsonFormData.toString()).getAsJsonObject();
        JsonElement object = null;
        for (String splitItem : split) {
            object = jsonObject.get(splitItem);
            if (object instanceof JsonObject) {
                jsonObject = jsonObject.getAsJsonObject(splitItem);
            }
        }
        return object == null ? "" : object.getAsString();
    }

    private String isScripted(JsonObject formData, DecisionState decisionState) {
        DecisionState.Script requiredScript = decisionState.getRequiredScript();
        if (requiredScript == null) {
            return "noScript";
        }
        JsonObject scriptJson = new JsonObject();
        scriptJson.addProperty("#text", requiredScript.getCode());
        scriptJson.addProperty("@name", "authorize");
        scriptJson.addProperty("@language", requiredScript.getLanguage());
        JsonObject responseJson = new JsonObject();
        GroovyUtils.validateJson(scriptJson, formData, responseJson);
        JsonObject result = responseJson.getAsJsonObject("result");
        int permission = result.get("permission").getAsInt();
        return processRequest(permission, resourceMap) + "";
    }

    public DecisionState.LogicalIf getLogicalIf(List<DecisionState.LogicalIf> logicalIfList, String requestParameterValue, JsonObject jsonFormData) {
        for (DecisionState.LogicalIf logicalIf : logicalIfList) {
            String parameterValue = logicalIf.getParameterValue();
            String hasKey = logicalIf.getHasKey();
            if (parameterValue == null) {
                throw new ConfigurationException("The configuration of the logical state is " +
                        "wrong" + ". Missing parameter value.");
            }
            if (hasKey != null) {
                if (jsonFormData.has(hasKey)) {
                    return logicalIf;
                }
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
    private final Map<String, Object> getPermissionMap(String url) {
    	long start = System.currentTimeMillis();
    	HIResourceOfActiveUser allResources = hiResourceServiceDB.findAllResources(url);
        Map<String,Object> permissionMap =   allResources.getResourcePermission();
        long end = System.currentTimeMillis();
        logger.debug("Time taken to fetch the permission map : {}" , ((end-start)/1000.0));
        return permissionMap;
    } 
}

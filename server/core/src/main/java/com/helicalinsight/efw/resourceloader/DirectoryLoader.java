package com.helicalinsight.efw.resourceloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.admin.model.FileBrowserCache;
import com.helicalinsight.admin.service.FileBrowserCacheService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;

/**
 * This class is responsible for loading all the solution directory(EFW). The
 * files and folders information is put in a json format and sent to the caller.
 * The json excludes System and Images directories.
 *
 * @author Rajasekhar
 * @author Rajesh.
 * @version 3.0
 * @since 1.0
 * <p>
 * Updated the code used Cache for FileBrowser instead of File system.
 */
public class DirectoryLoader {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryLoader.class);

    private final IProcessor processor = ResourceProcessorFactory.getIProcessor();

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    private final String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();

    private final int noAccessLevel;
    private final ResourcePermissionFactory factory;
    /**
     * The map of visible extensions in the setting.xml
     */
    private Map<String, List<String>> visibleExtensionsMap;
    /**
     * Whether to apply folder specific rules
     */
    private boolean useFolderRules;
    /**
     * Map of folders tag of setting.xml(child of extensions)
     */
    private Map<String, List<String>> foldersTagMap;
    /**
     * The json of generic rules from setting.xml
     */
    private JsonObject genericRulesJSON;
    /**
     * The json of generic rules from setting.xml
     */
    private JsonArray genericRulesJSONArray;
    /**
     * Indicates the type of property
     */
    private boolean multipleGenericRules;
    /**
     * Indicates to whether to apply generic rules or not
     */
    private boolean isGenericRulePresent;
    /**
     * For processing purposes; a handy data structure
     */
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private List<String> genericRulesList;

    /**
     * Instantiates the settingsLoader property and prepares the <code>java.util.Map</code>s
     * foldersTagMap and the visibleExtensionsMap
     */
    public DirectoryLoader(JsonObject visibleExtensions) {
        // Prepare the list and map for iterations
        factory = new ResourcePermissionFactory();
        this.resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
        this.noAccessLevel = resourcePermissionLevelsHolder.readAccessLevel();
        prepareExtensionsMap(visibleExtensions);
        prepareFoldersTagMap(visibleExtensions);
    }

    /**
     * Prepares the member variable visibleExtensionsMap
     *
     * @param visibleExtensions The JSON from the setting.xml for which the visible attribute
     *                          is provided to be true
     */
    private void prepareExtensionsMap(JsonObject visibleExtensions) {
        Assert.notNull(visibleExtensions, "Visible extension's JSON is null!");
        Iterator<?> iterator = visibleExtensions.keySet().iterator();
        this.visibleExtensionsMap = new HashMap<>();
        while (iterator.hasNext()) {
            int counter = 0;
            String key = (String) iterator.next();
            try {
                iterate(visibleExtensions, counter, key);
            } catch (JsonSyntaxException ex) {
                // If the key is not a JSON object move to the next key
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("List of visible extensions that will be viewable " +
                    "in the repository is " +
                    visibleExtensionsMap);
        }
    }

    /**
     * Prepares the member variables foldersTagMap and generic rules related
     * member variables
     *
     * @param visibleExtensions The JSON from the setting.xml for which the visible attribute
     *                          is provided to be true
     */
    private void prepareFoldersTagMap(JsonObject visibleExtensions) {
        Assert.notNull(visibleExtensions, "Visible extension's JSON is null!");
        try {
            JsonObject foldersJSONObject = initialize(visibleExtensions);
            /*
             * genericRulesJSON has been prepared. Now prepare the foldersTagMap
			 */
            prepareTextAndRuleValuesList(foldersJSONObject);
        } catch (JsonSyntaxException ignore) {
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The configured folder rule setting is " + foldersTagMap);
        }
    }

    /**
     * Prepares the visibleExtensionsMap
     *
     * @param visibleExtensions The JSON from the setting.xml for which the visible attribute is
     *                          provided to be true
     * @param counter           Loop counter
     * @param key               A string
     */
    private void iterate(JsonObject visibleExtensions, int counter, String key) {
        /*
         * visible attribute is provided. Hence no scope for
         * JSONException as it is a JSON object
         */
        JsonObject json = visibleExtensions.getAsJsonObject(key);
        try {
            List<String> textAndRuleValuesList = new ArrayList<>();
            /*
             * Look for the visible tag's rule implementation if it is
             * not null. If rule is not provided move to the catch block
             * as there will be an exception
             */
            try {
                if (json.get("rule").getAsString() != null) {
                    /*
                     * Whether or not rule attribute is provided text
                     * value must have been provided. Place the key's
                     * text value in the list
                     */
                    textAndRuleValuesList.add(counter, json.get("").getAsString());
                    // Increment the counter
                    counter = counter + 1;
                    /*
                     * Add the rule attribute value as the second index
                     * in the list
                     */
                    textAndRuleValuesList.add(counter, json.get("rule").getAsString());
                    visibleExtensionsMap.put(key, textAndRuleValuesList);
                }
            } catch (JsonSyntaxException ex) {
                textAndRuleValuesList.add(counter, json.get("").getAsString());
                counter = counter + 1;
                // No rule attribute is provided. Add null to the list
                textAndRuleValuesList.add(counter, null);
                visibleExtensionsMap.put(key, textAndRuleValuesList);
            }
        } catch (JsonSyntaxException ignore) {
        }
    }

    /**
     * Sets the booleans related to generic rules, prepares the genericRulesArray or
     * genericRulesJSON accordingly.
     * Returns the foldersTagMap.
     *
     * @param visibleExtensions The JSON from the setting.xml for which the visible attribute is
     *                          provided to be true
     * @return JSONObject of folder tag in setting.xml
     */
    private JsonObject initialize(JsonObject visibleExtensions) {
        JsonObject foldersJSONObject = visibleExtensions.getAsJsonObject("folder");
        this.foldersTagMap = new HashMap<>();
        useFolderRules = true;
        try {
            genericRulesJSONArray = foldersJSONObject.getAsJsonArray("generic");
            isGenericRulePresent = true;
            multipleGenericRules = true;
        } catch (JsonSyntaxException ex) {
            try {
                genericRulesJSON = new JsonObject();
                genericRulesJSON.addProperty("generic", foldersJSONObject.get("generic").getAsString());
            } catch (JsonSyntaxException ignore) {
            }
        }
        return foldersJSONObject;
    }

    /**
     * Prepares the textAndRuleValuesList of this object
     *
     * @param foldersJSONObject The json of folder tag from setting.xml
     */
    private void prepareTextAndRuleValuesList(JsonObject foldersJSONObject) {
        Iterator<?> iterator = foldersJSONObject.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            int counter = 0;
            if (!"generic".equalsIgnoreCase(key) && !"visible".equalsIgnoreCase(key)) {
                JsonObject json = foldersJSONObject.getAsJsonObject(key);
                List<String> textAndRuleValuesList = new ArrayList<>();
                try {
                    textAndRuleValuesList.add(counter, json.get("").getAsString());
                    counter = counter + 1;
                    try {
                        textAndRuleValuesList.add(counter, json.get("rule").getAsString());
                    } catch (JsonSyntaxException ignore) {
                        textAndRuleValuesList.add(counter, null);
                    }
                } catch (JsonSyntaxException ignore) {
                    textAndRuleValuesList.add(counter, null);
                    counter = counter + 1;
                    textAndRuleValuesList.add(counter, null);
                    continue;
                }
                foldersTagMap.put(key, textAndRuleValuesList);
            }
        }
    }

    /**
     * Returns the content of the solution directory
     *
     * @param rootPath Usually EFW solution directory
     * @return The content of the solution directory
     */
    public List<Map<String, String>> getSolutionDirectory(String rootPath, boolean isRequestedRecursive, boolean discardEmptyFolders) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        if (JsonUtils.isFileBrowserCacheEnabled()) {
            return getSolutionDirectoryCache(rootPath, isRequestedRecursive, discardEmptyFolders);
        } else {
            return getSolutionDirectoryPlain(rootPath, isRequestedRecursive);
        }
    }

    public List<Map<String, String>> getSolutionDirectoryPlain(String rootPath, boolean isRequestedRecursive) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        if (multipleGenericRules) {
            this.genericRulesList = getGenericRulesList(genericRulesJSONArray);
        } else {
            this.genericRulesList = getGenericRulesList(genericRulesJSON);
        }

        List<Map<String, String>> foldersAndFiles = getFoldersAndFiles(rootPath, isRequestedRecursive);

        File file = new File(rootPath);
        List<Map<String, String>> firstDirectory = new ArrayList<>();

        if (file.isDirectory() && !file.getAbsolutePath().equalsIgnoreCase(ApplicationProperties.getInstance()
                .getSolutionDirectory())) {

            processDirectory(firstDirectory, file, false);
            Map<String, String> indexMap = firstDirectory.get(0);
            indexMap.put("children", ApplicationUtilities.getJSONArray(foldersAndFiles).toString());
            return firstDirectory;
        } else {
            return foldersAndFiles;
        }

    }

    public List<Map<String, String>> getSolutionDirectoryCache(String rootPath, boolean isRequestedRecursive, boolean discardEmptyFolders) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        if (multipleGenericRules) {
            this.genericRulesList = getGenericRulesList(genericRulesJSONArray);
        } else {
            this.genericRulesList = getGenericRulesList(genericRulesJSON);
        }
        long now = System.currentTimeMillis();
        logger.debug("[method :getSolutionDirectory,path :" + rootPath + ",startTime :" + now + "]");
        List<Map<String, String>> foldersAndFiles = getFoldersAndFiles(rootPath, isRequestedRecursive, null, null, discardEmptyFolders);
        long endTime = System.currentTimeMillis();
        logger.debug("[method :getSolutionDirectory,path :" + rootPath + ",endTime :" + endTime + "]");
        logger.debug("[method :getSolutionDirectory,path :" + rootPath + ",totalTime :" + (endTime - now) + "]");

        File file = new File(rootPath);
        List<Map<String, String>> firstDirectory = new ArrayList<>();

        if (file.isDirectory() && !file.getAbsolutePath().equalsIgnoreCase(ApplicationProperties.getInstance()
                .getSolutionDirectory())) {
            String relativeSolutionPath = ApplicationUtilities.getRelativeSolutionPath(rootPath);
            FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
            FileBrowserCache fileBrowserCache = fileBrowserCacheService.findFileBrowserCache(relativeSolutionPath.replaceAll("\\\\", "/"));
            processDirectory(firstDirectory, fileBrowserCache, false, false, false);
            Map<String, String> indexMap = new HashMap<>();
            if (firstDirectory.size() > 0)
                indexMap = firstDirectory.get(0);
            indexMap.put("children", ApplicationUtilities.getJSONArray(foldersAndFiles).toString());
            return firstDirectory;
        } else {
            return foldersAndFiles;
        }

    }

    /**
     * Prepares the list of solution directory content
     *
     * @param rootPath Usually EFW solution directory
     * @return The content of the solution directory
     */
    private List<Map<String, String>> getFoldersAndFiles(String rootPath, boolean isRequestedRecursive, List<FileBrowserCache> allFileBrowserCaches, FileBrowserCache theFile, boolean discardEmptyFolder) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        String solutionDirectoryPath = ApplicationProperties.getInstance().getSolutionDirectory();
        FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
        String relativeSolutionPath = ApplicationUtilities.getRelativeSolutionPath(rootPath);

        boolean directory;
        int parentId = 0;
        if (rootPath.equals(solutionDirectoryPath)/* || rootPath.equals(solutionDirectoryPath + "\\") || rootPath.equals(solutionDirectoryPath + "/") || rootPath.equals(solutionDirectoryPath + "\\/") || rootPath.equals(solutionDirectoryPath + "\\\\") || rootPath.equals(solutionDirectoryPath + "\\//")*/) {
            directory = true;
        } else {
            if (theFile == null) {
                if (relativeSolutionPath == null) {
                    throw new EfwException("invalid path please check your path.");
                }
                long startTime = System.currentTimeMillis();
                logger.debug("[method :getFoldersAndFiles,path :" + rootPath + " ,action: retrieveFromDatabase ,startTime :" + startTime + "]");
                FileBrowserCache fileBrowserCache = fileBrowserCacheService.findFileBrowserCache(relativeSolutionPath.replaceAll("\\\\", "/"));
                if (fileBrowserCache == null) {
                    throw new EfwException("invalid path please check your path. :" + relativeSolutionPath.replaceAll("\\\\", "/"));
                }
                long totalTime = (System.currentTimeMillis() - startTime);
                logger.debug("[method :getFoldersAndFiles,path :" + rootPath + " ,action: retrieveFromDatabase ,totalTime :" + totalTime + "]");
                directory = fileBrowserCache.getFileType().equals("FOLDER");
                parentId = fileBrowserCache.getId();
            } else {
                parentId = theFile.getId();
                directory = theFile.getFileType().equals("FOLDER");
            }

        }
        List<Map<String, String>> listOfFoldersAndFiles = new ArrayList<>();

        if (directory) {
            if (allFileBrowserCaches == null) {
                long startTime = System.currentTimeMillis();
                logger.debug("[method :getFoldersAndFiles,path :" + rootPath + ", parentId : " + parentId + " ,action: retrieveFromDatabase ,startTime :" + startTime + "]");
                allFileBrowserCaches = fileBrowserCacheService.getAllFileBrowserCaches(parentId);
                long totalTime = (System.currentTimeMillis() - startTime);
                logger.debug("[method :getFoldersAndFiles,path :" + rootPath + " ,action: retrieveFromDatabase ,totalTime :" + totalTime + "]");
                //split and join
                listOfFoldersAndFiles = prepareListOfMaps(allFileBrowserCaches, isRequestedRecursive, discardEmptyFolder);
            } else {
                listOfFoldersAndFiles = prepareListOfMaps(allFileBrowserCaches, isRequestedRecursive, discardEmptyFolder);
            }
        } else {
            FileBrowserCache fileBrowserCache = fileBrowserCacheService.findFileBrowserCache(relativeSolutionPath.replaceAll("\\\\", "/"));
            if (fileBrowserCache.getFileType().equals("FILE")) {
                processFile(listOfFoldersAndFiles, fileBrowserCache, false, false);
            }
        }
        return listOfFoldersAndFiles;
    }

    private List<Map<String, String>> getFoldersAndFiles(String rootPath, boolean isRequestedRecursive) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        File rootPathFile = new File(rootPath);
        List<Map<String, String>> listOfFoldersAndFiles = new ArrayList<>();
        if (rootPathFile.isDirectory()) {
            File[] files = rootPathFile.listFiles();
            listOfFoldersAndFiles = prepareListOfMaps(files, isRequestedRecursive);
        } else if (rootPathFile.isFile()) {
            processFile(listOfFoldersAndFiles, rootPathFile);
        }
        return listOfFoldersAndFiles;
    }

    /**
     * Prepares the list of solution directory content
     *
     * @param files The files and folders in solution directory
     * @return The list of maps
     */
    private List<Map<String, String>> prepareListOfMaps(File[] files, boolean isRequestedRecursive) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        List<Map<String, String>> listOfFoldersAndFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                processFile(listOfFoldersAndFiles, file);
            } else {
                String name = file.getName();
                if (file.isDirectory() && !name.equalsIgnoreCase("system") && !name.equalsIgnoreCase("images")) {
                    processDirectory(listOfFoldersAndFiles, file, isRequestedRecursive);
                }
            }
        }
        return listOfFoldersAndFiles;
    }

    public List<Map<String, String>> prepareListOfMaps(List<FileBrowserCache> files, boolean isRequestedRecursive, boolean discardEmptyFolder) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        List<Map<String, String>> listOfFoldersAndFiles = new ArrayList<>();
        for (FileBrowserCache eachFile : files) {
            if (eachFile.getFileType().equals("FILE")) {
                processFile(listOfFoldersAndFiles, eachFile, false, false);
            } else {
                String name = eachFile.getFileName();
                if (eachFile.getFileType().equals("FOLDER") && !name.equalsIgnoreCase("system") && !name.equalsIgnoreCase("images")) {
                    processDirectory(listOfFoldersAndFiles, eachFile, isRequestedRecursive, false, discardEmptyFolder);
                }
            }
        }
        return listOfFoldersAndFiles;
    }

    public List<Map<String, String>> prepareOnlyPermission(List<FileBrowserCache> files, boolean isRequestedRecursive, boolean isForReportStats) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        List<Map<String, String>> listOfFoldersAndFiles = new ArrayList<>();
        FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
        Map<String, Boolean> fileFolderCheckMap = new HashMap<>();
        for (FileBrowserCache aFile : files) {
            String filePath = aFile.getFilePath();
            boolean flag = isForReportStats ? true : checkParent(fileBrowserCacheService, filePath, fileFolderCheckMap);
            if (flag) {
                String fileType = aFile.getFileType();
                if ("FILE".equals(fileType)) {
                    processFile(listOfFoldersAndFiles, aFile, true, isForReportStats);
                } else {
                    String name = aFile.getFileName();
                    if ("FOLDER".equals(fileType) && !"system".equalsIgnoreCase(name) && !"images".equalsIgnoreCase(name)) {
                        processDirectory(listOfFoldersAndFiles, aFile, isRequestedRecursive, true, false);
                    }
                }
            }
        }
        //logger.error("map  :" + fileFolderCheckMap);
        return listOfFoldersAndFiles;
    }

    /* private boolean checkParent(FileBrowserCacheService fileBrowserCacheService, String pathHierarchy) {
         while (pathHierarchy.contains("/")) {
             pathHierarchy = pathHierarchy.substring(0, pathHierarchy.lastIndexOf("/"));
             FileBrowserCache fileBrowserModel = fileBrowserCacheService.findFileBrowserCache(pathHierarchy);
             if (fileBrowserModel != null) {
                 if (!"PUBLIC".equals(fileBrowserModel.getFolderType())) {
                     if (!checkParentPermission(fileBrowserModel)) {
                         return false;
                     }
                 }
             }
         }

         return true;
     }*/
    private boolean checkParent(FileBrowserCacheService fileBrowserCacheService, String pathHierarchy, Map<String, Boolean> fileFolderCheckMap) {
        List<String> listOfPaths = new ArrayList<>();
        List<String> paths = Arrays.asList(pathHierarchy.split("/"));
        pathHierarchy = paths.get(0);
        listOfPaths.add(pathHierarchy);
        for (int index = 1; index < paths.size(); index++) {
            pathHierarchy = pathHierarchy + "/" + paths.get(index);
            listOfPaths.add(pathHierarchy);
        }
        for (String eachHierarchy : listOfPaths) {

            Boolean aBoolean = fileFolderCheckMap.containsKey(eachHierarchy) ? fileFolderCheckMap.get(eachHierarchy) : null;
            if (aBoolean == null || aBoolean) {
                continue;
            } else if (!aBoolean) {
                return false;
            }
            FileBrowserCache fileBrowserModel = fileBrowserCacheService.findFileBrowserCache(eachHierarchy);
            if (fileBrowserModel != null && !"PUBLIC".equals(fileBrowserModel.getFolderType())) {
                if (!checkParentPermission(fileBrowserModel, fileFolderCheckMap)) {
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * If the directory name is not system or images, the directory content will be included in
     * the list.
     * The directory will be searched for the content recursively.
     *
     * @param listOfFoldersAndFiles The list of folders and files
     * @param file                  The directory under concern
     */

    private void processDirectory(List<Map<String, String>> listOfFoldersAndFiles, File file,
                                  boolean isRequestedRecursive) throws UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        File[] listOfFiles = file.listFiles();
        if (listOfFiles != null) {
            if ((listOfFiles.length > 0)) {
                boolean isPublicDirectory = true;
                if (useFolderRules) {
                    //The following call to applyGenericRules method has no significance as of now.
                    applyGenericRules(listOfFoldersAndFiles, file);
                    File[] contents = file.listFiles();
                    if (contents != null) {
                        for (File theFile : contents) {
                            if (!theFile.isDirectory()) {
                                String actualFileExtension;
                                String[] fileNameAndExtensionArray = theFile.getName().split("\\" +
                                        "." + "(?=[^\\.]+$)");
                                if (fileNameAndExtensionArray.length <= 1) {
                                    continue;
                                } else {
                                    actualFileExtension = fileNameAndExtensionArray[fileNameAndExtensionArray.length
                                            - 1];
                                }
                                String textValue;
                                String ruleClass;
                                for (Map.Entry<String, List<String>> entry : foldersTagMap.entrySet()) {
                                    textValue = entry.getValue().get(0);
                                    ruleClass = entry.getValue().get(1);
                                    if (actualFileExtension.equalsIgnoreCase(textValue)) {
                                        isPublicDirectory = false;
                                        processOwnedOrSharedDirectory(listOfFoldersAndFiles, file, theFile,
                                                ruleClass, isRequestedRecursive);
                                    }
                                }
                            }
                        }
                    }
                }
                if (isPublicDirectory) {
                    processPublicDirectory(listOfFoldersAndFiles, file, isRequestedRecursive);
                }
            } else {
                processPublicDirectory(listOfFoldersAndFiles, file, isRequestedRecursive);
            }
        }

    }

    private void processDirectory(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file,
                                  boolean isRequestedRecursive, boolean onlyPermission, boolean discardEmptyFolder) throws UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        long now = System.currentTimeMillis();
        logger.debug("[method : processDirectory, path :" + file.getFilePath() + " parentId :" + file.getId() + " ,action: processingDirectory ,startTime :" + now + "]");
        FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
        List<FileBrowserCache> allFileBrowserCaches;

        allFileBrowserCaches = fileBrowserCacheService.getAllFileBrowserCaches(file.getId());
        long endTimeForDatabase = System.currentTimeMillis();
        logger.debug("[method : processDirectory, path :" + file.getFilePath() + " ,action: retrieving all files of dir from db , totalTime :" + (endTimeForDatabase - now) + "]");
        if (allFileBrowserCaches != null && allFileBrowserCaches.size() > 0) {
            boolean isPublicDirectory = true;
            if (useFolderRules) {
                for (FileBrowserCache theFile : allFileBrowserCaches) {
                    if (!theFile.getFileType().equals("FOLDER")) {
                        String actualFileExtension = FileUtils.getExtension(theFile.getFileName());
                        String textValue;
                        String ruleClass;
                        for (Map.Entry<String, List<String>> entry : foldersTagMap.entrySet()) {
                            textValue = entry.getValue().get(0);
                            ruleClass = entry.getValue().get(1);
                            if (actualFileExtension.equalsIgnoreCase(textValue)) {
                                isPublicDirectory = false;
                                if (discardEmptyFolder)
                                    processOwnedOrSharedDirectoryDiscardEmptyFolders(listOfFoldersAndFiles, file, theFile,
                                            ruleClass, isRequestedRecursive, allFileBrowserCaches);
                                else
                                    processOwnedOrSharedDirectory(listOfFoldersAndFiles, file, theFile,
                                            ruleClass, isRequestedRecursive, allFileBrowserCaches, onlyPermission);
                            }
                        }
                    }
                }
            }
            if (isPublicDirectory) {
                if (discardEmptyFolder)
                    processPublicDirectoryDiscardEmptyDirectories(listOfFoldersAndFiles, file, isRequestedRecursive, allFileBrowserCaches);
                else
                    processPublicDirectory(listOfFoldersAndFiles, file, isRequestedRecursive, allFileBrowserCaches, onlyPermission);
            }
        } else {
            if (discardEmptyFolder)
                processPublicDirectoryDiscardEmptyDirectories(listOfFoldersAndFiles, file, isRequestedRecursive, allFileBrowserCaches);
            else
                processPublicDirectory(listOfFoldersAndFiles, file, isRequestedRecursive, allFileBrowserCaches, onlyPermission);
        }
        long endTime = System.currentTimeMillis();
        logger.debug("[method : processDirectory, path :" + file.getFilePath() + " ,action: processingDirectory , endTime :" + endTime + "]");
        logger.debug("[method : processDirectory, path :" + file.getFilePath() + " ,action: processingDirectory ,totalTime :" + (endTime - now) + "]");
    }

    /**
     * Whether a file to be included in the list or not, if to be,
     * then what should be the content etc. such information
     * is configured through the setting.xml. The configured class object will be invoked.
     *
     * @param listOfFoldersAndFiles The list of folders and files
     * @param file                  The directory under concern
     * @param theFile               A file in the directory 'file'
     * @param ruleClass             A string that represents a class configured in setting.xml
     */
    private void processOwnedOrSharedDirectory(List<Map<String, String>> listOfFoldersAndFiles, File file,
                                               File theFile, String ruleClass, boolean isRequestedRecursive) {
        //noinspection StatementWithEmptyBody
        if (ruleClass == null) {
            // No rule implementation is found
        } else {
            /*
             * There is an implementation class. Use it
             */
            try {
                IResourceRule ruleInstance = FactoryMethodWrapper.getTypedInstance(ruleClass, IResourceRule.class);
                if (ruleInstance != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("The folder %s is being processed.", file));
                    }
                    JsonObject indexFileAsJson = processor.getJsonObject(theFile.toString(), false);
                    indexFileAsJson.addProperty("absolutePath", file.getAbsolutePath());
                    if (ruleInstance.validateFile(indexFileAsJson)) {

                        Map<String, String> resourceMap = ruleInstance.getResourceMap(indexFileAsJson, null,
                                file.getAbsolutePath(), file.getName(), "" + file.lastModified());
                        //Include the content only if the permission is not noAccessLevel
                        String permissionLevel = resourceMap.get("permissionLevel");
                        Integer permission = Integer.valueOf(permissionLevel);
                        if (permissionLevel != null) {
                            if (permission >= noAccessLevel) {
                                String relativePath = ApplicationUtilities.getRelativeSolutionPath(file.getAbsolutePath());
                                resourceMap.put("lastModified", file.lastModified() + "");
                                resourceMap.put("path", relativePath.replaceAll("\\\\", "/"));
                                if (isRequestedRecursive) {
                                    JsonArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(file.getAbsolutePath(),
                                            isRequestedRecursive));
                                    resourceMap.put("children", children.toString());
                                } else {
                                    resourceMap.put("children", "[]");
                                }
                                listOfFoldersAndFiles.add(resourceMap);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                logger.warn("The folder " + file + " couldn't be processed.", ex);
            }
        }
    }

    private void processOwnedOrSharedDirectory(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file,
                                               FileBrowserCache theFile, String ruleClass, boolean isRequestedRecursive, List<FileBrowserCache> allFileBrowserCaches, boolean onlyPermission) {
        //noinspection StatementWithEmptyBody
        if (ruleClass == null) {
            // No rule implementation is found
        } else {
            /*
             * There is an implementation class. Use it
             */
            try {
                IResourceRule ruleInstance = FactoryMethodWrapper.getTypedInstance(ruleClass, IResourceRule.class);
                if (ruleInstance != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("The folder %s is being processed.", file));
                    }
                    JsonObject jsonProcessedObject = jsonFromExtensionKey(theFile);

                    // JSONObject indexFileAsJson = processor.getJSONObject(theFile.getFilePath(), false);
                    String absolutePath = solutionDirectory + "/" + file.getFilePath();
                    jsonProcessedObject.addProperty("absolutePath", absolutePath);
                    if (ruleInstance.validateFile(jsonProcessedObject)) {
                        Map<String, String> resourceMap = ruleInstance.getResourceMap(jsonProcessedObject, null,
                                absolutePath, file.getFileName(), "" + file.getLastModified());
                        //Include the content only if the permission is not noAccessLevel
                        String permissionLevel = resourceMap.get("permissionLevel");
                        Integer permission = Integer.valueOf(permissionLevel);
                        if (permissionLevel != null) {
                            if (permission >= noAccessLevel) {
                                String relativePath = ApplicationUtilities.getRelativeSolutionPath(absolutePath);
                                if (onlyPermission) {
                                    Map<String, String> formatedResourceMap = new HashMap<>();
                                    formatedResourceMap.put("logicalPath", file.getLogicalPath());
                                    formatedResourceMap.put("permissionLevel", resourceMap.get("permissionLevel"));
                                    //formatedResourceMap.put("visible", null);
                                    //formatedResourceMap.put("inherit", null);
                                    formatedResourceMap.put("physicalPath", file.getFilePath());
                                    //formatedResourceMap.put("parentpermission", null);
                                    resourceMap.clear();
                                    resourceMap.putAll(formatedResourceMap);
                                } else {
                                    resourceMap.put("lastModified", file.getLastModified() + "");
                                    resourceMap.put("path", relativePath.replaceAll("\\\\", "/"));
                                }
                                if (isRequestedRecursive && !onlyPermission) {
                                    JsonArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(absolutePath,
                                            isRequestedRecursive, allFileBrowserCaches, file, false));
                                    resourceMap.put("children", children.toString());
                                } else if (!onlyPermission) {
                                    resourceMap.put("children", "[]");
                                }
                                listOfFoldersAndFiles.add(resourceMap);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                logger.warn("The folder " + file + " couldn't be processed.", ex);
            }
        }
    }

    private void processOwnedOrSharedDirectoryDiscardEmptyFolders(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file,
                                                                  FileBrowserCache theFile, String ruleClass, boolean isRequestedRecursive, List<FileBrowserCache> allFileBrowserCaches) {
        //noinspection StatementWithEmptyBody
        if (ruleClass == null) {
            // No rule implementation is found
        } else {
            /*
             * There is an implementation class. Use it
             */
            try {
                IResourceRule ruleInstance = FactoryMethodWrapper.getTypedInstance(ruleClass, IResourceRule.class);
                if (ruleInstance != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("The folder %s is being processed.", file));
                    }
                    JsonObject jsonProcessedObject = jsonFromExtensionKey(theFile);

                    // JSONObject indexFileAsJson = processor.getJSONObject(theFile.getFilePath(), false);
                    String absolutePath = solutionDirectory + "/" + file.getFilePath();
                    jsonProcessedObject.addProperty("absolutePath", absolutePath);
                    if (ruleInstance.validateFile(jsonProcessedObject)) {
                        Map<String, String> resourceMap = ruleInstance.getResourceMap(jsonProcessedObject, null,
                                absolutePath, file.getFileName(), "" + file.getLastModified());
                        //Include the content only if the permission is not noAccessLevel
                        String permissionLevel = resourceMap.get("permissionLevel");
                        Integer permission = Integer.valueOf(permissionLevel);
                        if (permissionLevel != null) {
                            if (permission >= noAccessLevel) {
                                String relativePath = ApplicationUtilities.getRelativeSolutionPath(absolutePath);
                                boolean isDirectoryEmpty = false;
                                resourceMap.put("lastModified", file.getLastModified() + "");
                                resourceMap.put("path", relativePath.replaceAll("\\\\", "/"));

                                if (isRequestedRecursive) {
                                    JsonArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(absolutePath,
                                            isRequestedRecursive, allFileBrowserCaches, file, true));
                                    isDirectoryEmpty = children.isEmpty();
                                    resourceMap.put("children", children.toString());
                                } else {
                                    isDirectoryEmpty = true;
                                    resourceMap.put("children", "[]");
                                }
                                if (!isDirectoryEmpty)
                                    listOfFoldersAndFiles.add(resourceMap);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                logger.warn("The folder " + file + " couldn't be processed.", ex);
            }
        }
    }


    private JsonObject jsonFromExtensionKey(FileBrowserCache theFile) {
    	String json = new Gson().toJson(theFile);
    	JsonObject jsonContent = new Gson().fromJson(json, JsonObject.class);
        //JSONObject jsonContent = JSONObject.fromObject(theFile.getJson());
        TreeSet<String> setOfKeys = new TreeSet<String>(jsonContent.keySet());
        String firstKey = setOfKeys.first();
        return  jsonContent.get(firstKey).getAsJsonObject();
       /* String extension = FileUtils.getExtension(theFile.getFileName());
        return jsonContent.getJSONObject(extension);*/
    }

    /**
     * All the files in the directory specified by the parameter 'file' will be examined. Files
     * with no extension
     * won't be processed.
     *
     * @param listOfFoldersAndFiles The list of folders and files
     * @param file                  The file under concern
     */
    private void processFile(List<Map<String, String>> listOfFoldersAndFiles,
                             File file) throws UnSupportedRuleImplementationException {
        // Split the file into its name and extension and get them in an
        // array
        String[] fileNameAndExtensionArray = file.getName().split("\\.(?=[^\\.]+$)");
        String actualFileExtension;
        /*
            FIXED: Fixed the issue of ArrayIndexOutOfBoundsException if
            file does not have any extension
		*/
        if (fileNameAndExtensionArray.length <= 1) {
            return;
        } else {
            actualFileExtension = fileNameAndExtensionArray[fileNameAndExtensionArray.length - 1];
        }
        String extensionKey;
        String visibleExtension = "";
        String rule;
        List<String> supportedImagesList = null;
        for (Map.Entry<String, List<String>> entry : visibleExtensionsMap.entrySet()) {
            extensionKey = entry.getKey();
            final List<String> values = entry.getValue();

            if ("image".equals(extensionKey)) {
                supportedImagesList.add(values.get(0));
            } else {
                visibleExtension = values.get(0);
            }
            rule = values.get(1);
            boolean isImage = supportedImagesList != null ? supportedImagesList.contains(actualFileExtension) : false;
             if (actualFileExtension.equalsIgnoreCase(visibleExtension) || isImage) {
                applyFileRules(listOfFoldersAndFiles, file, extensionKey, rule);
            }
        }
    }

    private boolean checkParentPermission(FileBrowserCache parentFileBrowser, Map<String, Boolean> fileFolderCheckMap) {
        FileBrowserCacheService fileBrowserCacheService = ApplicationContextAccessor.getBean(FileBrowserCacheService.class);
        String parentFilePath;
        String filePath = parentFileBrowser.getFilePath();
        parentFilePath = filePath.replace("/" + parentFileBrowser.getFileName(), "");
        if ("0".equals(parentFileBrowser.getParentId()) && "FOLDER".equals(parentFileBrowser.getFileType())) {
            parentFilePath = filePath;
        }
        String indexFilePath = parentFilePath + "/index." + JsonUtils.getFolderFileExtension();
        FileBrowserCache indexFileBrowserCache = fileBrowserCacheService.findFileBrowserCache(indexFilePath);
        if (processShareAndPermission(parentFileBrowser, indexFileBrowserCache)) {
            fileFolderCheckMap.put(parentFilePath, true);
            return true;
        } else {
            fileFolderCheckMap.put(parentFilePath, false);
            return false;
        }
    }

    private void processFile(List<Map<String, String>> listOfFoldersAndFiles,
                             FileBrowserCache file, boolean onlyPermission, boolean isForReportStats) throws UnSupportedRuleImplementationException {
        long now = System.currentTimeMillis();
        logger.debug("[method : processFile, path :" + file.getFilePath() + " ,action: processingFile ,startTime :" + now + "]");
        // Split the file into its name and extension and get them in an
        // array
        String[] fileNameAndExtensionArray = file.getFileName().split("\\.(?=[^\\.]+$)");
        String actualFileExtension;
        /*
            FIXED: Fixed the issue of ArrayIndexOutOfBoundsException if
            file does not have any extension
		*/
        if (fileNameAndExtensionArray.length <= 1) {
            return;
        } else {
            actualFileExtension = fileNameAndExtensionArray[fileNameAndExtensionArray.length - 1];
        }
        String extensionKey;
        String visibleExtension;
        String rule;
        for (Map.Entry<String, List<String>> entry : visibleExtensionsMap.entrySet()) {
            extensionKey = entry.getKey();
            final List<String> values = entry.getValue();
            visibleExtension = values.get(0);
            rule = values.get(1);
            if (actualFileExtension.equalsIgnoreCase(visibleExtension)) {
                applyFileRules(listOfFoldersAndFiles, file, extensionKey, rule, onlyPermission, isForReportStats);
            }
        }
        long endTime = System.currentTimeMillis();
        logger.debug("[method : processFile, path :" + file.getFilePath() + " ,action: processingFile(withRules) ,endTime :" + endTime + "]");
        logger.debug("[method : processFile, path :" + file.getFilePath() + " ,action: processingFile ,totalTime(withRules) :" + (endTime - now) + "]");
    }

    /**
     * A file content is added to the list of files and folders based on the file type. For some
     * files a rule
     * class is configured in setting.xml. That class object will be used reflectively.
     *
     * @param listOfFoldersAndFiles The list of folders and files
     * @param file                  The directory under concern
     * @param extensionKey          The key value in the setting.xml for which the visible
     *                              attribute is true
     * @param rule                  A string that represents a class configured in setting.xml
     */
    private void applyFileRules(List<Map<String, String>> listOfFoldersAndFiles, File file, String extensionKey,
                                String rule) throws UnSupportedRuleImplementationException {
        if (rule == null) {
            // Visible but no rule implementation is found
            listOfFoldersAndFiles.add(includeVisibleFile(file, extensionKey));
        } else {
            // There is an implementation class. So use it
            IResourceRule resourceRule = FactoryMethodWrapper.getTypedInstance(rule, IResourceRule.class);
            //Fixing issue to catch and log the exceptions instead  of stopping the request in case
            //of malformed resources. For rule classes if they do not handle the exception,
            //suppress the exception by just logging.
            if (resourceRule != null) {
                try {
                    String lastModified = file.lastModified() + "";
                    JsonObject fileAsJson;
                    try {
                        fileAsJson = processor.getJsonObject(file.getAbsolutePath(), false);
                    } catch (ClassCastException ex) {
                        logger.error("Error while parsing a file " + file);
                        fileAsJson = processor.getJsonObject(file.getAbsolutePath(), true);
                    }
                    //All Rules here use AbstractResourceRule.validationResult() except EFW
                    //The above uses setting.xml resourceSecurityRule and rules in security tag.
                    fileAsJson.addProperty("absolutePath", file.getAbsolutePath());
                    if (resourceRule.validateFile(fileAsJson)) {
                        Map<String, String> resourceMap = resourceRule.getResourceMap(fileAsJson, extensionKey,
                                file.getAbsolutePath(), file.getName(), lastModified);
                        //Include the content only if the permission is not noAccessLevel
                        String permissionLevel = resourceMap.get("permissionLevel");
                        Integer permission = Integer.valueOf(permissionLevel);
                        if (permissionLevel != null) {
                            if (permission >= noAccessLevel) {
                                listOfFoldersAndFiles.add(resourceMap);
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("The file " + file + " is malformed.", ex);
                }
            }
        }
    }

    private void applyFileRules(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file, String extensionKey,
                                String rule, boolean onlyPermission, boolean isForCacheStats) throws UnSupportedRuleImplementationException {
        if (rule == null) {
            listOfFoldersAndFiles.add(includeVisibleFile(file, extensionKey, onlyPermission));
        } else {
            IResourceRule resourceRule = FactoryMethodWrapper.getTypedInstance(rule, IResourceRule.class);
            if (resourceRule != null) {
                try {
                    String lastModified = "" + file.getLastModified();

                    String systemSolutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
                    String filePath = file.getFilePath();
                    JsonObject fileAsJson = getFileAsJson(file, systemSolutionDirectory, filePath);
                    String absolutePath = solutionDirectory + "/" + filePath;
                    fileAsJson.addProperty("absolutePath", absolutePath);
                    if (resourceRule.validateFile(fileAsJson)) {
                        String fileName = file.getFileName();
                        Map<String, String> resourceMap = resourceRule.getResourceMap(fileAsJson, extensionKey,
                                absolutePath, fileName, lastModified);
                        isSearch(file, onlyPermission, isForCacheStats, filePath, fileName, resourceMap);
                        String permissionLevel = resourceMap.get("permissionLevel");
                        Integer permission = Integer.valueOf(permissionLevel);
                        if (permissionLevel != null && permission >= noAccessLevel) {
                            listOfFoldersAndFiles.add(resourceMap);
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("The file " + file + " is malformed.", ex);
                }
            }
        }
    }

    private JsonObject getFileAsJson(FileBrowserCache file, String systemSolutionDirectory, String filePath) {
        JsonObject fileAsJson;
        try {
            if (file.getIsFileLarge()) {
                fileAsJson = processor.getJsonObject(systemSolutionDirectory + File.separator + filePath, false);
            } else {
                fileAsJson = jsonFromExtensionKey(file);
            }
        } catch (ClassCastException ex) {
            logger.error("Error while parsing a file " + file);
            String json = new Gson().toJson(file);
            fileAsJson = new Gson().fromJson(json, JsonObject.class);
            //fileAsJson = JSONObject.fromObject(file.getJson());
        }
        return fileAsJson;
    }

    private void isSearch(FileBrowserCache file, boolean onlyPermission, boolean isForCacheStats, String filePath, String fileName, Map<String, String> resourceMap) {
        if (onlyPermission) {
            Map<String, String> formattedResourceMap = new HashMap<>();
            if (isForCacheStats) {
                String dir = filePath.replace("/" + fileName, "");
                formattedResourceMap.put("dir", dir);
                formattedResourceMap.put("file", fileName);
                formattedResourceMap.put("lastModified", file.getLastModified().toString());
                formattedResourceMap.put("reportPath", filePath);
                formattedResourceMap.put("title", file.getTitle());
            } else {
                formattedResourceMap.put("physicalPath", filePath);
            }
            formattedResourceMap.put("logicalPath", file.getLogicalPath());
            formattedResourceMap.put("permissionLevel", resourceMap.get("permissionLevel"));

            resourceMap.clear();
            resourceMap.putAll(formattedResourceMap);
        }
    }


    private void log(FileBrowserCache file) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The file %s is being processed.", file));
        }
    }

    /**
     * Generic rules segment. Applies all the generic rules from
     * the list genericRulesList.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    private void applyGenericRules(List<Map<String, String>> listOfFoldersAndFiles, File file) {
        if (isGenericRulePresent && listOfFoldersAndFiles != null && file != null) {
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void applyGenericRules(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file) {
        if (isGenericRulePresent && listOfFoldersAndFiles != null && file != null) {
        }
    }


    /**
     * Overloaded method for files
     *
     * @param listOfFoldersAndFiles The listOfFoldersAndFiles
     * @param file                  The file for which the particular rule attribute is mentioned
     */
    private void processPublicDirectory(List<Map<String, String>> listOfFoldersAndFiles, File file,
                                        boolean isRequestedRecursive) throws UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        Map<String, String> foldersMap = new HashMap<>();
        String relativePath = ApplicationUtilities.getRelativeSolutionPath(file.getAbsolutePath());
        //The directory is a public directory.
        foldersMap.put("permissionLevel", Integer.toString(resourcePermissionLevelsHolder.publicResourceAccessLevel()));
        foldersMap.put("type", "folder");
        foldersMap.put("name", file.getName());
        foldersMap.put("lastModified", file.lastModified() + "");
        foldersMap.put("path", relativePath.replaceAll("\\\\", "/"));
        if (isRequestedRecursive) {
            JsonArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(file.getAbsolutePath(), isRequestedRecursive));
            foldersMap.put("children", children.toString());
        } else {
            foldersMap.put("children", "[]");
        }
        listOfFoldersAndFiles.add(foldersMap);
    }

    private void processPublicDirectory(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file,
                                        boolean isRequestedRecursive, List<FileBrowserCache> fileBrowserCaches, boolean onlyPermission) throws UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        Map<String, String> foldersMap = new HashMap<>();
        //String relativePath = ApplicationUtilities.getRelativeSolutionPath(file.getAbsolutePath());
        //The directory is a public directory.
        if (onlyPermission) {
            foldersMap.put("logicalPath", file.getLogicalPath());
            foldersMap.put("permissionLevel", Integer.toString(resourcePermissionLevelsHolder.publicResourceAccessLevel()));
            foldersMap.put("physicalPath", file.getFilePath());
        } else {
            foldersMap.put("permissionLevel", Integer.toString(resourcePermissionLevelsHolder.publicResourceAccessLevel()));
            foldersMap.put("type", "folder");
            foldersMap.put("name", file.getFileName());
            foldersMap.put("lastModified", file.getLastModified() + "");
            foldersMap.put("path", file.getFilePath());
            if (isRequestedRecursive) {
                JsonArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(solutionDirectory + "/" + file.getFilePath(), true, fileBrowserCaches, file, false));
                foldersMap.put("children", children.toString());
            } else {
                foldersMap.put("children", "[]");
            }
        }
        listOfFoldersAndFiles.add(foldersMap);
    }

    private void processPublicDirectoryDiscardEmptyDirectories(List<Map<String, String>> listOfFoldersAndFiles, FileBrowserCache file,
                                                               boolean isRequestedRecursive, List<FileBrowserCache> fileBrowserCaches) throws UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        Map<String, String> foldersMap = new HashMap<>();
        //String relativePath = ApplicationUtilities.getRelativeSolutionPath(file.getAbsolutePath());
        //The directory is a public directory.
        boolean isChildrenEmpty = false;
        foldersMap.put("permissionLevel", Integer.toString(resourcePermissionLevelsHolder.publicResourceAccessLevel()));
        foldersMap.put("type", "folder");
        foldersMap.put("name", file.getFileName());
        foldersMap.put("lastModified", file.getLastModified() + "");
        foldersMap.put("path", file.getFilePath());
        if (isRequestedRecursive) {
            JsonArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(solutionDirectory + "/" + file.getFilePath(), true, fileBrowserCaches, file, true));
            isChildrenEmpty = children.isEmpty();
            foldersMap.put("children", children.toString());
        } else {
            isChildrenEmpty = true;
            foldersMap.put("children", "[]");
        }
        if (!isChildrenEmpty)
            listOfFoldersAndFiles.add(foldersMap);
    }


    /**
     * Includes the file under concern in the map which is part of json
     *
     * @param file         The file for which the particular rule attribute is not
     *                     mentioned
     * @param extensionKey The key value in the setting.xml for which the visible
     *                     attribute is true
     * @return Returns the custom map for the file
     */
    private Map<String, String> includeVisibleFile(File file, String extensionKey) {
        Map<String, String> foldersMap = new HashMap<>();
        String relativePath = ApplicationUtilities.getRelativeSolutionPath(file.getAbsolutePath());
        foldersMap.put("type", "file");
        foldersMap.put("permissionLevel", Integer.toString(resourcePermissionLevelsHolder.publicResourceAccessLevel()));
        foldersMap.put("extension", extensionKey);
        foldersMap.put("lastModified", file.lastModified() + "");
        foldersMap.put("name", file.getName());
        foldersMap.put("path", relativePath.replaceAll("\\\\", "/"));
        foldersMap.put("visible", "true");
        foldersMap.put("description", file.getName());
        foldersMap.put("title", file.getName());
        return foldersMap;
    }

    private Map<String, String> includeVisibleFile(FileBrowserCache file, String extensionKey, boolean onlyPermission) {
        Map<String, String> foldersMap = new HashMap<>();
        String permissionLevel = Integer.toString(resourcePermissionLevelsHolder.publicResourceAccessLevel());
        if (onlyPermission) {
            foldersMap.put("logicalPath", file.getLogicalPath());
            foldersMap.put("physicalPath", file.getFilePath());
        } else {
            foldersMap.put("type", "file");
            foldersMap.put("extension", extensionKey);
            foldersMap.put("lastModified", file.getLastModified() + "");
            foldersMap.put("name", file.getFileName());
            foldersMap.put("path", file.getFilePath());
            foldersMap.put("visible", "true");
            foldersMap.put("description", file.getFileName());
            foldersMap.put("title", file.getFileName());
        }
        foldersMap.put("permissionLevel", permissionLevel);

        return foldersMap;
    }

    /**
     * Yet to be implemented. Currently no operation method(NOP)
     *
     * @param genericRulesArray the genericRules json
     * @return List of rule classes
     */
    @SuppressWarnings("UnusedParameters")
    private List<String> getGenericRulesList(JsonArray genericRulesArray) {
        return new ArrayList<>();
    }

    /**
     * Yet to be implemented. Currently no operation method(NOP)
     *
     * @param genericRulesObject the genericRules json
     * @return List of rule classes
     */
    @SuppressWarnings("UnusedParameters")
    private List<String> getGenericRulesList(JsonObject genericRulesObject) {
        return new ArrayList<>();
    }

    private boolean processShareAndPermission(FileBrowserCache theFile, FileBrowserCache indexFileBrowserCaches) {
        if (indexFileBrowserCaches != null) {
            String actualFileExtension = FileUtils.getExtension(indexFileBrowserCaches.getFileName());
            List<String> entry = foldersTagMap.get(actualFileExtension);
            return processOwnedOrSharedDirectory(entry.get(1), theFile, indexFileBrowserCaches);
        }
        return false;
    }


    private boolean processOwnedOrSharedDirectory(String ruleClass, FileBrowserCache folder, FileBrowserCache indexFile) {
        try {
            IResourceRule ruleInstance = FactoryMethodWrapper.getTypedInstance(ruleClass, IResourceRule.class);
            if (ruleInstance != null) {
                JsonObject jsonProcessedObject = jsonFromExtensionKey(indexFile);
                String absolutePath = solutionDirectory + "/" + folder.getFilePath();
                jsonProcessedObject.addProperty("absolutePath", absolutePath);
                return ruleInstance.validateFile(jsonProcessedObject);
            }
        } catch (Exception ex) {
            logger.warn("The folder " + folder + " couldn't be processed.", ex);
        }
        return false;
    }
}
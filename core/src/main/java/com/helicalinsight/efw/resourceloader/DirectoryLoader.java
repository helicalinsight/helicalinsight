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

package com.helicalinsight.efw.resourceloader;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
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
 * @version 1.1
 * @since 1.0
 */
public class DirectoryLoader {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryLoader.class);

    private final IProcessor processor = ResourceProcessorFactory.getIProcessor();

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

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
    private JSONObject genericRulesJSON;
    /**
     * The json of generic rules from setting.xml
     */
    private JSONArray genericRulesJSONArray;
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
    public DirectoryLoader(JSONObject visibleExtensions) {
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
    private void prepareExtensionsMap(JSONObject visibleExtensions) {
        Assert.notNull(visibleExtensions, "Visible extension's JSON is null!");
        Iterator<?> iterator = visibleExtensions.keys();
        this.visibleExtensionsMap = new HashMap<>();
        while (iterator.hasNext()) {
            int counter = 0;
            String key = (String) iterator.next();
            try {
                iterate(visibleExtensions, counter, key);
            } catch (JSONException ex) {
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
    private void prepareFoldersTagMap(JSONObject visibleExtensions) {
        Assert.notNull(visibleExtensions, "Visible extension's JSON is null!");
        try {
            JSONObject foldersJSONObject = initialize(visibleExtensions);
            /*
             * genericRulesJSON has been prepared. Now prepare the foldersTagMap
			 */
            prepareTextAndRuleValuesList(foldersJSONObject);
        } catch (JSONException ignore) {
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
    private void iterate(JSONObject visibleExtensions, int counter, String key) {
        /*
         * visible attribute is provided. Hence no scope for
         * JSONException as it is a JSON object
         */
        JSONObject json = visibleExtensions.getJSONObject(key);
        try {
            List<String> textAndRuleValuesList = new ArrayList<>();
            /*
             * Look for the visible tag's rule implementation if it is
             * not null. If rule is not provided move to the catch block
             * as there will be an exception
             */
            try {
                if (json.getString("@rule") != null) {
                    /*
                     * Whether or not rule attribute is provided text
                     * value must have been provided. Place the key's
                     * text value in the list
                     */
                    textAndRuleValuesList.add(counter, json.getString("#text"));
                    // Increment the counter
                    counter = counter + 1;
                    /*
                     * Add the rule attribute value as the second index
                     * in the list
                     */
                    textAndRuleValuesList.add(counter, json.getString("@rule"));
                    visibleExtensionsMap.put(key, textAndRuleValuesList);
                }
            } catch (JSONException ex) {
                textAndRuleValuesList.add(counter, json.getString("#text"));
                counter = counter + 1;
                // No rule attribute is provided. Add null to the list
                textAndRuleValuesList.add(counter, null);
                visibleExtensionsMap.put(key, textAndRuleValuesList);
            }
        } catch (JSONException ignore) {
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
    private JSONObject initialize(JSONObject visibleExtensions) {
        JSONObject foldersJSONObject = visibleExtensions.getJSONObject("folder");
        this.foldersTagMap = new HashMap<>();
        useFolderRules = true;
        try {
            genericRulesJSONArray = foldersJSONObject.getJSONArray("generic");
            isGenericRulePresent = true;
            multipleGenericRules = true;
        } catch (JSONException ex) {
            try {
                genericRulesJSON = new JSONObject();
                genericRulesJSON.accumulate("generic", foldersJSONObject.getString("generic"));
            } catch (JSONException ignore) {
            }
        }
        return foldersJSONObject;
    }

    /**
     * Prepares the textAndRuleValuesList of this object
     *
     * @param foldersJSONObject The json of folder tag from setting.xml
     */
    private void prepareTextAndRuleValuesList(JSONObject foldersJSONObject) {
        Iterator<?> iterator = foldersJSONObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            int counter = 0;
            if (!"generic".equalsIgnoreCase(key) && !"@visible".equalsIgnoreCase(key)) {
                JSONObject json = foldersJSONObject.getJSONObject(key);
                List<String> textAndRuleValuesList = new ArrayList<>();
                try {
                    textAndRuleValuesList.add(counter, json.getString("#text"));
                    counter = counter + 1;
                    try {
                        textAndRuleValuesList.add(counter, json.getString("@rule"));
                    } catch (JSONException ignore) {
                        textAndRuleValuesList.add(counter, null);
                    }
                } catch (JSONException ignore) {
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
    public List<Map<String, String>> getSolutionDirectory(String rootPath, boolean isRequestedRecursive) throws
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

    /**
     * Prepares the list of solution directory content
     *
     * @param rootPath Usually EFW solution directory
     * @return The content of the solution directory
     */
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
                    JSONObject indexFileAsJson = processor.getJSONObject(theFile.toString(), false);
                    indexFileAsJson.put("absolutePath", file.getAbsolutePath());
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
                                    JSONArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(file.getAbsolutePath(),
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
        String visibleExtension;
        String rule;
        for (Map.Entry<String, List<String>> entry : visibleExtensionsMap.entrySet()) {
            extensionKey = entry.getKey();
            final List<String> values = entry.getValue();
            visibleExtension = values.get(0);
            rule = values.get(1);
            if (actualFileExtension.equalsIgnoreCase(visibleExtension)) {
                applyFileRules(listOfFoldersAndFiles, file, extensionKey, rule);
            }
        }
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
            //Fixing issue to catch and log the exceptions instead of stopping the request in case
            //of malformed resources. For rule classes if they do not handle the exception,
            //suppress the exception by just logging.
            if (resourceRule != null) {
                try {
                    String lastModified = file.lastModified() + "";
                    log(file);
                    JSONObject fileAsJson;
                    try {
                        fileAsJson = processor.getJSONObject(file.getAbsolutePath(), false);
                    } catch (ClassCastException ex) {
                        logger.error("Error while parsing a file " + file);
                        fileAsJson = processor.getJSONObject(file.getAbsolutePath(), true);
                    }
                    //All Rules here use AbstractResourceRule.validationResult() except EFW
                    //The above uses setting.xml resourceSecurityRule and rules in security tag.
                    fileAsJson.put("absolutePath", file.getAbsolutePath());
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

    private void log(File file) {
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
            JSONArray children = ApplicationUtilities.getJSONArray(getFoldersAndFiles(file.getAbsolutePath(), true));
            foldersMap.put("children", children.toString());
        } else {
            foldersMap.put("children", "[]");
        }
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

    /**
     * Yet to be implemented. Currently no operation method(NOP)
     *
     * @param genericRulesArray the genericRules json
     * @return List of rule classes
     */
    @SuppressWarnings("UnusedParameters")
    private List<String> getGenericRulesList(JSONArray genericRulesArray) {
        return new ArrayList<>();
    }

    /**
     * Yet to be implemented. Currently no operation method(NOP)
     *
     * @param genericRulesObject the genericRules json
     * @return List of rule classes
     */
    @SuppressWarnings("UnusedParameters")
    private List<String> getGenericRulesList(JSONObject genericRulesObject) {
        return new ArrayList<>();
    }
}
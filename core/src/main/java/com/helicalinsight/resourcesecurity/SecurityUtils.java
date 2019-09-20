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

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceloader.rules.impl.IndexFileRule;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 26-05-2015.
 *
 * @author Rajasekhar
 */
public class SecurityUtils {
    private static final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
    private static ResourcePermissionFactory factory = new ResourcePermissionFactory();

    /**
     * @return The Security object that represents the xml structure of security
     */
    public static Security securityObject() {
        final Security security = ApplicationContextAccessor.getBean(Security.class);

        final String userId = AuthenticationUtils.getUserId();
        security.setCreatedBy(userId);

        return security;
    }

    /**
     * Returns the jaxb class name of the extension from the setting.xml
     * <p/>
     *
     * @param extension An extension of a file that is configured in xml
     * @return Returns the jaxb class of the extension. Only the resources extensions
     * configured in the Extentions tag of the setting.xml are returned.
     */
    public static String jaxBClassForKey(String extension) {
        if (extension == null) {
            throw new IllegalArgumentException("The parameter extension can't be null");
        }

        Map<String, String> jaxbClassesMap = ApplicationContextAccessor.getBean(FileExtensionKeyAndJaxbClassMapBean
                .class).resourceKeyClassMap();

        String value = null;
        for (Map.Entry<String, String> entry : jaxbClassesMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(extension)) {
                value = entry.getValue();
                break;
            }
        }

        if (value == null) {
            throw new XmlConfigurationException(String.format("No class is configured in the" + "" +
                    " application configuration for %s.", extension));
        }
        return value;
    }


    /*public static int maxInheritPermission(File file) {
        List<String> pathList= new ArrayList<String>();
        String parentPath = file.getParent();
        while(!parentPath.equalsIgnoreCase(ApplicationProperties.getInstance().getSolutionDirectory())) {
            pathList.add(file.getParent());
            file = file.getParentFile();
            parentPath = file.getParent();
        }
        if(pathList.size()>0) {
            for (int index = pathList.size() - 1; index >= 0; index--) {
                File indexFile = new File(pathList.get(index) + File.separator + "index.efwfolder");
                if (indexFile.exists()) {
                JSONObject parentJson = ResourceProcessorFactory.getIProcessor().getJSONObject(indexFile.getAbsolutePath(), false);

                int myPermission = whatIsMyPermission(parentJson);
                if(myPermission==-1){
                    return maxInheritPermission(file.getParentFile());
                }

            }
        }
           /* File indexFile = new File(parentPath + File.separator + "index.efwfolder");
            if (indexFile.exists()) {
                JSONObject parentJson = ResourceProcessorFactory.getIProcessor().getJSONObject(indexFile.getAbsolutePath(), false);
                boolean isSharePresent = ShareRuleHelper.isShareTagPresent(parentJson);

                if (!isSharePresent) {
                    return maxInheritPermission(file.getParentFile());
                } else {
                    int myPermission = whatIsMyPermission(parentJson);
                    if(myPermission==-1){
                        return maxInheritPermission(file.getParentFile());
                    }
                    return myPermission;
                }
            }
        }
        return 2;

    }*/

    public static int whatIsMyPermission(JSONObject fileAsJson) {
        IResourcePermission resourcePermission = factory.resourcePermission(fileAsJson);
        int permissionLevelOnResource = resourcePermission.maximumPermissionLevelOnResource();
        return permissionLevelOnResource;
    }

    ;

    public static int maxInheritPermission(File file) {
        String parentPath = file.getParent();
        if (!parentPath.equalsIgnoreCase(ApplicationProperties.getInstance().getSolutionDirectory())) {
            File indexFile = new File(parentPath + File.separator + "index.efwfolder");
            if (indexFile.exists()) {
                JSONObject parentJson = ResourceProcessorFactory.getIProcessor().getJSONObject(indexFile.getAbsolutePath(), false);
                boolean isSharePresent = ShareRuleHelper.isShareTagPresent(parentJson);

                if (!isSharePresent) {
                    return maxInheritPermission(file.getParentFile());
                } else {
                    int myPermission = whatIsMyPermission(parentJson);
                    if (myPermission == -1) {
                        return maxInheritPermission(file.getParentFile());
                    }
                    return myPermission;
                }
            } else {
                return resourcePermissionLevelsHolder.publicResourceAccessLevel();
            }
        }
        return resourcePermissionLevelsHolder.noAccessLevel();

    }


    public static boolean isTargetReachable(File file) {
        List<String> pathList = getPathList(file);

        for (String path : pathList) {
            File indexFile = new File(path + File.separator + "index.efwfolder");
            if (indexFile.exists()) {
                IResourceRule resourceRule = IndexFileRule.getInstance();
                JSONObject directoryJson = ResourceProcessorFactory.getIProcessor().getJSONObject(indexFile.getAbsolutePath(), false);
                try {
                    directoryJson.put("absolutePath", indexFile.getAbsolutePath());
                    if (!resourceRule.validateFile(directoryJson)) {
                        return false;
                    }
                } catch (Exception ignore) {


                }
            } else {
                return true;
            }

        }
        return true;
    }

    private static List<String> getPathList(File file) {
        List<String> pathList = new ArrayList<String>();
        String parentPath = file.getParent();
        while (!parentPath.equalsIgnoreCase(ApplicationProperties.getInstance().getSolutionDirectory())) {
            pathList.add(file.getParent());
            file = file.getParentFile();
            parentPath = file.getParent();
        }
        Collections.reverse(pathList);
        return pathList;
    }

}

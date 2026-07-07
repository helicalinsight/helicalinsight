package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.iresource.IResourceRuleDB;
import com.helicalinsight.resourcesecurity.FileExtensionKeyAndJaxbClassMapBean;
import com.helicalinsight.resourcesecurity.IResourcePermission;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SecurityUtilsDB {
	@Autowired
    private static  ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;
    private static ResourcePermissionFactory factory = new ResourcePermissionFactory();


    public static Security securityObject() {
        final Security security = ApplicationContextAccessor.getBean(Security.class);

        final String userId = AuthenticationUtils.getUserId();
        security.setCreatedBy(userId);
        String organizationId = AuthenticationUtils.getOrganizationId();
        if (organizationId == null) {
            organizationId = "";
        }
        security.setOrganization(organizationId);
        return security;
    }


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


    public static int maxInheritPermission(File file) {
        List<String> pathList = new ArrayList<String>();
        String parentPath = file.getParent();
        while (!parentPath.equalsIgnoreCase(ApplicationProperties.getInstance().getSolutionDirectory())) {
            pathList.add(file.getParent());
            file = file.getParentFile();
            parentPath = file.getParent();
        }
        if (pathList.size() > 0) {
            for (int index = pathList.size() - 1; index >= 0; index--) {
                File indexFile = new File(pathList.get(index) + File.separator + "index.efwfolder");
                if (indexFile.exists()) {
                    JSONObject parentJson = ResourceProcessorFactory.getIProcessor().getJSONObject(indexFile.getAbsolutePath(), false);

                    int myPermission = whatIsMyPermission(parentJson);
                    if (myPermission == -1) {
                        return maxInheritPermission(file.getParentFile());
                    }

                }
            }
            /*File indexFile = new File(parentPath + File.separator + "index.efwfolder");
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
            }*/
        }
        return 2;

    }


    public static int whatIsMyPermission(Map<String, Object> resourceMap) {
        IResourcePermission resourcePermission = factory.resourcePermissionDB(resourceMap);
        int permissionLevelOnResource = resourcePermission.maximumPermissionLevelOnResource();
        return permissionLevelOnResource;
    }

    //Edit This
    public static int maxInheritPermission(Map<String, Object> resourceMap) {
        HIResource hiResource = (HIResource) resourceMap.get("folder");
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();

        Map<String,Object> resourceSecurity = allResources.getResourcePermission();


        Integer resourcePermission = Integer.valueOf(""+resourceSecurity.get(hiResource.getResourceURL()));
        return resourcePermission;
    }


    public static boolean isTargetReachable(Map<String, Object> resourceMap) {
        //Todo need to implement Logic to check inheritance
        try{

            HIResource hiResource = (HIResource) resourceMap.get("folder");
            if(hiResource==null){
                return  true;
            }
            List<String> pathList = getPathList(hiResource);
            DBProcessor dbProcessor = new DBProcessor();
            for (String path : pathList) {
                if (!ResourceAuthenticatorDB.isRoot(path)) {
                    IResourceRuleDB resourceRule = IndexFileRuleDB.getInstance();
                    String absolutePath = (String) resourceMap.get("absolutePath");
                    try {
                        Map<String, Object> directoryMap = dbProcessor.getContent(absolutePath);
                        if (!resourceRule.validateMap(directoryMap)) {
                            return false;
                        }
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }else{
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static List<String> getPathList(HIResource hiResource) {
        List<String> pathList = new ArrayList<String>();
        String resourceUrl = hiResource.getResourceURL();
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        Integer parentId = hiResource.getParentId();
        while (!ResourceAuthenticatorDB.isRoot(resourceUrl)) {
            if(null!=parentId){
                HIResource resourceByParent = hiResourceServiceDB.getResourceByParent(parentId);
                resourceUrl = resourceByParent.getResourceURL();
                pathList.add(resourceUrl);
                parentId=resourceByParent.getParentId();
            }else{
                break;
            }
        }
        Collections.reverse(pathList);
        return pathList;
    }
}

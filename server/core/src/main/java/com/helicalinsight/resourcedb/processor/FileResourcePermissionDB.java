package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.processor.model.Pair;
import com.helicalinsight.resourcedb.processor.model.ShareMap;
import com.helicalinsight.resourcesecurity.*;


import java.util.*;

public class FileResourcePermissionDB implements IResourcePermission {

    // private static final Logger logger = LoggerFactory.getLogger(FileResourcePermissionDB.class);

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;
    private Map<String, Object> resourceMap = null;

    public FileResourcePermissionDB(Map<String, Object> resourceMap) {
        this.resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
        this.resourceMap = resourceMap;
    }

    public int maximumPermissionLevelOnResource() {
        //If the file has no security tag
        boolean isOwner = false;

        boolean isSecurityTagPresent = this.resourceMap.containsKey("security");
        //TODO need to change this hardcoded value
        HIResource hiResource = (HIResource) this.resourceMap.get("folder");

        if (isSecurityTagPresent) {

            IResourceSecurityRule securityRule = ResourceSecurityRuleDB.getInstance();
            isOwner = securityRule.validateMap(resourceMap);
        }

        if (isOwner) {
            //Send owner's permission level
            return this.resourcePermissionLevelsHolder.ownerAccessLevel();
        } else {
            //Not owner. Check if it is shared. Else send public resource permission level
            boolean isShareTagPresent = resourceMap.containsKey("share");
            //TODO need to change this
            if (!isSecurityTagPresent) {
                if (!isShareTagPresent) {
                    return this.resourcePermissionLevelsHolder.publicResourceAccessLevel();
                }
            } else {
                //Security is present but not shared.
                if (!isSecurityTagPresent) {
                    if (!hiResource.getResourceURL().isEmpty()) {
                        return SecurityUtilsDB.maxInheritPermission(resourceMap);
                    } else {
                        return this.resourcePermissionLevelsHolder.noAccessLevel();
                    }
                }
            }
        }

        ShareMap share = (ShareMap) this.resourceMap.get("share");
        Map<String, List<Pair>> shareMap = share.getSecurityMap();
        boolean isResourceSharedWithOtherRoles = ShareRuleHelperDB.isResourceSharedWithOtherRoles(shareMap);
        boolean isResourceSharedWithUsers = ShareRuleHelperDB.isResourceSharedWithUsers(shareMap);
        boolean isResourceSharedWithOrganizations = ShareRuleHelperDB.isResourceSharedWithOrganizations(shareMap);
        List<Integer> permissions = new ArrayList<>();


        if(isResourceSharedWithOtherRoles){
            List<Pair> sharedRoles = ShareRuleHelperDB.getSharedRoles(shareMap);
            for(Pair pair:sharedRoles){
                permissions.add(pair.getPermission());
            }
        }

        if(isResourceSharedWithOrganizations){
            List<Pair> shareOrganization = ShareRuleHelperDB.getSharedOrganizations(shareMap);
            for(Pair pair:shareOrganization){
                permissions.add(pair.getPermission());
            }
        }

        if(isResourceSharedWithUsers){
            List<Pair> sharedUsers = ShareRuleHelperDB.getSharedUsers(shareMap);
            for(Pair pair:sharedUsers){
                permissions.add(pair.getPermission());
            }
        }

        if (!permissions.isEmpty()) {
            Integer max = Collections.max(permissions);
            if (max == -1) {
                if (resourceMap.containsKey("absolutePath")) {
                    if(resourceMap.containsKey("share")){
                        return SecurityUtilsDB.maxInheritPermission(resourceMap);
                    }else{
                        return SecurityUtilsDB.maxInheritPermission(resourceMap);
                    }
                }
            }
            return max;
        }




        return this.resourcePermissionLevelsHolder.noAccessLevel();
    }
}

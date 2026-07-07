package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.model.ShareData;


import java.util.*;
import java.util.stream.Collectors;

public class FolderShareRetrieval {
    String dir;
    String file;

    public FolderShareRetrieval() {

    }

    public FolderShareRetrieval(String dir, String file) {
        this.dir = dir;
        this.file = file;
    }

    public Map<String, Object> folderShareContent() {
        Map<String, Object> responseMap = new HashMap<>();
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();
        Map<String, Object> resourceSecurity = allResources.getResourcePermission();
        Integer permission = null;
        if (null==resourceSecurity.get(this.dir)) {
            throw new EfwServiceException("The directory specified can't be shared as it is " + "a public " +
                    "resource.");
        }else{
            permission=Integer.valueOf(""+resourceSecurity.get(this.dir));
        }
        DBProcessor dbProcessor = new DBProcessor();
        Integer resourceId = dbProcessor.findIdFromResource(allResources.getResourceDTOList(), this.dir);
        List<HIResourceSecurityDB> hiResourceSecurityByResourceIdDB = hiResourceServiceDB.getHIResourceSecurityByResourceId(resourceId);
        if (hiResourceSecurityByResourceIdDB == null || hiResourceSecurityByResourceIdDB.isEmpty()) {
            hiResourceSecurityByResourceIdDB = new ArrayList<>();
            responseMap.put("message", "The selected " + this.dir + " is not shared with other " +
                    "users/roles/organizations.");
        } else {
            boolean superOrganizationUser = AuthenticationUtils.isSuperOrganizationUser();
            if (superOrganizationUser) {
                addOrganizations(responseMap, hiResourceSecurityByResourceIdDB);
            }
            addUsers(responseMap, hiResourceSecurityByResourceIdDB);
            addRoles(responseMap, hiResourceSecurityByResourceIdDB);
        }
        return responseMap;
    }

    private void addOrganizations(Map<String, Object> data, List<HIResourceSecurityDB> list) {
        if (list.size() > 0) {
            List<ShareData> orgList = new ArrayList<>();
            for (HIResourceSecurityDB globalConnectionSecurity : list) {
                Organization orgId = globalConnectionSecurity.getOrgId();
                if (orgId != null) {
                    ShareData shareOrg= new ShareData();
                    shareOrg.setId(orgId.getId());
                    shareOrg.setPermission(globalConnectionSecurity.getPermission());
                    orgList.add(shareOrg);
                    data.put("organization", orgList);
                }
            }
        }
    }

	private void addUsers(Map<String,Object> data, List<HIResourceSecurityDB> hiResourceSecurityList) {
        if (!hiResourceSecurityList.isEmpty()) {
            int loggedInUserId = Integer.valueOf(AuthenticationUtils.getUserId());
        	List<ShareData> userList = new ArrayList<>();
            for (HIResourceSecurityDB hiResourceSecurity : hiResourceSecurityList) {
                User userId = hiResourceSecurity.getUserId();
                if (userId != null) {
                    ShareData user = new ShareData();
                    int shareUserId = userId.getId();
                    if(loggedInUserId != shareUserId ) {
	                    user.setId(userId.getId());
	                    user.setPermission(hiResourceSecurity.getPermission());
	                    userList.add(user);
                    }
                }
            }
            data.put("user", userList);
        }
    }

    private void addRoles(Map<String,Object> data, List<HIResourceSecurityDB> globalConnectionSecurityList) {
        if (globalConnectionSecurityList.size() > 0) {
            List<ShareData> roleList = new ArrayList<>();
            for (HIResourceSecurityDB globalConnectionSecurity : globalConnectionSecurityList) {
                Role roleId = globalConnectionSecurity.getRoleId();
                if (roleId != null) {
                    ShareData role= new ShareData();
                    role.setId(roleId.getId());
                    role.setPermission(globalConnectionSecurity.getPermission());
                    roleList.add(role);
                    data.put("role", roleList);
                }
            }
        }
    }
}

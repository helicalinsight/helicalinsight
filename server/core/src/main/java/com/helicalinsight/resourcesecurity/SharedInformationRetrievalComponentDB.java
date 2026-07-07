package com.helicalinsight.resourcesecurity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.FolderShareRetrieval;
import com.helicalinsight.resourcedb.ResourceConstants;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.resourcesecurity.model.ShareData;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 20-07-2015.
 *
 * @author Rajasekhar
 */
//rename this filename from SharedInformationRetrievalComponentDS to SharedInformationRetrievalComponentDB
public class SharedInformationRetrievalComponentDB extends SharedInformationRetrievalComponent implements IComponent {
    private Map<String, Object> responseMap = new HashMap<>();

    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        Map<String, Object> payload = responseJSONMap(jsonFormData);
        try{
            String data=new ObjectMapper().writeValueAsString(payload);
            return data;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }




    //rename with addOrganization with shareWithOrganizations
    private void shareWithOrg(Map<String, Object> data, List<GlobalConnectionSecurity> list) {
        if (list.size() > 0) {
            List<ShareData> orgList = new ArrayList<>();
            for (GlobalConnectionSecurity globalConnectionSecurity : list) {
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

    private void shareWithUsers(Map<String,Object> data, List<GlobalConnectionSecurity> globalConnectionSecurityList) {
        if (globalConnectionSecurityList.size() > 0) {
            List<ShareData> userList = new ArrayList<>();
            int currentLoggedInUserId = Integer.valueOf(AuthenticationUtils.getUserId()); 
            for (GlobalConnectionSecurity globalConnectionSecurity : globalConnectionSecurityList) {
                User userId = globalConnectionSecurity.getUserId();
                if (userId != null) {
                    ShareData user = new ShareData();
                    int sharedUserId = userId.getId();
                    if(currentLoggedInUserId != sharedUserId ) {
	                    user.setId(userId.getId());
	                    user.setPermission(globalConnectionSecurity.getPermission());
	                    userList.add(user);
                    }
                }
            }
            data.put("user", userList);
        }
    }

    private void shareWithRoles(Map<String,Object> data, List<GlobalConnectionSecurity> globalConnectionSecurityList) {
        if (globalConnectionSecurityList.size() > 0) {
            List<ShareData> roleList = new ArrayList<>();
            for (GlobalConnectionSecurity globalConnectionSecurity : globalConnectionSecurityList) {
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

    @Override
    public Map<String, Object> responseJSONMap(String jsonFormData) {
        /*Gson gson = new Gson();*/
        /*String s = gson.toJson(jsonFormData);*/
        /*Map<Object,Object> jsonFormDataMap = gson.fromJson(s.substring(1,s.length()-2),Map.class);*/
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        HashMap<String,Object> jsonFormDataMap = new HashMap<>();
        try{
            jsonFormDataMap = mapper.readValue(jsonFormData, typeRef);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


        String type = (String) jsonFormDataMap.get("type");
        String dir = (String) jsonFormDataMap.get("dir");
        String file = (String) jsonFormDataMap.get("file");
        Map<String, Object> responseMap = new HashMap<>();
        if ("dataSource".equalsIgnoreCase(type)) {
            if (!type.equalsIgnoreCase("datasource") || StringUtils.isNotEmpty(dir) || !JsonUtils.isDSTypeStorageDatabase()) {
                JSONObject jsonObject = JSONObject.fromObject(jsonFormData);
                String responseJson = super.executeComponent(jsonObject.toString());
                return JSONObject.fromObject(responseJson);
            }
            GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
            Integer id= Integer.valueOf(jsonFormDataMap.get("id").toString());
            List<GlobalConnectionSecurity> permissionList = globalConnectionService.findPermissionByConnectionId(id);
            String typeMessage=type;
            if(typeMessage.equals("dataSource")){
                typeMessage="datasource";
            }
            if (permissionList == null || permissionList.isEmpty()) {
                 responseMap.put("message", "The selected " + typeMessage + " is not shared with other " +
                        "users/roles/organizations.");
            } else {
                boolean superOrganizationUser = AuthenticationUtils.isSuperOrganizationUser();
                if (superOrganizationUser) {
                    shareWithOrg(responseMap, permissionList);
                }
                shareWithUsers(responseMap, permissionList);
                shareWithRoles(responseMap, permissionList);
            }
        } else if ("folder".equalsIgnoreCase(type) || "file".equalsIgnoreCase(type) ) {
            if (!JsonUtils.isFileBrowserStorageDatabase()) {
                JSONObject jsonObject = JSONObject.fromObject(jsonFormData);
                String responseJson = super.executeComponent(jsonObject.toString());
                return JSONObject.fromObject(responseJson);
            }
            if(StringUtils.isNotEmpty(file) && type.equalsIgnoreCase("file")){
                dir = dir+ ResourceConstants.FILE_SEPERATOR+file;
            }
            FolderShareRetrieval folderShareRetrieval = new FolderShareRetrieval(dir, file);

            Map<String, Object> folderShareContents = folderShareRetrieval.folderShareContent();
            return folderShareContents;
        } else {
            throw new IllegalArgumentException("The type parameter values should be either file " + "or folder.");
        }
        return responseMap;
    }


}

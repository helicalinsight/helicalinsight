package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.model.Pair;
import com.helicalinsight.resourcedb.processor.model.Security;
import com.helicalinsight.resourcedb.processor.model.ShareMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>
 * This is the utility class for converting json to map
 * </p>
 *
 * @author Karthik
 * @author Somen
 */

public class DBProcessor implements IProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DBProcessor.class);
    private String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";


    public Map<String, Object> getContent(String resource) {
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResource resourceByUrl = hiResourceServiceDB.getResourceByUrl(resource);
        String key = resourceByUrl.getResourceType().getName();
        Map<String, Object> resourceMap = new HashMap<>();
        resourceMap.put(key, resourceByUrl);
        List<HIResourceSecurityDB> hiResourceSecurityByResourceIdDB = hiResourceServiceDB.getHIResourceSecurityByResourceId(resourceByUrl.getResourceId());
        resourceMap.put("security", hiResourceSecurityByResourceIdDB);
        return resourceMap;
    }

    public Map<String, Object> createSecurityMap(String resource) {
        Map<String, Object> map = new HashMap<>();
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
        UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
        HIResource resourceByUrl = hiResourceServiceDB.getResourceByUrl(resource);
        map.put("folder", resourceByUrl);
        map.put("visible", resourceByUrl.getVisible());
        map.put("title", resourceByUrl.getTitle());
        Security security = new Security();
        security.setCreatedBy(Integer.valueOf(resourceByUrl.getCreatedBy()));
        User user = userService.findUser(Integer.valueOf(resourceByUrl.getCreatedBy()));
        Organization organization = user.getOrganization();
        List<HIResourceSecurityDB> securityList = hiResourceServiceDB.getHIResourceSecurityByResourceId(resourceByUrl.getResourceId());
        List<Role> roles = user.getRoles();
        if (null == organization) {
            security.setOrganizationList(new ArrayList<>());
        } else {
            List<Organization> organizationList = new ArrayList<>();
            organizationList.add(organization);
            security.setOrganizationList(organizationList);
        }
        map.put("security", security);
        ShareMap shareMap = new ShareMap();

        Map<String, List<Pair>> securityMap = new HashMap<>();
        for (HIResourceSecurityDB secure : securityList) {
            if (secure.getUserId() != null) {
                List<Pair> userPair = securityMap.get("users");
                if (userPair == null) {
                    userPair = new ArrayList<>();
                }
                Integer id = secure.getUserId().getId();
                Integer permission = secure.getPermission();
                Pair pair = new Pair(id, permission);
                userPair.add(pair);
                securityMap.put("users", userPair);
            }

            if (secure.getOrgId() != null) {
                List<Pair> orgPair = securityMap.get("organizations");
                if (orgPair == null) {
                    orgPair = new ArrayList<>();
                }
                Integer id = secure.getOrgId().getId();
                Integer permission = secure.getPermission();
                Pair pair = new Pair(id, permission);
                orgPair.add(pair);
                securityMap.put("organizations", orgPair);
            }

            if (secure.getRoleId() != null) {
                List<Pair> rolePair = securityMap.get("roles");
                if (rolePair == null) {
                    rolePair = new ArrayList<>();
                }
                Integer id = secure.getRoleId().getId();
                Integer permission = secure.getPermission();
                Pair pair = new Pair(id, permission);
                rolePair.add(pair);
                securityMap.put("roles", rolePair);
            }
        }
        shareMap.setSecurityMap(securityMap);
        map.put("share", shareMap);
        map.put("absolutePath", resourceByUrl.getResourceURL());
        return map;
    }

    public Integer getIdFromResourcePath(String dir){
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();
        HIResourceDTO rootElement = allResources.getResourceDTOList().stream().filter(path -> path.getPath().equals(dir)).findAny().orElse(null);
        if(rootElement==null){
            HIResourceDTO childElement = allResources.getResourceDTOList().stream().filter(c->c.getChildren().size()>0).flatMap(t -> t.getChildren().stream()).filter(f -> f.getPath().equals(dir)).findAny().orElse(null);
            return childElement.getResourceId();
        }else{
            return rootElement.getResourceId();
        }
    }

    public Integer getIdByPath(String dir,List<HIResourceDTO> dtoList){
        Integer resourceId=null;
        for(HIResourceDTO hiResourceDTO:dtoList){
            if(hiResourceDTO.getPath().equals(dir)){
                resourceId=hiResourceDTO.getResourceId();
                break;
            }
            else{
                if(hiResourceDTO.getChildren().size()>0){
                    if(null!=processChildren(hiResourceDTO.getChildren(),dir)){
                        return resourceId;
                    }
                }
            }
        }
        return resourceId;
    }

    private Integer processChildren(List<HIResourceDTO> dtoList,String dir){
        for(HIResourceDTO resourceDTO:dtoList){
            if(dir==resourceDTO.getPath()){
                return resourceDTO.getResourceId();
            }else{
                if(resourceDTO.getChildren().size()>0){
                    processChildren(resourceDTO.getChildren(),dir);
                }
            }
        }
        return null;
    }


    public Integer findIdFromResource(List<HIResourceDTO> dtoList,String dir){
        List<HIResourceDTO> flatten = flatten(dtoList);
        HIResourceDTO dtoObject = flatten.stream().filter(res -> res.getPath().equals(dir)).findAny().orElse(null);
        if(null!=dtoObject){
            return dtoObject.getResourceId();
        }
        return null;
    }

    public List<Integer> findChildIdFromResource(HIResourceDTO hiResourceDTO){
        List<HIResourceDTO> flatten = flatten(hiResourceDTO.getChildren());
        if(flatten.size()>0 && flatten!=null){
            List<Integer> childIds = new ArrayList<>();
            flatten.stream().forEach(childId->{
                childIds.add(childId.getParentId());
            });
            return childIds;
        }
        return null;
    }

    public long countResource(String url,List<HIResourceDTO> dtoList){
        List<HIResourceDTO> flatten = flatten(dtoList);
        long count = flatten.stream().filter(res -> res.getPath().equals(url)).count();
        return count;
    }

   public static List<HIResourceDTO> flatten(List<HIResourceDTO> list) {
       List<HIResourceDTO> ret = new LinkedList<HIResourceDTO>();
       flattenHelper(list, ret);
       //System.out.println(ret);
       return ret;
   }


    public static void flattenHelperFolder(List<HIResourceDTO> nestedList, List<HIResourceDTO> flatList) {
            for (HIResourceDTO item : nestedList) {
                if (null!=item.getChildren() && item.getChildren().size()>0) {
                    flatList.add(item);
                    flattenHelper((List<HIResourceDTO>) item.getChildren(), flatList);
                } else {
                    flatList.add(item);
                }
            }
    }

    public static void flattenHelper(List<HIResourceDTO> nestedList, List<HIResourceDTO> flatList) {
           for (HIResourceDTO item : nestedList) {
               if (null!=item.getChildren() && item.getChildren().size()>0) {
                   flatList.add(item);
                   flattenHelper((List<HIResourceDTO>) item.getChildren(), flatList);
               } else {
                   flatList.add(item);
               }
           }
    }

    public static String checkAndReplaceSpecialChars(String resourceName){
        String replacedStr = resourceName.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("_{2,}", "_");
        if(replacedStr.equals("_") || replacedStr.startsWith("_")){
            throw  new EfwServiceException("The name cannot be only special characters or starts with a special  characters");
        }

        return replacedStr;
    }

}

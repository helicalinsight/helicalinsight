package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceTypeIDMap {
    private static Map<Long, ResourceType> resourceTypeMap=new HashMap<>();
    private static Map<Long, String> resourceIdExtension=new HashMap<>();
    private static Map<Long, String> resourceIdNameMap=new HashMap<>();
    private static Map<String,Long> resourceNameIdMap=new HashMap<>();

    public static Map<Long,ResourceType>  getResourceTypeMap(){

        if(resourceTypeMap.size()==0){
            ResourceTypeServiceDB resourceTypeService = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);
            List<ResourceType> allResourceTypes = resourceTypeService.getAllResourceTypes();
            resourceTypeMap = allResourceTypes.stream()
                    .collect(Collectors.toMap(ResourceType::getResourceTypeId, resourceType -> resourceType));
            resourceIdExtension = allResourceTypes.stream()
                    .collect(Collectors.toMap(ResourceType::getResourceTypeId, ResourceType::getExtension));
            resourceIdNameMap = allResourceTypes.stream()
                    .collect(Collectors.toMap(ResourceType::getResourceTypeId, ResourceType::getName));
            resourceNameIdMap = allResourceTypes.stream()
                    .collect(Collectors.toMap(ResourceType::getName,ResourceType::getResourceTypeId));
        }
        return resourceTypeMap;

    }
    public static ResourceType getResourceTypeById(Long id){
        getResourceTypeMap();
        return resourceTypeMap.get(id);
    }
    public static String getResourceTypExtension(Long id){
        getResourceTypeMap();
        return resourceIdExtension.get(id);
    }

    public static String getResourceTypName(Long id){
        getResourceTypeMap();
        return resourceIdNameMap.get(id);
    }
    
    public static Long getResourceIdByName(String name){
        getResourceTypeMap();
        return resourceNameIdMap.get(name);
    }

}

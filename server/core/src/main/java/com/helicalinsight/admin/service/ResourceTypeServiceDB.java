package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.ResourceType;

import java.util.List;

public interface ResourceTypeServiceDB {
    Long addResourceType(ResourceType resourceType);

    void editResourceType(ResourceType resourceType);

    void deleteResourceType(Long resourceTypeId);

    ResourceType getResourceType(Long resourceTypeId);

    ResourceType getResourceTypeByTypeAndExtension(String type, String extension);

    ResourceType findUniqueResourceType(ResourceType sampleResourceType);

    void deleteAllResourceType();

    List<ResourceType> getAllResourceTypes();


}
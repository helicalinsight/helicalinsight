package com.helicalinsight.scheduling.dao;

import com.helicalinsight.admin.model.ResourceType;



import java.util.List;

/**
 * Created by author on 3/13/2020.
 *
 * @author Rajesh
 */
public interface ResourceTypeDao {
    Long addResourceType(ResourceType resourceType);

    void editResourceType(ResourceType resourceType);

    void deleteResourceType(Long resourceTypeId);

    ResourceType getResourceType(Long resourceTypeId);

    ResourceType findUniqueResourceType(ResourceType sampleResourceType);

    ResourceType getResourceTypeByTypeAndExtension(String type, String extension);

    void deleteAllResourceType();

    List<ResourceType> getAllResourceTypes();


}

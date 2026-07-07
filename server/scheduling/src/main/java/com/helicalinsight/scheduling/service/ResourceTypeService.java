package com.helicalinsight.scheduling.service;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;

import java.util.List;

/**
 * Created by author on 3/13/2020.
 *
 * @author Rajesh
 */
public interface ResourceTypeService {
    Long addResourceType(ResourceType resourceType);

    void editResourceType(ResourceType resourceType);

    void deleteResourceType(Long resourceTypeId);

    ResourceType getResourceType(Long resourceTypeId);

    ResourceType getResourceTypeByTypeAndExtension(String type, String extension);

    ResourceType findUniqueResourceType(ResourceType sampleResourceType);

    void deleteAllResourceType();

    List<ResourceType> getAllResourceTypes();


}

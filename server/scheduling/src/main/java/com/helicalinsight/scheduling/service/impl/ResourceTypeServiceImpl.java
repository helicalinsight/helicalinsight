package com.helicalinsight.scheduling.service.impl;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.scheduling.dao.ResourceTypeDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import com.helicalinsight.scheduling.service.ResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * ResourceTypeServiceImpl class performs crud operation on {@link ResourceType} entity
 * which implements {@link ResourceTypeService} interface.
 * Created by author on 3/16/2020.
 * @author Rajesh
 */
@Service
public class ResourceTypeServiceImpl implements ResourceTypeService {

    @Autowired
    ResourceTypeDao resourceTypeDao;

    @Transactional
    @Override
    public Long addResourceType(ResourceType resourceType) {
        return resourceTypeDao.addResourceType(resourceType);
    }

    @Transactional
    @Override
    public void editResourceType(ResourceType resourceType) {
        resourceTypeDao.editResourceType(resourceType);
    }

    @Transactional
    @Override
    public void deleteResourceType(Long resourceTypeId) {
        resourceTypeDao.deleteResourceType(resourceTypeId);
    }

    @Transactional
    @Override
    public ResourceType getResourceType(Long resourceTypeId) {
        return resourceTypeDao.getResourceType(resourceTypeId);
    }

    @Transactional
    @Override
    public ResourceType getResourceTypeByTypeAndExtension(String type, String extension) {
        return resourceTypeDao.getResourceTypeByTypeAndExtension(type, extension);
    }

    @Transactional
    @Override
    public ResourceType findUniqueResourceType(ResourceType sampleResourceType) {
        return resourceTypeDao.findUniqueResourceType(sampleResourceType);
    }

    @Transactional
    @Override
    public void deleteAllResourceType() {
        resourceTypeDao.deleteAllResourceType();
    }

    @Transactional
    @Override
    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeDao.getAllResourceTypes();
    }
}

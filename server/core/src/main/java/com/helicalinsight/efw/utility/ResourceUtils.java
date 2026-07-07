package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

import java.util.Date;


public class ResourceUtils {
	
	private ResourceUtils() {
		// NOOP
	}


    public static HIResource newHIResource(String location, Boolean isDirectory, String title, String path, String resourceUrl,String extension) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResource parentHIResource = serviceDB.getResourceByUrl(location);
        Security security = SecurityUtils.securityObject();
        String createdBy = security.getCreatedBy();
        HIResource hiResource = new HIResource();
        Integer parentId = parentHIResource == null ? null : parentHIResource.getResourceId();
        setCommonProperties(hiResource, extension, Integer.valueOf(createdBy), parentId, resourceUrl, path, title);
        return hiResource;
    }
    
    public static HIResource newHIResource(String extension , Date date, Integer ownerId, String uniqueUrl,String path,String title, Integer parentId, boolean isPublic) {
    	HIResource resource = new HIResource();
    	if(isPublic) {
    		ownerId = null;
    	}
    	else {
    		ownerId = ownerId == null ? Integer.valueOf(SecurityUtils.securityObject().getCreatedBy()):ownerId;
    	}
    	setCommonProperties(resource, extension, ownerId, parentId, uniqueUrl, path, title);
		resource.setLastUpdatedTime(date);
		resource.setCreated_date(date);
    	return resource;
    }
    
    private static final void setCommonProperties(HIResource hiResource,String extension , Integer ownerId ,Integer parentId, String resourceUrl,String path,String title) {
    	Date date = new Date();
    	hiResource.setCreated_date(date);
    	hiResource.setLastUpdatedTime(date);
    	hiResource.setCreatedBy(ownerId);
    	hiResource.setParentId(parentId);
    	hiResource.setResourceURL(resourceUrl);
    	hiResource.setTitle(title);
    	hiResource.setResourcePath(path);
    	hiResource.setVisible(true);
    	hiResource.setMigrated(false);
    	hiResource.setDeleted(false);
    	ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);
    	ResourceType efwddType = resourceTypeServiceDB.getAllResourceTypes().stream().filter(rt -> rt.getExtension().equals("."+extension)).findFirst().orElse(null);
		if (null != efwddType) {
			hiResource.setResourceType(efwddType);
			hiResource.setFolder("folder".equalsIgnoreCase(efwddType.getName()));
		}

    }
}

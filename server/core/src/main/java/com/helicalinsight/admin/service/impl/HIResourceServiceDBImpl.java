package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.efw.utility.ResourceTypeIDMap;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class HIResourceServiceDBImpl implements HIResourceServiceDB {
    @Autowired
    private HIResourceDBDAO hiResourceDBDAO;

    @Autowired
    private ResourceTypeServiceDB resourceTypeServiceDB;

    @Override
    @Transactional
    public Integer addHIResource(HIResource hiResource) {
    	if (hiResource.isDeleted() == null) {
    		hiResource.setDeleted(false);
    	}
    	try {
    		return hiResourceDBDAO.addHIResource(hiResource);
    	} catch (ConstraintViolationException e) {
    		String newUrl = hiResourceDBDAO.getUniqueUrl(hiResource.getResourceURL(), hiResource.getResourceType());
    		hiResource.setResourceURL(newUrl);
    		hiResource.setResourcePath(FilenameUtils.getBaseName(newUrl));
    		return hiResourceDBDAO.addHIResource(hiResource);
    	}
	}



    @Override
    @Transactional
    public Integer editHIResource(HIResource hiResource) {
        return hiResourceDBDAO.editHIResource(hiResource);
    }

    @Override
    @Transactional
    public void deleteHIResource(Integer resourceId,List<Integer> resourceIds,Boolean isFolder) {
        hiResourceDBDAO.deleteHIResource(resourceId,resourceIds,isFolder);
    }

    @Override
    @Transactional
    public Integer addHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB) {
        return hiResourceDBDAO.addHIResourceSecurity(hiResourceSecurityDB);
    }

    @Override
    @Transactional
    public Integer editHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB) {
        return hiResourceDBDAO.editHIResourceSecurity(hiResourceSecurityDB);
    }

    @Override
    @Transactional
    public void deleteHIResourceSecurity(Integer resourceId) {
        hiResourceDBDAO.deleteHIResourceSecurity(resourceId);
    }

    @Override
    @Transactional
    public HIResource getHIResourceById(Integer resourceId) {
        HIResource resource =  hiResourceDBDAO.getHIResourceById(resourceId,true);
        if ( resource == null ) {
        	throw new EfwServiceException("Resource does not exist.");
        }
        return resource;
    }

    @Override
    @Transactional
    public List<HIResource> getAllHIResources() {
        return hiResourceDBDAO.getAllHIResources();
    }

    @Override
    @Transactional
    public List<HIResourceSecurityDB> getHIResourceSecurityByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceSecurityByResourceId(resourceId);
    }

    @Override
    @Transactional
    public Integer addHIResourceFolder(HIResourceFolder hiResourceFolder) {
        return hiResourceDBDAO.addHIResourceFolder(hiResourceFolder);
    }

    @Override
    @Transactional
    public Integer editHIResourceFolder(HIResourceFolder hiResourceFolder) {
        return hiResourceDBDAO.editHIResourceFolder(hiResourceFolder);
    }

    @Override
    @Transactional
    public void deleteHIResourceFolder(Integer id) {
        hiResourceDBDAO.deleteHIResourceFolder(id);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFW(HIResourceEFW hiResourceEFW) {
        return hiResourceDBDAO.addHIResourceEFW(hiResourceEFW);
    }

    @Override
    @Transactional
    public Integer editHIResourceEFW(HIResourceEFW hiResourceEFW) {
        return hiResourceDBDAO.editHIResourceEFW(hiResourceEFW);
    }

    @Override
    @Transactional
    public void deleteHIResourceEFW(Integer id) {
        hiResourceDBDAO.deleteHIResourceEFW(id);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFWDD(HIResourceEFWDD hiResourceEFWDD) {
        return hiResourceDBDAO.addHIResourceEFWDD(hiResourceEFWDD);
    }

    @Override
    @Transactional
    public Integer editHIResourceEFWDD(HIResourceEFWDD hiResourceEFWDD) {
        return hiResourceDBDAO.editHIResourceEFWDD(hiResourceEFWDD);
    }

    @Override
    @Transactional
    public void deleteHIResourceEFWDD(Integer id) {
        hiResourceDBDAO.deleteHIResourceEFWDD(id);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE) {
        return hiResourceDBDAO.addHIResourceEFWCE(hiResourceEFWCE);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFWContents(ResourceEfwContents resourceEfwContents) {
        return hiResourceDBDAO.addHIResourceEFWContents(resourceEfwContents);
    }

    @Override
    @Transactional
    public Boolean editHIResourceEFWContents(ResourceEfwContents resourceEfwContents) {
        return hiResourceDBDAO.editHIResourceEFWContents(resourceEfwContents);
    }

    @Override
    @Transactional
    public ResourceEfwContents getHIResourceEFWContents(Integer contentId) {
        return hiResourceDBDAO.getHIResourceEFWContents(contentId);
    }
    
    @Override
    @Transactional
	public ResourceEfwContents getHIResourceEfwContents(String fileName) {
		return hiResourceDBDAO.getHIResourceEFWContents(fileName);
	}

    @Override
    @Transactional
    public Boolean deleteHIResourceEFWContents(Integer contentId) {
        return hiResourceDBDAO.deleteHIResourceEFWContents(contentId);
    }

    @Override
    @Transactional
    public Integer editHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE) {
        return hiResourceDBDAO.editHIResourceEFWCE(hiResourceEFWCE);
    }

    @Override
    @Transactional
    public void deleteHIResourceEFWCE(Integer id) {
        hiResourceDBDAO.deleteHIResourceEFWCE(id);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR) {
        return hiResourceDBDAO.addHIResourceEFWSR(hiResourceEFWSR);
    }

    @Override
    @Transactional
    public Integer editHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR) {
        return hiResourceDBDAO.editHIResourceEFWSR(hiResourceEFWSR);
    }

    @Override
    @Transactional
    public void deleteHIResourceEFWSR(Integer id) {
        hiResourceDBDAO.deleteHIResourceEFWSR(id);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFWD(HIEFWD hiResourceEFWD) {
        return hiResourceDBDAO.addHIResourceEFWD(hiResourceEFWD);
    }

    @Override
    @Transactional
    public Integer editHIResourceEFWD(HIEFWD hiResourceEFWD) {
        return hiResourceDBDAO.editHIResourceEFWD(hiResourceEFWD);
    }

    @Override
    @Transactional
    public void deleteHIResourceEFWD(Integer id) {
        hiResourceDBDAO.deleteHIResourceEFWD(id);
    }


    @Override
    @Transactional
    public HIEFWD getHIResourceEFWDByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceEFWDByResourceId(resourceId);
    }

    @Override
    @Transactional
    public HIResourceEFWDD getHIResourceEFWDDByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceEFWDDByResourceId(resourceId);
    }

    @Override
    @Transactional
    public HIResourceEFWCE getHIResourceEFWCEByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceEFWCEByResourceId(resourceId);
    }

    @Override
    @Transactional
    public HIResourceEFW getHIResourceEFWByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceEFWByResourceId(resourceId);
    }

    @Override
    @Transactional
    public HIResourceResult getHIResourceResultByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceResultByResourceId(resourceId);
    }

    @Override
    @Transactional
    public HIResourceFolder getHIResourceFolderByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceFolderByResourceId(resourceId);
    }


    @Override
    @Transactional
    public HIResourceHReport getHIResourceHReportByResultId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceHReportByResultId(resourceId);
    }

    @Override
    @Transactional
    public Integer addHIResourceHwf(HIResourceHWF hiResourceHWF) {
        return hiResourceDBDAO.addHIResourceHwf(hiResourceHWF);
    }

    @Override
    @Transactional
    public Integer editHIResourceHwf(HIResourceHWF hiResourceHWF) {
        return hiResourceDBDAO.editHIResourceHwf(hiResourceHWF);
    }

    @Override
    @Transactional
    public void deleteHIResourceHwfById(Integer id) {
        hiResourceDBDAO.deleteHIResourceHwfById(id);
    }

    @Override
    @Transactional
    public HIResourceHWF getHIResourceByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceByResourceId(resourceId);
    }

    @Override
    @Transactional
    public Integer addHIHwfExecution(HWFExecution hiHwfExecution) {
        return hiResourceDBDAO.addHIHwfExecution(hiHwfExecution);
    }

    @Override
    @Transactional
    public Integer editHIHwfExecution(HWFExecution hiHwfExecution) {
        return hiResourceDBDAO.editHIHwfExecution(hiHwfExecution);
    }

    @Override
    @Transactional
    public void deleteHIHwfExecution(Integer id) {
        hiResourceDBDAO.deleteHIHwfExecution(id);
    }

    @Override
    @Transactional
    public HWFExecution getHIHwfExecutionByResourceId(Integer id) {
        return hiResourceDBDAO.getHIHwfExecutionByResourceId(id);
    }

    @Override
    @Transactional
    public Integer addHIHwfInput(HIHwfInput hiHwfInput) {
        return hiResourceDBDAO.addHIHwfInput(hiHwfInput);
    }

    @Override
    @Transactional
    public Integer editHIHwfInput(HIHwfInput hiHwfInput) {
        return hiResourceDBDAO.editHIHwfInput(hiHwfInput);
    }

    @Override
    @Transactional
    public void deleteHIHwfInput(Integer id) {
        hiResourceDBDAO.deleteHIHwfInput(id);
    }

    @Override
    @Transactional
    public HIHwfInput getHIHwfInput(Integer id) {
        return hiResourceDBDAO.getHIHwfInput(id);
    }

    @Override
    @Transactional
    public Integer addHIHwfOutput(HIHwfOutput hiHwfOutput) {
        return hiResourceDBDAO.addHIHwfOutput(hiHwfOutput);
    }

    @Override
    @Transactional
    public Integer editHIHwfOutput(HIHwfOutput hiHwfOutput) {
        return hiResourceDBDAO.editHIHwfOutput(hiHwfOutput);
    }

    @Override
    @Transactional
    public void deleteHIHwfOutput(Integer id) {
        hiResourceDBDAO.deleteHIHwfOutput(id);
    }

    @Override
    @Transactional
    public HIHwfOutput getHIHwfOutput(Integer id) {
        return hiResourceDBDAO.getHIHwfOutput(id);
    }

    @Override
    @Transactional
    public Integer addHwfExecutionInput(HWFExecutionInput hwfExecutionInput) {
        return hiResourceDBDAO.addHwfExecutionInput(hwfExecutionInput);
    }

    @Override
    @Transactional
    public Integer addHwfExecutionOutput(HWFExecutionOutput hwfExecutionOutput) {
        return hiResourceDBDAO.addHwfExecutionOutput(hwfExecutionOutput);
    }

    @Override
    @Transactional
    public Integer editHwfExecutionInput(HWFExecutionInput hwfExecutionInput) {
        return hiResourceDBDAO.editHwfExecutionInput(hwfExecutionInput);
    }

    @Override
    @Transactional
    public Integer editHwfExecutionOutput(HWFExecutionOutput hwfExecutionOutput) {
        return hiResourceDBDAO.editHwfExecutionOutput(hwfExecutionOutput);
    }

    @Override
    @Transactional
    public void deleteHwfExecutionInput(Integer id) {
        hiResourceDBDAO.deleteHwfExecutionInput(id);
    }

    @Override
    @Transactional
    public HWFExecutionInput getHwfExecutionInput(Integer id) {
        return hiResourceDBDAO.getHwfExecutionInput(id);
    }

    @Override
    @Transactional
    public void deleteHwfExecutionOutput(Integer id) {
        hiResourceDBDAO.deleteHwfExecutionOutput(id);
    }

    @Override
    @Transactional
    public HWFExecutionOutput getHwfExecutionOutput(Integer id) {
        return hiResourceDBDAO.getHwfExecutionOutput(id);
    }

    @Override
    @Transactional
    public HIResource getResourceByPath(String path, Integer createdBy) {
        return hiResourceDBDAO.getResourceByPath(path, createdBy);
    }

    @Override
    @Transactional
    public HIResource getHIResourceFolderByName(String name) {
        return hiResourceDBDAO.getHIResourceFolderByName(name);
    }

    @Override
    @Transactional
    public void deleteHIResourceById(Integer id) {
        hiResourceDBDAO.deleteHIResourceById(id);
    }

    @Override
    @Transactional
    public void cleanUp() {
        hiResourceDBDAO.cleanUp();
    }

    @Override
    @Transactional
    public Integer addHIUrlMapping(HIUrlMapping hiUrlMapping) {
        return hiResourceDBDAO.addHIUrlMapping(hiUrlMapping);
    }

    @Override
    @Transactional
    public Integer editHIUrlMapping(HIUrlMapping hiUrlMapping) {
        return hiResourceDBDAO.editHIUrlMapping(hiUrlMapping);
    }

    @Override
    @Transactional
    public void deleteHIUrlMapping(Integer id) {
        hiResourceDBDAO.deleteHIUrlMapping(id);
    }

    @Override
    @Transactional
    public HIUrlMapping getHIUrlMapping() {
        return hiResourceDBDAO.getHIUrlMapping();
    }

    @Override
    @Transactional
    public HIUrlMapping getHIUrlMappingByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIUrlMappingByResourceId(resourceId);
    }

    @Override
    @Transactional
    public List<HIUrlMapping> getUrlMapping(String url) {
        return hiResourceDBDAO.getUrlMapping(url);
    }

    @Override
    @Transactional
    public HIUrlMapping getUrlMap(String url) {
        return hiResourceDBDAO.getUrlMap(url);
    }

    @Override
    @Transactional
    public HIResource getIsFolderByResourceId(Integer id) {
        return hiResourceDBDAO.getIsFolderByResourceId(id);
    }

    @Override
    @Transactional
    public Long countResourceByUrl(String url, Integer createdBy) {
        return hiResourceDBDAO.countResourceByUrl(url, createdBy);
    }

    @Override
    @Transactional
    public HIResource getResourceByUrl(String url) {
        return hiResourceDBDAO.getResourceByUrl(url,true);
    }
    
    @Override
    @Transactional
    public HIResource getResourceByUrl(String url, boolean applyFilter) {
        return hiResourceDBDAO.getResourceByUrl(url,applyFilter);
    }
    
    @Override
    @Transactional
    public HIResource getResourceByFolderName(String folderName) {
        return hiResourceDBDAO.getResourceByFolderName(folderName);
    }

    @Override
    @Transactional
    public List<HIResource> getResourceListByUrl(String url) {
        return hiResourceDBDAO.getResourceListByUrl(url);
    }

    @Override
    @Transactional
    public void deleteResource(Integer resourceId,List<Integer> resourceIds,Boolean isFolder) {
        hiResourceDBDAO.deleteResource(resourceId,resourceIds,isFolder);
    }

    @Override
    @Transactional
    public Boolean isTableEmpty() {
        return hiResourceDBDAO.isTableEmpty();
    }

    @Override
    @Transactional
    public HIResource getResourceByParent(Integer parentId) {
        return hiResourceDBDAO.getResourceByParent(parentId);
    }

   /* @Override
    @Transactional
    public Integer addHIResourceReport(HIResourceReport hiReport) {
        return hiResourceDBDAO.addHIResourceReport(hiReport);
    }*/

    @Override
    @Transactional
    public List<HIResourceSecurityDB> getHIResourceSecurityByCreatedBy(Integer createdBy) {
        return hiResourceDBDAO.getHIResourceSecurityByCreatedBy(createdBy);
    }

    @Override
    @Transactional
    public Long countResourceByCreatedBy(Integer createdBy) {
        return hiResourceDBDAO.countResourceByCreatedBy(createdBy);
    }

    @Override
    @Transactional
    public List<HIResource> getHIResourceByCreatedBy(Integer createdBy) {
        return hiResourceDBDAO.getHIResourceByCreatedBy(createdBy,false);
    }

    @Override
    @Transactional
    public List<HIResource> getHIResourceByPath(String path) {
        return hiResourceDBDAO.getHIResourceByPath(path);
    }

    @Override
    @Transactional
    public List<HIResource> getResourceByParentId(Integer parentId) {
        return hiResourceDBDAO.getResourceByParentId(parentId,null);
    }
    
    @Override
    @Transactional
    public List<HIResource> getResourceByParentId(Integer parentId,Object searchParam) {
        return hiResourceDBDAO.getResourceByParentId(parentId,searchParam);
    }
    
    @Override
    @Transactional
    public List<HIResource> getAllConnectedResource(Integer resourceId){
        return hiResourceDBDAO.getAllConnectedResource(resourceId);
    }

    @Override
    @Transactional
    public HIResource getResourceByPath(String path) {
        return hiResourceDBDAO.getResourceByPath(path);
    }

    /*
     * @Inherited
     */
    @Deprecated
    @Override
    @Transactional
    public HIResourceOfActiveUser getAllResources() {
        List<HIResource> allResourceList = hiResourceDBDAO.getAllResourceList(true);
        Map<Integer, Integer> securityMap = hiResourceDBDAO.getSecurityMap();
        HIResourceOfActiveUser securityUtil = new HIResourceOfActiveUser(securityMap, allResourceList);
        return securityUtil;
    }
    
    @Transactional
	@Override
	public HIResourceOfActiveUser getResourceOfActiveUser() {
		Map<Integer,Integer> securityMap = getSecurityMap();
		Set<Integer> resourceIds = new HashSet<Integer>(securityMap.keySet());
		resourceIds.addAll( hiResourceDBDAO.getHIResourceIdsOfActiveUser());
		resourceIds.addAll(hiResourceDBDAO.getChildrenResourceByParentIds(new ArrayList<>(resourceIds)));
		List<HIResource> allResources = hiResourceDBDAO.getHIResourcesByIds(new ArrayList<>(resourceIds), true);
		return new HIResourceOfActiveUser(securityMap, allResources);
	}

    @Transactional
    @Override
    public HIResourceOfActiveUser getAllResourcesOfAnyUsr(Integer userId, List<String> roleId, Integer orgId) {
        // This method returns the security map for the given user, role, and org
        List<HIResource> allResourceList = hiResourceDBDAO.getAllResourceList(true);
        Map<Integer, Integer> securityMap = hiResourceDBDAO.getSecurityMapOfAnyUser(userId, roleId, orgId);
        HIResourceOfActiveUser securityUtil = new HIResourceOfActiveUser(securityMap, allResourceList,userId);
        return securityUtil;

    }
    



    @Override
    @Transactional
    public HIResourceOfActiveUser findAllResources() {
        List<HIResource> allResourceList = hiResourceDBDAO.getAllResourceList(false);
        Map<Integer, Integer> securityMap = hiResourceDBDAO.getSecurityMap();
        HIResourceOfActiveUser securityUtil = new HIResourceOfActiveUser(securityMap, allResourceList);
        return securityUtil;
    }
    @Override
    @Transactional
    public HIResourceOfActiveUser findAllResources(String resourceUrl) {
        List<HIResource> allResourceList = hiResourceDBDAO.findAllResources(resourceUrl);
        Map<Integer, Integer> securityMap = hiResourceDBDAO.getSecurityMap();
        HIResourceOfActiveUser securityUtil = new HIResourceOfActiveUser(securityMap, allResourceList);
        return securityUtil;
    }

    @Override
    @Transactional
    public HIResourceOfActiveUser findAllResourcesIncludingReadOnly() {

        ResourcePermissionLevelsHolder permissionLevelHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);

        List<HIResource> allResourceList = hiResourceDBDAO.getAllResourceList(false);
        Map<Integer, Integer> securityMap = hiResourceDBDAO.getSecurityMap();
        HIResourceOfActiveUser securityUtil = new HIResourceOfActiveUser(securityMap, allResourceList,null, permissionLevelHolder.executeAccessLevel());
        return securityUtil;
    }
    
    @Override
    @Transactional
    public  Map<Integer, Integer> getSecurityMap() {
       return  hiResourceDBDAO.getSecurityMap();
    }  @Override
    @Transactional
    public HIResourceEFWSR getHIResourceEFWSRByResourcesId(Integer resourceId){
        return hiResourceDBDAO.getHIResourceEFWSRByResourcesId(resourceId);
    }

   @Transactional
   public List<HIResourceEFWSR> getAllHIResourceEFWSRByResourcesIds(List<Integer> resourceIds){
       return hiResourceDBDAO.getAllHIResourceEFWSRByResourcesIds(resourceIds);
   }

    @Override
    @Transactional
    public HIResourceOfActiveUser getAllResourcesWithExtensions(List<String> extension) {
        List<Long> resourceTypeList = new ArrayList<>();
        List<ResourceType> allResourceTypes = resourceTypeServiceDB.getAllResourceTypes();
        ResourceType resourceTypeFolder = allResourceTypes.stream().filter(rt -> rt.getName().equals("folder")).findFirst().orElse(null);

        for (String s : extension) {
            if (s.equals("folder")) {
                    resourceTypeList.add(resourceTypeFolder.getResourceTypeId());
            }else{
            ResourceType resourceType = allResourceTypes.stream().filter(rt -> rt.getName().equals(s)).findFirst().orElse(null);
            if(resourceType!=null){
                resourceTypeList.add(resourceType.getResourceTypeId());
            }
            }

        }
        List<HIResource> allResourceList = hiResourceDBDAO.getAllResourceListWithExtension(resourceTypeList);
        Map<Integer, Integer> securityMap = hiResourceDBDAO.getSecurityMap();
        HIResourceOfActiveUser securityUtil = new HIResourceOfActiveUser(securityMap, allResourceList);
        return securityUtil;
    }

    @Override
    @Transactional
    public void deleteHIResourceSecurityByResourceId(int resourceId, Integer userId, Integer orgId, Integer roleId) {
        hiResourceDBDAO.deleteHIResourceSecurityByResourceId(resourceId, userId, orgId, roleId);
    }

    @Override
    @Transactional
    public void updateOrInsert(int resourceId, Integer userId, Integer orgId, Integer roleId, Integer permission, String createdBy) {
        hiResourceDBDAO.updateOrInsert(resourceId, userId, orgId, roleId, permission, createdBy);
    }

    @Override
    @Transactional
    public List<HIResourceSecurityDB> getAllResourceSecurity() {
        return hiResourceDBDAO.getAllResourceSecurity();
    }

    @Override
    @Transactional
    public HIResource getResourceIdFromTitleAndURL(String dir, String title) {
        return hiResourceDBDAO.getResourceIdFromTitleAndURL(dir,title);
    }

    @Override
    @Transactional
    public Integer addHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF) {
        return hiResourceDBDAO.addHIResourceEFWVF(hiResourceEFWVF);
    }

    @Override
    @Transactional
    public Integer editHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF) {
        return hiResourceDBDAO.editHIResourceEFWVF(hiResourceEFWVF);
    }

    @Override
    @Transactional
    public void deleteHIResourceEFWVF(Integer id) {
        hiResourceDBDAO.deleteHIResourceEFWVF(id);
    }

    @Override
    @Transactional
    public HIResourceEFWVF getHIResourceEFWVFById(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceEFWVFById(resourceId);
    }

    @Override
    @Transactional
   public void editHIReport(HIResourceHReport hiResourceHReport){
         hiResourceDBDAO.editHIReport(hiResourceHReport);
    }

    @Override
    @Transactional
    public void editHIResourceHCR(HIResourceHCR hiResourceHCR) {
        hiResourceDBDAO.editHIResourceHCR(hiResourceHCR);
    }

	@Override
	@Transactional
	
	public HIResource getDeletedResourceById(Integer id) {
		return hiResourceDBDAO.getDeletedResourceById(id);
	}

	@Override
	@Transactional
	public HIResource getResourceByIdIgnoreFilter(Integer id) {
		return hiResourceDBDAO.getHIResourceById(id,false);
	}
	
	@Transactional
	@Override
	public boolean hardDelete(HIResource hiResource) {
		return hiResourceDBDAO.hardDelete(hiResource);
	}

	@Override
	@Transactional
	public List<HIResource> getHIResourceByCreatedBy(Integer createdBy, boolean includeFilter) {
		return hiResourceDBDAO.getHIResourceByCreatedBy(createdBy,includeFilter);
	}

	@Override
	@Transactional
	public Integer getEfwContentIdByResourceId(Integer resourceId) {
		return hiResourceDBDAO.getEfwContentIdByResourceId(resourceId);
	}

	@Override
	@Transactional
	public void hardDeleteFromHiResourceEfw(Integer id) {
		hiResourceDBDAO.hardDeleteFromHiResourceEfw(id);
	}

	@Override
	@Transactional
	public Long getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(Integer hiResourceId) {
		return hiResourceDBDAO.getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(hiResourceId);
    }

    @Transactional
    @Override
	public List<HIResource> findAllReportsByMetadataResourceId(Integer resourceId) {
		return hiResourceDBDAO.findAllReportsByMetadataResourceId(resourceId);
	}

    @Transactional
	@Override
	public List<HIResource> findAllEfwsrFilesByReportDirAndFile(String directory, String reportFile) {
		return hiResourceDBDAO.findAllEfwsrFilesByReportDirAndFile(directory,reportFile);
	}

    @Transactional
	@Override
	public void hardDeleteFromHiResourceEfwByResourceId(Integer resourceId) {
		hiResourceDBDAO.hardDeleteFromHiResourceEfwByResourceId(resourceId);
	}

    @Transactional
	@Override
	public HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceId(Integer userId,Integer resourceId) {
		return hiResourceDBDAO.fetchPermissionBySharedUserIdAndResourceId(userId,resourceId);
	}
    @Transactional
	@Override
	public HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceIdTest(Integer userId,Integer resourceId) {
		return hiResourceDBDAO.fetchPermissionBySharedUserIdAndResourceIdTest(userId,resourceId);
	}

    @Transactional
	@Override
	public List<HIEFWD> getHIResourceEFWDByParentResourceId(Integer resourceId) {
		return hiResourceDBDAO.getHIResourceEFWDByParentResourceId(resourceId);
	}
    
    @Transactional
	@Override
	public List<HIEfwdDTO> findHIResourceEFWDByParentResourceId(Integer resourceId) {
		List<HIEFWD> resources =  hiResourceDBDAO.getHIResourceEFWDByParentResourceId(resourceId);
		List<HIEfwdDTO> dtoList = new ArrayList<>();
		for(HIEFWD resource : resources ) {
			HIResource parentResource = resource.getParentResource();
			HIEfwdDTO dto = new HIEfwdDTO();
			dto.setCreatedBy(resource.getCreatedBy());
			dto.setIsDeleted(parentResource.isDeleted());
			dto.setResourceId(resourceId);
			dto.setResourcePath(parentResource.getResourcePath());
			dto.setResourceUrl(parentResource.getResourceURL());
			dto.setTitle(parentResource.getTitle());
			dtoList.add(dto);
		}
		
		return dtoList;
	}

    @Transactional
	@Override
	public Long getCountOfSamePrefixUrlResources(String url,Long resourceTypeId,Integer parentId,Boolean isForUrlGen) {
    	Long nextNumber=0L;
		List<String> data=hiResourceDBDAO.fetchGeneratedUrlsFromSpecifiedPath(url,resourceTypeId,parentId,isForUrlGen);
		String ext=ResourceTypeIDMap.getResourceTypExtension(resourceTypeId);
		if(!ext.substring(1).equals("efwfolder") && isForUrlGen) {
			data=filterCounts(data,url,ext);
		}
		Integer N=data.size();
		Collections.sort(data,(a,b)->(Integer.valueOf(a)-Integer.valueOf(b)));
		for(int i=1;i<=N;i++) {
			if(i<Integer.valueOf(data.get(i-1))) {
				nextNumber=Long.valueOf(i);
				break;
			}
		}
		if(nextNumber.equals(0L) && N>0)
			nextNumber=Long.valueOf(N+1);
		return nextNumber;
	}

    private List<String> filterCounts(List<String> data,String url,String ext) {
    	String url2=url+"_";
		List<String> counts=new ArrayList<>();
		data.forEach(e->{
			if(!e.substring(url.length()).equals(ext)) {
				Integer len=(e.length()-url2.length()-ext.length())-1;
				counts.add(e.substring(url2.length(),url2.length()+len));
			}
		});
		return counts;
	}

	@Transactional
	@Override
	public HIResource fetchResourceBasedOnTitleAndParentId(String title, Integer parentId) {
		return hiResourceDBDAO.fetchResourceBasedOnTitleAndParentId(title, parentId);
	}

	@Transactional
	@Override
	public Map<Integer,Boolean> fetchAllParentResourceIdsForAUser(Integer userId) {
		return hiResourceDBDAO.fetchAllParentResourceIdsForAUser(userId);
	}

	@Transactional
	@Override
	public void deleteHIResource(HIResource hiResource) {
		hiResourceDBDAO.deleteHIResource(hiResource);
	}


	@Transactional
	@Override
	public HIResource findResourceById(Integer resourceId, boolean applyFilter) {
		return hiResourceDBDAO.findResourceById(resourceId, applyFilter);
	}
	
	@Transactional
	@Override
	public HIResource findResourceByUrl(String url) {
		return hiResourceDBDAO.findResourceByUrl(url);
	}
	
	@Transactional
	@Override
	public List<Integer> getChildrenResourceByParentIds(List<Integer> parentIds) {
		return hiResourceDBDAO.getChildrenResourceByParentIds(parentIds);
	}
	
	@Transactional
	@Override
	public  List<HIResource> getHIResourcesByIds(List<Integer> resourceIds, Boolean applyFilter) {
		return hiResourceDBDAO.getHIResourcesByIds(resourceIds, applyFilter);
	}


	@Transactional
	@Override
	public Map<Integer, List<HIResource>> findAllReportsByMetadataResourceIds(List<Integer> metadataResourceIds) {
		return hiResourceDBDAO.findAllReportsByMetadataResourceIds(metadataResourceIds);
	}

	@Transactional
	@Override
	public Map<Integer, List<HIResource>> findAllInstantReportsByModelResourceIds(List<Integer> modelResourceIds) {
		return hiResourceDBDAO.findAllInstantReportsByModelResourceIds(modelResourceIds);
	}


	@Transactional
	@Override
	public void restoreResourcesByIds(List<Integer> resourceIds) {
			hiResourceDBDAO.restoreResourcesByIds(resourceIds);
	}
    
}
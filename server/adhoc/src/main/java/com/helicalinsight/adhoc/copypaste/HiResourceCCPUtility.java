package com.helicalinsight.adhoc.copypaste;

import java.util.Date;
import java.util.List;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.resourcedb.FileOperationDTO;

/**
 * Utility class for handling operations related to copying, deleting, and manipulating resources.
 */
@Component
public class HiResourceCCPUtility {
	
	@Autowired
	private HIResourceServiceDB hiResourceServiceDB;
	
	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	

	private static final String DELETE="delete";
	
	/**
	 * It prepares the copy of HIResource .
	 * @param source            instance of HIResource provides id, title, type etc.
	 * @param destination		provides destination id
	 * @param prefix			to set prefix of url
	 * @param sourcePath		path to set in new replica.
	 * @return new replica or copy of HIResource instance.
	 */
	public HIResource prepareNewReplica(HIResource source,HIResource destination,String prefix,String sourcePath) {
		HIResource replica=new HIResource();
		replica.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		replica.setCreated_date(new Date());
		replica.setLastUpdatedTime(new Date());
		replica.setDeleted(Boolean.FALSE);
		replica.setFolder(source.getFolder());
		replica.setVisible(source.getVisible());
		if(source.getParentId()!=null && 
				source.getParentId().equals(destination.getResourceId())) {
			String updatedTitle=source.getTitle()+" Copy";
			HIResource isSameTitleExist=hiResourceServiceDB.fetchResourceBasedOnTitleAndParentId(updatedTitle, source.getParentId());
			Long nextNumber=hiResourceServiceDB.getCountOfSamePrefixUrlResources(updatedTitle, source.getResourceTypeId(), source.getParentId(), Boolean.FALSE);
			if(isSameTitleExist!=null && nextNumber.equals(0L)) {
				updatedTitle+="(1)";
			}
			else if(nextNumber>0L)
				updatedTitle+="("+nextNumber+")";
			replica.setTitle(updatedTitle);
		}
		else
			replica.setTitle(source.getTitle());
		replica.setResourceURL(prefix);
		replica.setResourcePath(sourcePath);
		replica.setParentId(destination.getResourceId());
		replica.setResourceTypeId(source.getResourceTypeId());
		replica.setResourceType(source.getResourceType());
		replica.setMigrated(source.getMigrated());
		replica.setResourceId(null);
		return replica;
	}
	/**
     * Prepares an entity of the specified class from the given source object.
     * 
     * @param source        The source object to convert.
     * @param className     The fully qualified class name of the target entity.
     * @return              The prepared entity of the specified class.
     */
	public static <T> T prepareEntity(Object source, Class<T> inputClazz) {

		T result = null;
		try {
			ObjectMapper ob = new ObjectMapper();
			String json = ob.writeValueAsString(source);
			result = ob.readValue(json, inputClazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
     * Saves security information for the replica HIResource.
     * 
     * @param sourceResourceId  id of the source HIResource.
     * @param dest              destination HIResource instance.
     */
	public void saveSecurityInfoReplica(Integer sourceResourceId, HIResource dest) {
		List<HIResourceSecurityDB> securityInfo=hiResourceServiceDB.getHIResourceSecurityByResourceId(sourceResourceId);
		securityInfo.forEach(e->{
			HIResourceSecurityDB entry=prepareEntity(e, HIResourceSecurityDB.class);
			entry.setPermission(e.getPermission());
			entry.setHiResource(dest);
			entry.setId(null);
			entry.setOrgId(e.getOrgId());
			entry.setRoleId(e.getRoleId());
			entry.setUserId(e.getUserId());
			hiResourceServiceDB.addHIResourceSecurity(entry);
		});
	}
	 /**
     * Performs a soft delete operation for the given source URL.
     * @param sourceUrl     to delete the resource.
     */
	public void doSoftDelete(String sourceUrl) {
		FileOperationDTO fileOperationDTO=new FileOperationDTO();
		fileOperationDTO.setAction(DELETE);
		fileOperationDTO.setSourceArray("["+"\""+sourceUrl+"\"]");
		fileSystemOperationsController.executeFileSystemOperations(fileOperationDTO);
	}
	/**
     * Deletes the overridden resource and updates the copied resource.
     * 
     * @param hiResource                    	HIResource instance to be updated.
     * @param isSameResouceNameAlreadyExisted  	HIResource instance of the resource with the same name that was deleted.
     */
	public void deleteOverridenResourceAndUpdateCopiedResource(HIResource hiResource,HIResource isSameResouceNameAlreadyExisted) {
		permanentDeleteOfOverridenResource(isSameResouceNameAlreadyExisted.getResourceId(),isSameResouceNameAlreadyExisted.getResourceURL());
		hiResource.setResourcePath(isSameResouceNameAlreadyExisted.getResourcePath());
		hiResource.setResourceURL(isSameResouceNameAlreadyExisted.getResourceURL());
		hiResource.setTitle(isSameResouceNameAlreadyExisted.getTitle());
		hiResourceServiceDB.editHIResource(hiResource);
	}
	 /**
     * Performs a permanent delete of the overridden resource.
     * 
     * @param resourceId                id of the overridden resource to delete permanently .
     * @param dupOfOriginalPrefixUrl    URL prefix of the overridden resource.
     */
	public void permanentDeleteOfOverridenResource(Integer resourceId, String dupOfOriginalPrefixUrl) {
		doSoftDelete(dupOfOriginalPrefixUrl);
		Long recycleBinId=hiResourceServiceDB.getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(resourceId);
		IService iService = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.services.RecycleBinService",
				IService.class);
		if (iService != null) {
			JsonObject formData = new JsonObject();
			JsonArray ids=new JsonArray();
			ids.add(recycleBinId);
			formData.addProperty("action", "delete");
			formData.add("recycleBinIds", ids);
			iService.doService("adhoc", "recycleBin", "recycle", formData.toString());
		}
	}
	
}

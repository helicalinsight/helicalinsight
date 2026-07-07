package com.helicalinsight.export.handler.importres;

import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;
/**
 * Handles the import of EFW resources.
 * This class is responsible for importing EFW resources from a specified file. It reads the EFW resource and associated
 * content from the files, determines the import mode (update or skip), and processes the import accordingly.
 * The EFW import handler is designed to be a prototype-scoped component, allowing for multiple instances to handle
 * concurrent imports independently.
 */
@Component("efwImportHandler")
@Scope("prototype")
public class EfwImportHandler extends AbstractResourceImportHandler{

	
	/**
     * Imports an EFW resource.
     *
     * @param resourceUrl 		URL of the EFW resource to import.
     * @return The imported HIResource representing the EFW resource.
     */
	@Override
	public HIResource importResource(String resourceUrl) {
		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		ImportRequest request = context.getRequest();
		String conflictMode = request.getOnConflict();
		HIResourceEFW efwResource = fileReader.read(context,resourceUrl, HIResourceEFW.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl,Deleted.FALSE);
		if (null != resource) {
			if ("update".equalsIgnoreCase(conflictMode) && context.recover(resource)) {
				updateEfwResource(efwResource, resource);
				context.appendUpdate(resource.getResourceURL());
			}else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewEfwResource(efwResource, parentUrl, resourceUrl,efwResource.getTitle());
			context.appendInsert(resource.getResourceURL());
		}
		String contentPath = resourceUrl+ResourceSuffix.EFW_CONTENT;
		ResourceEfwContents content = fileReader.read(context,contentPath, ResourceEfwContents.class);
		ResourceEfwContents dbContent = serviceDb.getHIResourceEfwContents(content.getFileName());		
		if(null != dbContent) {
			
			if(conflictMode.equalsIgnoreCase("update")) {
				dbContent.setContent(content.getContent());
				dbContent.setContentType(content.getContentType());
				dbContent.setResourceId(resource.getResourceId());
				resource.getHiResourceEFW().setEfwContentId(dbContent.getId());
				Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy() : 
					Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
				resource.setCreatedBy(importedBy);
				serviceDb.editHIResourceEFWContents(dbContent);
				serviceDb.editHIResource(resource);
			}
		}
		else {
			content.setResourceId(resource.getResourceId());
			int contentId = serviceDb.addHIResourceEFWContents(content);
			efwResource.setEfwContentId(contentId);
			serviceDb.editHIResource(resource);
		}
		shareHandler.importResource(resource, request, context.getManifest());
		return resource;
	}
	/**
     * Updates an existing EFW resource.
     *
     * @param efw     		EFW instance to update from.
     * @param resource 		existing HIResource representing the EFW resource.
     */
	public void updateEfwResource(HIResourceEFW efw, HIResource resource) {
		Date date = context.getDate();
		efw.setLastUpdatedTime(date);
		
		resource.setTitle(efw.getTitle());
		resource.setLastUpdatedTime(date);
		resource.setLastUpdatedTime(date);
		Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy() : 
			Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
		
		if(efw.getCreatedBy() == null ) {
			resource.setCreatedBy(null);
		}
		else {
			resource.setCreatedBy(importedBy);
			efw.setCreatedBy(importedBy);
		}
		resource.setHiResourceEFW(efw);
		resource.setTitle(efw.getTitle());
		serviceDb.editHIResource(resource);
	}
	/**
     * Creates a new EFW resource.
     *
     * @param efw         		EFW instance to create from , sets Date and owner.
     * @param parentUrl  		URL of the parent resource.
     * @param resourceUrl 		URL of the new resource.
     * @param title       		title of the new resource.
     * @return The created HIResource representing the new EFW resource.
     */
	public HIResource createNewEfwResource(HIResourceEFW efw, String parentUrl, String resourceUrl,String title) {
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? efw.getCreatedBy() : null;
		HIResource parent = context.getResourceUrlMap().get(parentUrl+"."+JsonUtils.getFolderFileExtension());
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getEfwExtension(), context.getDate(),ownerId, 
				resourceUrl, FilenameUtils.getName(resourceUrl), title, parent.getResourceId(), efw.getCreatedBy() == null);
		efw.setCreatedBy(resource.getCreatedBy());
		efw.setLastUpdatedTime(context.getDate());
		efw.setCreatedDate(context.getDate());
		resource.setHiResourceEFW(efw);
		serviceDb.addHIResource(resource);
		return resource;
	}
	
}

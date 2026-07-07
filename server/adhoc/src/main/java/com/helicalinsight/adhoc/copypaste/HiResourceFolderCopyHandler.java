package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * This class HiResourceFolderCopyHandler extends {@code HiResourceCopyHandler}.
 * Handles the copying of folders and their contents, including recursive copying of nested folders and 
 * associated resources.
 */
@Component
public class HiResourceFolderCopyHandler extends HiResourceCopyHandler{

	@Autowired
	HIResourceServiceDB hiResourceServiceDB;
	
	@Autowired
	HiResourceCCPUtility hiResourceCCPUtility;

	@Autowired
	private ResourceEfwContentsService resourceEfwContent;
	/*

	/**
     * Copies the folder and its contents , including nested folders and associated resources.
     */
	@Override
	public void copyResource() {
		List<String> folderResourcesTobeDeleted=new ArrayList<String>();
		copyAll(getSource(), getDestinationResourceId(), getPrefix(),getSourcePath(),folderResourcesTobeDeleted);
		folderResourcesTobeDeleted.forEach(e->{
			hiResourceCCPUtility.doSoftDelete(e);
		});
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());
	}
	/**
     * Recursively copies all resources and folders from the source to the destination, updating paths and URLs accordingly.
     * 
     * @param source                       to create new replica HIResource and provides, resource id.
     * @param destResource                 destination HIResource folder.
     * @param prefixUrl                    prefix URL to set for the copied resources.
     * @param sourcePath                   The source path of the copied resources.
     * @param folderResourcesTobeDeleted   List to track folder resources to be deleted.
     */
	private void copyAll(HIResource source, HIResource destResource, String prefixUrl, String sourcePath,List<String> folderResourcesTobeDeleted) {
		HIResource hiResource;
		HIResource isSameResouceNameAlreadyExisted = hiResourceServiceDB.getResourceByUrl(prefixUrl, Boolean.FALSE);
		if (isSameResouceNameAlreadyExisted != null && !isSameResouceNameAlreadyExisted.getDeleted())
			hiResource = isSameResouceNameAlreadyExisted;
		else {
			hiResource = hiResourceCCPUtility.prepareNewReplica(source, destResource, prefixUrl, sourcePath);
			HIResourceFolder hiResourceFolder = HiResourceCCPUtility.prepareEntity(getSource().getHiResourceFolder(),
					HIResourceFolder.class);
			if(isSameResouceNameAlreadyExisted!=null) {
				Format secondsFormat = new SimpleDateFormat("ss");
				String generatedSourcePath=DBProcessor.checkAndReplaceSpecialChars(sourcePath).trim() +
						"_"+ secondsFormat.format(new Date()).substring(0, 2);
				String generatedUrl=prefixUrl.substring(0,prefixUrl.length()-sourcePath.length())+generatedSourcePath;
				hiResource.setResourceURL(generatedUrl);
				hiResource.setResourcePath(generatedSourcePath);
			}
			hiResourceFolder.setTitle(hiResource.getTitle());
			hiResourceFolder.setCreatedDate(new Date());
			hiResourceFolder.setLastUpdatedTime(new Date());
			hiResourceFolder.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
			hiResource.setHiResourceFolder(hiResourceFolder);
			hiResourceServiceDB.addHIResource(hiResource);
			Integer resourceId = hiResource.getResourceId();
			Integer originalId = source.getResourceId();
			List<ResourceEfwContents> efwdFileContent=resourceEfwContent.fetchResourceEfwContentByResourceId(originalId);
for(ResourceEfwContents it:efwdFileContent){
	ResourceEfwContents rc = new ResourceEfwContents();
	rc.setContent(it.getContent());
	rc.setContentType(it.getContentType());
	rc.setFileName(it.getFileName());
	rc.setResourceId(resourceId);
	resourceEfwContent.addHIResourceEfwContent(rc);
}
			hiResourceCCPUtility.saveSecurityInfoReplica(source.getResourceId(), hiResource);
		}
		if (source.getDeleted())
			folderResourcesTobeDeleted.add(hiResource.getResourceURL());
		checkForPlainConnections(source,hiResource);
		List<HIResource> allChildResources=hiResourceServiceDB.getResourceByParentId(source.getResourceId());
		allChildResources.forEach(e->{
				String type = e.getResourceType().getName();
				String prefix=prefixUrl+"/"+e.getResourcePath();
				if(type.equals("folder")){
					copyAll(e, hiResource, prefix, e.getResourcePath(),folderResourcesTobeDeleted);
				}
				else {
					HiResourceCopyHandler hiResourceCopyHandler = CopyHandlerProvider.getInstance(type);
					prefix += e.getResourceType().getExtension();
					hiResourceCopyHandler.setData(prefix, e.getResourcePath(), e, hiResource,getOnConflictSkip());
					hiResourceCopyHandler.copyResource();
				}
		});
	}
	/**
     * Checks for plain connections associated with the source folder and copies them to the destination folder.
     * 
     * @param source        The source HIResource folder.
     * @param destination   The destination HIResource folder.
     */
	private void checkForPlainConnections(HIResource source,HIResource destination) {
		List<HIEfwdDTO> listOfPlainCons=hiResourceServiceDB.findHIResourceEFWDByParentResourceId(source.getResourceId());
		HiResourceCopyHandler hiResourceCopyHandler = CopyHandlerProvider.getInstance("efwd");
		listOfPlainCons.forEach(e->{
			hiResourceCopyHandler.setEfwdDto(e);
			hiResourceCopyHandler.setHiResource(destination);
			hiResourceCopyHandler.copyResource();
		});
	}
}

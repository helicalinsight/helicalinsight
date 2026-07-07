package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * Class HiResourceEfddCopyHandler extends {@link HiResourceCopyHandler}
 * Handles the copying of Efdd resources, ensuring uniqueness of names
 * and managing associated Efdd data and security.
 */
@Component
public class HiResourceEfddCopyHandler extends HiResourceCopyHandler{

	@Autowired
	private HIResourceServiceDB hiResourceServiceDB;
	
	@Autowired
	private HIResourceConstituentMappingService pathService;


	@Autowired
	HiResourceCCPUtility hiResourceCCPUtility;
	/**
     * Copies the resource, ensuring uniqueness and handling associated data.
     */
	@Override
	public void copyResource() {
		
		if(getSource().getParentId()!=getDestinationResourceId().getResourceId()) {
			HIResource isSameResouceNameAlreadyExisted=hiResourceServiceDB.getResourceByUrl(getPrefix(),Boolean.FALSE);
			if(isSameResouceNameAlreadyExisted!=null && !isSameResouceNameAlreadyExisted.getDeleted()) {
				if(!getOnConflictSkip()) {
					String sourcePath=UUIDGenerator.getUuid();
					String prefixUrl=getDestinationResourceId().getResourceURL()+"/"+sourcePath+getSource().getResourceType().getExtension();
					HIResource hiResource=doCopy(prefixUrl,sourcePath);
					hiResourceCCPUtility.deleteOverridenResourceAndUpdateCopiedResource(hiResource, isSameResouceNameAlreadyExisted);
				}
			}
			else {
				if(isSameResouceNameAlreadyExisted!=null) {
					Format secondsFormat = new SimpleDateFormat("ss");
					String generatedSourcePath=DBProcessor.checkAndReplaceSpecialChars(getSourcePath()).trim() +
							"_"+ secondsFormat.format(new Date()).substring(0, 2);
					String generatedUrl=getPrefix().substring(0,getPrefix().length()-getSourcePath().length())+generatedSourcePath;
					doCopy(generatedUrl,generatedSourcePath);
				}
				else
					doCopy(getPrefix(),getSourcePath());
			}
		}
		else
			doCopy(getPrefix(),getSourcePath());
	}
	/**
     * Performs the actual copy operation, prepares a replica, sets properties,
     * adds to the database, and handles security.
     *
     * @param prefixUrl  Prefix URL for the new replica.
     * @param sourcePath Source path for the new replica.
     * @return The copied HIResource object.
     */
	private HIResource doCopy(String prefixUrl, String sourcePath) {

		HIResource hiResource=hiResourceCCPUtility.prepareNewReplica(
				getSource(), getDestinationResourceId(), prefixUrl, sourcePath);
		HIResourceEFWDD hiResourceEFWDD=HiResourceCCPUtility.prepareEntity(getSource().getHiResourceEFWDD(),HIResourceEFWDD.class);
		hiResourceEFWDD.setFileName(hiResource.getTitle());
		hiResourceEFWDD.setCreatedDate(new Date());
		hiResourceEFWDD.setLastUpdatedTime(new Date());
		hiResourceEFWDD.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		hiResource.setHiResourceEFWDD(hiResourceEFWDD);
		hiResourceServiceDB.addHIResource(hiResource);
		hiResourceCCPUtility.saveSecurityInfoReplica(getSource().getResourceId(), hiResource);


		List<HIResourceMapping> existingList= pathService.findByParentId( getSource().getResourceId());
		List<HIResourceMapping> newList = new ArrayList<>();	
		for (HIResourceMapping component : existingList) {
			HIResourceMapping resourceEfwddResource = new HIResourceMapping();
			resourceEfwddResource.setParentResource(hiResource);// bug-7960 fix
			resourceEfwddResource.setChildResource(component.getChildResource());
			newList.add(resourceEfwddResource);
		}
		pathService.saveBatch(newList);

		hiResourceCCPUtility.saveSecurityInfoReplica(getSource().getResourceId(), hiResource);
		if(getSource().getDeleted())
			hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());
		return hiResource;
	}
	
}

package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWSR;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * Handles the copying of EFWSR resources,
 * ensuring uniqueness of names and managing associated EFWSR data and security.
 */
@Component
public class HiResourceEfwsrCopyHandler extends HiResourceCopyHandler{

	@Autowired
	HIResourceServiceDB hiResourceServiceDB;
	
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
     * Performs the copy operation, prepares a replica, sets properties,
     * adds to the database, and handles security.
     *
     * @param prefix     Prefix for the URL of the new replica.
     * @param sourcePath Source path for the new replica.
     * @return The copied HIResource object.
     */
	private HIResource doCopy(String prefix, String sourcePath) {
		HIResource hiResource=hiResourceCCPUtility.prepareNewReplica(
				getSource(), getDestinationResourceId(), prefix, sourcePath);
		HIResourceEFWSR hiResourceEFWSR=HiResourceCCPUtility.prepareEntity(getSource().getHiResourceEFWSR(),HIResourceEFWSR.class);
		hiResourceEFWSR.setReportName(hiResource.getTitle());
		hiResourceEFWSR.setCreatedDate(new Date());
		hiResourceEFWSR.setLastUpdatedTime(new Date());
		hiResourceEFWSR.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		hiResource.setHiResourceEFWSR(hiResourceEFWSR);
		hiResourceServiceDB.addHIResource(hiResource);
		hiResourceCCPUtility.saveSecurityInfoReplica(getSource().getResourceId(), hiResource);
		if(getSource().getDeleted())
			hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());
		return hiResource;
	}
	
}

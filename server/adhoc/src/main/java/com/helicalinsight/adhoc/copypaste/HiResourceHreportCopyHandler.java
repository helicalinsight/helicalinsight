package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * HiResourceHreportCopyHandler class extends {@link HiResourceCopyHandler}.
 * Handles the copying of HReport resources, ensuring uniqueness of names
 * and managing associated report data and security.
 */
@Component
public class HiResourceHreportCopyHandler extends HiResourceCopyHandler{

	
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
	 * This method performs the actual copy operation.
	 * It prepares a new replica of the source resource with a given prefix and source path.
	 * It sets properties of the HIResourceHReport associated with the copied resource, such as report name, creation date, etc.
	 * It adds the new resource to the database, saves security information, and deletion of source.
	 * 
	 * @param prefix          used to create new replica of {@code HIResource}   
	 * @param sourcePath	  used to set the path of new replica/copy of {@code HIResource} 
	 * @return {@code HIResource} object.
	 */
	private HIResource doCopy(String prefix, String sourcePath) {
		HIResource hiResource=hiResourceCCPUtility.prepareNewReplica(
				getSource(), getDestinationResourceId(), prefix, sourcePath);
		HIResourceHReport hiResourceHReport=HiResourceCCPUtility.prepareEntity(getSource().getHiResourceHReport(),HIResourceHReport.class);
		hiResourceHReport.setReportName(hiResource.getTitle());
		hiResourceHReport.setCreatedDate(new Date());
		hiResourceHReport.setLastUpdatedTime(new Date());
		hiResourceHReport.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		hiResource.setHiResourceHReport(hiResourceHReport);
		hiResourceServiceDB.addHIResource(hiResource);
		hiResourceCCPUtility.saveSecurityInfoReplica(getSource().getResourceId(), hiResource);
		if(getSource().getDeleted())
			hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());
		return hiResource;
	}
}

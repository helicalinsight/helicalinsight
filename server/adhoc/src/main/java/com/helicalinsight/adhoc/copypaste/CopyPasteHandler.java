package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.helicalinsight.resourcedb.HIResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.CutCopyFileInfo;
import com.helicalinsight.resourcedb.AbstractResourceAction;
import com.helicalinsight.resourcedb.processor.DBProcessor;


/**
 * The CopyPasteHandler class extends {@link AbstractResourceAction}.
 * Handles the copying of resources, including managing conflicts, generating unique names,
 * and performing the copy operation.
 */
@Component("copyResourceAction")
@Scope("prototype")
public class CopyPasteHandler extends AbstractResourceAction {
	private static final Logger logger = LoggerFactory.getLogger(CopyPasteHandler.class);
	
	@Autowired
	HIResourceServiceDB hiResourceServiceDB;

	private Boolean isConflictSkip;
	
	 /**
     * Performs the copy action for the resource.
     * manages the conflicts and generates unique names.
     * @return {@code true} if the copy action is successful, {@code false} otherwise.
     */
	@Override
	public Boolean performAction() {
		
		if(!ccpValidation("copy"))
			return false;
		HIResource source=this.source;
		HIResource destination=this.destination;
		if(getJsonInputFormData().has("isConflictSkip"))
			setIsConflictSkip(getJsonInputFormData().getBoolean("isConflictSkip"));
		else
			setIsConflictSkip(Boolean.TRUE);
		String sourcePath=source.getResourcePath(),prefixUrl=destination.getResourceURL()+"/"+sourcePath;
		HiResourceCopyHandler hiResourceCopyHandler;
		try {

			if (source.getParentId()!=null && source.getParentId().equals(destination.getResourceId())) {
				Long nextCount;
				HIResource parentOfSource=hiResourceServiceDB.getHIResourceById(source.getParentId());
				if (!source.getResourceType().getName().equals("folder")) {
					String urlWithoutExtension = source.getResourceURL().substring(0,
							source.getResourceURL().length() - source.getResourceType().getExtension().length());
					sourcePath =urlWithoutExtension.substring(parentOfSource.getResourceURL().length()+1)+"_Copy";
					urlWithoutExtension += "_Copy";
					HIResource isAlreadyExists=hiResourceServiceDB.getResourceByUrl(urlWithoutExtension+source.getResourceType().getExtension(), false);
					nextCount = hiResourceServiceDB.getCountOfSamePrefixUrlResources(urlWithoutExtension,
							source.getResourceTypeId(),source.getParentId(),Boolean.TRUE);
					if(isAlreadyExists!=null && nextCount.equals(0L))
						sourcePath+="_1_";
					else
						sourcePath = generateUniquePath(nextCount, sourcePath);
					prefixUrl=parentOfSource.getResourceURL() +"/"+sourcePath+source.getResourceType().getExtension();
				} else {
					sourcePath=source.getResourceURL().substring(parentOfSource.getResourceURL().length()+1)+"_Copy";
					prefixUrl=parentOfSource.getResourceURL() +"/"+sourcePath;
					HIResource isAlreadyExists=hiResourceServiceDB.getResourceByUrl(prefixUrl, false);
					nextCount = hiResourceServiceDB.getCountOfSamePrefixUrlResources(prefixUrl,source.getResourceTypeId(),source.getParentId(),Boolean.TRUE);
					if(isAlreadyExists!=null && nextCount.equals(0L)) {
						sourcePath+="_1_";
						prefixUrl+="_1_";
					}
					else if(nextCount>0L){
						sourcePath = generateUniquePath(nextCount, sourcePath);
						prefixUrl = parentOfSource.getResourceURL() +"/" + sourcePath;
					}
				}
			}
			else {
				if(!source.getResourceType().getName().equals("folder")){
					String extension = source.getResourceType().getExtension();
					if(!prefixUrl.endsWith(extension)) {
						prefixUrl+= extension;
					}
				}

				HIResource isSameResouceNameAlreadyExisted = hiResourceServiceDB.getResourceByUrl(prefixUrl, Boolean.FALSE);
				if(isSameResouceNameAlreadyExisted!=null && isSameResouceNameAlreadyExisted.getDeleted()) {
					Format secondsFormat = new SimpleDateFormat("ss");
					sourcePath=DBProcessor.checkAndReplaceSpecialChars(sourcePath).trim() +
							"_"+ secondsFormat.format(new Date()).substring(0, 2);
					prefixUrl=destination.getResourceURL()+"/"+sourcePath;
					if(!source.getResourceType().getName().equals("folder"))
						prefixUrl+=source.getResourceType().getExtension();
				}
			}
			hiResourceCopyHandler=CopyHandlerProvider.getInstance(source.getResourceType().getName());
			hiResourceCopyHandler.setData(prefixUrl, sourcePath, source, destination,getIsConflictSkip());
			hiResourceCopyHandler.copyResource();
            HIResource updatedSource=hiResourceServiceDB.getResourceByUrl(prefixUrl);
	        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
	        String[] pathSplit = updatedSource.getResourceURL().split(Pattern.quote("/"));
	        String fileName=pathSplit[pathSplit.length - 1];
	        CutCopyFileInfo fileInfo = bean.prepareFileInfoCopyPaste(updatedSource.getResourceURL().substring(0,
	        		updatedSource.getResourceURL().length()-fileName.length()-1), fileName);
			HIResourceDTO dtoData = prepareResponse(fileInfo);
			JsonObject options = fileInfo.getOptions();
			if(GsonUtility.optBoolean(options,"public")){
				dtoData.setPublicFolder(true);
				options.remove("public");
			}
			this.setData(dtoData);
	        this.setMessage("Resource(s) copied successfully.");
			return true;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.error(" There was error in copying ",ex);
			this.setMessage("We could not copy the resource(s) successfully."+ex.getMessage());
			return false;	
		}
	}
	/**
     * Generates a unique path by appending a numeric suffix to the given unique name.
     *
     * @param nextInteger 		 next available integer to use as the suffix.
     * @param uniqueName  		 base unique name.
     * @return The unique path with a numeric suffix.
     */
	private String generateUniquePath(Long nextInteger,String uniqueName) {
		if(nextInteger>=1) {
			uniqueName+="_"+nextInteger+"_";	
		}
		return uniqueName;
	}
	/**
     * Gets the conflict skip flag.
     * @return True if conflict skip is enabled, false otherwise.
     */
	public Boolean getIsConflictSkip() {
		return isConflictSkip;
	}
	 /**
     * Sets the conflict skip flag.
     * @param isConflictSkip The value to set for conflict skip.
     */
	public void setIsConflictSkip(Boolean isConflictSkip) {
		this.isConflictSkip = isConflictSkip;
	}
}

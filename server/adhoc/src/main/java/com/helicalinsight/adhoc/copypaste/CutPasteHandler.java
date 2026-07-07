package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.AbstractResourceAction;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * The class CutPasteHandler extends {@link AbstractResourceAction}
 * Handles the cut operation for resources, including managing conflicts, validating the cut action,
 * and performing the cut operation and updates url.
 */
@Component("cutResourceAction")
@Scope("prototype")
public class CutPasteHandler extends AbstractResourceAction{
	
	
	@Autowired
	HIResourceServiceDB hiResourceServiceDB;
	
	@Autowired
    ResourcePermissionLevelsHolder accessLevelService;
	
	@Autowired
	HiResourceCCPUtility utility;
	
	private Boolean isConflictSkip;

	/**
	 * Performs cut operation on resources. checks the conflict and validates the cut action.
	 * @return {@code true} if operation done successfully. otherwise if any exception caused then returns {@code false}.
	 * 
	 */
	@Override
	public Boolean performAction(){
		
		if(!ccpValidation("cut"))
			return false;
		try {
			String prefix;
			String sourcePath=source.getResourcePath();
			Format secondsFormat = new SimpleDateFormat("ss");
			if(getJsonInputFormData().has("isConflictSkip"))
				setIsConflictSkip(getJsonInputFormData().getBoolean("isConflictSkip"));
			else
				setIsConflictSkip(Boolean.TRUE);
			if (source.getResourceType().getName().equals("folder")) {
				HIResource checkForSameNameResExistance = hiResourceServiceDB
						.getResourceByUrl(destination.getResourceURL() + "/" + sourcePath,Boolean.FALSE);
				if (checkForSameNameResExistance != null) {
					if (checkForSameNameResExistance.getDeleted()) {
						sourcePath = DBProcessor.checkAndReplaceSpecialChars(sourcePath).trim() + "_"
								+ secondsFormat.format(new Date()).substring(0, 2);
						prefix=doCutPaste(sourcePath,Boolean.TRUE);
					} else {
						if(source.getParentId()==null || 
								source.getParentId()!=null && source.getParentId()!=destination.getResourceId())
							prefix=doCutPaste(sourcePath,Boolean.FALSE);
						else
							prefix=destination.getResourceURL() + "/" + source.getResourcePath();
					}
				} else {
					prefix=doCutPaste(sourcePath,Boolean.TRUE);
				}
			}
			else {
				prefix = destination.getResourceURL() + "/" + source.getResourcePath()+source.getResourceType().getExtension();
				if (source.getParentId() == null
						|| source.getParentId() != null && source.getParentId() != destination.getResourceId()) {
					HIResource isSameResouceNameAlreadyExisted = hiResourceServiceDB.getResourceByUrl(prefix,
							Boolean.FALSE);
					if (isSameResouceNameAlreadyExisted == null)
						updateAndSave(prefix, sourcePath);
					else {
						if (isSameResouceNameAlreadyExisted.getDeleted()) {
							sourcePath = DBProcessor.checkAndReplaceSpecialChars(source.getResourcePath()).trim() + "_"
									+ secondsFormat.format(new Date()).substring(0, 2);
							prefix = destination.getResourceURL() + "/" + sourcePath
									+ source.getResourceType().getExtension();
							updateAndSave(prefix, sourcePath);
						} else if (!getIsConflictSkip()) {
							utility.permanentDeleteOfOverridenResource(isSameResouceNameAlreadyExisted.getResourceId(),
									isSameResouceNameAlreadyExisted.getResourceURL());
							updateAndSave(prefix, sourcePath);
						}
					}
				}
			}
            HIResource updatedSource=hiResourceServiceDB.getResourceByUrl(prefix);
	        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
	        String[] pathSplit = updatedSource.getResourceURL().split(Pattern.quote("/"));
	        String fileName=pathSplit[pathSplit.length - 1];
	        CutCopyFileInfo fileInfo = bean.prepareFileInfoCopyPaste(updatedSource.getResourceURL().substring(0,
	        		updatedSource.getResourceURL().length()-fileName.length()-1), fileName);
			HIResourceDTO data = prepareResponse(fileInfo);
			JsonObject options = fileInfo.getOptions();
			if(GsonUtility.optBoolean(options,"public")){
				data.setPublicFolder(true);
				options.remove("public");
			}
			this.setData(data);
	        this.setMessage("Resource(s) copied successfully.");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			this.setMessage("We could not copy the resource(s) successfully.");
			return false;
		}
	}
	/**
	 * it performs the cut action and paste it into destination.
	 * @param sourcePath             path to cut given resources.
	 * @param isRequiredToUpdate     boolean flag to update.
	 * @return prefix of url.
	 */
	private String doCutPaste(String sourcePath,Boolean isRequiredToUpdate) {
		String prefix = destination.getResourceURL() + "/" + sourcePath;
		String oldResourceUrl=source.getResourceURL();
		if(isRequiredToUpdate)
			updateAndSave(prefix,sourcePath);
		updateResourceUrls(source,prefix,oldResourceUrl);
		if(!getIsConflictSkip()) {
			HIResource isSourceExists=hiResourceServiceDB.getResourceByUrl(oldResourceUrl);
			if(isSourceExists!=null) {
				if (isSourceExists.getParentId() != null) {
					HIResource selectedSourceParent = hiResourceServiceDB
							.getHIResourceById(isSourceExists.getParentId());
					selectedSourceParent.setLastUpdatedTime(new Date());
					hiResourceServiceDB.editHIResource(selectedSourceParent);
				}
				updateDestination(destination);
				utility.permanentDeleteOfOverridenResource(isSourceExists.getResourceId(),isSourceExists.getResourceURL());
			}
		}
		return prefix;
	}

	/**
     * Updates resource URLs after a cut-paste operation.
     * 
     * @param source   source resource to get contents.
     * @param prefix   prefix URL.
     * @param replace  replacement URL.
     */
	private void updateResourceUrls(HIResource source,String prefix,String replace) {
		List<HIResource> contents = hiResourceServiceDB.getResourceByParentId(source.getResourceId());
		if (contents != null && !contents.isEmpty()) {
			contents.forEach(e -> {
				String requiredUrl=e.getResourceURL().substring(replace.length());
				String updatedUrl=prefix+requiredUrl;
				HIResource isSameResourceAlreadyExisted=hiResourceServiceDB.getResourceByUrl(updatedUrl);
				if (isSameResourceAlreadyExisted != null) {
					if (!getIsConflictSkip()) {
						if (isSameResourceAlreadyExisted.getResourceType().getName().equals("folder")) {
							updateResourceUrls(e, prefix, replace);
						} else {
							e.setParentId(isSameResourceAlreadyExisted.getParentId());
							utility.permanentDeleteOfOverridenResource(isSameResourceAlreadyExisted.getResourceId(),
									isSameResourceAlreadyExisted.getResourceURL());
							e.setResourceURL(updatedUrl);
							hiResourceServiceDB.editHIResource(e);
							updateResourceUrls(e, prefix, replace);
							HIResource dest=hiResourceServiceDB.getHIResourceById(isSameResourceAlreadyExisted.getParentId());
							updateDestination(dest);
						}
					}
					else if(isSameResourceAlreadyExisted.getResourceType().getName().equals("folder")) {
						updateResourceUrls(e, prefix+"/"+isSameResourceAlreadyExisted.getResourcePath(), replace+"/"+e.getResourcePath());
					}
				}
				else {
					String urlToSearch=(e.getResourceType().getName().equals("folder") ? updatedUrl.substring(0,updatedUrl.length()-requiredUrl.length()):
											updatedUrl.substring(0,updatedUrl.length()-(e.getResourcePath()+e.getResourceType().getExtension()).length()-1));
					HIResource parent=hiResourceServiceDB.getResourceByUrl(urlToSearch);
					e.setParentId(parent.getResourceId());
					e.setResourceURL(updatedUrl);
					hiResourceServiceDB.editHIResource(e);
					updateResourceUrls(e, prefix+"/"+e.getResourcePath(), replace+"/"+e.getResourcePath());
					updateDestination(parent);
				}
			});
		}
	}
    /**
     * Updates the destination resource after a cut-paste operation.
     * @param hiResourceById The destination resource.
     */
	private void updateDestination(HIResource hiResourceById) {
		hiResourceById.setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(hiResourceById);
	}
	/**
     * Updates and saves the resource after a cut-paste operation.
     * 
     * @param prefixUrl  		 prefix URL.
     * @param sourcePath 		 source path.
     * @return The updated resource.
     */
	private HIResource updateAndSave(String prefixUrl, String sourcePath) {

		if (source.getParentId() != null) {
			HIResource sourceParent = hiResourceServiceDB.getResourceByParent(source.getParentId());
			sourceParent.setLastUpdatedTime(new Date());
			hiResourceServiceDB.editHIResource(sourceParent);
		}

		source.setParentId(destination.getResourceId());
		source.setResourceURL(prefixUrl);
		source.setResourcePath(sourcePath);
		hiResourceServiceDB.editHIResource(source);
		updateDestination(destination);
		
		return source;
	}
	/**
     * Gets the conflict skip flag.
     * @return {@code true} if conflict skip is enabled, {@code false} otherwise.
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

package com.helicalinsight.resourcedb;


import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.core.request.PayLoad;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.CutCopyFileInfo;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Calls the class based on the file-system operation for example newFolder,rename,delete action
 */
public abstract class AbstractResourceAction implements IResourceAction {

    protected PayLoad payLoad;
    protected String message;
    protected HIResourceDTO data;
    protected HIResource source;
    protected HIResource destination;
    protected JSONObject jsonInputFormData;


    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;

    @Autowired
    protected ResourceTypeServiceDB resourceTypeServiceDB;
    
    @Autowired
    ResourcePermissionLevelsHolder accessLevelService;

    public PayLoad getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(PayLoad payLoad) {
        this.payLoad = payLoad;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HIResourceDTO getData() {
        return data;
    }

    public void setData(HIResourceDTO data) {
        this.data = data;
    }

    public static AbstractResourceAction getActionClass(FileOperationDTO payload){
        String action = payload.getAction();
        AbstractResourceAction resourceAction = (AbstractResourceAction) ApplicationContextAccessor.getBean(action+"ResourceAction");
        resourceAction.setPayLoad(payload);
        return resourceAction;
    }
    
    public Boolean ccpValidation(String action) {
    	
    	FileOperationDTO payLoad = (FileOperationDTO) this.getPayLoad();
		JSONObject jsonObject=JSONObject.fromObject(payLoad.getFormData());
		setJsonInputFormData(jsonObject);
		String sp=jsonObject.getString("sourcePermission");
		String dp=jsonObject.getString("destPermission");
		String sUrl=jsonObject.getString("sourceUrl");
		String dUrl=jsonObject.getString("destinationUrl");
		if(sUrl==null || StringUtils.isEmpty(sUrl) || StringUtils.isBlank(sUrl) ||
				dUrl==null || StringUtils.isEmpty(dUrl) || StringUtils.isBlank(dUrl)) {
			this.setMessage("The source or destination is wrong or missing");
			return false;
		}
		this.source=hiResourceServiceDB.getResourceByUrl(sUrl);
		this.destination=hiResourceServiceDB.getResourceByUrl(dUrl);
		Integer actualSourcePermission=fetchPermission(Integer.valueOf(AuthenticationUtils.getUserId()),source);
		Integer actualDestinationPermission=fetchPermission(Integer.valueOf(AuthenticationUtils.getUserId()),destination);
		if(this.source==null || this.destination==null || !this.destination.getResourceType().getName().equals("folder")
				|| actualSourcePermission==null || actualDestinationPermission==null) {
			this.setMessage("The source or destination is wrong or missing");
			return false;
		}
		if( sp==null || StringUtils.isEmpty(sp) || StringUtils.isBlank(sp) || !StringUtils.isNumeric(sp)
			|| dp==null || StringUtils.isEmpty(dp) || StringUtils.isBlank(dp) || !StringUtils.isNumeric(dp)) {
			this.setMessage("You do not have sufficient privileges to do this action");
			return false;
		}
		Integer sourcePermission=Integer.parseInt(sp);
		Integer destPermission=Integer.parseInt(dp);
		Integer permissionToCompare= (action.equals("cut") ? accessLevelService.readWriteDeleteAccessLevel() : accessLevelService.readAccessLevel());
		Boolean publicSourceCheck= (source.getCreatedBy()!=null ? actualSourcePermission!=sourcePermission :false);
		if( publicSourceCheck || destPermission!=actualDestinationPermission
				|| sourcePermission< permissionToCompare 
				|| destPermission<accessLevelService.readWriteAccessLevel() 
				|| sourcePermission > accessLevelService.ownerAccessLevel()
				|| destPermission > accessLevelService.ownerAccessLevel()) {
			this.setMessage("You do not have sufficient privileges to do this action");
			return false;
		}
		if(source.getResourceId().equals(destination.getResourceId()) || isParentToChildCopy(source,destination)) {
			this.setMessage("We could not copy the resource(s) successfully");
			return false;
		}
		return true;
    }

	private boolean isParentToChildCopy(HIResource source, HIResource destination) {
		if(destination.getParentId()==null || source.getParentId()!=null && source.getParentId().equals(destination.getParentId()))
			return false;
		if(destination.getParentId().equals(source.getResourceId()))
			return true;
		destination=hiResourceServiceDB.getHIResourceById(destination.getParentId());
		return isParentToChildCopy(source, destination);
	}

	public JSONObject getJsonInputFormData() {
		return jsonInputFormData;
	}

	public void setJsonInputFormData(JSONObject jsonInputFormData) {
		this.jsonInputFormData = jsonInputFormData;
	}

	private Integer fetchPermission(Integer userId, HIResource resource) {
		Integer permission=null;
		if (resource != null) {
			if (resource.getCreatedBy() != null) {
				if (resource.getCreatedBy().equals(userId))
					return accessLevelService.ownerAccessLevel();
				else
					permission=getPermission(userId, resource.getResourceId());
			} else
				permission = accessLevelService.publicResourceAccessLevel();
		}
		return permission;
	}
	
	private Integer getPermission(Integer userId,Integer resId) {
		Integer permission=null;
		while(true) {
			HIResourceSecurityDB security = hiResourceServiceDB
					.fetchPermissionBySharedUserIdAndResourceId(userId, resId);
			if(security != null && security.getPermission()!=null) {
				permission=security.getPermission();
				break;
			}
			HIResource e=hiResourceServiceDB.getHIResourceById(resId);
			if(e.getCreatedBy()==null) {
				permission=accessLevelService.publicResourceAccessLevel();
				break;
			}
			if(e.getCreatedBy().equals(userId)) {
				permission=accessLevelService.ownerAccessLevel();
				break;
			}
			if(e.getParentId()==null)
				break;
			else
				resId=e.getParentId();
		}
		return permission;
	}
	
	protected HIResourceDTO prepareResponse(CutCopyFileInfo fileInfo) {
		HIResourceDTO data = new HIResourceDTO();
		data.setExtension(fileInfo.getExtension());
		data.setLastModified(fileInfo.getLastModified());
		data.setName(fileInfo.getName());
		data.setPath(fileInfo.getPath());
		data.setPermissionLevel(fileInfo.getPermissionLevel());
		data.setTitle(fileInfo.getTitle());
		data.setType(fileInfo.getType());
		data.setResourceId(fileInfo.getResourceId());
		if(data.getType().equals("folder")) {
			List<HIResourceDTO> children = new ArrayList<>();
			fileInfo.getChildren().forEach(e->{
				children.add(prepareResponse(e));
			});
			data.setChildren(children);
		}
		return data;
	}

}

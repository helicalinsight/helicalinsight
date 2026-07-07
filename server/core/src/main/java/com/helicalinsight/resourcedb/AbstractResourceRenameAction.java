package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

public abstract class AbstractResourceRenameAction implements ResourceRenameHandler {

    private Integer resourceId;
    private String resourceType;
    private String updatedName;
    private String isPublic;
    private String isVisible;
    private String message;
    private HIResource hiResource;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUpdatedName() {
        return updatedName;
    }

    public void setUpdatedName(String updatedName) {
        this.updatedName = updatedName;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    public String getMessage() {
        return message;
    }

    public Boolean validateName() {
        String validationMessage = null;
        if (updatedName.length() <= 2) {
            validationMessage = "Please enter a valid name, minimum length 3.";
        }


        setMessage(validationMessage);
        return validationMessage == null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }

    public static AbstractResourceRenameAction performRenameAction(String resourceType, Integer resourceId, String updatedName, String isPublic, String isVisible, HIResource hiResource) {
        AbstractResourceRenameAction resourceAction = (AbstractResourceRenameAction) ApplicationContextAccessor.getBean(resourceType + "RenameHandler");
        resourceAction.setResourceId(resourceId);
        resourceAction.setUpdatedName(updatedName);
        resourceAction.setIsPublic(isPublic);
        resourceAction.setIsVisible(isVisible);
        resourceAction.setHiResource(hiResource);
        return resourceAction;
    }
}

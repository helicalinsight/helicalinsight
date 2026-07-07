package com.helicalinsight.resourcedb;


import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

@Component("renameResourceAction")
@Scope("prototype")
public class RenameResourceAction extends AbstractResourceAction {
    private static final Logger logger = LoggerFactory.getLogger(RenameResourceAction.class);

    @Override
    public Boolean performAction() {
        FileOperationDTO payLoad = (FileOperationDTO) this.getPayLoad();
        AtomicBoolean isUpdated = new AtomicBoolean(false);
        String isPublic = "";
        if (null != payLoad.getIsPublic()) {
            isPublic = payLoad.getIsPublic();
        }
        String isVisible = "true";
        if (null != payLoad.getIsVisible() && !payLoad.getIsVisible().isEmpty()) {
            isVisible = payLoad.getIsVisible();
        }
        List<List<String>> sourceArray = JsonToCollectionUtil.getMapList(payLoad.getSourceArray());

        String finalIsPublic = isPublic;
        String finalIsVisible = isVisible;
        sourceArray.stream().forEach(outerLoop -> {
            try {
                Boolean rename = rename(outerLoop, finalIsPublic, finalIsVisible);
                isUpdated.set(rename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return isUpdated.get();
    }

    private Boolean rename(List<String> sourceList, String isPublic, String isVisible) throws Exception {
        String resourcePath = sourceList.get(0);
        String updatedName = sourceList.get(1);
       // updatedName = DBProcessor.checkAndReplaceSpecialChars(updatedName).trim();
        String[] splitString = resourcePath.split("/");
        splitString[splitString.length - 1] = updatedName;
        Security security = SecurityUtils.securityObject();
        Integer createdBy = null;
        if (StringUtils.isNotEmpty(security.getCreatedBy())) {
            createdBy = Integer.valueOf(security.getCreatedBy());
        }

        if (updatedName.length() <= 2) {
            setMessage("Please enter a valid name, minimum length 3.");
            return false;
        }




        HIResource hiResource = hiResourceServiceDB.getResourceByUrl(resourcePath);
        hiResource.setTitle(updatedName);
        hiResource.setLastUpdatedTime(new Date());
        HIResourceFolder hiResourceFolder = hiResource.getHiResourceFolder();
        if (null != hiResourceFolder) {
            hiResourceFolder.setLastUpdatedTime(new Date());
            hiResourceFolder.setTitle(updatedName);
            if (StringUtils.isNotEmpty(isPublic)) {
                if (isPublic.equals("true")) {
                    hiResourceFolder.setCreatedBy(null);
                    hiResource.setCreatedBy(null);
                } else {
                    hiResourceFolder.setCreatedBy(createdBy);
                    hiResource.setCreatedBy(createdBy);
                }
            }
            if (StringUtils.isNotEmpty(isVisible)) {
                Boolean bVisible = Boolean.valueOf(isVisible);
                hiResourceFolder.setVisible(bVisible);
                hiResource.setVisible(bVisible);

            }
            try {
                logger.debug("trying to rename folder");
                hiResourceServiceDB.editHIResource(hiResource);
                setMessage("Rename is successful");
                return true;
            } catch (Exception e) {
                String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
                logger.error("There was a problem in serving the request. The cause is " + rootCauseMessage);
            }
        } else {
            hiResource.setTitle(updatedName);
            Date lastUpdatedTime = new Date();
            hiResource.setLastUpdatedTime(lastUpdatedTime);
            if (StringUtils.isNotEmpty(isPublic)) {
                if (isPublic.equals("true")) {
                    hiResource.setCreatedBy(null);
                } else {
                    hiResource.setCreatedBy(createdBy);
                }
            }

            AbstractResourceRenameAction abstractResourceRenameAction =
                    AbstractResourceRenameAction.performRenameAction(hiResource.getResourceType().getName(), hiResource.getResourceId(), updatedName, isPublic, isVisible, hiResource);
            hiResourceServiceDB.editHIResource(hiResource);
            abstractResourceRenameAction.performAction();
            setMessage(abstractResourceRenameAction.getMessage());
            return true;
        }

        return false;
    }
}

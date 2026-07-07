package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

@Component("efwvfRenameHandler")
@Scope("prototype")
public class EFWVFReportResourceRenameHandler extends AbstractResourceRenameAction  {

    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;

    @Override
    public Boolean performAction() {
        Integer resourceId = this.getResourceId();
        String updatedName = this.getUpdatedName();
        String isVisible = this.getIsVisible();
        HIResource hiResource = getHiResource();
        if(!validateName()){
            return false;
        }
        HIResource hiResourceById = hiResourceServiceDB.getHIResourceById(resourceId);
        if(null!=hiResourceById){
            HIResourceEFWVF hiResourceEFWVFById = hiResourceServiceDB.getHIResourceEFWVFById(resourceId);
            if(null!=hiResourceEFWVFById){
                if(StringUtils.isNotEmpty(isVisible)){
                    hiResource.setVisible(Boolean.valueOf(isVisible));
                    hiResource.setTitle(updatedName);
                    Date lastUpdatedTime = new Date();
                    hiResource.setLastUpdatedTime(lastUpdatedTime);
                    hiResourceEFWVFById.setLastUpdatedTime(lastUpdatedTime);
                    if(StringUtils.isNotEmpty(isVisible)){
                        hiResource.setVisible(Boolean.valueOf(isVisible));
                    }
                }
            }
            try{
                hiResourceServiceDB.addHIResource(hiResource);
                hiResourceServiceDB.addHIResourceEFWVF(hiResourceEFWVFById);
                setMessage("Rename is successful");
                return true;
            }catch (Exception e){
                String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
                setMessage(rootCauseMessage);
                return false;
            }
        }
        return false;
    }
}

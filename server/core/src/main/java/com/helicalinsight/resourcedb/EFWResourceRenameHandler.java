package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

@Component("efwRenameHandler")
@Scope("prototype")
public class EFWResourceRenameHandler extends AbstractResourceRenameAction  {

    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;

    @Override
    public Boolean performAction() {
        HIResource hiResource = this.getHiResource();
        String updatedName =getUpdatedName();
        String isVisible = getIsVisible();
        if(!validateName()){
            return false;
        }
        if(null!=hiResource){
            HIResourceEFW hiResourceEFW = hiResource.getHiResourceEFW();
            if(null!=hiResourceEFW){
                    hiResource.setTitle(updatedName);
                    hiResourceEFW.setTitle(updatedName);
                    Date lastUpdatedTime = new Date();
                    hiResource.setLastUpdatedTime(lastUpdatedTime);
                    hiResourceEFW.setLastUpdatedTime(lastUpdatedTime);
                    if(StringUtils.isNotEmpty(isVisible)){
                        hiResource.setVisible(Boolean.valueOf(isVisible));
                        hiResourceEFW.setVisible(Boolean.valueOf(isVisible));
                    }

            }
            try{
                hiResourceServiceDB.editHIResource(hiResource);
                hiResourceServiceDB.editHIResourceEFW(hiResourceEFW);
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

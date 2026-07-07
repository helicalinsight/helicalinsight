package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWCE;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

@Component("efwceRenameHandler")
@Scope("prototype")
public class EFWCEResourceRenameHandler extends AbstractResourceRenameAction  {

    @Autowired
    private HIResourceServiceDB hiResourceServiceDB;

    @Override
    public Boolean performAction() {
        HIResource hiResource = this.getHiResource();
        String updatedName = this.getUpdatedName();
        String isVisible = this.getIsVisible();
       if(!validateName()){
           return false;
       }
        if(null!=hiResource){
            HIResourceEFWCE hiResourceEFWCE = hiResource.getHiResourceEFWCE();
            if(null!=hiResourceEFWCE){

                    hiResource.setTitle(updatedName);
                    hiResourceEFWCE.setFileName(updatedName);
                    Date lastUpdatedTime = new Date();
                    hiResource.setLastUpdatedTime(lastUpdatedTime);
                    hiResourceEFWCE.setLastUpdatedTime(lastUpdatedTime);
                    if(StringUtils.isNotEmpty(isVisible)){
                        hiResource.setVisible(Boolean.valueOf(isVisible));
                    }

            }
            try{
                hiResourceServiceDB.editHIResource(hiResource);
                hiResourceServiceDB.editHIResourceEFWCE(hiResourceEFWCE);
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

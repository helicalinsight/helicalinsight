package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWCE;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

@Component("efwddRenameHandler")
@Scope("prototype")
public class EFWDDResourceRenameHandler extends AbstractResourceRenameAction  {

    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;

    @Override
    public Boolean performAction() {
        HIResource hiResource = this.getHiResource();
        String updatedName = getUpdatedName();
        String isVisible = getIsVisible();
        if(!validateName()){
            return false;
        }
        if(null!=hiResource){
            HIResourceEFWDD hiResourceEFWDD = hiResource.getHiResourceEFWDD();
            if(null!=hiResourceEFWDD){
                    hiResource.setTitle(updatedName);
                    hiResourceEFWDD.setFileName(updatedName);
                    Date lastUpdatedTime = new Date();
                    hiResource.setLastUpdatedTime(lastUpdatedTime);
                    hiResourceEFWDD.setLastUpdatedTime(lastUpdatedTime);
                    if(StringUtils.isNotEmpty(isVisible)){
                        hiResource.setVisible(Boolean.valueOf(isVisible));
                    }
            }
            try{
                hiResourceServiceDB.editHIResource(hiResource);
                //hiResourceServiceDB.editHIResourceEFWDD(hiResourceEFWDD);
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

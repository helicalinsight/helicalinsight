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

@Component("reportRenameHandler")
@Scope("prototype")
public class ReportResourceRenameHandler extends AbstractResourceRenameAction  {

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
            HIResourceReport hiResourceReport = hiResource.getHiResourceReport();
            if(null!=hiResource.getHiResourceEFW()){
                if(StringUtils.isNotEmpty(isVisible)){
                    hiResource.setVisible(Boolean.valueOf(isVisible));
                    hiResource.setTitle(updatedName);
                    Date lastUpdatedTime = new Date();
                    hiResource.setLastUpdatedTime(lastUpdatedTime);
                    hiResourceReport.setLastUpdatedTime(lastUpdatedTime);
                    if(StringUtils.isNotEmpty(isVisible)){
                        hiResource.setVisible(Boolean.valueOf(isVisible));
                    }
                }
            }
            try{
                hiResourceServiceDB.addHIResource(hiResource);
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

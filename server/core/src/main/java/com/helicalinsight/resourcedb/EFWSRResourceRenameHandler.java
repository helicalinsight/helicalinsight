package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWCE;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.model.HIResourceEFWSR;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.IScheduleService;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

@Component("efwsrRenameHandler")
@Scope("prototype")
public class EFWSRResourceRenameHandler extends AbstractResourceRenameAction  {

    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;
    
    @Autowired
    IScheduleService scheduleService;

    @Override
    public Boolean performAction() {
        HIResource hiResource = this.getHiResource();
        String updatedName = getUpdatedName();
        String isVisible = getIsVisible();
        if(!validateName()){
            return false;
        }
        if(null!=hiResource){
            HIResourceEFWSR hiResourceEFWSR = hiResource.getHiResourceEFWSR();
            String oldName=hiResourceEFWSR.getReportName();
            if(hiResourceEFWSR!=null){
                if(StringUtils.isNotEmpty(isVisible)){
                    hiResource.setVisible(Boolean.valueOf(isVisible));
                    hiResource.setTitle(updatedName);
                    Date lastUpdatedTime = new Date();
                    hiResource.setLastUpdatedTime(lastUpdatedTime);
                    hiResourceEFWSR.setReportName(updatedName);
                    hiResourceEFWSR.setLastUpdatedTime(lastUpdatedTime);
                    if(StringUtils.isNotEmpty(isVisible)){
                        hiResource.setVisible(Boolean.valueOf(isVisible));
                    }
                }
            }
            try{
                hiResourceServiceDB.editHIResource(hiResource);
                HIResource usedHRepot=hiResourceServiceDB.getResourceByUrl(hiResourceEFWSR.getReportDirectory()+"/"+hiResourceEFWSR.getReportFile());
                scheduleService.updateScheduleByResourceIdAndScheduleName(usedHRepot.getResourceId(),oldName,hiResourceEFWSR.getReportName());
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

package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("instantRenameHandler")
@Scope("prototype")
public class InstantResourceRenameHandler extends AbstractResourceRenameAction {

    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;

    @Override
    public Boolean performAction() {
        HIResource hiResource = this.getHiResource();
        String updatedName = this.getUpdatedName();
        String isVisible = this.getIsVisible();
        if (!validateName()) {
            return false;
        }
        if (null != hiResource) {
            HIResourceInstantReport hiResourceHReport = hiResource.getHiResourceInstantReport();
            if (null != hiResourceHReport) {
                hiResource.setTitle(updatedName);
                Date lastUpdatedTime = new Date();
                hiResource.setLastUpdatedTime(lastUpdatedTime);
                hiResourceHReport.setLastUpdatedTime(lastUpdatedTime);
                hiResourceHReport.setReportName(updatedName);
                if (StringUtils.isNotEmpty(isVisible)) {
                    hiResource.setVisible(Boolean.valueOf(isVisible));
                }
            }
            try {
                hiResourceServiceDB.editHIResource(hiResource);

                setMessage("Rename is successful");
                return true;
            } catch (Exception e) {
                String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
                setMessage(rootCauseMessage);
                return false;
            }
        }
        return false;
    }
}

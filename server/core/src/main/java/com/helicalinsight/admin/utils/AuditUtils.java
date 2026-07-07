package com.helicalinsight.admin.utils;

import java.util.Date;

import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.AuditDetails;

public class AuditUtils {
	
	public static AuditDetails getAuditDetails(HIPhase phase) {
		RolesAccessComponent roleAccess =  ApplicationContextAccessor.getBean(RolesAccessComponent.class);
		AuditDetails audit = new AuditDetails();
		audit.setStartTime(new Date());
		audit.setUser(roleAccess.getLoggedInUser());
		audit.setType(phase.getResourceType());
    	audit.setStatus(phase.getStatus());
		return audit;
	}

}

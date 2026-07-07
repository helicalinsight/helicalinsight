package com.helicalinsight.admin.service;

import java.util.Deque;
import java.util.List;

import com.helicalinsight.admin.model.HIAuditDetails;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.resourcedb.AuditDetails;

public interface AuditService {
	
	void audit(AuditDetails auditDetails);

	void audit(Deque<AuditDetails> auditQueue,HIResource hiResource);
	
	List<HIAuditDetails> fetchAllBasedOnResourceId(Integer resourceId);
	void save(HIAuditDetails auditDetails);
}

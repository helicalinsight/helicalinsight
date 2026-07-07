package com.helicalinsight.admin.dao;

import java.util.List;

import com.helicalinsight.admin.model.HIAuditDetails;

public interface AuditDao {
	void audit(HIAuditDetails audit);
	List<HIAuditDetails> fetchAllBasedOnResourceId(Integer resourceId);
	void save(HIAuditDetails auditDetails);
}

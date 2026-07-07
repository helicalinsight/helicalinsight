package com.helicalinsight.admin.service.impl;

import java.util.Date;
import java.util.Deque;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helicalinsight.admin.dao.AuditDao;
import com.helicalinsight.admin.model.HIAuditDetails;
import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.service.AuditService;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.resourcedb.AuditDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService {

	private final PhaseDetailsService phaseDetailsService;
	private final AuditDao auditDao;

	@Transactional
	@Override
	public void audit(AuditDetails audit) {
		HIAuditDetails details = new HIAuditDetails();
		details.setActionName(audit.getAction());
		details.setHiResource(audit.getHiResource());
		details.setPhaseStartTime(audit.getStartTime());
		details.setPhaseEndTime(audit.getEndTime());
		details.setDescription(audit.getDescription());
		HIPhase phase = phaseDetailsService.getHIPhaseByTypeAndStatus(audit.getType(), audit.getStatus());
		details.setPhaseId(phase);
		details.setLastPhaseId(phaseDetailsService.findTheLastPhaseId(audit.getHiResource().getResourceId()));
		details.setTriggeredBy(audit.getUser());
		auditDao.audit(details);
	}
	
	@Transactional
	@Override
	public void audit(Deque<AuditDetails> auditQueue,HIResource resource) {
		AuditDetails finalStatus = auditQueue.getLast();
		while(!auditQueue.isEmpty()) {
			AuditDetails audit =  auditQueue.poll();
			audit.setHiResource(resource);
			audit(audit);
		}

		if (finalStatus != null && finalStatus.getDescription() != null
				&& !finalStatus.getDescription().equals("Interrupted")) {
					
			HIResourcePhaseStatus status = new HIResourcePhaseStatus();
			status.setAction(finalStatus.getAction());
			HIPhase phase = phaseDetailsService.getHIPhaseByTypeAndStatus(finalStatus.getType(),
					finalStatus.getStatus());
			status.setHiPhase(phase);
			status.setHiResource(finalStatus.getHiResource());
			status.setLastUpdatedDate(new Date());
			status.setUser(finalStatus.getUser());
			if (finalStatus.getDbNames() != null && !finalStatus.getDbNames().isEmpty()) {
				status.setDbName(finalStatus.getDbNames());
			}
			phaseDetailsService.addHIResourcePhaseStatus(status);
		}
	}

	@Transactional
	@Override
	public List<HIAuditDetails> fetchAllBasedOnResourceId(Integer resourceId) {
		return auditDao.fetchAllBasedOnResourceId(resourceId);
	}

	@Transactional
	@Override
	public void save(HIAuditDetails auditDetails) {
		auditDao.save(auditDetails);
	}
}

package com.helicalinsight.admin.utils;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.service.AuditService;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.AuditDetails;

public class AuditContext {

	private Deque<AuditDetails> auditQueue;
	private HIResource hiResource;
	private String action;
	private Map<String,HIPhase> hiResourcePhases;
	private List<String> dbNames;

	public AuditContext() {
		this.auditQueue = new ArrayDeque<>();
		hiResourcePhases = ApplicationProperties.getInstance().getHIResourcePhases();
		
	}
	
	public AuditDetails init(String phaseAction) {
		return init(phaseAction,"");
	}
	
	public AuditDetails init(String phaseAction , String description) {
		HIPhase phase =  hiResourcePhases.get(phaseAction);
		AuditDetails auditDetails = AuditUtils.getAuditDetails(phase);
		AuditDetails prev = !this.auditQueue.isEmpty() ? this.auditQueue.getLast():null;
		auditDetails.setStartTime(prev !=null ?prev.getEndTime():new Date());
		auditDetails.setAction(action);
		auditDetails.setDescription(description);
		this.auditQueue.offer(auditDetails);
		return auditDetails;
	}
	
	public Deque<AuditDetails> getAuditQueue(){
		if ( auditQueue == null ) {
			auditQueue = new ArrayDeque<>();
		}
		return auditQueue;
	}

	public void commit() {
		AuditService auditService = ApplicationContextAccessor.getBean(AuditService.class);
		AuditDetails finalAudit = auditQueue.getLast();
		finalAudit.setDbNames(dbNames);
		auditService.audit(auditQueue,hiResource);
	}
	
	public Integer getLastPhaseId() {
		PhaseDetailsService phaseService = ApplicationContextAccessor.getBean(PhaseDetailsService.class);
		HIResourcePhaseStatus status =  phaseService.findHIResourcePhaseStatusByResourceId(hiResource.getResourceId());
		if(status != null) return status.getId();
		else throw new EfwServiceException("Previous status is not available for this resource.");
	}
	
	public HIResource getHIResource() {
		return hiResource;
	}
	public void setHIResource(HIResource resource) {
		this.hiResource = resource;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public void setDbNames(List<String> dbNames) {
		if ( this.dbNames == null ) {
			this.dbNames = new ArrayList<>();
		}
		this.dbNames.addAll(dbNames);
	}
	public List<String> getDbNames() {
		return this.dbNames;
	}
}

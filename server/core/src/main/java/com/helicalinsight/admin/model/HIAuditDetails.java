package com.helicalinsight.admin.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "hi_audit_details")
public class HIAuditDetails implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "resource_id")
	private HIResource hiResource;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "phase_id")
	private HIPhase phaseId;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "last_phase_id")
	private HIPhase lastPhaseId;

	@Column(name = "action_name")
	private String actionName;

	@Column(name = "phase_start_time")
	private Date phaseStartTime;

	@Column(name = "phase_end_time")
	private Date phaseEndTime;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "triggered_by")
	private User triggeredBy;
	
	private String description;

}

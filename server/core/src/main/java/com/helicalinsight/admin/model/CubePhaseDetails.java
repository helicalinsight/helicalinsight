package com.helicalinsight.admin.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Somen
 *         Created on 1/13/2020.
 */

@Deprecated
//@Entity
//@Table(name = "metadata_dump_audit")
public class CubePhaseDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "process_name")
    private String processName;

    @Column(name = "process_status")
    private String processStatus;

    @Column(name = "process_start_time")
    private Date processStartTime;

    @Column(name = "process_end_time")
    private Date processEndTime;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "cube_id")
    private String cubeId;


    @Column(name = "phase_name")
    private String phaseName;

    @Column(name = "last_executed_phase")
    private String lastPhase;
    
    @Column(name = "metadata_id")
    private String metadataId;
    
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(
//            name = "metadata_dumped_dbs",
//            joinColumns=@JoinColumn(name = "phase_details_id", referencedColumnName = "id")
//        )
//    private List<String> dbName = new ArrayList<>();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubePhaseDetails that = (CubePhaseDetails) o;

        if (cubeId != null ? !cubeId.equals(that.cubeId) : that.cubeId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastPhase != null ? !lastPhase.equals(that.lastPhase) : that.lastPhase != null) return false;
        if (lastPhaseId != null ? !lastPhaseId.equals(that.lastPhaseId) : that.lastPhaseId != null) return false;
        if (phaseName != null ? !phaseName.equals(that.phaseName) : that.phaseName != null) return false;
        if (processEndTime != null ? !processEndTime.equals(that.processEndTime) : that.processEndTime != null)
            return false;
        if (processName != null ? !processName.equals(that.processName) : that.processName != null) return false;
        if (processStartTime != null ? !processStartTime.equals(that.processStartTime) : that.processStartTime != null)
            return false;
        if (processStatus != null ? !processStatus.equals(that.processStatus) : that.processStatus != null)
            return false;
        if (triggeredBy != null ? !triggeredBy.equals(that.triggeredBy) : that.triggeredBy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (processName != null ? processName.hashCode() : 0);
        result = 31 * result + (processStatus != null ? processStatus.hashCode() : 0);
        result = 31 * result + (processStartTime != null ? processStartTime.hashCode() : 0);
        result = 31 * result + (processEndTime != null ? processEndTime.hashCode() : 0);
        result = 31 * result + (triggeredBy != null ? triggeredBy.hashCode() : 0);
        result = 31 * result + (cubeId != null ? cubeId.hashCode() : 0);
        result = 31 * result + (phaseName != null ? phaseName.hashCode() : 0);
        result = 31 * result + (lastPhase != null ? lastPhase.hashCode() : 0);
        result = 31 * result + (lastPhaseId != null ? lastPhaseId.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public Date getProcessStartTime() {
        return processStartTime;
    }

    public void setProcessStartTime(Date processStartTime) {
        this.processStartTime = processStartTime;
    }

    public Date getProcessEndTime() {
        return processEndTime;
    }

    public void setProcessEndTime(Date processEndTime) {
        this.processEndTime = processEndTime;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getLastPhase() {
        return lastPhase;
    }

    public void setLastPhase(String lastPhase) {
        this.lastPhase = lastPhase;
    }

    public Long getLastPhaseId() {
        return lastPhaseId;
    }

    public void setLastPhaseId(Long lastPhaseId) {
        this.lastPhaseId = lastPhaseId;
    }

    @Column(name = "last_phase_id")
    private Long lastPhaseId;

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

//	public List<String> getDbNames() {
//		return dbName;
//	}
//
//	public void setDbNames(List<String> dbName) {
//		this.dbName = dbName;
//	}
	
}

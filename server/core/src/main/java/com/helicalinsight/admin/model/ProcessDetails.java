package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by helical021 on 1/13/2020.
 */


@Entity
@Table(name = "cube_process")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProcessDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long processId;

    @Column(name = "process_name")
    private String processName;

    @Column(name = "process_status")
    private String processStatus;

    @Column(name = "process_start_time")
    private Date processStartTime;

    @Column(name = "process_stop_time")
    private Date processStopTime;

    @Column(name = "process_initiated_by")
    private String processInitiatedBy;

    @Column(name = "cube_id")
    private String cubeId;


    @Column(name = "driver_class")
    private String driver;

    @Column(name = "dialect")
    private String dialect;


    @Override
    public String toString() {
        return "ProcessDetails{" +
                "processId=" + processId +
                ", processName='" + processName + '\'' +
                ", processStatus='" + processStatus + '\'' +
                ", processStartTime=" + processStartTime +
                ", processStopTime=" + processStopTime +
                ", processInitiatedBy='" + processInitiatedBy + '\'' +
                ", cubeId='" + cubeId + '\'' +
                ", driver='" + driver + '\'' +
                ", dialect='" + dialect + '\'' +
                ", processingClass='" + processingClass + '\'' +
                ", cubeName='" + cubeName + '\'' +
                ", connectionId='" + connectionId + '\'' +
                ", metadatDir='" + metadatDir + '\'' +
                ", metadataFile='" + metadataFile + '\'' +
                ", connectionType='" + connectionType + '\'' +
                ", is_schedule=" + is_schedule +
                ", scheduleDate=" + scheduleDate +
                ", scheduleTime=" + scheduleTime +
                ", scheduleType='" + scheduleType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", metadataName='" + metadataName + '\'' +
                ", createdDate=" + createdDate +
                ", scheduleStatus='" + scheduleStatus + '\'' +
                ", nextScheduleTime=" + nextScheduleTime +
                ", databaseSize=" + databaseSize +
                ", comments='" + comments + '\'' +
                '}';
    }

    @Column(name = "processing_class")
    private String processingClass;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessDetails that = (ProcessDetails) o;

        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (connectionId != null ? !connectionId.equals(that.connectionId) : that.connectionId != null) return false;
        if (connectionType != null ? !connectionType.equals(that.connectionType) : that.connectionType != null)
            return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (cubeId != null ? !cubeId.equals(that.cubeId) : that.cubeId != null) return false;
        if (cubeName != null ? !cubeName.equals(that.cubeName) : that.cubeName != null) return false;
        if (databaseSize != null ? !databaseSize.equals(that.databaseSize) : that.databaseSize != null) return false;
        if (dialect != null ? !dialect.equals(that.dialect) : that.dialect != null) return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (is_schedule != null ? !is_schedule.equals(that.is_schedule) : that.is_schedule != null) return false;
        if (metadatDir != null ? !metadatDir.equals(that.metadatDir) : that.metadatDir != null) return false;
        if (metadataFile != null ? !metadataFile.equals(that.metadataFile) : that.metadataFile != null) return false;
        if (metadataName != null ? !metadataName.equals(that.metadataName) : that.metadataName != null) return false;
        if (nextScheduleTime != null ? !nextScheduleTime.equals(that.nextScheduleTime) : that.nextScheduleTime != null)
            return false;
        if (processId != null ? !processId.equals(that.processId) : that.processId != null) return false;
        if (processInitiatedBy != null ? !processInitiatedBy.equals(that.processInitiatedBy) : that.processInitiatedBy != null)
            return false;
        if (processName != null ? !processName.equals(that.processName) : that.processName != null) return false;
        if (processStartTime != null ? !processStartTime.equals(that.processStartTime) : that.processStartTime != null)
            return false;
        if (processStatus != null ? !processStatus.equals(that.processStatus) : that.processStatus != null)
            return false;
        if (processStopTime != null ? !processStopTime.equals(that.processStopTime) : that.processStopTime != null)
            return false;
        if (processingClass != null ? !processingClass.equals(that.processingClass) : that.processingClass != null)
            return false;
        if (scheduleDate != null ? !scheduleDate.equals(that.scheduleDate) : that.scheduleDate != null) return false;
        if (scheduleStatus != null ? !scheduleStatus.equals(that.scheduleStatus) : that.scheduleStatus != null)
            return false;
        if (scheduleTime != null ? !scheduleTime.equals(that.scheduleTime) : that.scheduleTime != null) return false;
        if (scheduleType != null ? !scheduleType.equals(that.scheduleType) : that.scheduleType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = processId != null ? processId.hashCode() : 0;
        result = 31 * result + (processName != null ? processName.hashCode() : 0);
        result = 31 * result + (processStatus != null ? processStatus.hashCode() : 0);
        result = 31 * result + (processStartTime != null ? processStartTime.hashCode() : 0);
        result = 31 * result + (processStopTime != null ? processStopTime.hashCode() : 0);
        result = 31 * result + (processInitiatedBy != null ? processInitiatedBy.hashCode() : 0);
        result = 31 * result + (cubeId != null ? cubeId.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (dialect != null ? dialect.hashCode() : 0);
        result = 31 * result + (processingClass != null ? processingClass.hashCode() : 0);
        result = 31 * result + (cubeName != null ? cubeName.hashCode() : 0);
        result = 31 * result + (connectionId != null ? connectionId.hashCode() : 0);
        result = 31 * result + (metadatDir != null ? metadatDir.hashCode() : 0);
        result = 31 * result + (metadataFile != null ? metadataFile.hashCode() : 0);
        result = 31 * result + (connectionType != null ? connectionType.hashCode() : 0);
        result = 31 * result + (is_schedule != null ? is_schedule.hashCode() : 0);
        result = 31 * result + (scheduleDate != null ? scheduleDate.hashCode() : 0);
        result = 31 * result + (scheduleTime != null ? scheduleTime.hashCode() : 0);
        result = 31 * result + (scheduleType != null ? scheduleType.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (metadataName != null ? metadataName.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (scheduleStatus != null ? scheduleStatus.hashCode() : 0);
        result = 31 * result + (nextScheduleTime != null ? nextScheduleTime.hashCode() : 0);
        result = 31 * result + (databaseSize != null ? databaseSize.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }

    public String getDriver() {
        return driver;

    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getProcessingClass() {
        return processingClass;
    }

    public void setProcessingClass(String processingClass) {
        this.processingClass = processingClass;
    }

    @Column(name = "cube_name")
    private String cubeName;

    @Column(name = "connection_id")
    private String connectionId;

    @Column(name = "metadata_dir")
    private String metadatDir;

    @Column(name = "metadata_file")
    private String metadataFile;

    @Column(name = "connection_type")
    private String connectionType;

    @Column(name = "is_schedule")
    private Boolean is_schedule;

    @Column(name = "schedule_date")
    private Date scheduleDate;

    @Column(name = "schedule_time")
    private Date scheduleTime;

    @Column(name = "schedule_type")
    private String scheduleType;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "metadata_name")
    private String metadataName;

    public String getMetadataName() {
        return metadataName;
    }

    public void setMetadataName(String metadataName) {
        this.metadataName = metadataName;
    }

    @Column(name = "created_date")
    private Date createdDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
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

    public Date getProcessStopTime() {
        return processStopTime;
    }

    public void setProcessStopTime(Date processStopTime) {
        this.processStopTime = processStopTime;
    }

    public String getProcessInitiatedBy() {
        return processInitiatedBy;
    }

    public void setProcessInitiatedBy(String processInitiatedBy) {
        this.processInitiatedBy = processInitiatedBy;
    }

    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getMetadatDir() {
        return metadatDir;
    }

    public void setMetadatDir(String metadatDir) {
        this.metadatDir = metadatDir;
    }

    public String getMetadataFile() {
        return metadataFile;
    }

    public void setMetadataFile(String metadataFile) {
        this.metadataFile = metadataFile;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public Boolean getIs_schedule() {
        return is_schedule;
    }

    public void setIs_schedule(Boolean is_schedule) {
        this.is_schedule = is_schedule;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Date getNextScheduleTime() {
        return nextScheduleTime;
    }

    public void setNextScheduleTime(Date nextScheduleTime) {
        this.nextScheduleTime = nextScheduleTime;
    }

    public Long getDatabaseSize() {
        return databaseSize;
    }

    public void setDatabaseSize(Long databaseSize) {
        this.databaseSize = databaseSize;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "schedule_status")
    private String scheduleStatus;

    @Column(name = "next_schedule_time")
    private Date nextScheduleTime;

    @Column(name = "database_size")
    private Long databaseSize;

    @Column(name = "comments")
    private String comments;


}

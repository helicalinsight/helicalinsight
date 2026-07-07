package com.helicalinsight.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Rajesh
 *         Created by author on 2/17/2020.
 */
@Entity
@Table(name = "schedules")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedules implements Identifiable<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(
            name = "CustomIdGenerator",
            strategy = "com.helicalinsight.scheduling.model.CustomIdGenerator",
            parameters = {@org.hibernate.annotations.Parameter(
                    name = "sequence_name",
                    value = "post_sequence"),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value", value = "1000")})
    @GeneratedValue(
            generator = "CustomIdGenerator",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "active")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "schedule_type")
    private ResourceType scheduleType;

    @ManyToOne
    @JoinColumn(name = "hi_resource_db_id")
    private HIResource hiResource;


    @Column(name = "schedule_name")
    private String scheduleName;

    @Column(name = "last_execution_date")
    private Date lastExecutionDate;

    @Column(name = "last_execution_status")
    private Integer lastExecutionStatus;

    @Column(name = "next_execution_date")
    private Date nextExecutionDate;

    @Column(name = "no_of_execution")
    private Integer noOfExecution;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "days_of_week")
    private String daysOfWeek;

    @Column(name = "frequency")
    private String frequency;


    @Column(name = "repeat_by")
    private String repeatBy;

    @Column(name = "repeats_every")
    private Integer repeatsEvery;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "ends_on")
    private String endsOn;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "end_after_executions")
    private Integer endAfterExecutions;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "scheduled_time")
    private String scheduledTime;

    @Column(name = "scheduled_end_time")
    private String scheduledEndTime;

    @OneToMany(mappedBy = "scheduleIdOfJobParameter", cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    private List<JobParameters> jobParameters;

    @Column(name = "is_zip")
    private Boolean isZip;

    @Column(name = "email_recipients")
    private String emailRecipients;

    @Column(name = "export_formats")
    private String exportFormats;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_body", length = 10000)
    private String emailBody;

    @Column(name = "is_migrated")
    private Boolean isMigrated;

    public Schedules() {
    }

    public Schedules(Long id) {
        this.scheduleId = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    @JsonIgnore
    public Long getScheduleId() {
        return scheduleId;
    }
    @JsonProperty
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ResourceType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ResourceType scheduleType) {
        this.scheduleType = scheduleType;
    }


    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public Integer getLastExecutionStatus() {
        return lastExecutionStatus;
    }

    public void setLastExecutionStatus(Integer lastExecutionStatus) {
        this.lastExecutionStatus = lastExecutionStatus;
    }

    public Date getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(Date nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public Integer getNoOfExecution() {
        return noOfExecution;
    }

    public void setNoOfExecution(Integer noOfExecution) {
        this.noOfExecution = noOfExecution;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getRepeatBy() {
        return repeatBy;
    }

    public void setRepeatBy(String repeatBy) {
        this.repeatBy = repeatBy;
    }

    public Integer getRepeatsEvery() {
        return repeatsEvery;
    }

    public void setRepeatsEvery(Integer repeatsEvery) {
        this.repeatsEvery = repeatsEvery;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEndsOn() {
        return endsOn;
    }

    public void setEndsOn(String endsOn) {
        this.endsOn = endsOn;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getEndAfterExecutions() {
        return endAfterExecutions;
    }

    public void setEndAfterExecutions(Integer endAfterExecutions) {
        this.endAfterExecutions = endAfterExecutions;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getScheduledEndTime() {
        return scheduledEndTime;
    }

    public void setScheduledEndTime(String scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }

    public List<JobParameters> getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(List<JobParameters> jobParameters) {
        this.jobParameters = jobParameters;
    }

    public Boolean getIsZip() {
        return isZip;
    }

    public void setIsZip(Boolean isZip) {
        this.isZip = isZip;
    }

    public String getEmailRecipients() {
        return emailRecipients;
    }

    public void setEmailRecipients(String emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

    public String getExportFormats() {
        return exportFormats;
    }

    public void setExportFormats(String exportFormats) {
        this.exportFormats = exportFormats;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }
    @JsonIgnore
    public HIResource getHIResource() {
		return hiResource;
	}
    @JsonProperty
	public void setHIResource(HIResource hiResource) {
		this.hiResource = hiResource;
	}

    @Override
    public Long getId() {
        return this.scheduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedules schedules = (Schedules) o;

        if (createdBy != null ? !createdBy.equals(schedules.createdBy) : schedules.createdBy != null) return false;
        if (dateFormat != null ? !dateFormat.equals(schedules.dateFormat) : schedules.dateFormat != null) return false;
        if (daysOfWeek != null ? !daysOfWeek.equals(schedules.daysOfWeek) : schedules.daysOfWeek != null) return false;
        if (emailBody != null ? !emailBody.equals(schedules.emailBody) : schedules.emailBody != null) return false;
        if (emailRecipients != null ? !emailRecipients.equals(schedules.emailRecipients) : schedules.emailRecipients != null)
            return false;
        if (emailSubject != null ? !emailSubject.equals(schedules.emailSubject) : schedules.emailSubject != null)
            return false;
        if (endAfterExecutions != null ? !endAfterExecutions.equals(schedules.endAfterExecutions) : schedules.endAfterExecutions != null)
            return false;
        if (endDate != null ? !endDate.equals(schedules.endDate) : schedules.endDate != null) return false;
        if (endsOn != null ? !endsOn.equals(schedules.endsOn) : schedules.endsOn != null) return false;
        if (exportFormats != null ? !exportFormats.equals(schedules.exportFormats) : schedules.exportFormats != null)
            return false;
        if (frequency != null ? !frequency.equals(schedules.frequency) : schedules.frequency != null) return false;
        if (isActive != null ? !isActive.equals(schedules.isActive) : schedules.isActive != null) return false;
        if (isMigrated != null ? !isMigrated.equals(schedules.isMigrated) : schedules.isMigrated != null) return false;
        if (isZip != null ? !isZip.equals(schedules.isZip) : schedules.isZip != null) return false;
        if (jobParameters != null ? !jobParameters.equals(schedules.jobParameters) : schedules.jobParameters != null)
            return false;
        if (lastExecutionDate != null ? !lastExecutionDate.equals(schedules.lastExecutionDate) : schedules.lastExecutionDate != null)
            return false;
        if (lastExecutionStatus != null ? !lastExecutionStatus.equals(schedules.lastExecutionStatus) : schedules.lastExecutionStatus != null)
            return false;
        if (nextExecutionDate != null ? !nextExecutionDate.equals(schedules.nextExecutionDate) : schedules.nextExecutionDate != null)
            return false;
        if (noOfExecution != null ? !noOfExecution.equals(schedules.noOfExecution) : schedules.noOfExecution != null)
            return false;
        if (repeatBy != null ? !repeatBy.equals(schedules.repeatBy) : schedules.repeatBy != null) return false;
        if (repeatsEvery != null ? !repeatsEvery.equals(schedules.repeatsEvery) : schedules.repeatsEvery != null)
            return false;
            if(hiResource != null? !hiResource.equals(schedules.hiResource): schedules.hiResource!=null) return false;
        if (scheduleId != null ? !scheduleId.equals(schedules.scheduleId) : schedules.scheduleId != null) return false;
        if (scheduleName != null ? !scheduleName.equals(schedules.scheduleName) : schedules.scheduleName != null)
            return false;
        if (scheduleType != null ? !scheduleType.equals(schedules.scheduleType) : schedules.scheduleType != null)
            return false;
        if (scheduledEndTime != null ? !scheduledEndTime.equals(schedules.scheduledEndTime) : schedules.scheduledEndTime != null)
            return false;
        if (scheduledTime != null ? !scheduledTime.equals(schedules.scheduledTime) : schedules.scheduledTime != null)
            return false;
        if (startDate != null ? !startDate.equals(schedules.startDate) : schedules.startDate != null) return false;
        if (timeZone != null ? !timeZone.equals(schedules.timeZone) : schedules.timeZone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = scheduleId != null ? scheduleId.hashCode() : 0;
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (scheduleType != null ? scheduleType.hashCode() : 0);
        result = 31 * result + (hiResource != null ? hiResource.hashCode() : 0);
        result = 31 * result + (scheduleName != null ? scheduleName.hashCode() : 0);
        result = 31 * result + (lastExecutionDate != null ? lastExecutionDate.hashCode() : 0);
        result = 31 * result + (lastExecutionStatus != null ? lastExecutionStatus.hashCode() : 0);
        result = 31 * result + (nextExecutionDate != null ? nextExecutionDate.hashCode() : 0);
        result = 31 * result + (noOfExecution != null ? noOfExecution.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (daysOfWeek != null ? daysOfWeek.hashCode() : 0);
        result = 31 * result + (frequency != null ? frequency.hashCode() : 0);
        result = 31 * result + (repeatBy != null ? repeatBy.hashCode() : 0);
        result = 31 * result + (repeatsEvery != null ? repeatsEvery.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (endsOn != null ? endsOn.hashCode() : 0);
        result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
        result = 31 * result + (endAfterExecutions != null ? endAfterExecutions.hashCode() : 0);
        result = 31 * result + (dateFormat != null ? dateFormat.hashCode() : 0);
        result = 31 * result + (scheduledTime != null ? scheduledTime.hashCode() : 0);
        result = 31 * result + (scheduledEndTime != null ? scheduledEndTime.hashCode() : 0);
        result = 31 * result + (jobParameters != null ? jobParameters.hashCode() : 0);
        result = 31 * result + (isZip != null ? isZip.hashCode() : 0);
        result = 31 * result + (emailRecipients != null ? emailRecipients.hashCode() : 0);
        result = 31 * result + (exportFormats != null ? exportFormats.hashCode() : 0);
        result = 31 * result + (emailSubject != null ? emailSubject.hashCode() : 0);
        result = 31 * result + (emailBody != null ? emailBody.hashCode() : 0);
        result = 31 * result + (isMigrated != null ? isMigrated.hashCode() : 0);
        return result;
    }
}

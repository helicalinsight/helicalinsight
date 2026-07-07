package com.helicalinsight.scheduling.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Rajesh
 *         Created by author on 2/17/2020.
 */
@Entity
@Table(name = "job_parameters")
public class JobParameters implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parameter_id")
    private Long parameterId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedules scheduleIdOfJobParameter;

    @Column(name = "parameter_key")
    private String key;

    @Lob
    @Column(name = "parameter_value",length = 2147483647)
    private String value;

    @Column(name = "parameter_type")
    private String type;

    @Column(name = "is_migrated")
    private Boolean isMigrated;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }
    @JsonIgnore
    public Schedules getScheduleIdOfJobParameter() {
        return scheduleIdOfJobParameter;
    }
    @JsonProperty
    public void setScheduleIdOfJobParameter(Schedules scheduleIdOfJobParameter) {
        this.scheduleIdOfJobParameter = scheduleIdOfJobParameter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }
}

package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceAIModel implements Serializable {

    @Column(name = "report_name", table = "hi_resource_model")
    private String aiModelName;


    @Column(name = "state", table = "hi_resource_model", length = Integer.MAX_VALUE)
    @Lob
    private String state;

    @Column(name = "created_date", table = "hi_resource_model")
    private Date createdDate;

    @Column(name = "last_updated_time", table = "hi_resource_model")
    private Date lastUpdatedTime;

    @Column(name = "created_by", table = "hi_resource_model")
    private Integer createdBy;


    @Column(name = "metadata_id", table = "hi_resource_model")
    private Integer hiResourceMetadata;

    public Integer getHiResourceMetadata() {
        return hiResourceMetadata;
    }

    public String getAiModelName() {
        return aiModelName;
    }

    public void setAiModelName(String reportName) {
        this.aiModelName = reportName;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setHiResourceMetadata(Integer hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
}

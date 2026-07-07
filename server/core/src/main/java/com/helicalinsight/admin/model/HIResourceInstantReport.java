package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceInstantReport implements Serializable {

    @Column(name = "report_name", table = "hi_resource_instant")
    private String reportName;


    @Column(name = "state", table = "hi_resource_instant", length = Integer.MAX_VALUE)
    @Lob
    private String state;

    @Column(name = "created_date", table = "hi_resource_instant")
    private Date createdDate;

    @Column(name = "last_updated_time", table = "hi_resource_instant")
    private Date lastUpdatedTime;

    @Column(name = "created_by", table = "hi_resource_instant")
    private Integer createdBy;


    @Column(name = "agent_id", table = "hi_resource_instant")
    private Integer hiResourceAgent;

    public Integer getHiResourceAgent() {
        return hiResourceAgent;
    }

    public void setHiResourceAgent(Integer hiResourceAgent) {
        this.hiResourceAgent = hiResourceAgent;
    }


    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
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


    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
}

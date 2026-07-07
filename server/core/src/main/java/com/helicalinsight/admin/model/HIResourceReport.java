package com.helicalinsight.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helicalinsight.admin.model.HIResource;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Embeddable
public class HIResourceReport implements Serializable {


    @Column(name = "report_name", table = "hi_resource_report")
    private String reportName;

    @Column(name = "created_by", table = "hi_resource_report")
    private String createdBy;

    @Column(name = "created_date", table = "hi_resource_report")
    private Date createdDate;

    @Column(name = "last_updated_time", table = "hi_resource_report")
    private Date lastUpdatedTime;

    @Column(name = "state", table = "hi_resource_report", length = Integer.MAX_VALUE)
    @Lob
    private String state;

    @Column(name = "canvas_columns", table = "hi_resource_report", length = Integer.MAX_VALUE)
    @Lob
    private String canvasColumns;

    @Column(name = "metadata_id",table="hi_resource_report")
    private Integer hiResourceMetadata;

    @ManyToOne
    @JoinColumn(name = "resource_id", insertable = false, updatable = false)
    private HIResource hiResource;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

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

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCanvasColumns() {
        return canvasColumns;
    }

    public void setCanvasColumns(String canvasColumns) {
        this.canvasColumns = canvasColumns;
    }

    public Integer getHiResourceMetadata() {
        return hiResourceMetadata;
    }

    public void setHiResourceMetadata(Integer hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }

    @JsonIgnore
    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HIResourceReport that = (HIResourceReport) o;
        return Objects.equals(reportName, that.reportName) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(lastUpdatedTime, that.lastUpdatedTime) && Objects.equals(state, that.state) && Objects.equals(canvasColumns, that.canvasColumns) && Objects.equals(hiResourceMetadata, that.hiResourceMetadata) && Objects.equals(hiResource, that.hiResource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportName, createdBy, createdDate, lastUpdatedTime, state, canvasColumns, hiResourceMetadata, hiResource);
    }

    @Override
    public String toString() {
        return "HIResourceReport{" +
                ", reportName='" + reportName + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", state='" + state + '\'' +
                ", canvasColumns='" + canvasColumns + '\'' +
                ", hiResourceMetadata=" + hiResourceMetadata +
                ", hiResource=" + hiResource +
                '}';
    }
}

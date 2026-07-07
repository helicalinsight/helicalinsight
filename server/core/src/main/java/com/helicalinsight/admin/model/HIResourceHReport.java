package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceHReport implements Serializable {

    @Column(name="report_name",table="hi_resource_hreport")
    private String reportName;

    @Column(name="canvas_columns",table="hi_resource_hreport", length = Integer.MAX_VALUE)
    @Lob
    private String canvasColumns;

    @Column(name="state",table="hi_resource_hreport",length = Integer.MAX_VALUE)
    @Lob
    private String state;

    @Column(name="created_date",table="hi_resource_hreport")
    private Date createdDate;

    @Column(name="last_updated_time",table="hi_resource_hreport")
    private Date lastUpdatedTime;

    @Column(name="created_by",table="hi_resource_hreport")
    private Integer createdBy;


    @Column(name = "metadata_resource_id",table="hi_resource_hreport")
    private Integer hiResourceMetadata;

    @Column(name = "cube_id",table="hi_resource_hreport")
    private Integer hiResourceCube;

    public Integer getHiResourceCube() {
        return hiResourceCube;
    }

    public void setHiResourceCube(Integer hiResourceCube) {
        this.hiResourceCube = hiResourceCube;
    }

    public Integer getHiResourceMetadata() {
        return hiResourceMetadata;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }





    public String getCanvasColumns() {
        return canvasColumns;
    }

    public void setCanvasColumns(String canvasColumns) {
        this.canvasColumns = canvasColumns;
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

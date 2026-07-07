package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceEFWSR implements Serializable {

    @Column(name="report_name",table="hi_resource_efwsr")
    private String reportName;

    @Column(name="report_file",table="hi_resource_efwsr")
    private String reportFile;

    @Column(name="report_directory",table="hi_resource_efwsr")
    private String reportDirectory;

    @Column(name="report_parameters",table="hi_resource_efwsr",length = Integer.MAX_VALUE)
    @Lob
    private String reportParameters;

    @Column(name="is_favorite",table="hi_resource_efwsr")
    private Boolean favorite;

    @Column(name="created_by",table="hi_resource_efwsr")
    private Integer createdBy;

    @Column(name="created_date",table="hi_resource_efwsr")
    private Date createdDate;

    @Column(name="last_updated_time",table="hi_resource_efwsr")
    private Date lastUpdatedTime;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(String reportParameters) {
        this.reportParameters = reportParameters;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HIResourceEFWSR that = (HIResourceEFWSR) o;
        return Objects.equals(reportName, that.reportName) && Objects.equals(reportFile, that.reportFile) && Objects.equals(reportDirectory, that.reportDirectory) && Objects.equals(reportParameters, that.reportParameters) && Objects.equals(favorite, that.favorite) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(lastUpdatedTime, that.lastUpdatedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportName, reportFile,reportDirectory, reportParameters, favorite, createdBy, createdDate, lastUpdatedTime);
    }

    @Override
    public String toString() {
        return "HIResourceEFWSR{" +
                "reportName='" + reportName + '\'' +
                ", reportFile='" + reportFile + '\'' +
                ", reportDirectory='" + reportDirectory + '\'' +
                ", reportParameters='" + reportParameters + '\'' +
                ", favorite=" + favorite +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }
}

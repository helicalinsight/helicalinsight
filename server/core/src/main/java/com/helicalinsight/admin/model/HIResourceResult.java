package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceResult implements Serializable {

    @Column(name="report_name",table="hi_resource_result")
    private String reportName;

    @Column(name="report_file",table="hi_resource_result")
    private String reportFile;

    @Column(name="report_dir",table="hi_resource_result")
    private String reportDir;

    @Column(name="report_parameters",table="hi_resource_result", length = Integer.MAX_VALUE)
    @Lob
    private String reportParameters;

    @Column(name="report_type",table="hi_resource_result")
    private String reportType;

    @Column(name="result_name",table="hi_resource_result")
    private String resultName;

    @Column(name="result_directory",table="hi_resource_result")
    private String resultDirectory;

    @Column(name="result_file",table="hi_resource_result")
    private String resultFile;

    @Column(name="run_date",table="hi_resource_result")
    private Date runDate;

    @Column(name="created_date",table="hi_resource_result")
    private Date createdDate;

    @Column(name="last_updated_time",table="hi_resource_result")
    private Date lastUpdatedTime;

    @Column(name="created_by",table="hi_resource_result")
    private Integer createdBy;


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

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(String reportParameters) {
        this.reportParameters = reportParameters;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getResultDirectory() {
        return resultDirectory;
    }

    public void setResultDirectory(String resultDirectory) {
        this.resultDirectory = resultDirectory;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
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

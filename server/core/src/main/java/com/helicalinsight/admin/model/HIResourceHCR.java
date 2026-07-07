package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;


@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceHCR implements Serializable {

    @Column(name="file_name",table="hi_resource_hcr")
    public String fileName;


    @Column(name="created_date",table="hi_resource_hcr")
    public Date createdDate;

    @Column(name="last_updated_time",table="hi_resource_hcr")
    public Date lastUpdatedTime;

    @Column(name="state",table="hi_resource_hcr", length = Integer.MAX_VALUE)
    @Lob
    public String state;

    @Column(name="preview_formdata",table="hi_resource_hcr", length = Integer.MAX_VALUE)
    @Lob
    public String previewFormData;

    @Column(name="diagram",table="hi_resource_hcr", length = Integer.MAX_VALUE)
    @Lob
    public String diagram;

    @Column(name="created_by",table="hi_resource_hcr")
    private Integer createdBy;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getPreviewFormData() {
        return previewFormData;
    }

    public void setPreviewFormData(String previewFormData) {
        this.previewFormData = previewFormData;
    }

    public String getDiagram() {
        return diagram;
    }

    public void setDiagram(String diagram) {
        this.diagram = diagram;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
}

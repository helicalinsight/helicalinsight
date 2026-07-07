package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceEFWCE implements Serializable {

    @Column(name="file_name",table="hi_resource_efwce")
    private String fileName;

    @Column(name="efw_id",table="hi_resource_efwce")
    private String efw;

    @Column(name="description",table="hi_resource_efwce")
    private String description;

    @Column(name="state",table="hi_resource_efwce", length = Integer.MAX_VALUE)
    @Lob
    private String state;

    @Column(name="created_by",table="hi_resource_efwce")
    private Integer created_by;

    @Column(name="created_date",table="hi_resource_efwce")
    private Integer created_date;

    @Column(name="last_updated_time",table="hi_resource_efwce")
    private Date lastUpdatedTime;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getEfw() {
        return efw;
    }

    public void setEfw(String efw) {
        this.efw = efw;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public Integer getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Integer created_date) {
        this.created_date = created_date;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}

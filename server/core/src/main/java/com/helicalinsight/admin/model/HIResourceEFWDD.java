package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceEFWDD implements Serializable {

	@Deprecated
    @Column(name="efw_id",table="hi_resource_efwdd")
    private String efw;

    @Column(name="description",table="hi_resource_efwdd")
    private String description;

    @Column(name="file_name",table="hi_resource_efwdd")
    private String fileName;

    @Column(name="state",table="hi_resource_efwdd", length = Integer.MAX_VALUE)
    @Lob
    private String state;

    @Column(name="created_by",table="hi_resource_efwdd")
    private Integer createdBy;

    @Column(name="created_date",table="hi_resource_efwdd")
    private Date createdDate;

    @Column(name="last_updated_time",table="hi_resource_efwdd")
    private Date lastUpdatedTime;


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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
        HIResourceEFWDD that = (HIResourceEFWDD) o;
        return Objects.equals(efw, that.efw) && Objects.equals(description, that.description) && Objects.equals(fileName, that.fileName) && Objects.equals(state, that.state) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(lastUpdatedTime, that.lastUpdatedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(efw,description, fileName, state, createdBy, createdDate, lastUpdatedTime);
    }

    @Override
    public String toString() {
        return "HIResourceEFWDD{" +
                "efw='" + efw + '\'' +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +
                ", state='" + state + '\'' +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }
}

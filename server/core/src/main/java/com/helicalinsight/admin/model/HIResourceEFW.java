package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceEFW implements Serializable {

    @Column(name = "title", table = "hi_resource_efw")
    private String title;

    @Column(name = "author", table = "hi_resource_efw")
    private String author;

    @Column(name = "description", table = "hi_resource_efw")
    private String description;


    @Column(name = "created_date", table = "hi_resource_efw")
    private Date createdDate;

    @Column(name = "last_updated_time", table = "hi_resource_efw")
    private Date lastUpdatedTime;

    @Column(name = "is_visible", table = "hi_resource_efw")
    private Boolean isVisible;

    @Column(name = "created_by", table = "hi_resource_efw")
    private Integer createdBy;

    @Column(name = "style", table = "hi_resource_efw")
    private String style;

    @Column(name="template",table = "hi_resource_efw", length = Integer.MAX_VALUE)
    @Lob
    private String template;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Column(name="efw_content_id", table = "hi_resource_efw")
    private Integer efwContentId;

    @Column(name="efw_icon",table = "hi_resource_efw" )
    private String icon;

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }



    public Integer getEfwContentId() {
        return efwContentId;
    }

    public void setEfwContentId(Integer efwContentId) {
        this.efwContentId = efwContentId;
    }

    public void setIcon(String icon) {
        this.icon = icon;

    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
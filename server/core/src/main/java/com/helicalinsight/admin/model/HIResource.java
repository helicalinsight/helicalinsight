package com.helicalinsight.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.helicalinsight.efw.utility.ResourceTypeIDMap;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(
        name = "hi_resource_db",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"resource_url"})}

)
@SecondaryTables(
        {
                @SecondaryTable(name = "hi_resource_folder", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_efw", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_efwce", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_efwdd", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_efwsr", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_result", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_hreport", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_instant", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_model", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_hcr", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_images", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id")),
                @SecondaryTable(name = "hi_resource_report", pkJoinColumns = @PrimaryKeyJoinColumn(name = "resource_id"))
        }
)
@FilterDef(name = "isDeletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "isDeletedFilter", condition = "is_deleted = :isDeleted")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonSerialize(as = HIResource.class)
public class HIResource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Integer resourceId;

    public HIResource(HIResourceEFWSR hiResourceEFWSR) {
        this.hiResourceEFWSR = hiResourceEFWSR;
    }

    @Column(name = "created_date")
    private Date created_date;




    @Column(name = "is_migrated")
    private Boolean isMigrated;
    public HIResource(){

    }

    public HIResource(Integer resourceId, Integer parentId,boolean isDeleted) {
    	this(parentId,isDeleted);
    	this.resourceId = resourceId;
    }
    
    public HIResource(Integer parentId,boolean isDeleted) {
    	this.parentId = parentId;
    	this.isDeleted = isDeleted;
    }
    
    public HIResource(Integer resourceId, Date created_date, Boolean isMigrated, Date lastUpdatedTime, Integer parentId, Integer createdBy, Boolean isFolder, String resourcePath, String resourceURL, Long resourceTypeId, String title, Boolean isVisible, Boolean isDeleted) {
        this.resourceId = resourceId;
        this.created_date = created_date;
        this.isMigrated = isMigrated;
        this.lastUpdatedTime = lastUpdatedTime;
        this.parentId = parentId;
        this.createdBy = createdBy;
        this.isFolder = isFolder;
        this.resourcePath = resourcePath;
        this.resourceURL = resourceURL;
        this.resourceTypeId = resourceTypeId;
        this.title = title;
        this.isVisible = isVisible;
        this.isDeleted = isDeleted;
    }

    @Column(name = "last_updated_time")
    private Date lastUpdatedTime;

    //TODO Need to implement SELF JOIN
    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "is_folder")
    private Boolean isFolder;

    @Column(name = "resource_path")
    private String resourcePath;

    @Column(name = "resource_url", unique = true)
    private String resourceURL;


    @Transient
    private ResourceType resourceType;


    @Column(name = "resource_type_id", nullable = false)
    private Long resourceTypeId;

    public Long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    @Embedded
    private HIResourceFolder hiResourceFolder;

    @Embedded
    private HIResourceEFW hiResourceEFW;

    @Embedded
    private HIResourceEFWCE hiResourceEFWCE;


    @Embedded
    private HIResourceEFWDD hiResourceEFWDD;

    @Embedded
    private HIResourceEFWSR hiResourceEFWSR;

    @Embedded
    private HIResourceHCR hiResourceHCR;

    @Embedded
    private HIResourceResult hiResourceResult;

    @Embedded
    private HIResourceHReport hiResourceHReport;

    public HIResourceAIModel getAiModel() {
        return aiModel;
    }

    public void setAiModel(HIResourceAIModel aiModel) {
        this.aiModel = aiModel;
    }

    @Embedded
    private HIResourceInstantReport hiResourceInstantReport;

    @Embedded
    private HIResourceAIModel aiModel;

    @Embedded
    private HIResourceReport hiResourceReport;

    @Embedded
    private HIResourceImages hiResourceImages;

    public HIResourceImages getHiResourceImages() {
        return hiResourceImages;
    }

    public void setHiResourceImages(HIResourceImages hiResourceImages) {
        this.hiResourceImages = hiResourceImages;
    }

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "parentResource", fetch = FetchType.LAZY)
    private List<HIEFWD> hiResourceEfwd = new ArrayList<>();

    //Added Title and ISVisible columns for easy retrival of data

    @Column(name = "title", length = 60)
    private String title;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
    
    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "hiResource", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HIResourceSecurityDB> securityList = new ArrayList<>();

    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Boolean getMigrated() {
        return isMigrated;
    }

    public void setMigrated(Boolean migrated) {
        isMigrated = migrated;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public ResourceType getResourceType() {

        return ResourceTypeIDMap.getResourceTypeById(this.resourceTypeId);
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
        this.resourceTypeId = resourceType.getResourceTypeId();

    }

    public HIResourceFolder getHiResourceFolder() {
        return hiResourceFolder;
    }

    public void setHiResourceFolder(HIResourceFolder hiResourceFolder) {
        this.hiResourceFolder = hiResourceFolder;
    }

    public HIResourceEFW getHiResourceEFW() {
        return hiResourceEFW;
    }

    public void setHiResourceEFW(HIResourceEFW hiResourceEFW) {
        this.hiResourceEFW = hiResourceEFW;
    }

    public HIResourceEFWCE getHiResourceEFWCE() {
        return hiResourceEFWCE;
    }

    public void setHiResourceEFWCE(HIResourceEFWCE hiResourceEFWCE) {
        this.hiResourceEFWCE = hiResourceEFWCE;
    }

    public HIResourceEFWDD getHiResourceEFWDD() {
        return hiResourceEFWDD;
    }

    public void setHiResourceEFWDD(HIResourceEFWDD hiResourceEFWDD) {
        this.hiResourceEFWDD = hiResourceEFWDD;
    }

    public HIResourceEFWSR getHiResourceEFWSR() {
        return hiResourceEFWSR;
    }

    public void setHiResourceEFWSR(HIResourceEFWSR hiResourceEFWSR) {
        this.hiResourceEFWSR = hiResourceEFWSR;
    }

    public HIResourceHCR getHiResourceHCR() {
        return hiResourceHCR;
    }

    public void setHiResourceHCR(HIResourceHCR hiResourceHCR) {
        this.hiResourceHCR = hiResourceHCR;
    }

    public HIResourceResult getHiResourceResult() {
        return hiResourceResult;
    }

    public void setHiResourceResult(HIResourceResult hiResourceResult) {
        this.hiResourceResult = hiResourceResult;
    }

    public HIResourceHReport getHiResourceHReport() {
        return hiResourceHReport;
    }

    public void setHiResourceHReport(HIResourceHReport hiResourceHReport) {
        this.hiResourceHReport = hiResourceHReport;
    }

    public HIResourceInstantReport getHiResourceInstantReport() {
        return hiResourceInstantReport;
    }

    public void setHiResourceInstantReport(HIResourceInstantReport hiResourceInstant) {
        this.hiResourceInstantReport = hiResourceInstant;
    }

    public HIResourceReport getHiResourceReport() {
        return hiResourceReport;
    }

    public void setHiResourceReport(HIResourceReport hiResourceReport) {
        this.hiResourceReport = hiResourceReport;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }


    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @JsonIgnore
    public List<HIResourceSecurityDB> getSecurityList() {
        return securityList;
    }

    @JsonProperty
    public void setSecurityList(List<HIResourceSecurityDB> securityList) {
        this.securityList = securityList;
    }

    @JsonIgnore
    public List<HIEFWD> getHiResourceEfwd() {
        return hiResourceEfwd;
    }

    @JsonProperty
    public void setHiResourceEfwd(List<HIEFWD> hiResourceEfwd) {
        this.hiResourceEfwd = hiResourceEfwd;
    }

}
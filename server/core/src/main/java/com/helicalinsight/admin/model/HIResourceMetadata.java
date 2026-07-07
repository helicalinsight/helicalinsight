package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name = "hi_resource_metadata")
public class HIResourceMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metadata_id")
    private Integer id;

    @Column(name = "type")
    private String type;

    @Column(name = "database_type")
    private String databaseType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "connection_type")
    private String connectionType;

    @Column(name = "is_cached")
    private Boolean isCached;

    public Boolean getCached() {
        return isCached;
    }

    public void setCached(Boolean cached) {
        isCached = cached;
    }

    @Column(name = "last_updated_time")
    private Date lastUpdatedTime;

    @Column(name = "created_by")
    private Integer createdBy;

    @OneToOne(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private HIResource hiResource;

    @OneToMany(mappedBy = "hiResourceMetadata", cascade=CascadeType.REMOVE, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<HIMetadataConnections> hiMetadataConnections = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.REMOVE, mappedBy = "hiResourceMetadata")
    private List<HIMetadataSecurity> metadataSecurityList;

    @OneToMany(cascade = CascadeType.REMOVE , mappedBy = "hiResourceMetadata")
    private List<HIMetadataRelationships> joins;

    @OneToMany(cascade = CascadeType.REMOVE , mappedBy = "hiResourceMetadata")
    private List<HIMetadataCube> hiMetadataCubes;

    @OneToMany(cascade = CascadeType.REMOVE , mappedBy = "hiResourceMetadata")
    private List<HIMetadataView> hiMetadataViews;

    @OneToMany(cascade = CascadeType.REMOVE , mappedBy = "hiResourceMetadata")
    private List<HIMetadataTables> hiMetadataTables;

    @OneToMany(cascade = CascadeType.REMOVE , mappedBy = "hiResourceMetadata")
    private List<MetadataDatabases> metadataDatabases;

    @OneToMany(cascade = CascadeType.REMOVE , mappedBy = "hiResourceMetadata")
    private List<HIMetadataColumns> hiMetadataColumns;
    
    
    @JsonIgnore
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
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

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }
    
    public List<HIMetadataConnections> getHiMetadataConnections() {
        return hiMetadataConnections;
    }
    
    public void setHiMetadataConnections(List<HIMetadataConnections> hiMetadataConnections) {
        this.hiMetadataConnections = hiMetadataConnections;
    }
    
    public List<HIMetadataSecurity> getMetadataSecurityList() {
        return metadataSecurityList;
    }
	@JsonProperty
    public void setMetadataSecurityList(List<HIMetadataSecurity> metadataSecurityList) {
        this.metadataSecurityList = metadataSecurityList;
    }
}

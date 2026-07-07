package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.WhereJoinTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="hi_metadata_views")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataView implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="view_query", length = Integer.MAX_VALUE)
    @Lob
    private String viewQuery;

    @Column(name="view_type")
    private String viewType;

    @ManyToOne
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;
    @Column(name="view_id")
    private String viewId;

    @Column(name="view_name")
    private String viewName;

    @Column(name="view_alias")
    private String viewAlias;

    @Column(name="has_stored_procedure")
    private Boolean hasStoredProcedure;
    
    @JsonIgnore
    public MetadataDatabases getHiMetadataDatabases() {
        return hiMetadataDatabases;
    }
    @JsonProperty
    public void setHiMetadataDatabases(MetadataDatabases hiMetadataDatabases) {
        this.hiMetadataDatabases = hiMetadataDatabases;
    }

    @ManyToOne(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "db_id")
    private MetadataDatabases hiMetadataDatabases;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewAlias() {
        return viewAlias;
    }

    public void setViewAlias(String viewAlias) {
        this.viewAlias = viewAlias;
    }

    public Boolean getHasStoredProcedure() {
        return hasStoredProcedure;
    }

    public void setHasStoredProcedure(Boolean hasStoredProcedure) {
        this.hasStoredProcedure = hasStoredProcedure;
    }

    public String getViewQuery() {
        return viewQuery;
    }

    public void setViewQuery(String viewQuery) {
        this.viewQuery = viewQuery;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
    @JsonIgnore
    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }
    @JsonProperty
    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }
}

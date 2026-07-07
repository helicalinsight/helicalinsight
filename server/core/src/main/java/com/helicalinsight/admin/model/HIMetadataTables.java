package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "hi_metadata_tables")
public class HIMetadataTables implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "table_alias_name")
    private String tableAliasName;

    @Column(name = "is_view")
    private Boolean isView;

    @Column(name = "original_name")
    private String originalName;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "db_id")
    private MetadataDatabases hiMetadataDatabases;
    
    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hiMetadataTables", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HIMetadataColumns> columnsList;

    public Integer getId() {
        return id;
    }

    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableAliasName() {
        return tableAliasName;
    }

    public void setTableAliasName(String tableAliasName) {
        this.tableAliasName = tableAliasName;
    }

    public List<HIMetadataColumns> getColumnsList() {
        return columnsList;
    }

    public void setColumnsList(List<HIMetadataColumns> columnsList) {
        this.columnsList = columnsList;
    }
    @JsonIgnore
    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }
    @JsonProperty
    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }
    @JsonIgnore
    public MetadataDatabases getHiMetadataDatabases() {
        return hiMetadataDatabases;
    }
    @JsonProperty
    public void setHiMetadataDatabases(MetadataDatabases hiMetadataDatabases) {
        this.hiMetadataDatabases = hiMetadataDatabases;
    }

    public Boolean getView() {
        return isView;
    }

    public void setView(Boolean view) {
        isView = view;
    }
}

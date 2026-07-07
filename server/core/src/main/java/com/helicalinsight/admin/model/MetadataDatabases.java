package com.helicalinsight.admin.model;



import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name="hi_metadata_database")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MetadataDatabases implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="db_id")
    private Integer id;

    @Column(name="db_name")
    private String name;

    @Column(name="db_catalog")
    private String catalog;

    @Column(name="db_schema")
    private String schema;
   
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "hiMetadataDatabases",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<HIMetadataTables> metadataTablesList;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "hiMetadataDatabases",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<HIMetadataView> metadataViewList;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "hiMetadataDatabases",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<HIMetadataRelationships> metadataRelationShipList;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connection_id")
    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private HIMetadataConnections metadataConnections; 

    @JsonIgnore
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    @JsonIgnore
    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }
    @JsonProperty
    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }

    public List<HIMetadataTables> getMetadataTablesList() {
        return metadataTablesList;
    }

    public void setMetadataTablesList(List<HIMetadataTables> metadataTablesList) {
        this.metadataTablesList = metadataTablesList;
    }

    public List<HIMetadataView> getMetadataViewList() {
        return metadataViewList;
    }

    public void setMetadataViewList(List<HIMetadataView> metadataViewList) {
        this.metadataViewList = metadataViewList;
    }

    public List<HIMetadataRelationships> getMetadataRelationShipList() {
        return metadataRelationShipList;
    }

    public void setMetadataRelationShipList(List<HIMetadataRelationships> metadataRelationShipList) {
        this.metadataRelationShipList = metadataRelationShipList;
    }
    @JsonIgnore
	public HIMetadataConnections getMetadataConnections() {
		return metadataConnections;
	}
    @JsonProperty
	public void setMetadataConnections(HIMetadataConnections metadataConnections) {
		this.metadataConnections = metadataConnections;
	}
	
    
    
}

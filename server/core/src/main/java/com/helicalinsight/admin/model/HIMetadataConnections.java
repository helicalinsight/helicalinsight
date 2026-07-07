package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hi_metadata_connections")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataConnections implements Serializable {
	
	//TODO : Remove eager loading for better performance.

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;
    public HIMetadataConnections() {
       
    }
    public HIMetadataConnections(Integer id, String connectionType) {
        this.id = id;
        this.connectionType = connectionType;
    }

    @Column(name = "connection_type")
    private String connectionType;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "hiMetadataConnections", cascade=CascadeType.REMOVE, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<HIMetadataConnectionGlobal> metadataGlobalConnList = new ArrayList<>();
    
    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "hiMetadataConnections",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER , orphanRemoval = true)
    private List<HIMetadataConnectionEFWD> metadataConnectionEfwd;
    
    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "metadataConnections",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER , orphanRemoval = true)
    private List<MetadataDatabases> metadataDatabases = new ArrayList<>();
    
    public List<HIMetadataConnectionGlobal> getMetadataGlobalConnList() {
        return metadataGlobalConnList;
    }
    @JsonProperty
    public void setMetadataGlobalConnList(List<HIMetadataConnectionGlobal> metadataGlobalConnList) {
        this.metadataGlobalConnList = metadataGlobalConnList;
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
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
	public List<HIMetadataConnectionEFWD> getMetadataConnectionEfwd() {
		return metadataConnectionEfwd;
	}
	public void setMetadataConnectionEfwd(List<HIMetadataConnectionEFWD> metadataConnectionEfwd) {
		this.metadataConnectionEfwd = metadataConnectionEfwd;
	}
	public List<MetadataDatabases> getMetadataDatabases() {
		return metadataDatabases;
	}
	public void setMetadataDatabases(List<MetadataDatabases> metadataDatabases) {
		this.metadataDatabases = metadataDatabases;
	}
	
	
}

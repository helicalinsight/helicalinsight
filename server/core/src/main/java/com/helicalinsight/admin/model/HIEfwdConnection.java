package com.helicalinsight.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "hi_efwd_connection", indexes = {
		@Index(name = "idx_efwd_conn_efwd_id", columnList = "efwd_id"),
		@Index(name = "idx_efwd_conn_connection_id", columnList = "connection_id"),
		@Index(name = "idx_efwd_conn_type_deleted", columnList = "type, is_deleted")
})
@BatchSize(size = 20)
@DynamicUpdate
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Filter(name = "isDeletedFilter", condition = "is_deleted = :isDeleted")
public class HIEfwdConnection implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "type")
    private String type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "efwd_id", referencedColumnName = "id")
    private HIEFWD hiResourceEFWD;
    
    @Column(name = "connection_id")
    private String connectionId;
    
    @Column(name = "is_deleted")
    private boolean deleted;
    
    @OneToMany(mappedBy = "hiEfwdConnection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 20)
	@JsonBackReference(value = "efwdConnSqlJDBC")
    private List<EFWDConnSqlJDBC> efwdConnSqlJDBC = new ArrayList<>();

    @OneToMany(mappedBy = "hiEfwdConnection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 20)
	@JsonBackReference(value = "efwdConnGroovy")
    private List<EFWDConnGroovy> efwdConnGroovy = new ArrayList<>();

    @OneToMany(mappedBy = "hiEfwdConnection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 20)
	@JsonBackReference(value = "dataMapList")
    private List<HIEfwdDataMap> dataMapList = new ArrayList<>();
    
    @OneToMany(mappedBy = "hiEfwdConnection", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 20)
    private List<HIEfwdConnSecurity> securityList = new ArrayList<>();
      
	@OneToMany(mappedBy = "hiEfwdConnection", fetch = FetchType.LAZY)
	private List<HIMetadataConnectionEFWD> metadataConnections = new ArrayList<>();
    
	@OneToMany(mappedBy = "hiEfwdConnection", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@BatchSize(size = 20)
	private List<HIHcrConnectionsEfwd> hiHcrConnectionsEfwds = new ArrayList<>();
    
    public List<EFWDConnGroovy> getEfwdConnGroovy() {
        return efwdConnGroovy;
    }

    public void setEfwdConnGroovy(List<EFWDConnGroovy> efwdConnGroovy) {
        this.efwdConnGroovy = efwdConnGroovy;
    }

    public List<EFWDConnSqlJDBC> getEfwdConnSqlJDBC() {
        return efwdConnSqlJDBC;
    }

    public void setEfwdConnSqlJDBC(List<EFWDConnSqlJDBC> efwdConnSqlJDBC) {
        this.efwdConnSqlJDBC = efwdConnSqlJDBC;
    }

    public HIEFWD getHiResourceEFWD() {
        return hiResourceEFWD;
    }

    public void setHiResourceEFWD(HIEFWD hiResourceEFWD) {
        this.hiResourceEFWD = hiResourceEFWD;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	
	@JsonIgnore
	public List<HIMetadataConnectionEFWD> getMetadataConnections() {
		return metadataConnections;
	}
	@JsonProperty
	public void setMetadataConnections(List<HIMetadataConnectionEFWD> metadataConnections) {
		this.metadataConnections = metadataConnections;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof HIEfwdConnection other)) {
			return false;
		}
		return id != null && id.equals(other.id);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
    public String toString() {
        return "HIEfwdConnection [id=" + id + ", type=" + type + ", hiResourceEFWD=" + hiResourceEFWD + "]";
    }
}

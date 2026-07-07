package com.helicalinsight.admin.model;

import java.io.Serializable;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="hi_metadata_connection_efwd")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataConnectionEFWD implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "efwd_connection_id")
    private HIEfwdConnection hiEfwdConnection;
    
    @Column(name="dialect")
    private String dialect;

    @Column(name="driver_class")
    private String driverClass;

    @Column(name="driver_class_reference")
    private String driverClassReference;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id")
    private HIMetadataConnections hiMetadataConnections;

    
    @JsonIgnore
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }
    
    public HIEfwdConnection getHiEfwdConnection() {
        return hiEfwdConnection;
    }
    public void setHiEfwdConnection(HIEfwdConnection hiEfwdConnection) {
        this.hiEfwdConnection = hiEfwdConnection;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverClassReference() {
        return driverClassReference;
    }

    public void setDriverClassReference(String driverClassReference) {
        this.driverClassReference = driverClassReference;
    }
    @JsonIgnore
	public HIMetadataConnections getHiMetadataConnections() {
		return hiMetadataConnections;
	}
    @JsonProperty
	public void setHiMetadataConnections(HIMetadataConnections hiMetadataConnections) {
		this.hiMetadataConnections = hiMetadataConnections;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dialect == null) ? 0 : dialect.hashCode());
		result = prime * result + ((driverClass == null) ? 0 : driverClass.hashCode());
		result = prime * result + ((driverClassReference == null) ? 0 : driverClassReference.hashCode());
		result = prime * result + ((hiEfwdConnection == null) ? 0 : hiEfwdConnection.hashCode());
		result = prime * result + ((hiMetadataConnections == null) ? 0 : hiMetadataConnections.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HIMetadataConnectionEFWD other = (HIMetadataConnectionEFWD) obj;
		if (dialect == null) {
			if (other.dialect != null)
				return false;
		} else if (!dialect.equals(other.dialect))
			return false;
		if (driverClass == null) {
			if (other.driverClass != null)
				return false;
		} else if (!driverClass.equals(other.driverClass))
			return false;
		if (driverClassReference == null) {
			if (other.driverClassReference != null)
				return false;
		} else if (!driverClassReference.equals(other.driverClassReference))
			return false;
		if (hiEfwdConnection == null) {
			if (other.hiEfwdConnection != null)
				return false;
		} else if (!hiEfwdConnection.equals(other.hiEfwdConnection))
			return false;
		if (hiMetadataConnections == null) {
			if (other.hiMetadataConnections != null)
				return false;
		} else if (!hiMetadataConnections.equals(other.hiMetadataConnections))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

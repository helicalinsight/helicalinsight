package com.helicalinsight.admin.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hi_resource_efwd")
public class HIEFWD implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "hiResourceEFWD",fetch = FetchType.LAZY)
    private List<HIEfwdConnection> hiEfwdConnection = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_resource_id")
    private HIResource parentResource;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="created_by")
    private Integer createdBy;

    @Column(name="last_updated_time")
    private Date lastUpdatedTime;
    @JsonIgnore
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }
    @JsonIgnore
    public List<HIEfwdConnection> getHiEfwdConnection() {
        return hiEfwdConnection;
    }
    @JsonProperty
    public void setHiEfwdConnection(List<HIEfwdConnection> hiEfwdConnection) {
        this.hiEfwdConnection = hiEfwdConnection;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public HIResource getParentResource() {
        return parentResource;
    }

    public void setParentResource(HIResource parentResource) {
        this.parentResource = parentResource;
    }
}

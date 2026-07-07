package com.helicalinsight.datasource.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;




import jakarta.persistence.*;
import reactor.support.Identifiable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Helical on 5/17/2021.
 */

@Entity
@Table(name="ds_global_connections")
@Cacheable(true)
@Filter(name = "isDeletedFilter", condition = "is_deleted = :isDeleted")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GlobalConnections implements Identifiable<Integer>,Serializable {

    @Id
    @GenericGenerator(
            name = "CustomDataSourceIdGenerator",
            strategy = "com.helicalinsight.scheduling.model.CustomDataSourceIdGenerator",
            parameters = {@org.hibernate.annotations.Parameter(
                    name = "sequence_name",
                    value = "post_sequence_ds"),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value", value = "1000")})
    @GeneratedValue(
            generator = "CustomDataSourceIdGenerator",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name="global_id")
    private Integer globalId;

    @Column(name="name")
    private String name;

    @Column(name="type")
    private String type;


    @Column(name="vendor_name")
    private String vendor;
    @Column(name="base_type")
    private String baseType;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="last_updated_time")
    private Date lastUpdatedTime;

    @Column(name="ds_type")
    private String dsType;

    @Column(name = "is_migrated")
    private Boolean isMigrated;
    @JsonIgnore
    public Integer getGlobalId() {
        return globalId;
    }
    @JsonProperty
    public void setGlobalId(Integer globalId) {
        this.globalId = globalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }
    
    @Column(name="is_deleted")
    private Boolean isDeleted;
/*
    @Override
    public Identifiable<Integer> setId(Integer integer) {
       // this.globalId
        return null;
    }*/
    
    //TODO : Remove identifier
    @Deprecated(forRemoval = true)
    @Override
    public Identifiable<Integer> setId(Integer aLong) {
       this.setGlobalId(aLong);
        return null;
    }
    public Boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	@Override
    public Integer getId() {
        return this.globalId;
    }
}
package com.helicalinsight.datasource.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.helicalinsight.admin.dto.DsTypeDTO;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ds_type_jndi")
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DSTypeJndi implements Serializable, DsTypeDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "global_id")
    private GlobalConnections globalConnections;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "data_source_provider")
    private String dataSourceProvider;

    @Column(name = "database_dialect")
    private String databaseDialect;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name= "driver_class_name")
    private String driverClassName;

    @Column(name= "lookup_name")
    private String lookUpName;

    @Column(name = "last_update_time")
    private Date lastUpdatedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GlobalConnections getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(GlobalConnections globalConnections) {
        this.globalConnections = globalConnections;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public String getDatabaseDialect() {
        return databaseDialect;
    }

    public void setDatabaseDialect(String databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getLookUpName() {
        return lookUpName;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    public Integer globalConnections() {
        return this.globalConnections.getGlobalId();
    }
}


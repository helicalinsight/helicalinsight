package com.helicalinsight.admin.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helicalinsight.datasource.model.GlobalConnections;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="hi_metadata_connection_global")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataConnectionGlobal implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "global_connection_id")
    private GlobalConnections globalConnections;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connection_id")
    private HIMetadataConnections hiMetadataConnections;

    @Column(name="dialect")
    private String dialect;

    @Column(name="driver_class")
    private String driverClass;

    @Column(name="driver_class_reference")
    private String driverClassReference;
    
    @JsonIgnore
    public HIMetadataConnections getHiMetadataConnections() {
        return hiMetadataConnections;
    }
    @JsonProperty
    public void setHiMetadataConnections(HIMetadataConnections hiMetadataConnections) {
        this.hiMetadataConnections = hiMetadataConnections;
    }
    @JsonIgnore
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public GlobalConnections getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(GlobalConnections globalConnections) {
        this.globalConnections = globalConnections;
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


}
package com.helicalinsight.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.helicalinsight.datasource.model.GlobalConnections;

import jakarta.persistence.*;

import java.io.Serializable;

//
//@Entity
//@Table(name="hi_efwd_connection_managed")
//@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class EFWDConnManaged implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "efwd_connection_id",referencedColumnName = "id")
    private HIEfwdConnection efwdConnection;

    @ManyToOne
    @JoinColumn(name="global_id")
    private GlobalConnections globalConnections;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HIEfwdConnection getEfwdConnection() {
        return efwdConnection;
    }

    public void setEfwdConnection(HIEfwdConnection efwdConnection) {
        this.efwdConnection = efwdConnection;
    }

    public GlobalConnections getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(GlobalConnections globalConnections) {
        this.globalConnections = globalConnections;
    }
}

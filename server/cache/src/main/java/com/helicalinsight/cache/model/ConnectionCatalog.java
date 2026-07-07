package com.helicalinsight.cache.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by helical019 on 12/31/2018.
 */
@Entity
@Table(name = "connection_catalog")
public class ConnectionCatalog implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany( mappedBy = "connectionCatalog", cascade = CascadeType.REMOVE)
    private List<ConnectionSchema> connectionSchema;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connection_id", nullable = false, insertable = true, updatable = false)
    private ConnectionDetails connectionDetails;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catalog_id")
    private Long catalogId;


    @Column(name = "catalog_name")
    private String catalog;


    public List<ConnectionSchema> getConnectionSchema() {
        return connectionSchema;
    }

    public void setConnectionSchema(List<ConnectionSchema> connectionSchema) {
        this.connectionSchema = connectionSchema;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public void setConnectionDetails(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}

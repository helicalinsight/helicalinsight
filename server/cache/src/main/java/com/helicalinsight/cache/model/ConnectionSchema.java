package com.helicalinsight.cache.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Rajesh on 12/31/2018.
 */
@Entity
@Table(name = "connection_schema")
public class ConnectionSchema implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "connectionSchema", cascade = CascadeType.REMOVE)
    private List<ConnectionTable> connectionTable;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "catalog_id", nullable = false, insertable = true, updatable = false)
    private ConnectionCatalog connectionCatalog;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schema_id")
    private Long schemaId;


    @Column(name = "schema_name")
    private String schema;


    public List<ConnectionTable> getConnectionTable() {
        return connectionTable;
    }

    public void setConnectionTable(List<ConnectionTable> connectionTable) {
        this.connectionTable = connectionTable;
    }

    public ConnectionCatalog getConnectionCatalog() {
        return connectionCatalog;
    }

    public void setConnectionCatalog(ConnectionCatalog connectionCatalog) {
        this.connectionCatalog = connectionCatalog;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Long schemaId) {
        this.schemaId = schemaId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}

package com.helicalinsight.cache.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by helical019 on 12/31/2018.
 */
@Entity
@Table(name = "connection_table")
public class ConnectionTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "connectionTable", cascade = CascadeType.REMOVE)
    private List<ConnectionColumn> connectionColumn;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schema_id", nullable = false, insertable = true, updatable = false)
    private ConnectionSchema connectionSchema;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "table_namae")
    private String tables;


    public List<ConnectionColumn> getConnectionColumn() {
        return connectionColumn;
    }

    public void setConnectionColumn(List<ConnectionColumn> connectionColumn) {
        this.connectionColumn = connectionColumn;
    }

    public ConnectionSchema getConnectionSchema() {
        return connectionSchema;
    }

    public void setConnectionSchema(ConnectionSchema connectionSchema) {
        this.connectionSchema = connectionSchema;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }
}

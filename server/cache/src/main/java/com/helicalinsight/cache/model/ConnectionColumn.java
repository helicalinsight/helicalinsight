package com.helicalinsight.cache.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Created by helical019 on 12/31/2018.
 */
@Entity
@Table(name = "connection_column")
public class ConnectionColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    private Long columnId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id", nullable = false, insertable = true, updatable = false)
    private ConnectionTable connectionTable;


    @Column(name = "column_name")
    private String columns;

    @Column(name = "type")
    private String type;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "column_size")
    private String size;

    @Column(name = "nullable")
    private String nullable;

    @Column(name = "column_position")
    private String position;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public ConnectionTable getConnectionTable() {
        return connectionTable;
    }

    public void setConnectionTable(ConnectionTable connectionTable) {
        this.connectionTable = connectionTable;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

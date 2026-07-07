package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Somen
 */
@Entity

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "hi_cube_dimension")
public class HICubeDimension  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column
    private String type;

    @Column
    private Boolean visible;

    @Column
    private String name;

    @Column(name = "dim_id")
    private String dimId;

    @Column(name = "dim_table")
    private String table;

    @Column(name = "table_id")
    private String tableId;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "column_id")
    private String columnId;


    @Column(name = "dim_column")
    private String column;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
    @JsonIgnore
    public HIMetadataCube getHiMetadataCube() {
        return hiMetadataCube;
    }
    @JsonProperty
    public void setHiMetadataCube(HIMetadataCube hiMetadataCube) {
        this.hiMetadataCube = hiMetadataCube;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hiCubeDimension", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HIDimensionHierarchy> hierarchies;


    @ManyToOne(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "cube_id")
    private HIMetadataCube hiMetadataCube;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDimId() {
        return dimId;
    }

    public void setDimId(String dimId) {
        this.dimId = dimId;
    }

    public List<HIDimensionHierarchy> getHierarchies() {
        return hierarchies;
    }

    public void setHierarchies(List<HIDimensionHierarchy> hierarchies) {
        this.hierarchies = hierarchies;
    }
    @JsonIgnore
    public HIMetadataCube getMetadataCube() {
        return hiMetadataCube;
    }
    @JsonProperty
    public void setMetadataCube(HIMetadataCube metadataCube) {
        this.hiMetadataCube = metadataCube;
    }
}

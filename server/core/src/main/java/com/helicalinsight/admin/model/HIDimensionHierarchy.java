package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by somen
 */
@Entity

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "hi_cube_dimension_hierarchy")
public class HIDimensionHierarchy  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "hierarchy_id")
    private String hierarchyId;


    @Column
    private String caption;

    public String getColumn() {
        return column;
    }

    @Column(name="hierarchy_column")
    private String column;

    public String getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(String hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getHierarchyTable() {
        return hierarchyTable;
    }

    public void setHierarchyTable(String hierarchyTable) {
        this.hierarchyTable = hierarchyTable;
    }

    @Column
    private String hierarchyType;

    @Column
    private String columnName;



    @Column
    private String hierarchyTable;

    @Column
    private Boolean visible;

    @Column(name="primary_key_column_id")
    private String primaryKeyColumnId;

    @Column
    private String tableId;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hiDimensionHierarchy", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HICubeHierarchyLevel> levels;

    @ManyToOne(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "dimension_id")
    private HICubeDimension hiCubeDimension;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getPrimaryKeyColumnId() {
        return primaryKeyColumnId;
    }

    public void setPrimaryKeyColumnId(String primaryKeyColumnId) {
        this.primaryKeyColumnId = primaryKeyColumnId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public List<HICubeHierarchyLevel> getLevels() {
        return levels;
    }
    
    public void setLevels(List<HICubeHierarchyLevel> levels) {
        this.levels = levels;
    }

    @JsonIgnore
    public HICubeDimension getHiCubeDimension() {
        return hiCubeDimension;
    }
    
    @JsonProperty
    public void setHiCubeDimension(HICubeDimension hiCubeDimension) {
        this.hiCubeDimension = hiCubeDimension;
    }

    public void setColumn(String column) {
        this.column=column;
    }
}

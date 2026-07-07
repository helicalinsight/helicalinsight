package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Created by somen
 */
@Entity

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "hi_cube_hierarcy_level")
public class HICubeHierarchyLevel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name="apprx_row_count")
    private Integer approximateRowCount;

    @Column
    private String name;

    @Column(name="level_id")
    private String levelId;


    @Column
    private Boolean visible;

    @Column(name = "column_id")
    private String columnId;


    @Column
    private String caption;

    @Column
    private String description;


    @Column(name="data_type")
    private String dataType;


    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getSemanticType() {
        return semanticType;
    }

    public void setSemanticType(String semanticType) {
        this.semanticType = semanticType;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    @Column(name = "nested_member_formatter")
    private String nestedMemberFormatter;

    @Column(name="level_type")
    private String levelType;

    public String getColumn() {
        return column;
    }

    @Column(name="level_column")
    private String column;

    @Column(name="synonyms")
    private String synonyms;

    @Column(name="semantic_type")
    private String semanticType;

    @Column(name="topics")
    private String topics;

    @Column(name="formula")
    private String formula;

    @Column(name="filter")
    private String filter;

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Column(name="example")
    private String example;





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

    @Column(name="hie_col_name")
    private String columnName;
    @Column(name="hie_table")
    private String hierarchyTable;


    @Column(name = "hide_member_if_expression")
    private String hideMemberIfExpression;


    @Column(name = "table_id")
    private String tableId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApproximateRowCount() {
        return approximateRowCount;
    }

    public void setApproximateRowCount(Integer approximateRowCount) {
        this.approximateRowCount = approximateRowCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getNestedMemberFormatter() {
        return nestedMemberFormatter;
    }

    public void setNestedMemberFormatter(String nestedMemberFormatter) {
        this.nestedMemberFormatter = nestedMemberFormatter;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public String getHideMemberIfExpression() {
        return hideMemberIfExpression;
    }

    public void setHideMemberIfExpression(String hideMemberIfExpression) {
        this.hideMemberIfExpression = hideMemberIfExpression;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @ManyToOne(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "dim_hierarch_id")
    private HIDimensionHierarchy hiDimensionHierarchy;
    
    @JsonIgnore
    public HIDimensionHierarchy getHiDimensionHierarchy() {
        return hiDimensionHierarchy;
    }
    @JsonProperty
    public void setHiDimensionHierarchy(HIDimensionHierarchy hiDimensionHierarchy) {
        this.hiDimensionHierarchy = hiDimensionHierarchy;
    }

    public void setColumn(String column) {
        this.column=column;
    }
}

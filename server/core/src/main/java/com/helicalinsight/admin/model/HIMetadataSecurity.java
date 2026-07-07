package com.helicalinsight.admin.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="hi_metadata_security")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataSecurity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="expression_id")
    private String expressionId;

    @Column(name="type")
    private String type;

    public void setExpressionOn(String expressionOn) {
        this.expressionOn = expressionOn;
    }

    @Column(name="expression_on")
    private String expressionOn;

    public void setType(String type) {
        this.type = type;
    }

    public String getExpressionName() {
        return expressionName;
    }

    public void setExpressionName(String expressionName) {
        this.expressionName = expressionName;
    }

    @Column(name="expression_name")
    private String expressionName;

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String getExpressionCondition() {
        return expressionCondition;
    }

    public void setExpressionCondition(String expressionCondition) {
        this.expressionCondition = expressionCondition;
    }

    public String getExpressionFilter() {
        return expressionFilter;
    }

    public void setExpressionFilter(String expressionFilter) {
        this.expressionFilter = expressionFilter;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @Column(name="expression_type")
    private String expressionType;

    @Column(name="expression_condition", length = Integer.MAX_VALUE)
    @Lob
    private String expressionCondition;

    @Column(name="expression_filter", length = Integer.MAX_VALUE)
    @Lob
    private String expressionFilter;

    @Column(name="access_type")
    private String accessType;


    @ManyToOne
    @JoinColumn(name = "metadata_id",nullable = false, insertable = true, updatable = false)
    private HIResourceMetadata hiResourceMetadata;

    @JsonIgnore
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public String getExpressionId() {
        return expressionId;
    }

    public void setExpressionId(String expressionId) {
        this.expressionId = expressionId;
    }

    @JsonIgnore
    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }
    @JsonProperty
    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }

    public String getExpressionOn() {

        return this.expressionOn;
    }

    public String getType() {

        return this.type;
    }
}

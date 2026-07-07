package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="hi_metadata_columns")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataColumns implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="column_name")
    private String columnName;

    @Column(name="original_name")
    private String originalName;

    @Column(name="column_alias_name")
    private String columnAliasName;

    @Column(name="column_type")
    private String column_type;

    @Column(name="default_function")
    private String defaultFunction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id",referencedColumnName = "id")
    private HIMetadataTables hiMetadataTables;
    
    @OneToMany(mappedBy = "leftMetadataColumns",cascade = CascadeType.REMOVE)
    private List<HIMetadataRelationships> leftColumnRelationships;
    @OneToMany(mappedBy = "rightMetadataColumns",cascade = CascadeType.REMOVE)
    private List<HIMetadataRelationships> rightColumnRelationships;
    
    public Integer getId() {
        return id;
    }
    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnAliasName() {
        return columnAliasName;
    }

    public void setColumnAliasName(String columnAliasName) {
        this.columnAliasName = columnAliasName;
    }

    public String getColumn_type() {
        return column_type;
    }

    public void setColumn_type(String column_type) {
        this.column_type = column_type;
    }

    public String getDefaultFunction() {
        return defaultFunction;
    }

    public void setDefaultFunction(String defaultFunction) {
        this.defaultFunction = defaultFunction;
    }
    @JsonIgnore
    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }
    @JsonProperty
    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }
    @JsonIgnore
    public HIMetadataTables getHiMetadataTables() {
        return hiMetadataTables;
    }
    @JsonProperty
    public void setHiMetadataTables(HIMetadataTables hiMetadataTables) {
        this.hiMetadataTables = hiMetadataTables;
    }

}

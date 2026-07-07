package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;


@Entity
@Table(name="hi_metadata_relationships")
@SecondaryTables(
        {
              @SecondaryTable(name = "hi_relationship_positions", pkJoinColumns = @PrimaryKeyJoinColumn(name = "relationship_id")),
        })

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIMetadataRelationships implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="relationship_id")
    private Integer id;

    @Column(name="join_type")
    private String joinType;

    @Column(name="join_operator")
    private String operator;
    
    @Column(name = "is_external")
    private Boolean external;

    @ManyToOne
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;

    @ManyToOne
    @JoinColumn(name="left_column_id",referencedColumnName="id")
    private HIMetadataColumns leftMetadataColumns;

    @ManyToOne
    @JoinColumn(name="right_column_id",referencedColumnName = "id")
    private HIMetadataColumns rightMetadataColumns;

    @ManyToOne(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "db_id")
    private MetadataDatabases hiMetadataDatabases;
    
    @Embedded
    private HIRelationshipPositions joinsPositions;
    
    
    @JsonIgnore
    public MetadataDatabases getHiMetadataDatabases() {
        return hiMetadataDatabases;
    }
    @JsonProperty
    public void setHiMetadataDatabases(MetadataDatabases hiMetadataDatabases) {
        this.hiMetadataDatabases = hiMetadataDatabases;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }
    @JsonIgnore
    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }
    @JsonProperty
    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public HIMetadataColumns getLeftMetadataColumns() {
        return leftMetadataColumns;
    }

    public void setLeftMetadataColumns(HIMetadataColumns leftMetadataColumns) {
        this.leftMetadataColumns = leftMetadataColumns;
    }

    public HIMetadataColumns getRightMetadataColumns() {
        return rightMetadataColumns;
    }

    public void setRightMetadataColumns(HIMetadataColumns rightMetadataColumns) {
        this.rightMetadataColumns = rightMetadataColumns;
    }

	public Boolean getExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}
	
	public HIRelationshipPositions getJoinsPositions() {
		return joinsPositions;
	}
	public void setJoinsPositions(HIRelationshipPositions joinsPositions) {
		this.joinsPositions = joinsPositions;
	}
	
	
}

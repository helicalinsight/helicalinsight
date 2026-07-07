package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Data
@Embeddable

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIRelationshipPositions implements Serializable {
	
	@Column(name = "metadata_id",table = "hi_relationship_positions")
	private Integer metadataId;
	@Column(name = "position",table = "hi_relationship_positions")
	private Integer position;
	
}

package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataRelationshipsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String joinType;
	private String operator;
	private boolean external;
	private Integer metadataId;
	private HIMetadataColumnsDTO leftMetadataColumns;
	private HIMetadataColumnsDTO rightMetadataColumns;
	private Integer position;
	
}

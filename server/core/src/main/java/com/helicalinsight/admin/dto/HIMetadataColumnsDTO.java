package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataColumnsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String columnName;
	private String originalName;
	private String columnAliasName;
	private String column_type;
	private String defaultFunction;
	private Integer metadataId;
	private Integer tableId;

}

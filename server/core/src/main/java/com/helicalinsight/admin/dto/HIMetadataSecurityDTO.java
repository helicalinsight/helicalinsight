package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataSecurityDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String expressionId;
	private String type;
	private String expressionOn;
	private String expressionName;
	private String expressionType;
	private String expressionCondition;
	private String expressionFilter;
	private String accessType;
	private Integer metadataId;
}

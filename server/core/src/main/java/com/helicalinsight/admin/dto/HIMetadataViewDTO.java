package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataViewDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private String viewQuery;
    private String viewType;
    private Integer metadataId;
    private String viewId;
    private String viewName;
    private String viewAlias;
    private Boolean hasStoredProcedure;

}


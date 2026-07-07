package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataConnectionDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer metadataId;
	private String connectionType;
	private List<HIMetadataConnectionGlobalDTO> metadataGlobalConnList =  new ArrayList<>();
	private List<HIMetadataConnectionEFWDDTO> metadataConnectionEfwd = new ArrayList<>();
	private List<MetadataDatabasesDTO> metadataDatabases;
}

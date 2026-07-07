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
public class MetadataDatabasesDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String catalog;
	private String schema;
	private Integer metadataId;
	private List<HIMetadataTableDTO> metadataTablesList = new ArrayList<>();
	private List<HIMetadataViewDTO> metadataViewList  = new ArrayList<>();
	private List<HIMetadataRelationshipsDTO> metadataRelationShipList = new ArrayList<>();
	
}

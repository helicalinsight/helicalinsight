package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataTableDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;
    private String tableName;
    private String tableAliasName;
    private Boolean view;
    private String originalName;
    private Integer metadataId;
    private Integer databaseId;
    private List<HIMetadataColumnsDTO> columnsList = new ArrayList<>();

}

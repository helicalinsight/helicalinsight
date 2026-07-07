package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HICubeDimensionDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String type;
	private Boolean visible;
	private String name;
	private String dimId;
	private String table;
	private String tableId;
	private String columnName;
	private String columnId;
	private String column;
	private Integer cubeId;

}

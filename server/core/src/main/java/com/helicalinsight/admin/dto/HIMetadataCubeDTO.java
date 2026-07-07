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
public class HIMetadataCubeDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private Boolean visible;
	private String cubeId;
	private String caption;
	private String defaultMeasure;
	private String description;
	private Boolean cache;
	private Boolean enabled;
	private Integer metadataId;
	private List<HICubeDimensionDTO> dimensions = new ArrayList<>();
	private List<HICubeMeasureDTO> measures = new ArrayList<>();
	
}

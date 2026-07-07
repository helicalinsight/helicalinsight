package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataConnectionGlobalDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private GlobalConnectionDTO globalConnections;
	private HIMetadataConnectionDTO hiMetadataConnections;
	private String dialect;
	private String driverClass;
	private String driverClassReference;
	
}

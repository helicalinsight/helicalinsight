package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HICubeMeasureDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private String name;
    private String columnId;
    private String column;
    private String tableId;
    private String formatString;
    private String formatter;
    private String dataType;
    private String caption;
    private String description;
    private String measureType;
    private String measureTable;
    private String columnName;
    private String measureId;
    private String aggregator;
    private Boolean visible;
    private Integer cubeId;
}

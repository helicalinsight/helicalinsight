package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIMetadataConnectionEFWDDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private HIEfwdConnectionDTO hiEfwdConnection;
	private String dialect;
	private String driverClass;
	private String driverClassReference;
	private Integer connectionId;
}

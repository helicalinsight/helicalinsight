package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EfwdConnDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String type;
	private String createdBy;
	private List<PlainConnDTO> plainConnections = new ArrayList<>();
	private HIEfwdDTO resource;
	private boolean deleted;
	private String name;
}

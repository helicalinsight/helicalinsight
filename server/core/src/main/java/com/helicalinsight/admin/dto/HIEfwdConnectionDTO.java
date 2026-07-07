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
public class HIEfwdConnectionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String type;
	private Integer efwdId;
	private String connectionId;
	private boolean deleted;
	private HIEfwdDTO hiResourceEFWD;
	private List<EFWDConnSqlJDBCDTO> efwdConnSqlJDBC = new ArrayList<>();
	private List<EFWDConnGroovyDTO> efwdConnGroovy = new ArrayList<>();
	private List<HIEfwdConnSecurityDTO> securityList = new ArrayList<>();
	private List<HIMetadataConnectionEFWDDTO> metadataConnections = new ArrayList<>();
}

package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.helicalinsight.admin.enums.RecycleBinType;

import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecycleBinDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private RecycleBinType type;
	private Long recycleBinId;
	private Integer resourceId;
	private String name;
	private Long resourceTypeId;
	private Date deletedOn;
	private String deletedBy;	
	private String title;
	private String resourceUrl;
	private Object createdBy;
	
	
	public RecycleBinDTO() {
	}
	
	public RecycleBinDTO(Long recycleBinId,  RecycleBinType type, Date deletedOn, String deletedBy) {
		this.recycleBinId = recycleBinId;
		this.type = type;
		this.deletedOn = deletedOn;
		this.deletedBy = deletedBy;
	}

	public RecycleBinDTO(Long recycleBinId, RecycleBinType type, Date deletedOn, String deletedBy,
			Integer resourceId, String name, String title, String resourceUrl, Integer createdBy ) {
		this.recycleBinId = recycleBinId;
		this.type = type;
		this.deletedOn = deletedOn;
		this.deletedBy = deletedBy;
		this.resourceId = resourceId;
		this.name = name;
		this.title = title;
		this.resourceUrl = resourceUrl;
		this.createdBy = createdBy;
	}
}

package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.helicalinsight.resourcedb.HIResourceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIEfwdDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	//quick access
	private Integer resourceId;
	private String resourceUrl;
	private Integer createdBy;
	private Boolean isDeleted;
	private String title;
	private String resourcePath;
	private HIResourceDTO parentResource;
	private Date createdDate;
	private Date lastUpdatedTime;
	
	
	public HIEfwdDTO(Integer resourceId, String resourceUrl, Integer createdBy, Boolean isDeleted, String title, String resourcePath) {
		this.resourceId = resourceId;
        this.resourceUrl = resourceUrl;
        this.createdBy = createdBy;
        this.isDeleted = isDeleted;
        this.title = title;
        this.resourcePath = resourcePath;
	}
	
}

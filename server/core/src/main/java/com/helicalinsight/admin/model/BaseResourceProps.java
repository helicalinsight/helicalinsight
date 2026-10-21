package com.helicalinsight.admin.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * Shared properties for resource payloads.
 * {@code isDeleted} is transient (not a DB column on embeddables).
 */
@MappedSuperclass
public abstract class BaseResourceProps implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	@JsonProperty("isDeleted")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean isDeleted;

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}

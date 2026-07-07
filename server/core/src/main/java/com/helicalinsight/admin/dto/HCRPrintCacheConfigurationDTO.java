package com.helicalinsight.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class HCRPrintCacheConfigurationDTO {

	@SerializedName("enabled")
	private Boolean enabled;
	@SerializedName("maxPagesInMemory")
	private int maxPagesInMemory;
	@SerializedName("swap")
	private SwapConfigDTO swap;
}
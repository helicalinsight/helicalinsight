package com.helicalinsight.admin.dto;


import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.helicalinsight.efw.model.Embed;
import com.helicalinsight.efw.model.Module;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(builderClassName = "Builder")
@JsonInclude(value = Include.NON_EMPTY)
public class ProductInformationDTO {
		
		@JsonProperty("Product Name")
		private String productName;
		
		@JsonProperty("Build")
		private String buildNumber;
		
		@JsonProperty("Version")
		private String version;
		
		@JsonProperty("Product Type")
		private String productType;
		
		@JsonProperty("License Type")
		private String licenseType;
		
		@JsonProperty("Expiration")
		private String expiration;
		
		private Limits limits;
		
		
		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		@JsonInclude(value = Include.NON_EMPTY)
		public static class Limits {
			private Integer users;
			private Embed embed;
			private Set<Module> modules;
		}
}

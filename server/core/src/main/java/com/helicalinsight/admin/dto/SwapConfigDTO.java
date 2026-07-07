package com.helicalinsight.admin.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SwapConfigDTO {
	
	@SerializedName("blockSize")
	private int blockSize;
	
	@SerializedName("minGrowCount")
	private int minGrowCount;
}
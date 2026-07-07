package com.helicalinsight.admin.dto;

import java.util.Map;


public record ResultSetConfigDTO (
		int batchSize, 
		int fetchSize, 
		Map<String,ConfigOverrides> dbConfigOverrides, 
		boolean autoCommit
		) {
	
	public record ConfigOverrides(
			int fetchSize,
			boolean autoCommit
			) {
		
	}
}
